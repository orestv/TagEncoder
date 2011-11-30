package tagencoder.main;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initLists();
        initTabs();
    }

    private void initTabs() {

        TabSpec spec;
        TabHost host = getTabHost();
        spec = host.newTabSpec("Artists").setIndicator("Artists").setContent(R.id.TabLayoutArtists);
        host.addTab(spec);

        spec = host.newTabSpec("Albums").setIndicator("Albums").setContent(R.id.TabLayoutAlbums);
        host.addTab(spec);

        spec = host.newTabSpec("Songs").setIndicator("Songs").setContent(R.id.TabLayoutSongs);
        host.addTab(spec);

    }
    
    private void initLists() {
        ListView lvArtists = (ListView)findViewById(R.id.TabListArtists);
        lvArtists.setAdapter(new ArtistListAdapter(this));
    }
}
