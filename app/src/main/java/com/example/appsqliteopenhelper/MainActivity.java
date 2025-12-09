package com.example.appsqliteopenhelper;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.sql.SQLDataException;

public class MainActivity extends AppCompatActivity {

    private EditText etCodigo, etDescripcion, etPrecio;
    private ImageView ivProducto;
    private Button btnRegistrar, btnBuscar, btnEditar, btnEliminar, btnMostrarLista, btnTomarFoto;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private Bitmap fotoBitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etCodigo = findViewById(R.id.txtCodigo);
        etDescripcion = findViewById(R.id.txtDescripcion);
        etPrecio = findViewById(R.id.txtPrecio);
        ivProducto = findViewById(R.id.ivProducto);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnMostrarLista = findViewById(R.id.btnMostrarLista);

        AccionesBotones();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            fotoBitmap = (Bitmap) extras.get("data");
            ivProducto.setImageBitmap(fotoBitmap);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Metodo para registrar producto
    public void registrarProducto() {
        AdminSQLiteHelper adminBD = new AdminSQLiteHelper(this, "administracionBD", null, 2);
        SQLiteDatabase basededatos = adminBD.getWritableDatabase();

        String codigo = etCodigo.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precio = etPrecio.getText().toString().trim();

        if (codigo.isEmpty() || descripcion.isEmpty() || precio.isEmpty()) {
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fotoBitmap == null) {
            Toast.makeText(this, "Debe tomar una foto", Toast.LENGTH_SHORT).show();
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        fotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imagenBytes = baos.toByteArray();

        ContentValues registro = new ContentValues();
        registro.put("codigo", codigo);
        registro.put("descripcion", descripcion);
        registro.put("precio", precio);
        registro.put("imagen", imagenBytes);

        basededatos.insert("articulos", null, registro);
        basededatos.close();

        // Limpiar campos
        etCodigo.setText("");
        etDescripcion.setText("");
        etPrecio.setText("");
        ivProducto.setImageDrawable(null);
        fotoBitmap = null;

        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
    }

    //Metodo para buscar producto
    public void buscarProducto(){
        AdminSQLiteHelper adminBD = new
                AdminSQLiteHelper(this, "administracionBD", null, 2);
        SQLiteDatabase basededatos = adminBD.getWritableDatabase();

        String codigo = etCodigo.getText().toString().trim();

        if (!codigo.isEmpty()){
            Cursor cursor = basededatos.rawQuery(
                    "select descripcion, precio, imagen from articulos where codigo = ?",
                    new String[]{codigo}
            );


            //Cursor que busque la informacion
            if (cursor.moveToFirst()){
                etDescripcion.setText(cursor.getString(0));
                etPrecio.setText(cursor.getString(1));

                byte[] imagenBytes = cursor.getBlob(2);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
                ivProducto.setImageBitmap(bitmap);

                cursor.close();
                basededatos.close();

            }
            else {
                Toast.makeText(this, "No existe un producto con ese codigo", Toast.LENGTH_SHORT).show();
        }
        }else {
            Toast.makeText(this, "Debe ingresar un codigo", Toast.LENGTH_SHORT).show();
        }

    }

    //Metodo para editar producto
    public void editarProducto(){
        AdminSQLiteHelper adminBD = new
                AdminSQLiteHelper(this, "administracionBD", null, 2);
        SQLiteDatabase basededatos = adminBD.getWritableDatabase();

        String codigo = etCodigo.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();

        if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){
            ContentValues registro = new ContentValues();
            registro.put("codigo", codigo);
            registro.put("descripcion", descripcion);
            registro.put("precio", precio);

            //Actualizar los datos en la BD
            int cantidad = basededatos.update("articulos", registro, "codigo = ?", new String[]{codigo});
            basededatos.close();

            if (cantidad !=0){
                etCodigo.setText("");
                etDescripcion.setText("");
                etPrecio.setText("");
                Toast.makeText(this, "Articulo Modificado Correctamente", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Tu Articulo No Existe", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show();
            }
    }
    //Metodo para eliminar producto
    public void eliminarProducto()
    {
        AdminSQLiteHelper adminBD = new
                AdminSQLiteHelper(this, "administracionBD", null, 2);
        SQLiteDatabase baseDeDatos = adminBD.getWritableDatabase();

        String codigo = etCodigo.getText().toString().trim();
        if(!codigo.isEmpty())
        {
            int cantidad = baseDeDatos.delete("articulos", "codigo= ?", new String[]{codigo});
            baseDeDatos.close();

            etCodigo.setText("");
            etDescripcion.setText("");
            etPrecio.setText("");
            ivProducto.setImageDrawable(null);

            if(cantidad == 1)
            {
                Toast.makeText(this, "Articulo Eliminado Correctamente", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Articulo no Existe", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Debe Ingresar un Codigo", Toast.LENGTH_SHORT).show();
        }
    }


    //Metodo para mostrar lista de productos

    public void AccionesBotones(){
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarProducto();
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarProducto();
            }
        });
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarProducto();
            }
        });
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarProducto();
            }
        });
        btnMostrarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SiguienteActivity();
            }
        });
        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTomarFoto.setOnClickListener(v -> verificarPermisoCamara());
            }
        });
    }

    private void verificarPermisoCamara() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            abrirCamara();
        }
    }



    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }



    //Metodo para pasar de activity
    public void SiguienteActivity(){
        Intent intent = new Intent(this, ListadeArticulosV2.class);
        startActivity(intent);
    }
}