package uk.ac.cam.cl.foxtrot.ecolocations;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullscreenImageActivity extends AppCompatActivity {
    private static final String ARG_PHOTO = "photo";

    public static Intent getIntent(Context context, String photo) {
        Intent intent = new Intent(context, FullscreenImageActivity.class);
        intent.putExtra(ARG_PHOTO, photo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        ImageView fullscreenView = (ImageView) findViewById(R.id.fullscreen_view);
        String photo = getIntent().getStringExtra(ARG_PHOTO);

        Picasso.with(this)
                .load(photo)
                .into(fullscreenView);
    }
}
