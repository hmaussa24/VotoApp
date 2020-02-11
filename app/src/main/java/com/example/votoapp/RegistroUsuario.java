package com.example.votoapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegistroUsuario extends AppCompatActivity {


    private EditText nombreuser,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        nombreuser = (EditText)findViewById(R.id.nombreuser);
        email = (EditText)findViewById(R.id.email);
    }


    public void regitrar(View view){
        AdminSQLite admin = new AdminSQLite(this,"votos",null,1);
        SQLiteDatabase basededatos = admin.getWritableDatabase();

        String nom = nombreuser.getText().toString();
        String mail = email.getText().toString();
        if(!nom.isEmpty() && !mail.isEmpty()){
            ContentValues registro = new ContentValues();
            registro.put("nombre", nom);
            registro.put("mail", mail);

            basededatos.insert("usuario",null, registro);
            basededatos.close();
            Toast.makeText(this,"Registro realizado", Toast.LENGTH_SHORT).show();
            Intent lis = new Intent(this, MainActivity.class);
            startActivity(lis);
        }else{
            Toast.makeText(this,"Debes llenar los campos", Toast.LENGTH_SHORT).show();
        }
    }
}
