package com.example.marciano.sms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListaSMS extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_sms);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        possuiPermissaoLerSMS();
    }

    private void listarSMS() {
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        List<String> listaSMS = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                listaSMS.add("De: " + cursor.getString(cursor.getColumnIndexOrThrow("address")) + " - Mensagem: " + cursor.getString(cursor.getColumnIndexOrThrow("body")));
            } while (cursor.moveToNext());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaSMS);
        ListView listView = findViewById(R.id.lvListaSMS);
        listView.setAdapter(adapter);
    }

    private void possuiPermissaoLerSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            return;
        }

        listarSMS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    listarSMS();
                else
                    Toast.makeText(getApplicationContext(), "Sem permissão para ler SMS.", Toast.LENGTH_LONG).show();
                return;
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
