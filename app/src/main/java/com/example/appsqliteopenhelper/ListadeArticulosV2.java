package com.example.appsqliteopenhelper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ListadeArticulosV2 extends AppCompatActivity {

    private ListView tvArticulos;
    private ArrayList<Articulo> articulos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listade_articulos_v2);

        tvArticulos = findViewById(R.id.tvArticulos);
        cargarListaArticulos();
    }

    public void cargarListaArticulos(){
        AdminSQLiteHelper adminBD = new
                AdminSQLiteHelper(this, "administracionBD", null, 2);
        SQLiteDatabase basededatos = adminBD.getReadableDatabase();

        articulos = new ArrayList<>();

        Cursor cursor = basededatos.rawQuery("select codigo, descripcion, precio, imagen from articulos", null);


        if (cursor.moveToFirst()){

            do {
                String codigo = cursor.getString(0);
                String descripcion = cursor.getString(1);
                double precio = cursor.getDouble(2);
                byte[] imagen = cursor.getBlob(3);

                articulos.add(new Articulo(codigo, descripcion, precio, imagen));

            }while (cursor.moveToNext());
        }
        else {
            Toast.makeText(this, "No hay articulos registrados", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        basededatos.close();

        //Mostrar informacion en el listview usando ArrayAdapter
        ArrayAdapter<Articulo> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, articulos);
        tvArticulos.setAdapter(adapter);
        OnItemClick();
    }

    public void OnItemClick() {

        tvArticulos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Articulo articulo = articulos.get(i);

                Intent intent = new Intent(ListadeArticulosV2.this, DetalleArticulos.class);
                intent.putExtra("codigo", articulo.getCodigo());
                intent.putExtra("descripcion", articulo.getDescripcion());
                intent.putExtra("precio", articulo.getPrecio());
                intent.putExtra("imagen", articulo.getImagen());
                startActivity(intent);

            }
        });

    }


}