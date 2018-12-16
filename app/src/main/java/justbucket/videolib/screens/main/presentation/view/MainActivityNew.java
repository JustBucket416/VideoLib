package justbucket.videolib.screens.main.presentation.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.android.AndroidInjection;
import justbucket.videolib.R;
import justbucket.videolib.SecondActivity;
import justbucket.videolib.domain.exception.Failure;
import justbucket.videolib.domain.feature.ddsearch.SearchByImage;
import justbucket.videolib.domain.functional.Either;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivityNew extends AppCompatActivity {

    @Inject
    SearchByImage mSearchByImage;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int READ_REQUEST_CODE = 42;
    private View progressBarContainer;

    public static void start(Activity activity){
        activity.startActivity(new Intent(activity, MainActivityNew.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_new);
        AndroidInjection.inject(this);
        findViewById(R.id.photoContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        findViewById(R.id.uploadFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
        progressBarContainer = findViewById(R.id.progressBarContainer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        progressBarContainer.setVisibility(View.VISIBLE);
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                requestTags(byteArray);
                return;
            }
            if (requestCode == READ_REQUEST_CODE){
                if (data != null) {
                    try {
                        Uri imageUri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream .toByteArray();
                        requestTags(byteArray);
                    }catch (Exception e){
                        showToast("Error");
                    }
                }
            }
        }
        progressBarContainer.setVisibility(View.GONE);
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startSecondActivity(@NonNull List<String> bagTags) {
        List<String> tags = new ArrayList<>();
        for (String badTag : bagTags) {
            tags.add(badTag.split(",")[0]);
        }
        Intent intent = SecondActivity.newIntent(this, tags);
        startActivity(intent);
    }

    private void requestTags(byte[] byteArray) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), byteArray);
        MultipartBody.Part.createFormData("confidentialPicture", "confidentialPicture.jpg", requestBody);
        mSearchByImage.execute(new Function1<Either<? extends Failure, ? extends ArrayList<String>>, Unit>() {
            @Override
            public Unit invoke(Either<? extends Failure, ? extends ArrayList<String>> either) {
                either.either(new Function1<Failure, Object>() {
                    @Override
                    public Object invoke(Failure failure) {
                        progressBarContainer.setVisibility(View.GONE);
                        return null;
                    }
                }, new Function1<ArrayList<String>, Object>() {
                    @Override
                    public Object invoke(ArrayList<String> strings) {
                        progressBarContainer.setVisibility(View.GONE);
                        startSecondActivity(strings);
                        return null;
                    }
                });
                return Unit.INSTANCE;
            }
        }, SearchByImage.Params.createParams(requestBody));
    }

}
