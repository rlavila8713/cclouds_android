package com.xedrux.cclouds.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.xedrux.cclouds.R;
import com.xedrux.cclouds.models.Language;

public class LanguageAdapter extends BaseAdapter{

	Context context;
	public ArrayList<Language> languages;

	public LanguageAdapter(Context ctx){
		context = ctx;	
		languages = listAll();
	}

	class ViewHolder {
		TextView language;
		TextView languageEnglish;
		ImageView flagImage;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_language, parent, false);
			holder = new ViewHolder();
			holder.language = (TextView) convertView.findViewById(R.id.textViewlanguage);
			holder.languageEnglish = (TextView) convertView.findViewById(R.id.textViewlanguageEnglish);
			holder.flagImage = (ImageView)convertView.findViewById(R.id.imageView1);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		Language lan = languages.get(position);

		holder.language.setText(lan.name);
		holder.languageEnglish.setText(lan.nameEnglish);	
		try {
			holder.flagImage.setImageBitmap(Bitmap.createBitmap(BitmapFactory.decodeStream(context.getAssets().open("flags/flag_" + lan.flag + ".png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return convertView;
	}

	@Override
	public int getCount() {		
		if(languages == null)
			return 0;
		return languages.size();
	}

	@Override
	public Object getItem(int position) {		
		return null;
	}

	@Override
	public long getItemId(int position) {		
		return 0;
	}	


	public ArrayList<Language> listAll(){
		ArrayList<Language> localArrayList = new ArrayList<Language>();
		try
		{
			int resourceId = context.getResources().getIdentifier("languages", "raw", context.getPackageName());
			InputStream localInputStream = context.getResources().openRawResource(resourceId);

			byte[] arrayOfByte = new byte[localInputStream.available()];
			localInputStream.read(arrayOfByte);
			String[] arrayOfString1 = new String(arrayOfByte, "UTF-8").split("\n");
			/*Language localLanguage1 = new Language();
			localLanguage1.code = null;
			localLanguage1.flag = null;
			localLanguage1.name = null;
			localLanguage1.nameEnglish = null;*/

			//localArrayList.add(localLanguage1);
			for (int i = 0; i < arrayOfString1.length; i++)
			{

				String[] arrayOfString2 = arrayOfString1[i].split(";");
				if (arrayOfString2.length == 4)
				{
					Language localLanguage2 = new Language();
					localLanguage2.code = arrayOfString2[0];
					localLanguage2.flag = arrayOfString2[1];
					localLanguage2.name = arrayOfString2[2];
					localLanguage2.nameEnglish = arrayOfString2[3];
					localArrayList.add(localLanguage2);
				}

			}
		}
		catch (Exception localException){	 
			localException.printStackTrace();
		}		
		return localArrayList;
	}	
}
