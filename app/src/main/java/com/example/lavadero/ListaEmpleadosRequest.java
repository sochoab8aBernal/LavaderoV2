package com.example.lavadero;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class ListaEmpleadosRequest extends JsonObjectRequest {

    private static final String URL = "https://serviciotecnicoindico.com/prueba/listaEmpleados.php";

    public ListaEmpleadosRequest(Response.Listener<JSONObject> listener) {
        super(Method.POST,URL,null,listener,null);

    }

}
