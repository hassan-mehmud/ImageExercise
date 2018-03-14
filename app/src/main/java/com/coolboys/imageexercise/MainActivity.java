package com.coolboys.imageexercise;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class MainActivity extends AppCompatActivity {

    private final int RC_PICTURE_TAKEN = 1111;
    private final int RC_PERMISSIONS = 2222;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // always check for permissions, otherwise weird things happen (or nothing happens)
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) !=  PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, RC_PERMISSIONS);
            //finish();
        }
    }

    public void onBtnClk(View view)
    {
        // when the user clicks the button… {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, RC_PICTURE_TAKEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    // this method gets called when you return from the camera application, with the picture included within
    // the data object
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICTURE_TAKEN && resultCode == RESULT_OK) {
            // the newly taken photo is now stored in a Bitmap object

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            FaceDetector detector = new FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                    .setProminentFaceOnly(true).build();

            // Copy and create the SafeFaceDetector class from the link on the next page
            Detector<Face> safeDetector = new SafeFaceDetector(detector);

            // Create a frame object from the bitmap and run face detection on the frame.
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Face> faces = safeDetector.detect(frame);
            // Number of faces detected (there should be only one with .setProminentFaceOnly(true)
            Log.d("", "faces detected: " + faces.size());
            // Get the first face in the faces array (you might have to add a check here that the array has any faces!)

            if(faces.size ()>0){
                Face face = faces.valueAt(0);
                //use the face object and its method to get the details, and launch a new activity if smile probability high enough …

                float smile = face.getIsSmilingProbability ();
                if (smile > 0.25){
                    Intent f = new  Intent (getApplicationContext (),Main2Activity.class);
                    startActivity (f);
                }
                Log.d ("Smile : ", Float.toString (smile));


            }
            else {

            }



            // release the objects for reuse
            detector.release();
            bitmap.recycle();
        }
        if (requestCode == RC_PERMISSIONS && resultCode == RESULT_OK) {
            // restart the activity if you arrive here from the permission dialog
            Intent reboot = new Intent(this, MainActivity.class);
            startActivity(reboot);
        }
    }
}


