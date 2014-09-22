package com.jona.gridimagesearch.adaptors;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jona.gridimagesearch.R;
import com.jona.gridimagesearch.models.ImageResult;
import com.squareup.picasso.Picasso;

public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {

	public ImageResultsAdapter(Context context, List<ImageResult> images) {
		super(context, R.layout.item_image_result, images);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//find item
		ImageResult imageInfo = getItem(position);
		//inflate
		if(convertView == null){
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image_result, parent, false);
		}
		//find subviews of root layout
		ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
		TextView tvTitle =  (TextView) convertView.findViewById(R.id.tvTitle);
		
		//Clear image from last time
		ivImage.setImageResource(0);
		tvTitle.setText(Html.fromHtml(imageInfo.title));
		
		//Remotely download image data in the background
		Picasso.with(getContext()).load(imageInfo.thumbUrl).into(ivImage);
		
		//return view to be displayed
		return convertView;
	}
	
	

}
