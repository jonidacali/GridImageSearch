package com.jona.gridimagesearch.models;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageResult implements Serializable{
	private static final long serialVersionUID = -1327371582092397531L;
	public String fullUrl;
	public String thumbUrl;
	public String title;
	
	public ImageResult(JSONObject obj){
		try{
			this.fullUrl = obj.getString("url");
			this.thumbUrl = obj.getString("tbUrl");
			this.title = obj.getString("title");
			
		} catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	public static ArrayList<ImageResult> fromJsonArray(JSONArray array){
		ArrayList<ImageResult> results = new ArrayList<ImageResult>();
		for(int i=0; i< array.length(); i++){
			try{
				results.add(new ImageResult(array.getJSONObject(i)));
			} catch (JSONException e){
				e.printStackTrace();
			}
		}
		return results;
	}
	
}
