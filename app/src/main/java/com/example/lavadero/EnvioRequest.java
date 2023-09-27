package com.example.lavadero;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class EnvioRequest extends StringRequest {
    private static final String URL = "https://api.twilio.com/2010-04-01/Accounts/ACf6ef56ee292c9269ed721191b9482326/Messages";
    private Map<String, String> parametros;

    public EnvioRequest( String numero, String nombre, String placas, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parametros = new HashMap<>();
        parametros.put("To",  numero);
        parametros.put("From", "+18146224209");
        parametros.put("Body", "Señor/a "+nombre+" su vehiculo de placas "+placas+" ya esta listo." );

    }

    @Override
    protected Map<String, String> getParams() {
        return parametros;
    }

    @Override
    public Map<String, String> getHeaders() {
        HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Basic " + "QUNmNmVmNTZlZTI5MmM5MjY5ZWQ3MjExOTFiOTQ4MjMyNjo4NDVhNGRiMWNmNGFiZTg4MDllMjA2YTFjMTY2OTI4NA==");
        return headers;

    }

}

