package com.yanir.supersmartalpha;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class csv_imp extends AppCompatActivity {

    private static final int PICK_CSV_FILE = 1;
    private static final String TAG = "ImportCsvActivity";
    private TableLayout csvTable;

    Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv);
        in = getIntent();
        Log.d(TAG, "onCreate: ");

        Button selectCsvButton = findViewById(R.id.selectCsvButton);
        csvTable = findViewById(R.id.csvTable);

        selectCsvButton.setOnClickListener(v -> selectCsvFile());
    }

    private void selectCsvFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow any file type
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select CSV File"), PICK_CSV_FILE);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_CSV_FILE && resultCode == RESULT_OK && data != null) {
            Uri csvFileUri = data.getData();
            if (csvFileUri != null) {
                String fileName = getFileName(csvFileUri);
                if (fileName != null && fileName.endsWith(".csv")) {
                    Log.d(TAG, "CSV file selected: " + csvFileUri);
                    parseAndDisplayCsv(csvFileUri);
                } else {
                    Toast.makeText(this, "Please select a valid CSV file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "File selection failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseAndDisplayCsv(Uri csvFileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(csvFileUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            csvTable.removeAllViews(); // Clear previous data

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                addRowToTable(columns);
            }

            reader.close();
            inputStream.close();
            Toast.makeText(this, "CSV file loaded", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error reading CSV file", e);
            Toast.makeText(this, "Failed to read CSV file", Toast.LENGTH_SHORT).show();
        }
    }

    private void addRowToTable(String[] columns) {
        TableRow tableRow = new TableRow(this);

        for (String column : columns) {
            TextView cell = new TextView(this);
            cell.setText(column.trim());
            cell.setPadding(8, 8, 8, 8);
            tableRow.addView(cell);
        }

        csvTable.addView(tableRow);
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