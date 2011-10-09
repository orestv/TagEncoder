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

        Cursor c = getCursor();
        final ListView lv = (ListView) findViewById(R.id.lvSongs);

        final Handler handler = new Handler();
        final ProgressDialog dlg = new ProgressDialog(this);
        dlg.setCancelable(false);
        dlg.show();

        new Thread(new Runnable() {

            public void run() {
                final SongListAdapter adapter = new SongListAdapter(SongListActivity.this);
                handler.post(new Runnable() {

                    public void run() {
                        lv.setAdapter(adapter);
                        dlg.cancel();
                    }
                });

            }
        }).start();

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
