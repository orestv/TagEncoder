/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 *
 * @author seth
 */
public class SongListActivity extends Activity implements OnItemClickListener, OnClickListener {

    private ListView lvSongs = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.songlist);

        lvSongs = (ListView) findViewById(R.id.lvSongs);
        lvSongs.setAdapter(new SongListAdapter(SongListActivity.this));
        //lvSongs.setOnItemClickListener(this);
        //lvSongs.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
        ((Button)findViewById(R.id.btnShowSelectedSongs)).setOnClickListener(this);
    }

    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        if (!view.isEnabled())
            return;
        SongData data = (SongData) adapter.getItemAtPosition(position);
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        uri = ContentUris.withAppendedId(uri, data.getId());
        Intent i = new Intent();
        i.setData(uri);
        i.setClass(this, SongRecodeActivity.class);
        startActivity(i);
    }

    public void onClick(View arg0) {
        long[] arrIDs = lvSongs.getCheckItemIds();
        SparseBooleanArray checkedItemPositions = lvSongs.getCheckedItemPositions();
        String s = checkedItemPositions.toString();
    }
}
