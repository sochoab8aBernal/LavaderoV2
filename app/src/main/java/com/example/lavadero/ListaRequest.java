package com.example.lavadero;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class ListaRequest extends JsonObjectRequest {
    private static final String URL = "https://serviciotecnicoindico.com/prueba/Lista.php";

    public ListaRequest(Response.Listener<JSONObject> listener) {
        super(Method.POST,URL,null,listener,null);

    }

}

