package com.jona.gridimagesearch.activities;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;

import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.jona.gridimagesearch.R;
import com.jona.gridimagesearch.adapters.ImageResultsAdapter;
import com.jona.gridimagesearch.helper.EndlessScrollListener;
import com.jona.gridimagesearch.models.FilterSelection;
import com.jona.gridimagesearch.models.ImageResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchActivity extends SherlockFragmentActivity{
	private EditText etQuery;
	private GridView gvResults;
	private ArrayList<ImageResult> imageResults;
	private ImageResultsAdapter aImageResults;
	private SearchView searchView;
	private FilterSelection filterOptions;
	private final int REQUEST_CODE = 80;
	private AsyncHttpClient client = new AsyncHttpClient();
	private String searchQuery = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		filterOptions = new FilterSelection();
		filterOptions.color = "";
		filterOptions.searchSite = "";
		filterOptions.type = "";
		filterOptions.size = "";
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
		        // customLoadMoreDataFromApi(page); 
                
		    	customLoadMoreDataFromApi(totalItemsCount); 
		    }
    	});
	}

	private void setupViews(){
//		etQuery = (EditText) findViewById(R.id.etQuery);
		gvResults = (GridView) findViewById(R.id.gvResults);		
		gvResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// launch image display activity activity
				Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
				if(isIntentAvailable(SearchActivity.this, i)){
					//Get image result
					ImageResult result = imageResults.get(position);
					//Pass image result into intent
					i.putExtra("result", result);
					//launch activity
					startActivity(i);
				}
			}
		});
	}	
	
	public void onImageSearch(View v){
		imageResults.clear();//clear the result (in case it is a new search)
		//String query = etQuery.getText().toString();
		String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + searchQuery + "&rsz=8";
		searchUrl = constructSearchUrl(searchUrl);
		
		if(searchQuery != null) {
			if(isConnectivityAvailable(this)){
				client.get(searchUrl, new JsonHttpResponseHandler(){
					@Override
					public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
						try {
							JSONArray imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
							//When changing adapter update the underlying data automatically
							aImageResults.addAll(ImageResult.fromJsonArray(imageResultsJson));
						} catch (JSONException e) {
							//show error message
							e.printStackTrace();
						}
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
						Log.d("ERROR", responseString.toString());
					}
				});	
			} else {
				//give message that there is no connectivity
			}
		} else {
			//give message nothing to search for
		}
	}
	
	// Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
    	// This method probably sends out a network request and appends new data items to your adapter. 
    	//Get query string
//    	String query = etQuery.getText().toString();
    	
    	// Use the offset value and add it as a parameter to your API request to retrieve paginated data.
    	//Construct search url including offset value
    	String searchUrlPaginated = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + searchQuery + "&rsz=8&start=" + offset;
    	searchUrlPaginated = constructSearchUrl(searchUrlPaginated);
    	
    	if(searchQuery != null) {
	    	if(isConnectivityAvailable(this)){
	
		    	client.get(searchUrlPaginated, new JsonHttpResponseHandler(){
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
			} else {
				//show error message for no connectivity
			}
    	} else {
    		//message nothing to search for
    	}
    	
    	aImageResults.notifyDataSetChanged();
    }
    
    public String constructSearchUrl(String baseSearchUrl){
    	if(filterOptions.color != null && !filterOptions.color.trim().isEmpty()) {
    		baseSearchUrl += "&imgcolor=" + filterOptions.color.toLowerCase();
    	}
    	
    	if(filterOptions.size != null && !filterOptions.size.trim().isEmpty()) {
    		baseSearchUrl += "&imgsize=" + filterOptions.size.toLowerCase();
    	}
    	
    	if(filterOptions.type != null && !filterOptions.type.trim().isEmpty()) {
    		baseSearchUrl += "&imgtype=" + filterOptions.type.toLowerCase();
    	}
    	
    	if(filterOptions.searchSite != null && !filterOptions.searchSite.trim().isEmpty()) {
    		baseSearchUrl += "&as_sitesearch=" + filterOptions.searchSite.toLowerCase();
    	}
    	return baseSearchUrl;
    	
    }
    
    public void saveFilterSelection(MenuItem mi) {
		Intent i = new Intent(SearchActivity.this, FilterActivity.class);
		i.putExtra("filterOptions", filterOptions);
		//Execute intent
		startActivityForResult(i, REQUEST_CODE);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == REQUEST_CODE){
            if (resultCode == RESULT_OK){
                filterOptions = (FilterSelection) data.getSerializableExtra("filterSelection");
            }
        }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	    MenuInflater inflater = getSupportMenuInflater();
	    //inflater.inflate(R.menu.search, menu);
	    inflater.inflate(R.menu.search, (com.actionbarsherlock.view.Menu) menu);
	    MenuItem searchItem = menu.findItem(R.id.action_search);
	    searchView = (SearchView) searchItem.getActionView();
	    
	    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				searchQuery	= query;
				onImageSearch(findViewById(R.layout.activity_search));
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	   return super.onCreateOptionsMenu(menu);
	    
	}
	
	//Method to check if intent is available
	public static boolean isIntentAvailable(Context ctx, Intent intent) {
		final PackageManager mgr = ctx.getPackageManager();
		List<ResolveInfo> list =
		mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}
	
	public static boolean isConnectivityAvailable(Context ctx){
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null) {
			return (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE);
		} else {
			return false;
		}
	 }
}
