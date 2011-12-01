package tagencoder.main;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends TabActivity implements OnItemSelectedListener{

    private ListView lvArtists = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ArrayAdapter<CharSequence> encodingAdapter = 
                ArrayAdapter.createFromResource(this, R.array.encodings, android.R.layout.simple_spinner_item);
        Spinner spEncodings = (Spinner)findViewById(R.id.spMainEncodingSpinner);
        spEncodings.setAdapter(encodingAdapter);
        spEncodings.setOnItemSelectedListener(this);
        
        
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
        lvArtists = (ListView)findViewById(R.id.TabListArtists);
        lvArtists.setAdapter(new ArtistListAdapter(this));
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int index, long id) {
        String sCharset = (String) arg0.getAdapter().getItem(index);        
        try {
            ((ArtistListAdapter)lvArtists.getAdapter()).setCharset(sCharset);
        } catch (UnsupportedEncodingException ex) {
            Toast.makeText(this, "Invalid charset: " + sCharset, Toast.LENGTH_LONG);
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
