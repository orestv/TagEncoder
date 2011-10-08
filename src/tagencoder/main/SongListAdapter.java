/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;

/**
 *
 * @author seth
 */
public class SongListAdapter extends BaseAdapter {

    private ArrayList<SongData> lsSongs = null;
    private Context context = null;

    public SongListAdapter(Context context) {
        this.context = context;
        initSongList(context);
    }

    private void initSongList(Context context) {
        String[] projection = new String[]{Media._ID, Media.TITLE, Media.DATA};
        Cursor c = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        lsSongs = new ArrayList<SongData>(c.getCount());
        while (c.moveToNext()) {
            String sTitle = c.getString(c.getColumnIndex(Media.TITLE));
            String sPath = c.getString(c.getColumnIndex(Media.DATA));
            long nId = c.getLong(c.getColumnIndex(Media._ID));
            lsSongs.add(new SongData(nId, sTitle, sPath));
        }
        c.close();
    }

    public int getCount() {
        return lsSongs.size();
    }

    public Object getItem(int arg0) {
        return lsSongs.get(arg0);
    }

    public long getItemId(int arg0) {
        return lsSongs.get(arg0).getId();
    }

    public View getView(int arg0, View arg1, ViewGroup arg2) {
        SongData song = lsSongs.get(arg0);
        SongListView sv;
        
        if (true || arg1 == null) {
            sv = new SongListView(context, song.getId(), song.getTitle());
        } else {
            sv = (SongListView) arg1;

            sv.setTitle(song.getTitle());
        }

        return sv.getView();
    }
}
