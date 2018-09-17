/**
 *      Sales Gong
 *
 *      Usage: see https://github.com/pkovanen/salesgong/
 *
 *      This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package zyzzyx.salesgong;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static final int IDM_SETTINGS = 101;
    private String PUSHER_API_KEY = null;
    private Pusher pusher = null;
    static final String TAG = "SalesGongMainActivity";

    public void sendMessageNoAPIKey(View view) {
        Intent myIntent = new Intent(MainActivity.this, MyPreferencesActivity.class);
        startActivity(myIntent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(Menu.NONE, IDM_SETTINGS, Menu.NONE, R.string.menu_settings);

        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case IDM_SETTINGS:
                Intent i = new Intent(this, MyPreferencesActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        PUSHER_API_KEY = prefs.getString("pref_pusher_apikey", null);

        initPusher();

        Button preferencesButton = (Button) findViewById(R.id.button_api_key);

        if (PUSHER_API_KEY == null || PUSHER_API_KEY == "") {
            preferencesButton.setVisibility(View.VISIBLE);
        } else {
            preferencesButton.setVisibility(View.GONE);
        }

        super.onResume();
    }

    private void initPusher() {

        Log.d(TAG, "init: " + PUSHER_API_KEY);

        if (pusher != null) {
            pusher.disconnect();
        }

        if (PUSHER_API_KEY != null && PUSHER_API_KEY != "") {
            Channel channel = pusherListenChannel();

            channel.bind("sales-event", new SubscriptionEventListener() {
                @Override
                public void onEvent(String channelName, String eventName, final String data) {

                    Log.d(TAG, "EVENT");

                    String mp3Url = extractMP3Url(data, mp3Url);

                    if (mp3Url == "") {
                        // MP3 URL not specified, play local sample
                        playLocalSample();
                    } else {
                        playRemoteSample(mp3Url);
                    }
                }
            });

        }
    }

    private Channel pusherListenChannel() {
        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        pusher = new Pusher(PUSHER_API_KEY, options);
        pusher.connect();

        return pusher.subscribe("sales-gong");
    }

    private String extractMP3Url(String data) {
        String mp3Url = "";

        JSONObject jObject;
        try {
            jObject = new JSONObject(data);
            mp3Url = jObject.getString("mp3-url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mp3Url;
    }

    private void playLocalSample() {
        Log.d(TAG, "LOCAL SAMPLE");
        MediaPlayer mediaPlayer;
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sample);
        mediaPlayer.start();
    }

    private void playRemoteSample(String mp3Url) {
        try {
            // Stream
            Log.d(TAG, "REMOTE SAMPLE " + mp3Url);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(mp3Url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
