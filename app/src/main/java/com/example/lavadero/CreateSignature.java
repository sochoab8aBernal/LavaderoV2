package com.example.lavadero;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tscdll.TSCActivity;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateSignature extends AppCompatActivity {

    private SignaturePad mSignaturePad;
    ProgressDialog progressDialog;
    private String correo, placas,foto,precio;
    private Button mClearButton;
    private Button mSaveButton;
    Bitmap decode;
    private Bitmap signatureBitmap;
    String currentPhotoPath,nombreCliente,cedulaCliente,nombreEmpleado,servicio,cedulaClienteModificada,consecutivo;
    TSCActivity TscEthernetDll;

    public static final int REQUEST_CODE_PERMISSION= 1;


    Calendar calendar = Calendar.getInstance();

    // Formatea la fecha en el formato deseado: día/mes/año
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    String fechaActual = dateFormat.format(calendar.getTime());

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_signature);

        mSignaturePad = findViewById(R.id.signature_pad);
        mClearButton = findViewById(R.id.clear_button);
        mSaveButton = findViewById(R.id.save_button);
        correo= getIntent().getStringExtra("correo");
        placas= getIntent().getStringExtra("placas");
        nombreCliente= getIntent().getStringExtra("nombreCliente");
        cedulaCliente= getIntent().getStringExtra("cedulaCliente");
        nombreEmpleado= getIntent().getStringExtra("nombreEmpleado");
        servicio= getIntent().getStringExtra("servicio");
        TscEthernetDll= new TSCActivity();

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignaturePad.clear();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog= ProgressDialog.show(CreateSignature.this,
                        "Generando comprobante de pago",
                        "Espere por favor",
                        true,
                        false
                );

                signatureBitmap = mSignaturePad.getSignatureBitmap();


                saveImage();


            }
        });
    }

    private String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte[] imageBytes = bytes.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void saveImage(){
        checkPermissions();

        try {

            setToImageView(getResizeBitmap(signatureBitmap, 1014));

            foto = getStringImage(decode);

            String URL= "https://serviciotecnicoindico.com/prueba/UploadServicesFinished.php";
            StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try {
                        JSONObject jsonObject= new JSONObject(response);
                        precio= jsonObject.getString("precio");
                        consecutivo= jsonObject.getString("consecutivo");

                        progressDialog.dismiss();


                        Toast.makeText(CreateSignature.this, "Lavado terminado con exito", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        setResult(RESULT_OK, intent);
                        finish();
                        TscEthernetDll.openport("00:0C:BF:0A:C6:73");

                        TscEthernetDll.sendcommand("SIZE 72.5 mm, 110.1 mm" + "\n");

                        TscEthernetDll.sendcommand("DENSITY 10\n");
                        TscEthernetDll.sendcommand("DIRECTION 0,0\n");
                        TscEthernetDll.sendcommand("REFERENCE 0,0\n");
                        TscEthernetDll.sendcommand("GAP 0 mm, 0 mm\n");
                        TscEthernetDll.sendcommand("OFFSET 0 mm\n");

                        TscEthernetDll.sendcommand("SET PEEL OFF\n");
                        TscEthernetDll.sendcommand("SET CUTTER OFF\n");
                        TscEthernetDll.sendcommand("SET PARTIAL_CUTTER OFF\n");
                        TscEthernetDll.sendcommand("CODEPAGE 1252\n");
                        TscEthernetDll.sendcommand("CLS\n");


                        TscEthernetDll.sendcommand("TEXT 439,594,\"2\",180,1,1,\"Comprobante de venta"+"\"\n");
                        TscEthernetDll.sendcommand("TEXT 355,514,\"2\",180,1,1,\"Telefono1"+"\"\n");
                        TscEthernetDll.sendcommand("TEXT 355,475,\"2\",180,1,1,\"Telefono2"+"\"\n");
                        TscEthernetDll.sendcommand("TEXT 439,554,\"2\",180,1,1,\"Nombre Establecimiento"+"\"\n");
                        TscEthernetDll.sendcommand("TEXT 559,418,\"2\",180,1,1,\"CLIENTE:     "+nombreCliente+"\"\n");
                        TscEthernetDll.sendcommand("TEXT 559,378,\"2\",180,1,1,\"ID CIENTE:   "+cedulaCliente+"\"\n");
                        TscEthernetDll.sendcommand("TEXT 559,334,\"2\",180,1,1,\"FUE ATENDIDO POR:   "+nombreEmpleado+"\"\n");

                        TscEthernetDll.sendcommand("BAR 13,299, 610, 4"+"\n");
                        TscEthernetDll.sendcommand("TEXT 533,284,\"2\",180,1,1,\"CONSECUTIVO                  CV-"+consecutivo+"\"\n");
                        TscEthernetDll.sendcommand("BAR 14,255, 610,4"+"\n");
                        TscEthernetDll.sendcommand("TEXT 533,217,\"2\",180,1,1,\"FECHA:        "+fechaActual+"\"\n");
                        TscEthernetDll.sendcommand("BAR 14,164, 610, 4"+"\n");
                        TscEthernetDll.sendcommand("TEXT 557,144,\"2\",180,1,1,\"TOTAL A PAGAR:           $"+precio+"\"\n");
                        TscEthernetDll.sendcommand("BAR 14,105, 610, 4"+"\n");

                        TscEthernetDll.sendcommand("TEXT 546,83,\"1\",180,1,1,\"REFERENCIA"+"\"\n");
                        TscEthernetDll.sendcommand("TEXT 354,83,\"1\",180,1,1,\"CANT"+"\"\n");
                        TscEthernetDll.sendcommand("TEXT 285,83,\"1\",180,1,1,\"VR.UNITARIO"+"\"\n");
                        TscEthernetDll.sendcommand("TEXT 118,83,\"1\",180,1,1,\"VR.TOTAL"+"\"\n");
                        TscEthernetDll.sendcommand("TEXT 548,57,\"1\",180,1,1,\" "+servicio+"\"\n");
                        TscEthernetDll.sendcommand("TEXT 358,57,\"1\",180,1,1,\"  1"+"\"\n");
                        TscEthernetDll.sendcommand("TEXT 285,57,\"1\",180,1,1,\"  $"+precio+"\"\n");
                        TscEthernetDll.sendcommand("TEXT 118,57,\"1\",180,1,1,\" $"+precio+"\"\n");


                        TscEthernetDll.sendcommand("PRINT 1,2"+ "\n");
                        TscEthernetDll.closeport();

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CreateSignature.this, ""+error.toString(), Toast.LENGTH_SHORT).show();

                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parametros= new HashMap<>();
                    parametros.put("placas",placas);
                    parametros.put("correo",correo);
                    parametros.put("firma",foto);
                    parametros.put("servicio",servicio);
                    parametros.put("empleado",nombreEmpleado);
                    parametros.put("fecha",fechaActual);
                    return parametros;
                }

            };

            RequestQueue queue= Volley.newRequestQueue(CreateSignature.this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(stringRequest);






        } catch (Exception e) {
            System.out.println(e.toString());
        }


    }

    private void setToImageView(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        decode = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

    }

    private Bitmap getResizeBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxSize && height <= maxSize) {
            return bitmap;
        }

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);

    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Si no se tiene el permiso, solicitarlo al usuario
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, realiza la operación que necesites
            } else {
                // Permiso denegado, muestra un mensaje o toma alguna acción
            }
        }
    }
}