package com.yanir.supersmartalpha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

import java.util.UUID;

public class uplodeFromGallery extends AppCompatActivity {
    ImageView imageView;
    Button btnUplode;
    private Uri filePath;
    private FirebaseStorage storage;
    Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uplode_from_gallery);

        imageView = findViewById(R.id.imvPhoto);
        btnUplode = findViewById(R.id.btnUplode);

        //firebase storage
        storage = FirebaseStorage.getInstance();
        in = getIntent();

        btnUplode.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, 100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            filePath = data.getData();
            imageView.setImageURI(filePath);
            uplodeImage();
        }
    }

    private void uplodeImage() {
        // uplode image to firebase
        if (filePath != null) {
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
                    Toast.makeText(uplodeFromGallery.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(uplodeFromGallery.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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