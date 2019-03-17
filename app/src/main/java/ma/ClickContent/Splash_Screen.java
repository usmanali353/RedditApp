package ma.ClickContent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash_Screen extends AppCompatActivity {
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        image = (ImageView) findViewById(R.id.splash_screen_image);
        Animation splash_screen_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_screen_animation);
        image.setAnimation(splash_screen_animation);
        splash_screen_animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(isNetworkAvailable()) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    AlertDialog network_dialog=new AlertDialog.Builder(Splash_Screen.this)
                            .setTitle("ClickContent")
                            .setMessage("Your Device is Not Connected to Internet")
                            .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(
                                            Settings.ACTION_WIRELESS_SETTINGS          ));
                                    finish();
                                }
                            }).setNegativeButton("Close the App", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                    network_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary_dark));
                    network_dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary_dark));

                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
