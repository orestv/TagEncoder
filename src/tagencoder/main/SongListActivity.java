/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 *
 * @author seth
 */
public class SongListActivity extends Activity {

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
