package com.byteshaft.silentrecord.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.byteshaft.silentrecord.AppGlobals;
import com.byteshaft.silentrecord.R;

import java.util.List;


public class AboutFragment extends Fragment {

    Intent intent;
    String faceBookID = "340927136101273";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_fragment, container, false);
        AppGlobals.setIsMainActivityShown(false);

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
                openFacebookIfAppIsInstalledOtherWiseOpenInBrowser();
            }
        });

        ImageView imageView2 = (ImageView) rootView.findViewById(R.id.twitterImageView);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTwitterIfAppIsInstalledOtherWiseOpenInBrowser();
            }
        });

        ImageView imageView3 = (ImageView) rootView.findViewById(R.id.linkedInImageView);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkedInIfAppIsInstalledOtherWiseOpenInBrowser();
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

    public void openLinkedInIfAppIsInstalledOtherWiseOpenInBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://167965688"));
        final PackageManager packageManager = AppGlobals.getContext().getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=167965688"));
        }
        startActivity(intent);
    }

    public void openFacebookIfAppIsInstalledOtherWiseOpenInBrowser() {
        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + faceBookID));
            startActivity(intent);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + faceBookID));
            startActivity(intent);
        }
    }

    public void openTwitterIfAppIsInstalledOtherWiseOpenInBrowser() {
        Intent twitterIntent;
        try {
            // get the Twitter app if possible
            AppGlobals.getContext().getPackageManager().getPackageInfo("com.twitter.android", 0);
            twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=3184467546"));
            twitterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/byteshaft"));
        }
        startActivity(twitterIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppGlobals.setIsUnlocked(true);
    }
}
