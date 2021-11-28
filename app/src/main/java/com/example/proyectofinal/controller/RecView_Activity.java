package com.example.proyectofinal.controller;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectofinal.R;
import com.example.proyectofinal.adapter.Adapter;
import com.example.proyectofinal.io.ApiConect;
import com.example.proyectofinal.model.Planeta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecView_Activity extends AppCompatActivity {
    ArrayList<Planeta> listPlanetas;
    RecyclerView recyclerView;
    //Instancio adaptador de mi clase
    Adapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_view);

        /**Ejecuto la consulta*/
        new URL().execute();

        listPlanetas = new ArrayList<>();
        recyclerAdapter = new Adapter(listPlanetas, this);
        /**Creo un setOnclickListener que al pulsarlo, lanzará una nueva actividad donde se mostrará,la imagen del coche, con su nombre
         y descipcion*/
        recyclerAdapter.setOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecView_Activity.this, DescPlaneta_Activity.class);
                String url = listPlanetas.get(recyclerView.getChildAdapterPosition(view)).getUrl();
                String nasa_id = listPlanetas.get(recyclerView.getChildAdapterPosition(view)).getNasa_id();
                String titulo = listPlanetas.get(recyclerView.getChildAdapterPosition(view)).getTitulo();

                i.putExtra("nasa_id", nasa_id);
                i.putExtra("title", titulo);
                i.putExtra("url_imagen", url);

                startActivity(i);
            }
        });
        /**Creo un GridLayoutManager de tal manera que se muestren dos objetos por fila*/
        GridLayoutManager glayout = new GridLayoutManager(this, 2);
        recyclerView = findViewById(R.id.recView);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(glayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preferences, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(RecView_Activity.this, Preferences_Activity.class);
        startActivity(i);
        return true;
    }


    public class URL extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result, nasa_id = "\"\"";
            result = ApiConect.getRequest(nasa_id);
            return result;
        }

        @Override
        /**
         * Saca los valores necesarios para crear el objeto y rellenar el array de objetos
         */
        protected void onPostExecute(String s) {
            try {
                String nasa_id, title;
                if (s != null) {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject collection = jsonObject.getJSONObject("collection");
                    JSONArray items = collection.getJSONArray("items");
                    for (int i = 0; i < 50; i++) {
                        JSONArray data = items.getJSONObject(i).getJSONArray("data");
                        for (int z = 0; z < data.length(); z++) {
                            nasa_id = data.getJSONObject(z).getString("nasa_id");
                            title = data.getJSONObject(z).getString("title");

                            listPlanetas.add(new Planeta(nasa_id, title));
                        }
                    }
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    throw new JSONException("Error");
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Problema al cargar los datos", Toast.LENGTH_SHORT).show();
            }
        }
    }
}