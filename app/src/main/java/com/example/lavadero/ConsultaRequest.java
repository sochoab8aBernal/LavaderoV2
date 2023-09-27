package com.example.lavadero;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConsultaRequest extends StringRequest {
    private static final String URL="https://serviciotecnicoindico.com/prueba/fetch.php";
    private Map<String,String> parametros;
    public ConsultaRequest (String placas,Response.Listener<String> listener){
        super(Method.POST, URL, listener,null);
        parametros= new HashMap<>();
        parametros.put("placas",placas);
    }

    public Map<String, String> getParams() {
        return parametros;
    }
}
