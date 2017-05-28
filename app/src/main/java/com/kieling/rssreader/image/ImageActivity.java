package com.kieling.rssreader.image;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.kieling.rssreader.R;
import com.kieling.rssreader.util.Utils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageActivity extends AppCompatActivity {
    @BindView(R.id.imageFeed)
    ImageView imageFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);

        String url = getIntent().getStringExtra(Utils.IMAGE_INTENT_KEY);
        Picasso.with(this).load(url).into(imageFeed);
    }
}
