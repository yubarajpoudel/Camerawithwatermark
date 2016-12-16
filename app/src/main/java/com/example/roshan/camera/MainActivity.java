package com.example.roshan.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    ImageButton btn_captureiamge;
    EditText editTextCaption;
    Button btnProcessing,save,btnLoadImage;
    TextView textSource1;
    ImageView imageResult;
    public static final int MEDIA_TYPE_IMAGE =1;
    final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    final int RQS_IMAGE1 = 1;
    int c;
    boolean fromCam = false;
    private static final String IMAGE_DIRECTORY_NAME = "WaterMark Camera";
    Uri source1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoadImage = (Button) findViewById(R.id.load_image);
        textSource1 = (TextView) findViewById(R.id.sourceuri1);
        save = (Button) findViewById(R.id.save);
        btn_captureiamge = (ImageButton) findViewById(R.id.captureimage);
        editTextCaption = (EditText) findViewById(R.id.caption);
        btnProcessing = (Button) findViewById(R.id.processing);
        imageResult = (ImageView) findViewById(R.id.imgPreview);


        btnLoadImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE1);
            }
        });

        btn_captureiamge.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                fromCam = true;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                source1 = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, source1);
                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            }
        });

        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                imageResult.setDrawingCacheEnabled(true);
                imageResult.buildDrawingCache();
                Bitmap bm = imageResult.getDrawingCache();

                OutputStream fOut = null;
                try {
                    File root = new File(Environment.getExternalStorageDirectory()
                            + File.separator + "Edit_Image" + File.separator);
                    root.mkdirs();
                    File sdImageMainDirectory = new File(root, File.separator + "image" + System.currentTimeMillis() + ".jpg");
                    source1 = Uri.fromFile(sdImageMainDirectory);
                    fOut = new FileOutputStream(sdImageMainDirectory);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error occured. Please try again later.",
                            Toast.LENGTH_SHORT).show();
                }
                try {
                    bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                }
                Toast.makeText(MainActivity.this, "Image is saved.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnProcessing.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (source1 != null) {

                    if(fromCam){
                        Bitmap processedBitmap = ProcessingBitmap();
                        if (processedBitmap != null) {
                            imageResult.setImageBitmap(processedBitmap);
                            Toast.makeText(getApplicationContext(),
                                    "Done",
                                    Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Select  image!",
                                    Toast.LENGTH_LONG).show();
                        }

                    }else {
                        Bitmap processedBitmap = ProcessingBitmap1();
                        if (processedBitmap != null) {
                            imageResult.setImageBitmap(processedBitmap);
                            Toast.makeText(getApplicationContext(),
                                    "Done",
                                    Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Select  image!",
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                }
                fromCam = false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case RQS_IMAGE1:
                    source1 = data.getData();
                    textSource1.setText(source1.toString());
                    try {

                        System.out.println("Bitmap path = "+source1.getPath());
                        final Bitmap bm1 = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(source1));

                        imageResult.setImageBitmap(bm1);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

                case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                    try {

                        // bimatp factory
                        BitmapFactory.Options options = new BitmapFactory.Options();

                        // downsizing image as it throws OutOfMemory Exception for larger
                        // images
                        options.inSampleSize = 8;
                        System.out.println("Bitmap path = "+source1.getPath());
                        final Bitmap bm1 = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(source1));

                        imageResult.setImageBitmap(bm1);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            }
        }

    private Bitmap ProcessingBitmap(){
        Bitmap bm1 = null;
        Bitmap newBitmap = null;

        try {
            bm1 = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(source1));

            Config config = bm1.getConfig();
            if(config == null){
                config = Bitmap.Config.ARGB_8888;
            }

            newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
            Canvas newCanvas = new Canvas(newBitmap);

            newCanvas.drawBitmap(bm1 ,0 ,0 , null);


            String captionString = editTextCaption.getText().toString();
            if(captionString != null){

                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setColor(Color.BLUE);
                paintText.setTextSize(250);
                paintText.setStyle(Style.FILL);
                paintText.setAlpha(50);

                Rect rectText = new Rect();
                paintText.getTextBounds(captionString, 0, captionString.length(), rectText);

                newCanvas.drawText(captionString,
                        (bm1.getWidth()/2), bm1.getHeight()/2, paintText);

                Toast.makeText(getApplicationContext(),
                        "drawText: " + captionString,
                        Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(getApplicationContext(),
                        "caption empty!",
                        Toast.LENGTH_LONG).show();
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return newBitmap;
    }

    private Bitmap ProcessingBitmap1(){
        Bitmap bm1 = null;
        Bitmap newBitmap = null;

        try {
            bm1 = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(source1));

            Config config = bm1.getConfig();
            if(config == null){
                config = Bitmap.Config.ARGB_8888;
            }

            newBitmap = Bitmap.createBitmap(bm1.getWidth(), bm1.getHeight(), config);
            Canvas newCanvas = new Canvas(newBitmap);

            newCanvas.drawBitmap(bm1,0,0, null);


            String captionString = editTextCaption.getText().toString();
            if(captionString != null){

                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setColor(Color.BLUE);
                paintText.setTextSize(60);
                paintText.setStyle(Style.FILL);
                paintText.setAlpha(50);

                Rect rectText = new Rect();
                paintText.getTextBounds(captionString, 0, captionString.length(), rectText);

                newCanvas.drawText(captionString,
                        200, bm1.getHeight()/2, paintText);

                Toast.makeText(getApplicationContext(),
                        "drawText: " + captionString,
                        Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(getApplicationContext(),
                        "caption empty!",
                        Toast.LENGTH_LONG).show();
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return newBitmap;
    }


    /* handler helper

   */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "Image_" + System.currentTimeMillis() + ".jpg");

        }else {
            return null;
        }

        return mediaFile;
    }
    @Override
    public void onBackPressed() {
        editTextCaption.setText("");
        this.finishAffinity();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", source1);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        source1 = savedInstanceState.getParcelable("file_uri");
    }




}