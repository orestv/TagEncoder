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
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ovoloshchuk
 */
public class SongItemLayout extends RelativeLayout implements Checkable {

    private SongData song = null;
    private Context context = null;
    private TextView tvTitle = null;
    private TextView tvVersion = null;
    private ProgressBar pbVersion = null;
    private InertCheckBox chkSelected = null;
    private static int nThreadCount = 0;
    private final static int nMaxThreadCount = 3;

    public SongItemLayout(Context context) {
        super(context);
        this.context = context;
    }

    public SongItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    private void initChildren() {
        this.tvTitle = (TextView) findViewById(R.id.tvTitle_1);
        this.tvVersion = (TextView) findViewById(R.id.tvVersion_1);
        this.pbVersion = (ProgressBar) findViewById(R.id.pbVersion_1);
        this.chkSelected = (InertCheckBox) findViewById(R.id.chkSongItem_1);
    }

    public void setSong(SongData song) {
        this.song = song;
        tvTitle.setText(song.getTitle());
        checkVersion();
    }

    private void checkVersion() {
        tvVersion.setVisibility(View.GONE);
        pbVersion.setVisibility(View.VISIBLE);
        this.invalidate();
        final Handler handler = new Handler();
        final Context c = context;

        new Thread(new Runnable() {

            public void run() {
                {
                    synchronized (c) {
                        if (nThreadCount > nMaxThreadCount) {
                            try {
                                c.wait();
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
                                SongItemLayout.this.setEnabled(false);
                                chkSelected.setEnabled(false);
                                break;
                        }
                        synchronized (c) {
                            nThreadCount--;
                            c.notify();
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
                                invalidate();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public void setChecked(boolean arg0) {
        chkSelected.setChecked(arg0);
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isChecked() {
        return chkSelected.isChecked();
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void toggle() {
        chkSelected.toggle();
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public static SongItemLayout construct(LayoutInflater inflater) {
        SongItemLayout result = (SongItemLayout) inflater.inflate(R.layout.songitem_1, null, false);
        result.initChildren();
        return result;
    }
}
