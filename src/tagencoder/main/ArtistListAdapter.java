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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author seth
 */
public class ArtistListAdapter extends BaseAdapter implements CharsetUpdateableAdapter{

    private Context context = null;
    private ArrayList<Pair<Long, String>> lsArtists = null;

    public ArtistListAdapter(Context context){
        this.context = context;
        update();
    }
    
    public final void update()
    {   
        try {
            setCharset(null);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ArtistListAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public final void setCharset(String charset) throws UnsupportedEncodingException {
        String[] projection = new String[]{Artists._ID, Artists.ARTIST, Artists.NUMBER_OF_ALBUMS};

        Cursor c = context.getContentResolver().query(Artists.EXTERNAL_CONTENT_URI, projection, null, null, null);
        lsArtists = new ArrayList<Pair<Long, String>>(c.getCount());
        while (c.moveToNext()) {
            Long nId = c.getLong(c.getColumnIndex(Artists._ID));
            String sArtistName = c.getString(c.getColumnIndex(Artists.ARTIST));
            
            if (charset != null) {
                byte[] bytes = sArtistName.getBytes("ISO8859-1");
                sArtistName = new String(bytes, charset);
            }
            
            lsArtists.add(new Pair<Long, String>(nId, sArtistName));
        }
        c.close();
        this.notifyDataSetChanged();
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
