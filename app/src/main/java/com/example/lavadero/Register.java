package com.example.lavadero;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Register extends AppCompatActivity {
    Button btn_register2, btn_takeP,btnFotoVehiculo;
    String currentPhotoPath,firma;
    Bitmap decode,decoddevehi;
    ProgressDialog progressDialog;
    List<Bitmap> photos;
    ArrayList<String> empleados;
    ArrayList<String> servicios;


    JSONArray jsonArrayEmpleados, jsonArrayServicios;
    List<String> arregloCodificado;
    EditText edt_name,edt_correo,edt_cedula,edt_placas,edt_telefono;
    Spinner sp_tipo,sp_empleados ;


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


    static final int REQUEST_PERMISSION_CAMERA=100;
    static final int REQUEST_TAKE_PHOTO=101;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final int REQUEST_CODE_FIRMA = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        String [] tipo={"Manguera a presion","Lavado a mano", "Lavado ecologico","Tunel de lavado"};
        checkPermissions();
        arregloCodificado = new ArrayList<>();
        photos = new ArrayList<>();
        empleados= new ArrayList<>();
        servicios= new ArrayList<>();
        btn_register2= findViewById(R.id.btn_register2);
        btn_takeP= findViewById(R.id.btn_photo);
        edt_name= findViewById(R.id.edt_name);
        edt_cedula= findViewById(R.id.edt_cedula);
        edt_correo= findViewById(R.id.edt_email);
        edt_placas= findViewById(R.id.edt_placas3);
        edt_telefono= findViewById(R.id.edt_telefono);
        sp_tipo= findViewById(R.id.sp_tipo);
        btnFotoVehiculo= findViewById(R.id.btn_foto_vehiculo);
        sp_empleados= findViewById(R.id.sp_empleado);

        progressDialog= ProgressDialog.show(Register.this,
                "Cargando formulario de registro",
                "Espere por favor",
                true,
                true
        );





        Response.Listener<JSONObject> listener= new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    jsonArrayEmpleados= new JSONArray(response.getString("json"));

                    for(int i=0;i<jsonArrayEmpleados.length();i++) {
                        JSONObject jsonObject = jsonArrayEmpleados.getJSONObject(i);
                        empleados.add(jsonObject.getString("nombre"));
                    }
                    ArrayAdapter<String> adp_empleados= new ArrayAdapter<String>(Register.this, android.R.layout.simple_spinner_item, empleados);
                    sp_empleados.setAdapter(adp_empleados);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        };

        ListaEmpleadosRequest listaEmpleadosRequest= new ListaEmpleadosRequest(listener);
        RequestQueue queue= Volley.newRequestQueue(Register.this);
        queue.add(listaEmpleadosRequest);

        Response.Listener<JSONObject> listener2= new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    jsonArrayServicios= new JSONArray(response.getString("json"));

                    for(int i=0;i<jsonArrayServicios.length();i++) {
                        JSONObject jsonObject = jsonArrayServicios.getJSONObject(i);
                        servicios.add(jsonObject.getString("referencia"));
                    }
                    ArrayAdapter<String> adp_servicios= new ArrayAdapter<String>(Register.this, android.R.layout.simple_spinner_item, servicios);
                    sp_tipo.setAdapter(adp_servicios);
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        };

        ListaServiciosRequest listaServiciosRequest= new ListaServiciosRequest(listener2);
        RequestQueue queue2= Volley.newRequestQueue(Register.this);
        queue2.add(listaServiciosRequest);



        btnFotoVehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takePictureFullSizeVehi();
            }
        });

        btn_register2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressDialog= ProgressDialog.show(Register.this,
                        "Registrando lavado",
                        "Espere por favor",
                        true,
                        true
                );

                try{

                    String URL="https://serviciotecnicoindico.com/prueba/upload.php";
                    String name= edt_name.getText().toString();
                    String cedula= edt_cedula.getText().toString();
                    String correo= edt_correo.getText().toString();
                    String placas= edt_placas.getText().toString();
                    String telefono= edt_telefono.getText().toString();
                    String tipo= sp_tipo.getSelectedItem().toString();
                    String empleado= sp_empleados.getSelectedItem().toString();
                    String foto= getStringImage(decode);
                    String foto2= getStringImage(decoddevehi);


                    for (int i = 0; i < photos.size(); i++) {

                        arregloCodificado.add(getStringImage(photos.get(i)));



                    }





                    photos.clear();

                    StringRequest stringRequest= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            progressDialog.dismiss();
                            Intent intent= new Intent(Register.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(Register.this, "Lavado registrado con exito", Toast.LENGTH_SHORT).show();


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Toast.makeText(Register.this, ""+error.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> parametros= new HashMap<>();
                            parametros.put("nombre",name);
                            parametros.put("cedula",cedula);
                            parametros.put("correo",correo);
                            parametros.put("placas",placas);
                            parametros.put("telefono",telefono);
                            parametros.put("tipo",tipo);
                            parametros.put("empleado",empleado);
                            parametros.put("path",foto);
                            for (int i = 0; i < arregloCodificado.size(); i++) {
                                String paramName = "foto" + i;
                                parametros.put(paramName, arregloCodificado.get(i));
                            }


                            return parametros;
                        }


                    };
                    RequestQueue queue= Volley.newRequestQueue(Register.this);
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(stringRequest);

                }catch (Exception e){

                }

            }
        });


        btn_takeP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){


                takePictureFullSize();

            }
        });

    }



    private  String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream bytes= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,bytes);
        byte[] imageBytes= bytes.toByteArray();
        return Base64.encodeToString(imageBytes,Base64.DEFAULT);
    }

    private void checkPermissions(){
        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        } else if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    1
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode== REQUEST_PERMISSION_CAMERA){
            if(permissions.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                takePictureFullSize();

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_TAKE_PHOTO){
            if(resultCode== Activity.RESULT_OK){

                try {
                    File file = new File(currentPhotoPath);

                    Uri uri= Uri.fromFile(file);

                    Bitmap bitmap= MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),
                            uri
                    );

                    setToImageView(getResizeBitmap(bitmap,1014));
                    currentPhotoPath="";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else  if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                File file = new File(currentPhotoPath);

                Uri uri= Uri.fromFile(file);

                Bitmap bitmap= MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(),
                        uri
                );

                setToImageViewVehi(getResizeBitmap(bitmap,1014));
                currentPhotoPath="";
            } catch (IOException e) {
                e.printStackTrace();
            }



        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap getResizeBitmap(Bitmap bitmap, int maxSize) {
        int width= bitmap.getWidth();
        int height = bitmap.getHeight();

        if(width<= maxSize && height<=maxSize){
            return  bitmap;
        }

        float bitmapRatio= (float) width/ (float)  height;
        if(bitmapRatio >1){
            width= maxSize;
            height= (int) (width/bitmapRatio);
        }else{
            height= maxSize;
            width= (int)(height*bitmapRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width,height,true);

    }



    private void setToImageView(Bitmap bitmap) {
        ByteArrayOutputStream bytes= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        decode= BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));


    }

    private void setToImageViewVehi(Bitmap bitmap) {
        ByteArrayOutputStream bytes= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        decoddevehi= BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        photos.add(decoddevehi);



    }

    private File createImageFile()throws IOException {
        String timeStamp= new SimpleDateFormat("yyyyMMdd HHmmss", Locale.getDefault()).format(new Date());
        String imagenFileName= "JPEG_"+timeStamp+"_";
        File storageDir= getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image= File.createTempFile(
                imagenFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath= image.getAbsolutePath();
        return image;
    }

    private void takePictureFullSize(){
        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!= null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }catch (IOException e){
                e.getMessage();
            }

            if(photoFile != null){
                Uri photoUri= FileProvider.getUriForFile(
                        this,
                        "com.example.lavadero",
                        photoFile
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(intent,REQUEST_TAKE_PHOTO);

            }



        }
    }

    private void takePictureFullSizeVehi(){
        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!= null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }catch (IOException e){
                e.getMessage();
            }

            if(photoFile != null){
                Uri photoUri= FileProvider.getUriForFile(
                        this,
                        "com.example.lavadero",
                        photoFile
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);

            }



        }
    }

    @Override
    public void onBackPressed() {
        Intent intent= new Intent(Register.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}