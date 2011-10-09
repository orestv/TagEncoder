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
public class SongListView extends RelativeLayout {

    private Context context = null;
    private TextView tvTitle = null;
    private TextView tvVersion = null;
    private TextView tvArtist = null;
    private TextView tvAlbum = null;
    private ProgressBar pbVersion = null;
    private View view = null;

    public SongListView(final Context context, final SongData song) {
        super(context);
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.songitem, null, false);

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvVersion = (TextView) view.findViewById(R.id.tvVersion);
        tvAlbum = (TextView) view.findViewById(R.id.tvAlbum);
        tvArtist = (TextView) view.findViewById(R.id.tvArtist);
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
                                sRet = "V1";
                                break;
                            case ID3V2:
                                sRet = "V2";
                                break;
                        }
                    } catch (IOException ex) {
                        sRet = "Failed";
                    } finally {
                        final String ret = sRet;
                        handler.post(new Runnable() {
                            public void run() {
                                tvVersion.setText(ret);
                                tvVersion.setVisibility(VISIBLE);
                                pbVersion.setVisibility(GONE);
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
        tvAlbum.setText(data.getAlbum());
        tvArtist.setText(data.getArtist());
    }


    public View getView() {
        return view;
    }
}
