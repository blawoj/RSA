package com.example.ja.rsa;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity {
    private BigInteger p, q, n, d, e;
    private EditText editTextszyfr;
    private EditText editTextodszyfr;
    private String zaszyfrowane;
    private String odszyfrowane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ImageView img = (ImageView) findViewById(R.id.image);
        img.setImageResource(R.drawable.welcome);
    }

    public void przejdz_do_szyfruj(View V) {
        setContentView(R.layout.szyfrowanie_activity);
    }

    public void powrot(View V) {
        setContentView(R.layout.main_activity);
    }

    public void przejdz_do_odszyfruj(View V) {
        setContentView(R.layout.odszyfrowanie_activity);
    }

    public void wyjdz(View V) {
        finish();
        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Zamykanie aplikacji")
                .setMessage("Czy chcesz zamknąć aplikację??")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Nie", null)
                .show();
    }


    public void generuj(View V) throws IOException {
        int bit = 1024;
        SecureRandom rand = new SecureRandom();
        p = new BigInteger(bit / 2, 100, rand);
        q = new BigInteger(bit / 2, 100, rand);
        n = p.multiply(q);
        BigInteger m = (p.subtract(BigInteger.ONE));
        m = m.multiply(q.subtract(BigInteger.ONE));
        do {
            e = new BigInteger(2 * bit, new SecureRandom());
        } while ((e.compareTo(m) != 1) || (e.gcd(m).compareTo(BigInteger.valueOf(1)) != 0));
        d = e.modInverse(m);
        System.out.println(p + "\n" +
                q + "\n" +
                n + "\n" +
                e + "\n" +
                d + "\n");
        String tekst = ("p = " + p + "\n\n" +
                "q = " + q + "\n\n" +
                "n = " + n + "\n\n" +
                "e = " + e + "\n\n" +
                "d = " + d + "\n\n");
        zapis_do_pliku(tekst);
        Toast.makeText(this, "Wygenerowano nowe klucze,\n zapisno do pliku", Toast.LENGTH_SHORT).show();

    }


    public void szyfruj(View V) {
        editTextszyfr = (EditText) findViewById(R.id.szyfrowanie_editext);
        String str = editTextszyfr.getText().toString();
        try {
            zaszyfrowane = szyfrujtekst(str);
        } catch (NumberFormatException n) {
        }
        editTextszyfr.setText(zaszyfrowane);
    }


    public String szyfrujtekst(String message) {
        return (new BigInteger(message.getBytes())).modPow(e, n).toString();
    }


    public void odszyfruj(View V) {
        editTextodszyfr = (EditText) findViewById(R.id.odszyfrowanie_edittext);
        String str = editTextodszyfr.getText().toString();
        try {
            odszyfrowane = odszyfrujtekst(str);
        } catch (NumberFormatException n) {
        }
        System.out.println("Odszyfrowane ======" + odszyfrowane);
        editTextodszyfr.setText(odszyfrowane);
    }


    public String odszyfrujtekst(String message) {
        return new String((new BigInteger(message)).modPow(d, n).toByteArray());
    }


    public void kopiuj(View V) {
        editTextszyfr = (EditText) findViewById(R.id.szyfrowanie_editext);
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Skopiowany", editTextszyfr.getText().toString());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "Skopiowano do schowka", Toast.LENGTH_SHORT).show();

    }


    public void wklej(View V) {
        editTextodszyfr = (EditText) findViewById(R.id.odszyfrowanie_edittext);
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = clipboardManager.getPrimaryClip();
        ClipData.Item item = clipData.getItemAt(0);
        String tekst = item.getText().toString();
        editTextodszyfr.setText(tekst);
    }


    private void zapis_do_pliku(String data) throws IOException {
        Context context = this;
        File sciezka = context.getExternalFilesDir(null);
        File plik = new File(sciezka, "data.txt");
        FileOutputStream s = new FileOutputStream(plik);
        try {
            s.write(data.getBytes());
        } finally {
            s.close();
        }
    }


    public void importuj_publiczny(View V) {
        Context context = this;
        File sciezka = context.getExternalFilesDir(null);
        File plik = new File(sciezka, "public.txt");
        String odczytane = "";
        int[] intArray = new int[2];
        try {
            FileInputStream inputStream = new FileInputStream(plik);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                odczytane = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        String[] raw = odczytane.split("<>");
        n = new BigInteger(raw[0]);
        e = new BigInteger(raw[1]);
        System.out.println("Klucz publiczny (n,e) = (" + n + "," + e + ")");
        Toast.makeText(this, "Klucz zimportowany poprawnie", Toast.LENGTH_SHORT).show();

    }


    public void importuj_prywatny(View V) {
        Context context = this;
        File sciezka = context.getExternalFilesDir(null);
        File plik = new File(sciezka, "private.txt");
        String odczytane = "";
        int[] intArray = new int[2];
        try {
            FileInputStream inputStream = new FileInputStream(plik);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                odczytane = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] raw = odczytane.split("<>");
        n = new BigInteger(raw[0]);
        d = new BigInteger(raw[1]);
        System.out.println("Klucz prywatny (n,d) = (" + n + "," + d + ")");
        Toast.makeText(this, "Klucz zimportowany poprawnie", Toast.LENGTH_SHORT).show();

    }


}
