package com.xedrux.cclouds.views.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.utils.Utils;

public class About extends ActionBarActivity {

	private TextView email_cclouds;
	private ImageView googlePlus,facebook,twitter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
        Initialization();
        Events();

		TextView lblVersion = (TextView) findViewById(R.id.menu_item_sobre_lbl_version);

		try {
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			lblVersion.setText(versionName);
		} catch (Exception e) {
			Log.e("cclouds", "MenuItemSobre.version: "+e);
		}
	}

	private void Initialization()
	{
		email_cclouds = (TextView)findViewById(R.id.email_cclouds);
		googlePlus = (ImageView)findViewById(R.id.google_plus);
		facebook = (ImageView)findViewById(R.id.facebook);
		twitter = (ImageView)findViewById(R.id.twitter);
	}

	private void Events()
	{
		email_cclouds.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent contact_us = Utils.sendEmail(new String[]{getResources().getString(R.string.cclouds_email)},"");
				startActivity(Intent.createChooser(contact_us, getResources().getString(R.string.send_email_using)));
			}
		});

		googlePlus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent googlePlusIntent = Utils.linkInBrowser("http://plus.google.com/");
				startActivity(googlePlusIntent);
			}
		});

		facebook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent facebookIntent = Utils.linkInBrowser("https://m.facebook.com/");
				startActivity(facebookIntent);
			}
		});

		twitter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent twitterIntent = Utils.linkInBrowser("https://twitter.com/");
				startActivity(twitterIntent);
			}
		});
	}
}
