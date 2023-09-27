package com.example.lavadero;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class StartLicense extends AppCompatActivity {
    EditText edt_nombre_activacion, edt_codigo_activacion, edt_correo_activacion;
    Button btn_activar;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("mi_aplicacion5", this.MODE_PRIVATE);
        String nombre = sharedPreferences.getString("preference_licencia", "0");
        if (nombre.equals("1")) {

            Intent intent= new Intent(StartLicense.this,MainActivity.class);
            startActivity(intent);
        }
        setContentView(R.layout.activity_inicio);

        edt_nombre_activacion= findViewById(R.id.edt_nombre_activacion);
        edt_codigo_activacion= findViewById(R.id.edt_codigo_activacion);
        edt_correo_activacion= findViewById(R.id.edt_correo_activacion);
        btn_activar= findViewById(R.id.btn_activar);

        btn_activar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultaCodigo();
            }
        });


    }

    public void consultaCodigo(){
        Response.Listener<String> listener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject= new JSONObject(response);
                    boolean success= jsonObject.getBoolean("success");
                    if(success){
                        String activo= jsonObject.getString("activa");
                        if(activo.equals("SI")){
                            Toast.makeText(StartLicense.this, "Esta licencia ya se encuentra activa en otro dispositivo", Toast.LENGTH_SHORT).show();
                        }else{
                            actualizarEstado(edt_codigo_activacion.getText().toString(),edt_nombre_activacion.getText().toString(),edt_correo_activacion.getText().toString());
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(StartLicense .this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        };

        consultaCodigoRequest consultaCodigo= new consultaCodigoRequest(edt_codigo_activacion.getText().toString(),listener);
        RequestQueue queue= Volley.newRequestQueue(StartLicense.this);
        queue.add(consultaCodigo);
    }
    public void actualizarEstado(String codigo,String nombre, String correo){
        progressDialog= ProgressDialog.show(StartLicense.this,
                "Validando numero de licencia",
                "Espere por favor",
                true,
                false
        );
        Response.Listener<String> listener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject= new JSONObject(response);
                    boolean success= jsonObject.getBoolean("success");
                    if(success){
                        SharedPreferences sharedPreferences = getSharedPreferences("mi_aplicacion5", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("preference_licencia", "1");
                        editor.apply();
                        progressDialog.dismiss();
                        Toast.makeText(StartLicense.this, "Se ha activado la licencia correctamente ", Toast.LENGTH_LONG).show();
                        Intent intent= new Intent(StartLicense.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        progressDialog.dismiss();
                        Intent intent= new Intent(StartLicense.this,StartLicense.class);
                        startActivity(intent);
                        Toast.makeText(StartLicense.this, "Error en la activacion del numero de licencia", Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        ActualizaCodigoRequest actualizaCodigoRequest= new ActualizaCodigoRequest(codigo,nombre,correo,listener);
        RequestQueue queue= Volley.newRequestQueue(StartLicense.this);
        queue.add(actualizaCodigoRequest);
    }

    @Override
    public void onBackPressed() {
        // Cerrar la aplicación cuando se presiona el botón de retroceso
        finishAffinity();
    }


    }
