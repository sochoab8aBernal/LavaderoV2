package com.example.lavadero;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ActualizaCodigoRequest extends StringRequest {
    private static String URL="https://peritoneal-teeth.000webhostapp.com/actualizaLicencia.php";
    private Map<String,String> parametros;
    public ActualizaCodigoRequest(String codigoActivacion, String nombre, String correo, Response.Listener<String> listener){
        super(Method.POST, URL,listener,null );
        parametros= new HashMap<>();
        parametros.put("codigoActivacion",codigoActivacion);
        parametros.put("nombre",nombre);
        parametros.put("correo",correo);
    }


    @Override
    protected Map<String, String> getParams() {
        return parametros;
    }
}
