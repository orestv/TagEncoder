/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio.Artists;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 *
 * @author seth
 */
public class ArtistListAdapter extends BaseAdapter {

    private Context context = null;
    private ArrayList<Pair<Long, String>> lsArtists = new ArrayList<Pair<Long, String>>();

    public ArtistListAdapter(Context context) {
        this.context = context;

        String[] projection = new String[]{Artists._ID, Artists.ARTIST, Artists.NUMBER_OF_ALBUMS};

        Cursor c = context.getContentResolver().query(Artists.EXTERNAL_CONTENT_URI, projection, null, null, null);
        while (c.moveToNext()) {
            Long nId = c.getLong(c.getColumnIndex(Artists._ID));
            String sArtistName = c.getString(c.getColumnIndex(Artists.ARTIST));
            
            lsArtists.add(new Pair<Long, String>(nId, sArtistName));
        }
        c.close();
    }

    public int getCount() {
        return lsArtists.size();
    }

    public Object getItem(int arg0) {
        return lsArtists.get(arg0);
    }

    public long getItemId(int arg0) {
        return lsArtists.get(arg0).first;
    }

    public View getView(int index, View arg1, ViewGroup arg2) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.artistlistitem, null);
        
        TextView tvArtist = (TextView)v.findViewById(R.id.tvArtistItem);
        tvArtist.setText(lsArtists.get(index).second);
        
        return v;        
    }
}
