package com.yanir.supersmartalpha;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yanir.supersmartalpha.ml.Model;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;


public class TeachableMachine extends AppCompatActivity {

    TextView result, confidence;
    ImageView imageView;
    Button picture;
    int imageSize = 224;
    Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachable_machine);
        in = this.getIntent();

        result = findViewById(R.id.result);
        confidence = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imvPhoto);
        picture = findViewById(R.id.button);

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);

                clasifyImage(imageBitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void clasifyImage(Bitmap image) {
        // Resize the image to the model's input size
        Bitmap resizedImage = Bitmap.createScaledBitmap(image, imageSize, imageSize, true);

        // Run the model
        try {
            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 * 224 * 224 * 3);
            resizedImage.copyPixelsToBuffer(byteBuffer);
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Display the result
            result.setText(outputFeature0.getFloatArray()[0] > 0.5 ? "iphone" : "airpods");

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception

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