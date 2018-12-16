package justbucket.videolib.screens;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import justbucket.videolib.R;
import justbucket.videolib.screens.main.presentation.view.MainActivityNew;

public class SplashScreen extends AppCompatActivity {

    private final static int DELAY = 1500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainActivityNew.start(SplashScreen.this);
                    }
                }, DELAY);

    }
}
