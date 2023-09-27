package com.example.lavadero;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class consultaCodigoRequest extends StringRequest {
    private static final String  URL="https://peritoneal-teeth.000webhostapp.com/licencia.php";
    private Map<String,String> parametros;
    public consultaCodigoRequest(String codigoActivacion, Response.Listener<String> listener){
        super(Method.POST, URL,listener,null);
        parametros= new HashMap<>();
        parametros.put("codigoActivacion",codigoActivacion);
    }

    @Override
    protected Map<String, String> getParams() {
        return parametros;
    }
}
