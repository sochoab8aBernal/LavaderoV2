package com.example.lavadero;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GetSalesRequest extends JsonObjectRequest {
    private static final String URL="https://serviciotecnicoindico.com/prueba/getSalesEmployees.php";
    Map<String, String> parametros;
    public GetSalesRequest(JSONObject jsonObject,Response.Listener<JSONObject> listener) {
        super(Method.POST, URL, jsonObject, listener, null);

    }


}
