package com.example.votoapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText cedula, nombre, direccion, celular, observacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        if(usuario()){
            Intent lis = new Intent(this, RegistroUsuario.class);
            startActivity(lis);
            //this.isDestroyed();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);


        cedula = (EditText)findViewById(R.id.cedula);
        nombre = (EditText)findViewById(R.id.nombre);
        direccion = (EditText)findViewById(R.id.direccion);
        celular = (EditText)findViewById(R.id.celular);
        observacion = (EditText)findViewById(R.id.observacion);

        String strDate = getIntent().getStringExtra("cedula");
        if (strDate == null) {

        } else {
            Bundle datos = this.getIntent().getExtras();
            String ced = datos.getString("cedula");
            String nom = datos.getString("nombre");
            String dir = datos.getString("direccion");
            String cel = datos.getString("celular");
            String obs = datos.getString("observacion");
            cedula.setText(ced);
            nombre.setText(nom);
            direccion.setText(dir);
            celular.setText(cel);
            observacion.setText(obs);
            cedula.setEnabled(false);
            nombre.setEnabled(false);
            direccion.setEnabled(false);
            celular.setEnabled(false);
            observacion.setEnabled(false);
        }



;
    }


    public void regitrar(View view){
        AdminSQLite admin = new AdminSQLite(this,"votos",null,1);
        SQLiteDatabase basededatos = admin.getWritableDatabase();

        String ced = cedula.getText().toString();
        String nom = nombre.getText().toString();
        String dir = direccion.getText().toString();
        String cel = celular.getText().toString();
        String obser = observacion.getText().toString();
        String[] args = new String[] {ced};
        Cursor c = basededatos.rawQuery(" SELECT cedula FROM votos WHERE cedula=?", args);
        if(c.getCount()>0) {
            Toast.makeText(this, "La cedula ya esta registrada. Porfavor verifiquela.", Toast.LENGTH_SHORT).show();
        }else {
            if (!ced.isEmpty() && !nom.isEmpty() && !dir.isEmpty()) {
                ContentValues registro = new ContentValues();
                registro.put("cedula", ced);
                registro.put("nombre", nom);
                registro.put("direccion", dir);
                registro.put("celular", cel);
                registro.put("observacion", obser);
                registro.put("estado", 1);

                basededatos.insert("votos", null, registro);
                basededatos.close();
                limpiar(view);
                Toast.makeText(this, "Registro realizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Debes llenar los campos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void limpiar(View view){
        cedula.setText("");
        nombre.setText("");
        direccion.setText("");
        celular.setText("");
        observacion.setText("");
        cedula.setEnabled(true);
        nombre.setEnabled(true);
        direccion.setEnabled(true);
        celular.setEnabled(true);
        observacion.setEnabled(true);
    }

    public void buscar(View view){
        AdminSQLite admin = new AdminSQLite(this,"votos",null,1);
        SQLiteDatabase basededatos = admin.getWritableDatabase();
        String ced = cedula.getText().toString();

        if(!ced.isEmpty()){
            Cursor fila = basededatos.rawQuery
                    ("select nombre, direccion, celular, observacion from votos where cedula = " + ced, null);
            if(fila.moveToFirst()){
                nombre.setText(fila.getString(0));
                direccion.setText(fila.getString(1));
                celular.setText(fila.getString(2));
                observacion.setText(fila.getString(3));
                basededatos.close();


            }else {
                Toast.makeText(this,"No existe la cedula", Toast.LENGTH_SHORT).show();
                basededatos.close();
            }
        }else{
            Toast.makeText(this,"Debes ingresar la cedula", Toast.LENGTH_SHORT).show();

        }
    }

    public void listado(View view){
        Intent lis = new Intent(this, listado.class);
        startActivity(lis);
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
}
