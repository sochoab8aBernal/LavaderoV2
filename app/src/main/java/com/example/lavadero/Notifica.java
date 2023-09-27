package com.example.lavadero;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class Notifica extends AppCompatActivity {

    Button btnNotificar;
    ProgressDialog progressDialog;
    EditText edtPlacasNotificacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifica);

        edtPlacasNotificacion= findViewById(R.id.edt_placasNotifica);
        btnNotificar= findViewById(R.id.btn_notifica);


        btnNotificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog= ProgressDialog.show(Notifica.this,
                        "Notificando al cliente",
                        "Espere por favor",
                        true,
                        true
                );
                Response.Listener<String> listener= new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            String telefono= jsonObject.getString("telefono");
                            String nombre= jsonObject.getString("nombre");
                            String placas= jsonObject.getString("placas");

                            enviarMensaje(telefono,nombre,placas);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                ConsultaTelefonoRequest consultaTelefonoRequest= new ConsultaTelefonoRequest(edtPlacasNotificacion.getText().toString(),listener);
                RequestQueue queue= Volley.newRequestQueue(Notifica.this);
                queue.add(consultaTelefonoRequest);
            }
        });
    }

    public void enviarMensaje(String telefono,String nombre,String placas){
        Response.Listener<String> listener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(Notifica.this, "NOTIFICACION ENVIADA CON EXITO", Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(Notifica.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        EnvioRequest envioRequest= new EnvioRequest("+57"+telefono,nombre,placas,listener );
        RequestQueue queue=Volley.newRequestQueue(Notifica.this);
        queue.add(envioRequest);
    }

    @Override
    public void onBackPressed() {
        Intent intent= new Intent(Notifica.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}