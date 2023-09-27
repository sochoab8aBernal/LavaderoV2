package com.example.lavadero;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Terminar extends AppCompatActivity {
  EditText edt_placas;
  TextView txt_nombre, txt_cedula,txt_telefono;
  Button btn_consultar,btn_terminar;
    ProgressDialog progressDialog;
    List<ImageView> imageViewList;

    String correo, nombreCliente, cedulaCliente,nombreEmpleado,servicio;
  ImageView img_foto;
    public static final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminar);

        edt_placas= findViewById(R.id.edt_placas3);
        btn_consultar= findViewById(R.id.btn_consultar);
        img_foto= findViewById(R.id.img_foto);
        btn_terminar= findViewById(R.id.btn_terminar3);
        txt_nombre= findViewById(R.id.txt_nombre);
        txt_cedula= findViewById(R.id.txt_cedula);
        txt_telefono= findViewById(R.id.txt_telefono);
        imageViewList = new ArrayList<>();
        LinearLayout linearLayout = findViewById(R.id.fotosVehiculo);


        btn_terminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1= new Intent(Terminar.this,CreateSignature.class);
                intent1.putExtra("placas",edt_placas.getText().toString());
                intent1.putExtra("correo", correo);
                intent1.putExtra("nombreCliente",nombreCliente);
                intent1.putExtra("cedulaCliente",cedulaCliente);
                intent1.putExtra("nombreEmpleado",nombreEmpleado);
                intent1.putExtra("servicio",servicio);


                startActivityForResult(intent1 , REQUEST_CODE);

            }
        });

        btn_consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog= ProgressDialog.show(Terminar.this,
                        "Consultando por placa",
                        "Espere por favor",
                        true,
                        false
                );
                String placas= edt_placas.getText().toString();
                Response.Listener<String> listener= new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            String foto= jsonObject.getString("foto");
                            int cantidadFotos= Integer.parseInt(jsonObject.getString("cantidadArchivos"));

                            for(int i=0;i<cantidadFotos;i++){

                                String urlFoto="https://serviciotecnicoindico.com/prueba/FotosVehiculos/"+jsonObject.getString("cedula")+"/foto"+i+""+".jpg";
                                ImageView imageView = new ImageView(Terminar.this);
                                    imageViewList.add(imageView);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);

                                imageView.setLayoutParams(layoutParams);

                                linearLayout.addView(imageView);

                                cargarImagen(urlFoto, imageView);



                            }
                            String arch= jsonObject.getString("nombresArchivos");

                            txt_nombre.setText(Html.fromHtml("    <b>NOMBRE:</b> "+jsonObject.getString("nombre")));
                            txt_cedula.setText(Html.fromHtml("    <b>CEDULA:</b> "+jsonObject.getString("cedula")));
                            txt_telefono.setText(Html.fromHtml("    <b>TELEFONO:</b> "+jsonObject.getString("telefono")));
                            correo= jsonObject.getString("correo");
                            nombreCliente= jsonObject.getString("nombre");
                            cedulaCliente= jsonObject.getString("cedula");
                            nombreEmpleado= jsonObject.getString("Empleado");
                            servicio= jsonObject.getString("tipo");

                            ImageRequest imageRequest= new ImageRequest(foto, new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    img_foto.setImageBitmap(response);
                                    progressDialog.dismiss();

                                }
                            }, 0, 0, null, null, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }

                            );
                            RequestQueue queue= Volley.newRequestQueue(Terminar.this);
                            queue.add(imageRequest);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                 ConsultaRequest consultaRequest= new ConsultaRequest(placas,listener);
                RequestQueue queue= Volley.newRequestQueue(Terminar.this);
                queue.add(consultaRequest);
            }
        });

    }

    private void cargarImagen(String imageUrl, final ImageView imageView) {
        ImageRequest imageRequest = new ImageRequest(imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        // Mostrar la imagen en el ImageView correspondiente
                        imageView.setImageBitmap(response);
                    }
                },
                0,
                0,
                null,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        RequestQueue queue= Volley.newRequestQueue(Terminar.this);
        queue.add(imageRequest);
    }

    @Override
    public void onBackPressed() {
        Intent intent= new Intent(Terminar.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode  == RESULT_OK && requestCode== REQUEST_CODE ) {

                Intent intent= new Intent(Terminar.this,MainActivity.class);
                startActivity(intent);

            }






    }

}