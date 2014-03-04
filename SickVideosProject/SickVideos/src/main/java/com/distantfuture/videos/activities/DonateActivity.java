package com.distantfuture.videos.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.distantfuture.videos.R;
import com.distantfuture.videos.misc.Debug;
import com.distantfuture.videos.misc.PurchaseHelper;
import com.distantfuture.videos.misc.Utils;
import com.distantfuture.videos.youtube.YouTubeAPI;


public class DonateActivity extends Activity {
  private PurchaseHelper mPurchaseHelper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_donate);

    getActionBar().setDisplayHomeAsUpEnabled(true);
    setupSpinner();



    Button button = (Button) findViewById(R.id.donate_button);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Utils.toast(DonateActivity.this, "fuckk");
      }
    });


    mPurchaseHelper = new PurchaseHelper(this);

//    if (mPurchaseHelper != null)
//      mPurchaseHelper.onBuyGasButtonClicked(null, this);

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  // We're being destroyed. It's important to dispose of the helper here!
  @Override
  public void onDestroy() {
    super.onDestroy();

    // very important:
    if (mPurchaseHelper != null) {
      mPurchaseHelper.destroy();
      mPurchaseHelper = null;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    // Pass on the activity result to the helper for handling
    if (mPurchaseHelper != null && mPurchaseHelper.handleActivityResult(requestCode, resultCode, data)) {
      // handled by helper
    } else {
    }
  }

  private void setupSpinner() {
    Spinner spinner = (Spinner) findViewById(R.id.spinner);
    // Create an ArrayAdapter using the string array and a default spinner layout
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.donations_array, android.R.layout.simple_spinner_item);

    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    spinner.setAdapter(adapter);

    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      public void onItemSelected(AdapterView<?> parent, View view,
                                 int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
      }

      public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
      }

    });
  }

}