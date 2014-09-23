package com.jona.gridimagesearch.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import com.jona.gridimagesearch.R;
import com.jona.gridimagesearch.models.ImageResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageDisplayActivity extends Activity {
	private ShareActionProvider mShareActionProvider;
	ImageView ivImageResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		//Get the result from the intent
		ImageResult result = (ImageResult) getIntent().getSerializableExtra("result");
		//Find imageView 
		ivImageResult = (ImageView) findViewById(R.id.ivImageResult);
		//Load image url in imageView
		Picasso.with(this).load(result.fullUrl).into(ivImageResult);
	}
	
	// Returns the URI path to the Bitmap displayed in specified ImageView
	public Uri getLocalBitmapUri(ImageView imageView) {
	    // Extract Bitmap from ImageView drawable
	    Drawable drawable = imageView.getDrawable();
	    Bitmap bmp = null;
	    if (drawable instanceof BitmapDrawable){
	       bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
	    } else {
	       return null;
	    }
	    // Store image to default external storage directory
	    Uri bmpUri = null;
	    try {
	        File file =  new File(Environment.getExternalStoragePublicDirectory(  
		        Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
	        file.getParentFile().mkdirs();
	        FileOutputStream out = new FileOutputStream(file);
	        bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
	        out.close();
	        bmpUri = Uri.fromFile(file);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return bmpUri;
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_display, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_item_share) {
			// Get access to bitmap image from view
		    ImageView ivImage = (ImageView) findViewById(R.id.ivImageResult);
		    // Get access to the URI for the bitmap
		    Uri bmpUri = getLocalBitmapUri(ivImage);
		    if (bmpUri != null) {
		        // Construct a ShareIntent with link to image
		        Intent shareIntent = new Intent();
		        shareIntent.setAction(Intent.ACTION_SEND);
		        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
		        shareIntent.setType("image/*");
		        // Launch sharing dialog for image
		        startActivity(Intent.createChooser(shareIntent, "Share Image"));	
		    } else {
		        // ...sharing failed, handle error
		    }
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
