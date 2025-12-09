package com.example.appsqliteopenhelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetalleArticulos extends AppCompatActivity {
    ImageView ivDetalleImagen;
    TextView tvCodigo, tvDescripcion, tvPrecio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_articulos);

        tvCodigo = findViewById(R.id.DetalleCodigo);
        tvDescripcion = findViewById(R.id.DetaleDescripcion);
        tvPrecio = findViewById(R.id.DetallePrecio);
        ivDetalleImagen = findViewById(R.id.ivDetalleImagen);

        // Recuperar datos recibidos
        String codigo = getIntent().getStringExtra("codigo");
        String descripcion = getIntent().getStringExtra("descripcion");
        double precio = getIntent().getDoubleExtra("precio", 0);
        byte[] imagenBytes = getIntent().getByteArrayExtra("imagen");

        // Cargar datos en TextView
        tvCodigo.setText("Código: " + codigo);
        tvDescripcion.setText("Descripción: " + descripcion);
        tvPrecio.setText("Precio: $" + precio);

        // Cargar imagen
        if (imagenBytes != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
            ivDetalleImagen.setImageBitmap(bmp);
        }
    }

}


