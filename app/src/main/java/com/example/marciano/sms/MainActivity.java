package com.example.marciano.sms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSAO_ENVIAR_SMS = 0;
    private static final int PERMISSAO_LER_STATUS_TELEFONE = 1;
    EditText txtNumero;
    EditText txtSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtNumero = findViewById(R.id.txtNumero);
        txtSms = findViewById(R.id.txtSMS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;
            }
            case R.id.itemHistorico: {
                Intent historico = new Intent(this, ListaSMS.class);
                startActivity(historico);
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBtnEnviarSMSClick(View view) {
        enviarSMS();
    }

    public void enviarSMS() {
        if (!possuiPermissaoEnvioSMS() || !possuiPermissaoLerStatus())
            return;

        String numeroDestino;
        String mensagemSMS;

        numeroDestino = txtNumero.getText().toString();
        mensagemSMS = txtSms.getText().toString();

        if (numeroDestino.isEmpty()) {
            Toast.makeText(this, "Número inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mensagemSMS.isEmpty()) {
            Toast.makeText(this, "Mensagem inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(numeroDestino, null, mensagemSMS, null, null);
        Toast.makeText(getApplicationContext(), "SMS enviado.", Toast.LENGTH_LONG).show();
        txtNumero.setText("");
        txtSms.setText("");
    }

    public boolean possuiPermissaoEnvioSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
            return true;

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSAO_ENVIAR_SMS);
        return false;
    }

    public boolean possuiPermissaoLerStatus() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return true;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
            return true;

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSAO_LER_STATUS_TELEFONE);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSAO_ENVIAR_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    enviarSMS();
                else
                    Toast.makeText(getApplicationContext(), "Sem permissão para enviar SMS.", Toast.LENGTH_LONG).show();
                return;
            }
            case PERMISSAO_LER_STATUS_TELEFONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    enviarSMS();
                else
                    Toast.makeText(getApplicationContext(), "Sem permissão para ler status do telefone.", Toast.LENGTH_LONG).show();
                return;
            }
        }

    }
}
