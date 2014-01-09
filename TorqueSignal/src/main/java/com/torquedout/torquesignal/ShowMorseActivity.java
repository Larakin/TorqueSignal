package com.torquedout.torquesignal;

import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.AsyncTask;
import android.content.Intent;
import android.widget.TextView;
import android.util.Log;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.content.pm.PackageManager;
import android.text.Html;

import java.util.HashMap;
import java.util.Map;

public class ShowMorseActivity extends Activity {

    private static final String TAG = "ShowMorse";

    private Camera camera;
    private boolean hasFlash;
    private String dots;
    private AsyncTask flasher;
    Parameters params;


    private static final Map<String, String> code;
    static
    {
        code = new HashMap<String, String>();
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

    //@SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morse);
        // Show the Up button in the action bar.
        setupActionBar();

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //fillCodeHash();
        dots = convertMessage(message);

        TextView textView = (TextView) findViewById(R.id.converted);
        textView.setTextSize(40);
        textView.setText(dots);


        try {
            hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        } catch (RuntimeException e) {
            Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
        }

        if (!hasFlash){
            findViewById(R.id.flash_button).setEnabled(false);
        }
        else {
            findViewById(R.id.flash_button).setEnabled(true);
        }
    }

    private String convertMessage(String text) {
        String dots = "";

        for  (char ch: text.toUpperCase().toCharArray()) {
            Log.v(TAG, "Char:" + ch);
            switch(ch) {
                case ' ':
                    dots += "  ";
                    break;
                default:
                    dots += code.get(String.valueOf(ch)) + " ";
                    break;
            }
        }

        return dots;
    }

    private void showProgress(Integer progIndex) {
        TextView textView = (TextView) findViewById(R.id.converted);

        String text = "";
        int index = 0;

        for  (char ch: dots.toCharArray()) {
            if(progIndex == index) {
                text += "<font color=#FF0000>" + ch + "</font>";
            }
            else {
                text += ch;
            }
            index++;
        }

        if (progIndex == -1) {
            findViewById(R.id.flash_button).setEnabled(true);
        }

        textView.setText(Html.fromHtml(text));
    }

    public void runFlasher(View view) {
        if (hasFlash) {
            flasher = new FlashIt().execute(dots);
            findViewById(R.id.flash_button).setEnabled(false);
        }
    }



    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        if (flasher != null) {
            flasher.cancel(true);
        }
        super.onDestroy();

    }

    private class FlashIt extends AsyncTask<String, Integer, Boolean> {
        private int TIMEUNIT = 75;
        private int DOT = 1;
        private int DASH = 3;
        private int LETTER_GAP = 3;
        private int WORD_GAP = 7;

        private int messageLength = 1;

        private void flashMessage(String message) {

            if (camera == null) {
                try {
                    camera = Camera.open();
                    params = camera.getParameters();
                } catch (RuntimeException e) {
                    Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
                }
            }

            int index = 0;
            for  (char ch: message.toCharArray()) {
                index++;
                if(isCancelled()) {break;}
                try {
                    Log.v(TAG, "dot:" + ch);
                    switch(ch) {
                        case '.':
                            flash(DOT);
                            break;
                        case '-':
                            flash(DASH);
                            break;
                        case ' ':
                            Thread.sleep(WORD_GAP * TIMEUNIT);
                            break;
                    }
                    Thread.sleep(LETTER_GAP * TIMEUNIT);
                } catch (Exception e) {
                    e.getLocalizedMessage();
                }
                publishProgress(index);
            }

            if (camera != null) {
                camera.release();
                camera = null;
            }
        }

        private void flash(int signal) {
            try {
                params = camera.getParameters();
                params.setFlashMode(Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);
                camera.startPreview();

                Thread.sleep(signal*TIMEUNIT);

                //params = camera.getParameters();
                params.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);
                camera.stopPreview();
                Thread.sleep(TIMEUNIT);
            } catch (Exception e) {
                e.getLocalizedMessage();
            }
        }

        /** The system calls this to perform work in a worker thread and
         * delivers it the parameters given to AsyncTask.execute() */
        protected Boolean doInBackground(String... Alldots) {
            flashMessage(Alldots[0]);
            return true;
        }

        protected void onPreExecute() {
            showProgress(0);
        }

        /** The system calls this to perform work in the UI thread and delivers
         * the result from doInBackground() */
        protected void onPostExecute(Boolean result) {
            showProgress(-1);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            showProgress(values[0]);
            Log.v(TAG, "Updating... " + values[0]);
        }
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

