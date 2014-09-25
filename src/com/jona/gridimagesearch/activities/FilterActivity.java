package com.jona.gridimagesearch.activities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import com.jona.gridimagesearch.R;
import com.jona.gridimagesearch.models.FilterSelection;

public class FilterActivity extends Activity{
	private EditText etSearchSite;
	private Spinner spSizeFilter;
	private Spinner spColorFilter;
	private Spinner spTypeFilter;
	private FilterSelection filterOptions;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);
		etSearchSite = (EditText) findViewById(R.id.etSearchSite);
		spSizeFilter = (Spinner) findViewById(R.id.spSize);
		spColorFilter = (Spinner) findViewById(R.id.spColor);
		spTypeFilter = (Spinner) findViewById(R.id.spType);
		
		if(getIntent().getExtras() != null){
			filterOptions = (FilterSelection) getIntent().getSerializableExtra("filterOptions");
			setOption(spColorFilter, filterOptions.color);
			setOption(spSizeFilter, filterOptions.size);
			setOption(spTypeFilter, filterOptions.type);
			etSearchSite.setText(filterOptions.searchSite);
		}
	}

	public void setOption(Spinner spinner, String option){
		int optionVal = 0;
		SpinnerAdapter adapter = spinner.getAdapter();
		for (int i = 0; i < adapter.getCount(); i++) {
			if (adapter.getItem(i).equals(option)) {
				optionVal = i;
				continue;
			}
		}
		spinner.setSelection(optionVal);
	}

	public void onImageSearch(View v){
		//get new filter preferences
		filterOptions.color = spColorFilter.getSelectedItem().toString();
		filterOptions.size = spSizeFilter.getSelectedItem().toString();
		filterOptions.type = spTypeFilter.getSelectedItem().toString();
		filterOptions.searchSite = etSearchSite.getText().toString();
		//Pass expense as a result
		Intent filter = new Intent();
		filter.putExtra("filterSelection", filterOptions);
		setResult(RESULT_OK, filter);
		//Dismiss form
		finish();
	}
}