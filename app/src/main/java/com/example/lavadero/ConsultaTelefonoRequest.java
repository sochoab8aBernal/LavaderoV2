package com.example.lavadero;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ConsultaTelefonoRequest extends StringRequest {
    private static final String URL="https://serviciotecnicoindico.com/prueba/Notifica.php";
    private Map<String,String> parametros;
    public ConsultaTelefonoRequest (String placas, Response.Listener<String> listener){
        super(Request.Method.POST, URL,listener,null);
        parametros= new HashMap<>();
        parametros.put("placas",placas);



    }

    public Map<String, String> getParams() {
        return parametros;
    }
}
