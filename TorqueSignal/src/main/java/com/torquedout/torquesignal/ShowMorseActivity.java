package com.torquedout.torquesignal;

import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.content.Intent;
import android.widget.TextView;
import android.util.Log;

import java.util.Hashtable;

public class ShowMorseActivity extends Activity {

    private static final String TAG = "ShowMorse";

    private Hashtable code = new Hashtable();

    //@SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morse);
        // Show the Up button in the action bar.
        setupActionBar();

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String dots;

        fillCodeHash();
        dots = convertMessage(message);

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(dots);

        setContentView(textView);
    }

    private void fillCodeHash() {
        //TODO: Better way to do this!
        code.put("A",".-");
        code.put("B","-...");
        code.put("C","-.-.");
        code.put("D","-..");
        code.put("E",".");
        code.put("F","..-.");
        code.put("G","--.");
        code.put("H","....");
        code.put("I","..");
        code.put("J",".---");
        code.put("K","-.-");
        code.put("L",".-..");
        code.put("M","--");
        code.put("N","-.");
        code.put("O","---");
        code.put("P",".--.");
        code.put("Q","--.-");
        code.put("R",".-.");
        code.put("S","...");
        code.put("T","-");
        code.put("U","..-");
        code.put("V","...-");
        code.put("W",".--");
        code.put("X","-..-");
        code.put("Y","-.--");
        code.put("Z","--..");

        code.put("1",".----");
        code.put("2","..---");
        code.put("3","...--");
        code.put("4","....-");
        code.put("5",".....");
        code.put("6","-....");
        code.put("7","--...");
        code.put("8","---..");
        code.put("9","----.");
        code.put("0","-----");
    }

    private String convertMessage(String text) {
        String dots = "";

        for  (char ch: text.toUpperCase().toCharArray()) {
            Log.v(TAG, "Char:" + ch);
            dots += code.get(String.valueOf(ch)) + " ";
        }

        return dots;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
