package br.edu.ifpe.recife.tads.pratica12;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int MY_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_WRITE_EXTERNAL_STORAGE = 2;
    private boolean permissionRead;
    private boolean permissionWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    private void requestPermission() {
        this.permissionRead =
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED;
        this.permissionWrite =
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED;
        if (!this.permissionRead) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_READ_EXTERNAL_STORAGE);
        }
        if (!this.permissionWrite) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_WRITE_EXTERNAL_STORAGE);
        }
    }

    public void onClick(View view) {
        String url = ((EditText)findViewById(R.id.edit_url)).getText().toString();
        String filename = url.substring(url.lastIndexOf("/") + 1);
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra(DownloadService.URL_PATH, url);
        intent.putExtra(DownloadService.FILENAME, filename);
        startService(intent);
        Toast.makeText(this, "Download iniciado.", Toast.LENGTH_LONG).show();
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 ) // nothing granted
            return;
        switch (requestCode) {
            case MY_WRITE_EXTERNAL_STORAGE: {
                this.permissionWrite =
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                return;
            }
            case MY_READ_EXTERNAL_STORAGE: {
                this.permissionRead =
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                return;
            }
        }
    }




}