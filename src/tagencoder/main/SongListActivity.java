/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SimpleCursorAdapter;

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

        Cursor c = getCursor();
        ListView lv = (ListView) findViewById(R.id.lvSongs);
        String[] from = new String[]{Media.TITLE};
        int[] to = new int[]{R.id.tvSongName};
        lv.setAdapter(new SimpleCursorAdapter(this, R.layout.songitem, c, from, to));
        lv.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        Cursor c = (Cursor)adapter.getItemAtPosition(position);
        long nSongId = c.getLong(c.getColumnIndex(Media._ID));                
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        uri = ContentUris.withAppendedId(uri, nSongId);
        Intent i = new Intent();
        i.setData(uri);
        i.setClass(this, SongRecodeActivity.class);
        startActivity(i);
    }

    private class CursorParametersGenerator {

        private int _nArtistid = -1;
        private int _nAlbumId = -1;

        public void setArtistId(int nArtistId) {
            this._nArtistid = nArtistId;
        }

        public void setAlbumId(int nAlbumId) {
            this._nAlbumId = nAlbumId;
        }
    }

    private Cursor getCursor() {
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{Media._ID, Media.TITLE};
        //projection = null;

        Cursor c = managedQuery(uri, projection, null, null, null);
        startManagingCursor(c);
        c.moveToFirst();
        return c;
    }
}
