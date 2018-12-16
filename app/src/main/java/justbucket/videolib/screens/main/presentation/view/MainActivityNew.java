package justbucket.videolib.screens.main.presentation.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import justbucket.videolib.R;
import justbucket.videolib.SecondActivity;

public class MainActivityNew extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public static void start(Activity activity){
        activity.startActivity(new Intent(activity, MainActivityNew.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_new);
        findViewById(R.id.photoContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String s = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }else{

        }
    }

    private void startSecondActivity(@NonNull List<String> bagTags) {
        List<String> tags = new ArrayList<>();
        for (String badTag: bagTags) {
            tags.add(badTag.split(",")[0]);
        }
        Intent intent = SecondActivity.newIntent(this, tags);
        startActivity(intent);
    }
}
