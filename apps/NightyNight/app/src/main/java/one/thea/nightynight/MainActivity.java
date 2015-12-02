package one.thea.nightynight;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private CrySensor     crySensor;
    private MediaPlayer   mediaPlayer;
    private LullabyShuffle lullabyShuffle;
    final private String  LOG_TAG   = "NIGHT_NIGHT_MAIN";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            this.setContentView(R.layout.activity_main);
            this.setLullabyShuffle(new LullabyShuffle());

    }
    @Override
    protected void onResume() {
        try
        {
            super.onResume();
            this.loadPreferences();
            this.startCrySensor();
        }
        catch (Exception e)
        {
            Log.e(this.LOG_TAG, e.getMessage(), e);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getFragmentManager().beginTransaction()
                    .addToBackStack("settings")
                    .replace(R.id.content, new SettingsFragment())
                    .commit();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }

    }
    @Override
    protected void onPause()
    {
        super.onPause();
        this.stopCrySensor();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        this.stopCrySensor();
    }

    @Override
    public void onBackPressed()
    {
        if(getFragmentManager().getBackStackEntryCount() > 0)
        {
            getFragmentManager().popBackStack();
        }
        else
        {
            super.onBackPressed();
        }

    }

    private void stopCrySensor()
    {
        this.stopMediaPlayer();
        this.stopSensing();
    }
    private void stopSensing(){

            try
            {
                if(this.crySensor != null)
                {
                    this.crySensor.stop();
                }
            }
            catch (IOException e)
            {
                Log.e(this.LOG_TAG, e.getMessage(), e);
            }

    }
    private void stopMediaPlayer() {
        if(this.mediaPlayer != null)
        {
                this.mediaPlayer.release();
        }
    }

    private void startCrySensor() throws IOException, InterruptedException {
        this.initCrySensor();
        this.crySensor.start();
    }
    private void initCrySensor() throws IOException, InterruptedException
    {
        this.crySensor = new CrySensor(new MediaRecorderAudioSampler(new MediaRecorder()));
        this.crySensor.setSampleLength(this.getPreferences().getInt("SAMPLE_LENGTH_SECS",10) * 1000);
        this.crySensor.setSenseCryInterval(this.getPreferences().getInt("SENSE_CRY_INTERVAL_SECS",3) * 1000);
        this.crySensor.setSampleCount(this.getPreferences().getInt("SAMPLES_COUNT",5));
        this.crySensor.registerCryHandler(new CryHandler() {
            @Override
            public void handle() {
                if(MainActivity.this.mediaPlayer == null || !MainActivity.this.mediaPlayer.isPlaying())
                {
                    MainActivity.this.mediaPlayer = MediaPlayer.create(MainActivity.this.getApplicationContext(),
                                                                       MainActivity.this.getLullabyShuffle().getLullabyID());
                    MainActivity.this.mediaPlayer.start();
                    MainActivity.this.showSpeakerUI();
                }
                else
                {
                    MainActivity.this.hideSpeakerUI();
                }
            }
        });
    }
    private void loadPreferences()
    {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
    private SharedPreferences getPreferences()
    {
        return this.preferences;
    }
    private void showSpeakerUI() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.findViewById(R.id.speaker).setVisibility(View.VISIBLE);
            }
        });
    }
    private void hideSpeakerUI() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.findViewById(R.id.speaker).setVisibility(View.GONE);
            }
        });

    }

    private LullabyShuffle getLullabyShuffle() {
        return lullabyShuffle;
    }

    private void setLullabyShuffle(LullabyShuffle lullabyShuffle) {
        this.lullabyShuffle = lullabyShuffle;
    }
}
