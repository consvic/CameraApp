package com.example.cocoy.cameraapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button bTomar, bSeleccionar;
    private ImageView ivFoto;
    private TextView tvUrl;

    //TODO 1: Añadir permisos al manifesto

    //TODO 2: DEfinier constantes de permisos
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    private final int RESULT_LOAD_IMAGE = 100;

    String mCurrentPhotoPath;
    boolean camera,gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bTomar = (Button) findViewById(R.id.bTomar);
        bSeleccionar = (Button) findViewById(R.id.bSeleccionar);
        ivFoto = (ImageView) findViewById(R.id.imageView);
        tvUrl = (TextView) findViewById(R.id.textView);

        bSeleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarPermisosStorage();
            }
        });

        bTomar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarPermisosCamara();
            }
        });
    }

    public void validarPermisosStorage() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //debemos mostrar un mensaje
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //mostramos una explicacind eque no acepto dar permiso para acceder a la libreria

            } else  {
                //pedimos permiso
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE);

            }
        } else {
            Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }

    public void validarPermisosCamara() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //debemos mostrar un mensaje
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                //mostramos una explicacind eque no acepto dar permiso para acceder a la libreria

            } else  {
                //pedimos permiso
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_IMAGE_CAPTURE);


            }
        } else {
            dispatchTakePictureIntent();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case REQUEST_IMAGE_CAPTURE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Prueba","entro");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    dispatchTakePictureIntent();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.cocoy.cameraapp",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == RESULT_LOAD_IMAGE) {
            Uri pat = data.getData();
            //String path;
            ivFoto.setImageURI(pat);
            tvUrl.setText(pat.toString());
            //path=pat.toString();
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Picasso.with(this).load(tvUrl.getText().toString()).into(ivFoto);

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file://"+image.getAbsolutePath();
        tvUrl.setText(mCurrentPhotoPath);
        return image;
    }



}
