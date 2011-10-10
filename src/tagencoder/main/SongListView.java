/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import TagEncoderLib.BicycleTagEncoder;
import TagEncoderLib.BicycleTagEncoder.TagData;
import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore.Audio.Media;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author seth
 */
public class SongListView {

    private TextView tvTitle = null;
    private TextView tvVersion = null;
    private ProgressBar pbVersion = null;
    private SongListItem view = null;
    
    public static class SongListItem extends RelativeLayout {
        private SongListView slv = null;
        
        public SongListItem(Context context, SongListView slv) {
            super(context);
            this.slv = slv;
        }
        
        public SongListItem(Context context, AttributeSet set) {
            super(context, set);
        }
        
        public void setListView(SongListView slv) {
            this.slv = slv;
        }
        
        public void setSong(SongData song) {
            slv.setSong(song);
        }
        
        public SongListView getSongListView(){
            return slv;
        }
    }

    public SongListView(final Context context, final SongData song) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = (SongListItem) inflater.inflate(R.layout.songitem, null, false);
        view.setListView(this);

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvVersion = (TextView) view.findViewById(R.id.tvVersion);
        pbVersion = (ProgressBar) view.findViewById(R.id.pbVersion);

        final Handler handler = new Handler();

        setSong(song);

        new Thread(new Runnable() {

            public void run() {
                {

                    Uri uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, song.getId());
                    String sRet = null;
                    InputStream is = null;
                    try {
                        is = context.getContentResolver().openInputStream(uri);
                        TagData data = BicycleTagEncoder.parseTagVersion(is);
                        is.close();
                        switch(data.version) {
                            case ID3V1:
                                sRet = "ID3V1";
                                break;
                            case ID3V2:
                                sRet = "ID3V2";
                                break;
                        }
                    } catch (IOException ex) {
                        sRet = "Failed";
                    } finally {
                        final String ret = sRet;
                        handler.post(new Runnable() {
                            public void run() {                                
                                tvVersion.setText(ret);
                                tvVersion.setVisibility(View.VISIBLE);
                                pbVersion.setVisibility(View.GONE);
                                view.invalidate();
                            }
                        });
                    }
                }
            }
        }).start();
    }
    
    public void setSong(SongData data) {
        tvTitle.setText(data.getTitle());
    }


    public View getView() {
        return view;
    }
}
