/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *
 * @author seth
 */
public class SongItemLayout extends RelativeLayout implements Checkable{
    private SongData song = null;
    private TextView tvTitle = null;
    private TextView tvAlbum = null;
    private CheckBox chkSelected = null;

    public SongItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }//LayoutInflater inflater = (LayoutInflater)   getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
    private void initChildren() {
        tvTitle = (TextView)findViewById(R.id.tvSongitem_Title);
        tvAlbum = (TextView)findViewById(R.id.tvSongitem_Album);
        chkSelected = (CheckBox)findViewById(R.id.chkSongitem_Selected);
    }
    
    public void setSong(SongData song) {
        this.song = song;
        if (tvTitle != null)
            tvTitle.setText(song.getTitle());
        if (tvAlbum != null)
            tvAlbum.setText(song.getAlbum());
    }
    
    public static SongItemLayout create(LayoutInflater inflater, ViewGroup group) {
        
        View v =  inflater.inflate(R.layout.songlistitem, null);
        SongItemLayout ret = (SongItemLayout)v;
        ret.initChildren();
        return ret;
    }

    public void setChecked(boolean arg0) {
        chkSelected.setChecked(arg0);
    }

    public boolean isChecked() {
        return chkSelected.isChecked();
    }

    public void toggle() {
        chkSelected.toggle();
    }
}
