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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author seth
 */
public final class SongListItemCreator {

    private SongItemView view = null;
    private SongData song = null;
    private Context context = null;
    private static int nThreadCount = 0;
    private static int nMaxThreadCount = 3;

    public SongListItemCreator(final Context context, SongData song) {
        this.context = context;
        this.song = song;
    }

    private static SongItemView createView(final Context context, final SongData song, SongListItemCreator creator) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final SongItemView view = (SongItemView) inflater.inflate(R.layout.songitem, null, false);
        view.setListView(creator);

        view.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        view.tvVersion = (TextView) view.findViewById(R.id.tvVersion);
        view.pbVersion = (ProgressBar) view.findViewById(R.id.pbVersion);

        final Handler handler = new Handler();

        view.setSong(song);

        new Thread(new Runnable() {

            public void run() {
                {
                    synchronized (context) {
                        if (nThreadCount > nMaxThreadCount) {
                            try {
                                context.wait();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(SongListItemCreator.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        nThreadCount++;
                    }
                    Uri uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, song.getId());
                    String sRet = null;
                    InputStream is = null;
                    try {
                        is = context.getContentResolver().openInputStream(uri);
                        TagData data = BicycleTagEncoder.parseTagVersion(is);
                        is.close();
                        switch (data.version) {
                            case ID3V1:
                                sRet = "ID3V1";
                                break;
                            case ID3V2:
                                sRet = "ID3V2";
                                break;
                            case Unknown:
                                sRet = "Unknown";
                                view.setEnabled(false);
                                break;
                        }
                        synchronized(context) {
                            nThreadCount--;
                            context.notify();
                        }
                    } catch (IOException ex) {
                        sRet = "Failed";
                    } catch (NullPointerException ex) {
                        sRet = "NULL" + ex.getStackTrace()[0].toString();
                    } finally {
                        final String ret = sRet;
                        handler.post(new Runnable() {

                            public void run() {
                                view.tvVersion.setText(ret);
                                view.tvVersion.setVisibility(View.VISIBLE);
                                view.pbVersion.setVisibility(View.GONE);
                                view.invalidate();
                            }
                        });
                    }
                }
            }
        }).start();
        return view;
    }

    public View getView() {
        if (view == null) {
            view = createView(this.context, this.song, this);
        }
        return view;
    }

    public static class SongItemView extends RelativeLayout {

        private SongListItemCreator slv = null;
        private TextView tvTitle = null;
        private TextView tvVersion = null;
        private ProgressBar pbVersion = null;

        public SongItemView(Context context, SongListItemCreator slv) {
            super(context);
            this.slv = slv;
        }

        public SongItemView(Context context, AttributeSet set) {
            super(context, set);
        }

        public void setListView(SongListItemCreator slv) {
            this.slv = slv;
        }

        public void setSong(SongData song) {
            tvTitle.setText(song.getTitle());
        }

        public SongListItemCreator getSongListView() {
            return slv;
        }

        public SongItemView getView() {
            return this;
        }
    }
}
