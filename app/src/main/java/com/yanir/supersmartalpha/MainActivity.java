package com.yanir.supersmartalpha;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BarcodeScanner";
    private PreviewView previewView;
    private TextView resultTextView;
    private ExecutorService cameraExecutor;
    Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Activity started");

        previewView = findViewById(R.id.previewView);
        resultTextView = findViewById(R.id.resultTextView);
        Button startScanButton = findViewById(R.id.startScanButton);

        // Initialize a single-thread executor for camera operations
        cameraExecutor = Executors.newSingleThreadExecutor();

        startScanButton.setOnClickListener(v -> {
            Log.d(TAG, "Start Scan button clicked");
            startCamera();
        });

        in = getIntent();
    }

    private void startCamera() {
        Log.d(TAG, "Initializing Camera...");
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Log.d(TAG, "CameraProvider obtained");

                // Set up the camera preview use case
                Preview preview = new Preview.Builder().build();

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Set up the camera selector
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Set up image analysis for barcode scanning
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                // Bind all use cases to the lifecycle
                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );

                Log.d(TAG, "Camera is now running");

            } catch (Exception e) {
                Log.e(TAG, "Failed to bind use cases", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void analyzeImage(@NonNull ImageProxy imageProxy) {
        try {
            @SuppressWarnings("UnsafeOptInUsageError")
            InputImage inputImage = InputImage.fromMediaImage(
                    imageProxy.getImage(),
                    imageProxy.getImageInfo().getRotationDegrees()
            );

            BarcodeScanner scanner = BarcodeScanning.getClient();
            scanner.process(inputImage)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            Log.d(TAG, "Barcode detected: " + rawValue);
                            resultTextView.setText(rawValue);
                            break; // Stop after processing the first barcode
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Barcode scanning failed", e))
                    .addOnCompleteListener(task -> {
                        imageProxy.close(); // Important to close the image
                        Log.d(TAG, "Image processing complete");
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error during image analysis", e);
            imageProxy.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        Log.d(TAG, "Camera executor shut down");
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