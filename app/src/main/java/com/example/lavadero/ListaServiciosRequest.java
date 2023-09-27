package com.example.lavadero;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class ListaServiciosRequest extends JsonObjectRequest {

    private static final String URL = "https://serviciotecnicoindico.com/prueba/listaServicios.php";

    public ListaServiciosRequest(Response.Listener<JSONObject> listener) {
        super(Method.POST, URL, null, listener, null);

    }



    }
