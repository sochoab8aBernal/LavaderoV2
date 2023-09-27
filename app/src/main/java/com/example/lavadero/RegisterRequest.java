package com.example.lavadero;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    private static final String URL="https://peritoneal-teeth.000webhostapp.com/camera/Lista.php";
    private Map<String,String> parametros;
    public RegisterRequest (String nombre,String cedula,String correo, String placas, String telefono, String tipo, String foto, Response.Listener<String> listener){
        super(Method.POST, URL,listener,null);
        parametros= new HashMap<>();
        parametros.put("nombre",nombre);
        parametros.put("cedula",cedula);
        parametros.put("correo",correo);
        parametros.put("placas",placas);
        parametros.put("telefono",telefono);
        parametros.put("tipo",tipo);
        parametros.put("path",foto);

    }

    public Map<String, String> getParams() {
        return parametros;
    }
}
