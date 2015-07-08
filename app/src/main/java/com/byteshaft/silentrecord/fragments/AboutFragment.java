package com.byteshaft.silentrecord.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.byteshaft.silentrecord.R;



public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_fragment, container, false);

        ImageView imageView0 = (ImageView) rootView.findViewById(R.id.websiteImageView);
        imageView0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://google.com"));
                startActivity(intent);
            }
        });

        ImageView imageView1 = (ImageView) rootView.findViewById(R.id.facebookImageView);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://facebook.com/byteshaft"));
                startActivity(intent);
//                openFacebookIfAppIsInstalledOtherWiseOpenInBrowser();
            }
        });

        ImageView imageView2 = (ImageView) rootView.findViewById(R.id.twitterImageView);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://twitter.com/byteshaft"));
                startActivity(intent);
            }
        });

        ImageView imageView3 = (ImageView) rootView.findViewById(R.id.linkedInImageView);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.linkedin.com/"));
                startActivity(intent);
            }
        });

        ImageView imageView4 = (ImageView) rootView.findViewById(R.id.emailImageView);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.fromParts("mailto", "byteshaft@gmail.com", null));
                startActivity(i);
            }
        });

        return rootView;
    }

    public Intent openFacebookIfAppIsInstalledOtherWiseOpenInBrowser() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/340927136101273"));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/byteshaft")));
        }
        return new Intent(Intent.ACTION_VIEW);
    }

}
