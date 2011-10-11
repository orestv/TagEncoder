/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author seth
 */
public class SongListActivity extends Activity implements OnItemClickListener {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.songlist);

        ListView lv = (ListView) findViewById(R.id.lvSongs);

        lv.setAdapter(new SongListAdapter(SongListActivity.this));

        lv.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        SongData data = (SongData) adapter.getItemAtPosition(position);
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        uri = ContentUris.withAppendedId(uri, data.getId());
        Intent i = new Intent();
        i.setData(uri);
        i.setClass(this, SongRecodeActivity.class);
        startActivity(i);
    }
}
