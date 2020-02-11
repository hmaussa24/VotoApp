package com.example.votoapp;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class listado extends AppCompatActivity {
    private ListView listado;
    private String URL_POST = "https://vcct.000webhostapp.com/index.php";
    ProgressDialog progress;
    private TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        AdminSQLite admin = new AdminSQLite(this,"votos",null,1);
        SQLiteDatabase basededatos = admin.getWritableDatabase();
        titulo = (TextView) findViewById(R.id.textView2) ;



        if (conexion()){

             sicronizar();
             sendResponse();

        }else{
            Toast.makeText(this, "Estas trabajando sin INTERNET", Toast.LENGTH_SHORT).show();
            Cursor fila = basededatos.rawQuery
                    ("select * from votos", null);
            if(fila.getCount() > 0) {

                if (fila.moveToFirst()) {
                    //nombre.setText(fila.getString(0));
                    listado = (ListView) findViewById(R.id.lista);

                    String nombres[] = new String[fila.getCount()];
                    String cedulas[] = new String[fila.getCount()];
                    String imagen[] = new String[fila.getCount()];
                    int num[] = new int[fila.getCount()];
                    int i = 0;
                    do {
                        nombres[i] = fila.getString(0);
                        cedulas[i] = fila.getString(1);
                        imagen[i] = fila.getString(5);
                        num[i] = i;
                        i++;
                    } while (fila.moveToNext());
                    titulo.setText("Total Votos: " + i);
                    CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), nombres, cedulas, imagen,num);
                    listado.setAdapter(customAdapter);
                    basededatos.close();


                } else {
                    Toast.makeText(this, "No existen datos", Toast.LENGTH_SHORT).show();
                    basededatos.close();
                }
            }else {
                Toast.makeText(this, "No existen datos", Toast.LENGTH_SHORT).show();
                basededatos.close();
            }

        }

        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Se busca la referencia del TextView en la vista.
                TextView textView = (TextView) view.findViewById(R.id.subitem);
                //Obtiene el texto dentro del TextView.
                String textItemList  = textView.getText().toString();
                buscar(textItemList);
            }
        });


    }

    public boolean conexion(){

        boolean connected = false;

        ConnectivityManager connec = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

    public void sendResponse(){
        String url = "https://vcct.000webhostapp.com/sincronizar.php?lider="+usuarioNombre();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsObjectRequest = new JsonArrayRequest(Request.Method.GET, url, (String) null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {


                    //JSONArray jsonArray = response.getJSONArray("votos");
                   // JSONObject jsonObject = jsonArray.getJSONObject(0); //0 indica el primer objeto dentro del array.
                  // System.out.println(jsonObject.getString("character")); //Agrega valor de character a TextView.

                    System.out.println(response.toString() +" send");
                    //Toast.makeText(getApplication(), response.toString(), Toast.LENGTH_LONG).show();
                    JSONArray array = new JSONArray();
                try {
                      array = new JSONArray(response.toString());
                      listado = (ListView) findViewById(R.id.lista);

                    String nombres[] = new String[array.length()];
                    String cedulas[] = new String[array.length()];
                    String imagen[] = new String[array.length()];
                    int num[] = new int[array.length()];
                    int n = 0;
                        for(int i = 0; i<array.length();i++){

                            JSONObject jsonObject = array.getJSONObject(i);
                            nombres[i] = jsonObject.getString("nombre");
                            cedulas[i] = jsonObject.getString("cedula");
                            imagen[i] = "0";
                            num[i] = i;
                            n=i;
                            guardarSincronia(jsonObject.getString("cedula"),jsonObject.getString("nombre"),jsonObject.getString("direccion"),jsonObject.getString("celular"), jsonObject.getString("observacioon"));
                        }
                        n=n+1;
                    titulo.setText("Total Votos: " + n);
                    CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), nombres, cedulas, imagen,num);
                    listado.setAdapter(customAdapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "Error Respuesta en JSON: " + error.getMessage());
                    }
                });
        requestQueue.add(jsObjectRequest);
    }

    public void guardarSincronia(String cedula, String nombre, String dire, String celu, String obse){
        AdminSQLite admin = new AdminSQLite(this,"votos",null,1);
        SQLiteDatabase basededatos = admin.getWritableDatabase();

        String ced = cedula;
        String nom = nombre;
        String dir = dire;
        String cel = celu;
        String obser = obse;
        String[] args = new String[] {ced};
        Cursor c = basededatos.rawQuery(" SELECT cedula FROM votos WHERE cedula=?", args);
        if(c.getCount()>0) {
            //Toast.makeText(this, "La cedula ya esta registrada. Porfavor verifiquela.", Toast.LENGTH_SHORT).show();
        }else {
            if (!ced.isEmpty() && !nom.isEmpty() && !dir.isEmpty()) {
                ContentValues registro = new ContentValues();
                registro.put("cedula", ced);
                registro.put("nombre", nom);
                registro.put("direccion", dir);
                registro.put("celular", cel);
                registro.put("observacion", obser);
                registro.put("estado", 0);

                basededatos.insert("votos", null, registro);
                basededatos.close();
                //limpiar(view);
                //Toast.makeText(this, "Registro realizado", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Debes llenar los campos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void datosVotos(){

        AdminSQLite admin = new AdminSQLite(this,"votos",null,1);
        SQLiteDatabase basededatos = admin.getWritableDatabase();
        titulo = (TextView) findViewById(R.id.textView2) ;

        Toast.makeText(this, "Estas trabajando sin INTERNET", Toast.LENGTH_SHORT).show();
        Cursor fila = basededatos.rawQuery
                ("select * from votos", null);
        if(fila.getCount() > 0) {

            if (fila.moveToFirst()) {
                //nombre.setText(fila.getString(0));
                listado = (ListView) findViewById(R.id.lista);

                String nombres[] = new String[fila.getCount()];
                String cedulas[] = new String[fila.getCount()];
                String imagen[] = new String[fila.getCount()];
                int num[] = new int[fila.getCount()];
                int i = 0;
                do {
                    nombres[i] = fila.getString(1);
                    cedulas[i] = fila.getString(0);
                    imagen[i] = fila.getString(5);
                    num[i] = i;
                    i++;
                } while (fila.moveToNext());
                titulo.setText("Total Votos: " + i);
                CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), nombres, cedulas, imagen,num);
                listado.setAdapter(customAdapter);
                basededatos.close();


            } else {
                Toast.makeText(this, "No existen datos", Toast.LENGTH_SHORT).show();
                basededatos.close();
            }
        }else {
            Toast.makeText(this, "No existen datos", Toast.LENGTH_SHORT).show();
            basededatos.close();
        }
    }



    public void sicronizar(){
        progress = ProgressDialog.show(this, "Sincronizando",
                "Por favor Espere", true);
        Gson datos =  new Gson();
        final String votosJSON = datos.toJson(Datos());
        System.out.println(votosJSON + "sincronizar");
        StringRequest request = new StringRequest(Request.Method.POST, URL_POST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(usuario()){
                    //progress.dismiss();
                    Toast.makeText(getApplication(), "Debes registrarte primero", Toast.LENGTH_SHORT).show();
                    Intent lis = new Intent(getApplicationContext(), RegistroUsuario.class);
                    startActivity(lis);
                }else {
                    System.out.println(response + "sincronizacion 2");
                    AdminSQLite admin = new AdminSQLite(getApplicationContext(),"votos",null,1);
                    SQLiteDatabase basededatos = admin.getWritableDatabase();
                    ContentValues valores = new ContentValues();
                    valores.put("estado",0);
                    basededatos.update("votos", valores, "estado=1", null);
                    //progress.dismiss();
                    Toast.makeText(getApplication(), response, Toast.LENGTH_SHORT).show();

                }
                progress.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),"DATOS NO ENVIADOS "+ error , Toast.LENGTH_SHORT).show();
                //progress.dismiss();
                System.out.println("DATOS NO ENVIADOS "+ error);
                datosVotos();
                progress.dismiss();


            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("votos",votosJSON);
                parametros.put("lider",usuarioNombre());
                return parametros;
            }
        };

        RequestQueue reques = Volley.newRequestQueue(this);
        reques.add(request);
    }


    public void enviar(View view){
        Gson datos =  new Gson();
        final String votosJSON = datos.toJson(Datos());
        System.out.println(votosJSON);
        progress = ProgressDialog.show(this, "Sincronizando",
                "Enviando datos...", true);
        StringRequest request = new StringRequest(Request.Method.POST, URL_POST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(usuario()){
                    progress.dismiss();
                    Toast.makeText(getApplication(), "Debes registrarte primero", Toast.LENGTH_SHORT).show();
                    Intent lis = new Intent(getApplicationContext(), RegistroUsuario.class);
                    startActivity(lis);
                }else {
                    System.out.println(response);
                    AdminSQLite admin = new AdminSQLite(getApplicationContext(),"votos",null,1);
                    SQLiteDatabase basededatos = admin.getWritableDatabase();
                    ContentValues valores = new ContentValues();
                    valores.put("estado",0);
                    basededatos.update("votos", valores, "estado=1", null);
                    progress.dismiss();
                    Toast.makeText(getApplication(), response, Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplication(),"DATOS NO ENVIADOS "+ error , Toast.LENGTH_SHORT).show();
                progress.dismiss();
                System.out.println("DATOS NO ENVIADOS "+ error);


            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("votos",votosJSON);
                parametros.put("lider",usuarioNombre());
                return parametros;
            }
        };

        RequestQueue reques = Volley.newRequestQueue(this);
        reques.add(request);
    }

    public ArrayList<Votante> Datos(){
        AdminSQLite admin = new AdminSQLite(this,"votos",null,1);
        SQLiteDatabase basededatos = admin.getWritableDatabase();
        Cursor fila = basededatos.rawQuery
                ("select * from votos where estado=1", null);
        ArrayList<Votante> votos = new ArrayList<>();
        if(fila.moveToFirst()){


            do {
               Votante voto = new Votante();
                voto.setCedula(fila.getString(0));
                voto.setNombre(fila.getString(1));
                voto.setDireccion(fila.getString(2));
                voto.setCelular(fila.getString(3));
                voto.setObservacion(fila.getString(4));


                votos.add(voto);

            } while(fila.moveToNext());


            basededatos.close();


        }else {
            Toast.makeText(this,"No existen datos para actualizar", Toast.LENGTH_SHORT).show();
            basededatos.close();
        }

        return votos;
    }

    public boolean usuario(){
        AdminSQLite admin = new AdminSQLite(this,"votos",null,1);
        SQLiteDatabase basededatos = admin.getWritableDatabase();
        boolean res;

        try{

            Cursor fila = basededatos.rawQuery
                    ("select nombre from usuario", null);

            if(fila.moveToFirst()){
                //Toast.makeText(this,"q", Toast.LENGTH_SHORT).show();
                basededatos.close();
                res = false;


            }else{
                res = true;
            }
        }catch (Exception e){
            res = true;
        }

        return res;

    }

    public String usuarioNombre(){
        AdminSQLite admin = new AdminSQLite(this,"votos",null,1);
        SQLiteDatabase basededatos = admin.getWritableDatabase();
        String nombre="";
        Cursor fila = basededatos.rawQuery
                ("select mail from usuario", null);
        if(fila.moveToFirst()){
            //nombre.setText(fila.getString(0));
            listado = (ListView) findViewById(R.id.lista);

            String nombres [] = new String[fila.getCount()];
            int i = 0;
            do {
                nombres[i]= fila.getString(0);
                i++;
            } while(fila.moveToNext());
            basededatos.close();

            nombre = nombres[0];
        }else {
            Toast.makeText(this,"No existen datos", Toast.LENGTH_SHORT).show();
            basededatos.close();
        }

        return nombre;
    }

    public void buscar(String cedula){
        AdminSQLite admin = new AdminSQLite(this,"votos",null,1);
        SQLiteDatabase basededatos = admin.getWritableDatabase();
        String ced = cedula;

        if(!ced.isEmpty()){
            Cursor fila = basededatos.rawQuery
                    ("select nombre, direccion, celular, observacion from votos where cedula = " + ced, null);
            if(fila.moveToFirst()){
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("cedula", cedula);
                intent.putExtra("nombre", fila.getString(0));
                intent.putExtra("direccion", fila.getString(1));
                intent.putExtra("celular", fila.getString(2));
                intent.putExtra("observacion", fila.getString(3));
                startActivity(intent);
                basededatos.close();


            }else {
                Toast.makeText(this,"No existe la cedula", Toast.LENGTH_SHORT).show();
                basededatos.close();
            }
        }else{
            Toast.makeText(this,"Debes ingresar la cedula", Toast.LENGTH_SHORT).show();

        }
    }


}
