/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import TagEncoderLib.BicycleTagEncoder;
import TagEncoderLib.BicycleTagEncoder.Tag;
import TagEncoderLib.BicycleTagEncoder.UnknownFormatException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author seth
 */

//TODO: update actions should also update the database.
//TODO: move all data update functions away from the file.
public class SongRecodeActivity extends Activity implements OnItemSelectedListener {

    public enum ButtonAction {

        UPDATE_ARTIST, UPDATE_ALBUM, UPDATE_TITLE
    };
    private Uri songUri;
    private Long nSongId = null;
    private Long nAlbumId = null;
    private Long nArtistId = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.songedit);
        Intent i = this.getIntent();
        this.songUri = i.getData();
        this.initIDs();
        this.initUI();
    }

    private void initIDs() {
        this.nSongId = ContentUris.parseId(songUri);
        String[] projection = new String[]{
            Media._ID,
            Media.ALBUM_ID,
            Media.ARTIST_ID
        };
        String selection = Media._ID + " = ?";
        String[] selectionArgs = new String[]{
            nSongId.toString()
        };
        Cursor c = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);
        if (!c.moveToFirst()) {
            return;
        }
        this.nAlbumId = c.getLong(c.getColumnIndex(Media.ALBUM_ID));
        this.nArtistId = c.getLong(c.getColumnIndex(Media.ARTIST_ID));
        c.close();
    }

    private void initUI() {
        ArrayAdapter<CharSequence> encodingAdapter =
                ArrayAdapter.createFromResource(this, R.array.encodings, android.R.layout.simple_spinner_item);
        Spinner spinner = (Spinner) findViewById(R.id.Encoding);
        spinner.setAdapter(encodingAdapter);
        spinner.setOnItemSelectedListener(this);

        Button btnUpdateTitle = (Button) findViewById(R.id.UpdateTitle);
        Button btnUpdateAlbum = (Button) findViewById(R.id.UpdateAlbum);
        Button btnUpdateArtist = (Button) findViewById(R.id.UpdateArtist);


        btnUpdateTitle.setOnClickListener(new UpdateListener(ButtonAction.UPDATE_TITLE));
        btnUpdateAlbum.setOnClickListener(new UpdateListener(ButtonAction.UPDATE_ALBUM));
        btnUpdateArtist.setOnClickListener(new UpdateListener(ButtonAction.UPDATE_ARTIST));
    }

    private void setSongData(Uri uri, String sCharset) {
        String title = "", album = "", artist = "";
        HashMap<Tag, String> hmTags = null;
        FileInputStream fis = null;
        try {
            fis = (FileInputStream) getContentResolver().openInputStream(uri);
            hmTags = BicycleTagEncoder.getTags(fis, sCharset);
            fis.close();
        } catch (UnknownFormatException ex) {
            Logger.getLogger(SongRecodeActivity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SongRecodeActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        title = hmTags.get(Tag.TITLE);
        album = hmTags.get(Tag.ALBUM);
        artist = hmTags.get(Tag.ARTIST);
        fillData(title, album, artist);
    }

    private void fillData(String title, String album, String artist) {
        EditText etTitle = (EditText) findViewById(R.id.Title);
        EditText etAlbum = (EditText) findViewById(R.id.Album);
        EditText etArtist = (EditText) findViewById(R.id.Artist);
        etTitle.setText(title);
        etAlbum.setText(album);
        etArtist.setText(artist);
    }

    private void updateArtist() {
        EditText etArtist = (EditText) findViewById(R.id.Artist);
        final String sArtist = etArtist.getText().toString();
        String selection = Media.ARTIST_ID + " = ?";
        String[] selectionArgs = new String[]{nArtistId.toString()};
        String[] projection = new String[]{Media._ID, Media.ARTIST_ID};

        Cursor c = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);

        final long[] arrIds = new long[c.getCount()];
        int nIndex = 0;

        while (c.moveToNext()) {
            arrIds[nIndex] = c.getLong(c.getColumnIndex(Media._ID));
            nIndex++;
        }
        c.close();

        final ProgressDialog dlg = new ProgressDialog(this);
        dlg.setMessage("Updating songs...");
        dlg.setCancelable(false);
        dlg.setMax(arrIds.length);
        dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dlg.show();

        new Thread(new Runnable() {

            public void run() {
                for (int i = 0; i < arrIds.length; i++) {
                    long nID = arrIds[i];
                    Uri uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, nID);
                    try {
                        updateTag(uri, BicycleTagEncoder.Tag.ARTIST, sArtist);
                    } catch (IOException ex) {
                        Logger.getLogger(SongRecodeActivity.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnknownFormatException ex) {
                        Logger.getLogger(SongRecodeActivity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dlg.incrementProgressBy(1);
                }
                dlg.cancel();
            }
        }).start();

    }

    /*Updates all songs with current albumID and sets
     * their album name to the value in the proper textbox
     * also should update the database entry for the album.
     */
    
    private void updateAlbum() {
        EditText etAlbum = (EditText) findViewById(R.id.Album);
        final String sAlbum = etAlbum.getText().toString();

        //Query the database for songs in this album
        String selection = Media.ALBUM_ID + " = ?";
        String[] selectionArgs = new String[]{nAlbumId.toString()};
        String[] projection = new String[]{Media._ID, Media.ALBUM_ID};

        Cursor c = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);

        //Get list of song IDs for further processing in a separate thread
        final long[] arrIds = new long[c.getCount()];
        int nIndex = 0;
        while (c.moveToNext()) {
            arrIds[nIndex] = c.getLong(c.getColumnIndex(Media._ID));
            nIndex++;
        }
        c.close();

        final ProgressDialog dlg = new ProgressDialog(this);
        dlg.setMessage("Updating songs...");
        dlg.setCancelable(false);
        dlg.setMax(arrIds.length);
        dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dlg.show();

        new Thread(new Runnable() {

            public void run() {
                for (int i = 0; i < arrIds.length; i++) {
                    long nID = arrIds[i];
                    Uri uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, nID);
                    try {
                        updateTag(uri, BicycleTagEncoder.Tag.ALBUM, sAlbum);
                    } catch (IOException ex) {
                        Logger.getLogger(SongRecodeActivity.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnknownFormatException ex) {
                        Logger.getLogger(SongRecodeActivity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dlg.incrementProgressBy(1);
                }
                dlg.cancel();
            }
        }).start();
    }

    private void updateTitle() throws FileNotFoundException, IOException {

        final ProgressDialog dlg = new ProgressDialog(this);
        dlg.setMessage("Processing song...");
        dlg.setCancelable(false);
        dlg.show();
        
        EditText etTitle = (EditText) findViewById(R.id.Title);
        final String sTitle = etTitle.getText().toString();
        
        new Thread(new Runnable() {

            public void run() {
                try {
                    DataUpdater.updateTitle(nSongId, sTitle, getContentResolver());
                    dlg.cancel();

                } catch (UnknownFormatException ex) {
                    Logger.getLogger(SongRecodeActivity.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SongRecodeActivity.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    private void copyFile(InputStream is, OutputStream os) throws IOException {
        byte[] buf = new byte[1048576];
        int nReadCount = 0;
        while ((nReadCount = is.read(buf)) != -1) {
            os.write(buf, 0, nReadCount);
        }
        is.close();
        os.close();
    }

    private void updateTag(Uri uri, BicycleTagEncoder.Tag tag, String value) throws IOException, UnknownFormatException {
        InputStream is = getContentResolver().openInputStream(uri);
        File tmp = File.createTempFile("TagEncoder", "temp");
        OutputStream os = new FileOutputStream(tmp);
        BicycleTagEncoder.updateTagValue(is, os, tag, value);

        os = getContentResolver().openOutputStream(uri);
        is = new FileInputStream(tmp);
        copyFile(is, os);

        tmp.delete();
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        String sCharset = (String) arg0.getAdapter().getItem(arg2);
        setSongData(this.songUri, sCharset);
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private class UpdateListener implements OnClickListener {

        private ButtonAction action;

        public UpdateListener(ButtonAction action) {
            this.action = action;
        }

        private void showMessage(String sMessage, DialogInterface.OnClickListener listener) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SongRecodeActivity.this);
            builder.setMessage(sMessage).setCancelable(false).setPositiveButton("Yes", listener).setNegativeButton("No", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        }

        public void onClick(View v) {

            String sMessage = "";

            switch (this.action) {
                case UPDATE_ALBUM:
                    sMessage = "Are you sure you want to update the album name?";
                    break;
                case UPDATE_ARTIST:
                    sMessage = "Are you sure you want to update the artist name?";
                    break;
                case UPDATE_TITLE:
                    sMessage = "Are you sure you want to update the song text?";
                    break;
            }
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    try {
                        switch (action) {
                            case UPDATE_ALBUM:
                                updateAlbum();
                                break;
                            case UPDATE_ARTIST:
                                updateArtist();
                                break;
                            case UPDATE_TITLE:
                                updateTitle();
                                break;
                        }
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Media.EXTERNAL_CONTENT_URI)));
                    } catch (IOException ex) {
                        new AlertDialog.Builder(SongRecodeActivity.this).setMessage("Failed to update media").
                                create().
                                show();
                    }
                }
            };
            showMessage(sMessage, listener);
        }
    }
}
