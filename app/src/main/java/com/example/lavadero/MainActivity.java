package com.example.lavadero;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Button btn_register;
    Button btn_end ,btnConsult;
    ImageButton btnAlerta;
    JSONArray jsonArray;
    LinearLayout llBotonera;
    LinearLayout.LayoutParams lp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        llBotonera= findViewById(R.id.llBotonera);
        btn_register= findViewById(R.id.btn_register);
        btnAlerta= findViewById(R.id.btnAlerta);
        btn_end= findViewById(R.id.btn_end);
        btnConsult=findViewById(R.id.btnConsult);

        btnConsult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this, LoginAdmin.class);
                startActivity(intent);
                finish();
            }
        });
        btnAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Notifica.class);
                startActivity(intent);
                finish();
            }
        });

        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                listar(response);


            }




        };

        ListaRequest mainReques = new ListaRequest(listener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(mainReques);


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, Register.class);
                startActivity(intent);
                finish();
            }
        });

        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,Terminar.class);
                startActivity(intent);
                finish();
            }
        });
    }



    private void listar(JSONObject response) {
        try {
            jsonArray= new JSONArray(response.getString("json"));

            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                boolean success= jsonObject.getBoolean("success");
                if(success){




                    Button txt = new Button(this);
                    txt.setText(jsonObject.getString("placa"));
                    txt.setId(i);
                    txt.setGravity(Gravity.CENTER);
                    txt.setPadding(50,40,50,40);
                    txt.setWidth(3);
                    txt.setTextSize(20);
                    txt.setBackground(getResources().getDrawable(R.drawable.bordes2));
                    txt.setLayoutParams(lp);


                    llBotonera.addView(txt);

                }






            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}