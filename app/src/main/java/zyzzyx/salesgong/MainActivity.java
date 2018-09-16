package zyzzyx.salesgong;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
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
    public void onResume(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        PUSHER_API_KEY = prefs.getString("pref_apikey", null);

        initPusher();

        super.onResume();
    }

    private void initPusher() {

        if (PUSHER_API_KEY != null) {
            PusherOptions options = new PusherOptions();
            options.setCluster("eu");
            Pusher pusher = new Pusher(PUSHER_API_KEY, options);
            pusher.connect();

            Channel channel = pusher.subscribe("my-channel");

            channel.bind("my-event", new SubscriptionEventListener() {
                @Override
                public void onEvent(String channelName, String eventName, final String data) {
                    String mp3Url = null;
                    JSONObject jObject;
                    try {
                        jObject = new JSONObject(data);
                        mp3Url = jObject.getString("mp3_url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (mp3Url == null) {
                        // MP3 URL not specified, play local sample
                        System.out.println("Playing local sample");
                        MediaPlayer mediaPlayer;
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sample);
                        mediaPlayer.start();
                        mediaPlayer.release();
                    } else {
                        try {
                            // Stream
                            System.out.println("Playing " + mp3Url + " over network");
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(mp3Url);
                            mediaPlayer.prepare(); // might take long! (for buffering, etc)
                            mediaPlayer.start();
                            mediaPlayer.release();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }
    }
}
