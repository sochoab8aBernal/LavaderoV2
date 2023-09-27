package com.example.lavadero;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginAdmin extends AppCompatActivity {
      EditText edtUser,edtPass;
      Button btnStart;
      ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);
        edtUser=findViewById(R.id.edtUser);
        edtPass= findViewById(R.id.edtPass);
        btnStart= findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog= ProgressDialog.show(LoginAdmin.this,
                        "Validando credenciales...",
                        "Espere por favor",
                        true,
                        true
                );
                String url= "https://serviciotecnicoindico.com/prueba/getUser.php";
                String user= edtUser.getText().toString();
                String pass= edtPass.getText().toString();

                StringRequest stringRequest= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            boolean success= jsonObject.getBoolean("success");
                            if(success){
                                progressDialog.dismiss();
                                Intent intent= new Intent(LoginAdmin.this,Manages.class);
                                startActivity(intent);
                                finish();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(LoginAdmin.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                            }
                            
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginAdmin.this, "Error, vuelva a intentarlo nuevamente", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> parametros= new HashMap<>();
                        parametros.put("user",user);
                        parametros.put("pass",pass);
                         return parametros;
                    }
                };

                RequestQueue queue= Volley.newRequestQueue(LoginAdmin.this);
                queue.add(stringRequest);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent= new Intent(LoginAdmin.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}