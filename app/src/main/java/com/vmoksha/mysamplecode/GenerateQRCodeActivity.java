package com.vmoksha.mysamplecode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class GenerateQRCodeActivity extends Activity implements OnClickListener {

    private String LOG_TAG = "GenerateQRCode";
    private int smallerDimension;
    private Bitmap bitmap;
    private boolean flagABoolean = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        // Find screen size
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button1:
                EditText qrInput = (EditText) findViewById(R.id.qrInput);
                String qrInputText = qrInput.getText().toString();
                Log.v(LOG_TAG, qrInputText);

                // Encode with a QR Code image
                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText, null,
                        Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                        smallerDimension);
                try {
                    bitmap = qrCodeEncoder.encodeAsBitmap();
                    ImageView myImage = (ImageView) findViewById(R.id.imageView1);
                    myImage.setImageBitmap(bitmap);
                    flagABoolean = true;

                } catch (WriterException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.button2:
//from api
                HTTP_GET httpGetSeed = new HTTP_GET() {
                    @Override
                    protected void onPostExecute(String result) {
                        if (result != null && result.length() > 0) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("ViewModels");
                                StringBuffer sbr = new StringBuffer();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jObject = jsonArray.getJSONObject(i);
                                    sbr.append(jObject.getString("Code")+"#");
                                }
                                // String strQrInput = jsonArray.getJSONObject(5).getString("Name") + " " + jsonArray.getJSONObject(5).getString("Value");
                                String strQrInput = sbr.toString();
                                // Encode with a QR Code image
                                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(strQrInput, null,
                                        Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                                        smallerDimension);
                                try {
                                    bitmap = qrCodeEncoder.encodeAsBitmap();
                                    ImageView myImage = (ImageView) findViewById(R.id.imageView1);
                                    myImage.setImageBitmap(bitmap);
                                    flagABoolean = true;
                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                Log.i("mylog", e.getMessage());
                            }
                        }
                    }
                };
                // httpGetSeed.execute("http://125.21.227.181:8065/api/seed");
                httpGetSeed.execute("http://125.21.227.181:8065/api/location");
                break;
            case R.id.button3:
                //save image
                SaveImage(bitmap);
                break;

            // More buttons go here (if any) ...
        }
    }

    private void SaveImage(Bitmap finalBitmap) {
        if (flagABoolean) {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/QRCode");
            if (!myDir.exists())
                myDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image-" + n + ".jpg";
            File file = new File(myDir, fname);
            if (file.exists()) file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                flagABoolean = false;
                Toast.makeText(getApplicationContext(), "image saved to " + file.toString(), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "image already saved", Toast.LENGTH_SHORT).show();
        }

    }
}
