package com.jona.gridimagesearch.activities;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;

import com.jona.gridimagesearch.R;
import com.jona.gridimagesearch.adaptors.EndlessScrollListener;
import com.jona.gridimagesearch.adaptors.ImageResultsAdapter;
import com.jona.gridimagesearch.models.ImageResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends Activity {
	private EditText etQuery;
	private GridView gvResults;
	private ArrayList<ImageResult> imageResults;
	private ImageResultsAdapter aImageResults;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
		
		//Create resource for the list
		imageResults = new ArrayList<ImageResult>();
		//Attach data source to an adaptor
		aImageResults = new ImageResultsAdapter(this, imageResults);
		//Link adaptor to GridView
		gvResults.setAdapter(aImageResults);
		
		
		//Setup OnScrollListener
		gvResults.setOnScrollListener(new EndlessScrollListener() {
		    @Override
		    public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
//		        customLoadMoreDataFromApi(page); 
                customLoadMoreDataFromApi(totalItemsCount); 
		    }

    	});
	}

	private void setupViews(){
		etQuery = (EditText) findViewById(R.id.etQuery);
		gvResults = (GridView) findViewById(R.id.gvResults);		
		gvResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// launch image display activity activity
				Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
				
				//Get image result
				ImageResult result = imageResults.get(position);
				//Pass image result into intent
				i.putExtra("result", result);
				//launch activity
				startActivity(i);
				
			}
		});
	}	
	
	public void onImageSearch(View v){
		imageResults.clear();//clear the result (in case it is a new search)
		String query = etQuery.getText().toString();
		AsyncHttpClient client = new AsyncHttpClient();
		String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query + "&rsz=8";
		client.get(searchUrl, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
					JSONArray imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
					//When changing adapter update the underlying data automatically
					aImageResults.addAll(ImageResult.fromJsonArray(imageResultsJson));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Log.d("ERROR", responseString.toString());
			}
			
		});	
	}
	
	// Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
    	// This method probably sends out a network request and appends new data items to your adapter. 
    	//Get query string
    	String query = etQuery.getText().toString();
    	AsyncHttpClient client = new AsyncHttpClient();
    	String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query + "&rsz=8";
    	// Use the offset value and add it as a parameter to your API request to retrieve paginated data.
    	
    	//Construct search url including offset value
    	String searchUrlPaginated = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query + "&rsz=8&start=" + offset;
    	
    	client.get(searchUrlPaginated, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				Log.d("DEBUG", response.toString());
				try {
					JSONArray imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
					//When changing adapter update the underlying data automatically
					aImageResults.addAll(ImageResult.fromJsonArray(imageResultsJson));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.i("INFO", imageResults.toString());
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Log.d("ERROR", responseString.toString());
			}
    	});    	
    	
    	aImageResults.notifyDataSetChanged();

    }
    
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
