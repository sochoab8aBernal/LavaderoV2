package com.example.lavadero;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Manages extends AppCompatActivity {
    Spinner spEmpleados ;
    JSONArray jsonArrayEmpleados,jsonArraySales;
    ArrayList<String> empleados,salesEmployees;
    RadioGroup rdGroup;
    Button btnFilter;
    String rbSelected,spSelected;
    TableLayout table;
    ProgressDialog progressDialog;

    TextView txtTotal;
    EditText edtDate;
    int total=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manages);

        spEmpleados = findViewById(R.id.spEmpleados);
        rdGroup = findViewById(R.id.rdGroup);
        btnFilter = findViewById(R.id.btnFilter);
        table = findViewById(R.id.table);
        empleados = new ArrayList<>();
        salesEmployees = new ArrayList<>();
        edtDate= findViewById(R.id.edtDate);
        txtTotal= findViewById(R.id.txtTotal);
        txtTotal.setText("TOTAL: $0");


        rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton selectedRadioButton = findViewById(i);
                if (selectedRadioButton != null) {

                    if(selectedRadioButton.getText().equals("Diario")){
                        table.removeAllViews();
                        final Calendar calendario = Calendar.getInstance();
                        int año = calendario.get(Calendar.YEAR);
                        int mes = calendario.get(Calendar.MONTH);
                        int día = calendario.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                Manages.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                        if(month<9){
                                            String fechaSeleccionada = day + "/0" + (month + 1) + "/" + year;
                                            edtDate.setText(fechaSeleccionada);
                                        }else{
                                            String fechaSeleccionada = day + "/" + (month + 1) + "/" + year;
                                            edtDate.setText(fechaSeleccionada);
                                        }

                                    }
                                },
                                año, mes, día
                        );

                        datePickerDialog.show();
                    }else if(selectedRadioButton.getText().equals("Mensual")){
                        table.removeAllViews();
                        final Calendar calendario = Calendar.getInstance();
                        int año = calendario.get(Calendar.YEAR);
                        int mes = calendario.get(Calendar.MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                Manages.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                        if(month<9){
                                            String fechaSeleccionada = "0"+(month + 1) + "/" + year;
                                            edtDate.setText(fechaSeleccionada);
                                        }else{
                                            String fechaSeleccionada = (month + 1) + "/" + year;
                                            edtDate.setText(fechaSeleccionada);
                                        }


                                    }
                                },
                                año, mes, 0 // El día se establece en 0 para que solo muestre meses y años
                        );


                        datePickerDialog.show();
                    }else{
                        table.removeAllViews();
                        final Calendar calendario = Calendar.getInstance();
                        int año = calendario.get(Calendar.YEAR);
                        int mes = calendario.get(Calendar.MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                Manages.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                        String fechaSeleccionada =  year+"";
                                        edtDate.setText(fechaSeleccionada);
                                    }
                                },
                                año, 0, 0 // El día se establece en 0 para que solo muestre meses y años
                        );


                        datePickerDialog.show();
                    }

                }
            }
        });




        Response.Listener<JSONObject> listener= new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    jsonArrayEmpleados= new JSONArray(response.getString("json"));

                    for(int i=0;i<jsonArrayEmpleados.length();i++) {
                        JSONObject jsonObject = jsonArrayEmpleados.getJSONObject(i);
                        empleados.add(jsonObject.getString("nombre"));
                    }
                    ArrayAdapter<String> adp_empleados= new ArrayAdapter<String>(Manages.this, android.R.layout.simple_spinner_item, empleados);
                    spEmpleados.setAdapter(adp_empleados);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        };

        ListaEmpleadosRequest listaEmpleadosRequest= new ListaEmpleadosRequest(listener);
        RequestQueue queue= Volley.newRequestQueue(Manages.this);
        queue.add(listaEmpleadosRequest);


        btnFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                txtTotal.setText("TOTAL: $0");
                total=0;
                table.removeAllViews();
                spSelected= spEmpleados.getSelectedItem().toString();
                int selectedId= rdGroup.getCheckedRadioButtonId();
                String url="https://serviciotecnicoindico.com/prueba/getSalesEmployees.php";
                if(selectedId != -1){
                    RadioButton selectedRb= findViewById(selectedId);
                    rbSelected= selectedRb.getText().toString();
                }else{
                    Toast.makeText(Manages.this, "Debe seleccionar alguna opcion", Toast.LENGTH_SHORT).show();
                }

                progressDialog= ProgressDialog.show(Manages.this,
                        "Filtrando informacion",
                        "Espere por favor",
                        true,
                        true
                );

                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put("empleado", spEmpleados.getSelectedItem());
                    jsonParams.put("fecha", edtDate.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                Response.Listener<JSONObject> jsonObjectListener= new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            jsonArraySales= new JSONArray(response.getString("json"));

                            if(jsonArraySales.length()==0){
                                Toast.makeText(Manages.this, "No existen ventas registradas", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }else{
                                for(int i=0; i< jsonArraySales.length();i++){
                                    JSONObject jsonObject= jsonArraySales.getJSONObject(i);
                                    String placa= jsonObject.getString("placa");
                                    String valor= jsonObject.getString("valor");
                                    String fecha= jsonObject.getString("fecha");

                                    total = total + Integer.parseInt(valor);

                                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                                            TableRow.LayoutParams.WRAP_CONTENT, // Ancho
                                            TableRow.LayoutParams.WRAP_CONTENT // Alto
                                    );

                                    layoutParams.setMargins(70, 0, 10, 0);

                                    TableRow row= new TableRow(Manages.this);
                                    TextView placaTextView = new TextView(Manages.this);
                                    TextView fechaTextView = new TextView(Manages.this);
                                    TextView valorTextView = new TextView(Manages.this);


                                    placaTextView.setLayoutParams(layoutParams);
                                    valorTextView.setLayoutParams(layoutParams);
                                    fechaTextView.setLayoutParams(layoutParams);

                                    placaTextView.setTextSize(18.0f);
                                    valorTextView.setTextSize(18.0f);
                                    fechaTextView.setTextSize(18.0f);

                                    placaTextView.setText(placa);
                                    fechaTextView.setText(fecha);
                                    valorTextView.setText(valor);




                                    row.addView(placaTextView);
                                    row.addView(fechaTextView);
                                    row.addView(valorTextView);


                                    table.addView(row);

                                    progressDialog.dismiss();

                                }
                                txtTotal.setText("TOTAL: $"+total+"");
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                GetSalesRequest getSalesRequest= new GetSalesRequest(jsonParams,jsonObjectListener);
                RequestQueue queue= Volley.newRequestQueue(Manages.this);
                queue.add(getSalesRequest);
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent= new Intent(Manages.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}