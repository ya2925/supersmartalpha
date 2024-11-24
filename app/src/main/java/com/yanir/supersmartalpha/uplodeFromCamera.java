package com.yanir.supersmartalpha;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class uplodeFromCamera extends AppCompatActivity {

    ImageView imageView;
    Button btnUplode;
    private Uri filePath;
    private FirebaseStorage storage;
    Intent in;
    String TAG = "uplodeFromCameraActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uplode_from_camera);
        in = this.getIntent();
        btnUplode = findViewById(R.id.btnPhoto);
        imageView = findViewById(R.id.imvPhoto2);
        storage = FirebaseStorage.getInstance();
        Log.d(TAG, "onCreate: ");

        btnUplode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Get the image from the camera
            Log.d(TAG, "onActivityResult: ");
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);

                // Save the Bitmap to a file and get the URI
                try {
                    File file = new File(getExternalFilesDir(null), UUID.randomUUID().toString() + ".jpg");
                    FileOutputStream out = new FileOutputStream(file);
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                    filePath = Uri.fromFile(file);
                    Log.d(TAG, "onActivityResult: filePath = " + filePath);
                    uplodeImage();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onActivityResult: Error saving image", e);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uplodeImage() {
        Log.d(TAG, "uplodeImage: ");
        // uplode image to firebase
        if (filePath != null) {
            Log.d(TAG, "uplodeImage: filePath != null");
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storage.getReference().child("images/" + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            // Dismiss dialog
                            progressDialog.dismiss();
                            Toast.makeText(uplodeFromCamera.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(uplodeFromCamera.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }


    /**
     * This function presents the options menu for moving between activities.
     *
     * @param menu The options menu in which you place your items.
     * @return true in order to show the menu, otherwise false.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().toString().equals("barcode")) {
            in.setClass(this, MainActivity.class);
            startActivity(in);
        } else if (item.getTitle().toString().equals("sign up")) {
            in.setClass(this, signup.class);
            startActivity(in);
        } else if (item.getTitle().toString().equals("csv")) {
            in.setClass(this, csv_imp.class);
            startActivity(in);
        } else if (item.getTitle().toString().equals("machine learning")) {
            in.setClass(this, TeachableMachine.class);
            startActivity(in);
        } else if (item.getTitle().toString().equals("Gallery")) {
            in.setClass(this, uplodeFromGallery.class);
            startActivity(in);
        } else if (item.getTitle().toString().equals("Camera")) {
            in.setClass(this, uplodeFromCamera.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }
}