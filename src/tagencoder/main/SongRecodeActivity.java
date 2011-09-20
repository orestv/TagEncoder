/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import TagEncoderLib.BicycleTagEncoder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author seth
 */
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
        HashMap<String, String> hmTags = null;
        FileInputStream fis = null;
        try {
            fis = (FileInputStream) getContentResolver().openInputStream(uri);
            hmTags = BicycleTagEncoder.getTags(fis, sCharset);
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(SongRecodeActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        title = hmTags.get("TIT2");
        album = hmTags.get("TALB");
        artist = hmTags.get("TPE1");
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
        String sArtist = etArtist.getText().toString();
        ContentValues values = new ContentValues();
        values.put(Media.ARTIST, sArtist);
        getContentResolver().update(Media.EXTERNAL_CONTENT_URI,
                values,
                Media.ARTIST_ID + " = ?",
                new String[]{nArtistId.toString()});
    }

    private void updateAlbum() {
    }

    private void updateTitle() throws FileNotFoundException, IOException {
        EditText etTitle = (EditText) findViewById(R.id.Title);
        String sTitle = etTitle.getText().toString();
        ContentValues values = new ContentValues();
        values.put(Media.TITLE, sTitle);
        getContentResolver().update(Media.EXTERNAL_CONTENT_URI,
                values,
                Media._ID + " = ?",
                new String[]{nSongId.toString()});
        InputStream is = getContentResolver().openInputStream(songUri);
        File tmp = File.createTempFile("sdf", "asdf");
        FileOutputStream fos = new FileOutputStream(tmp);
        BicycleTagEncoder.updateTagValue(is, fos, BicycleTagEncoder.Tag.TITLE, sTitle);
        fos.close();
        is.close();
        OutputStream os = getContentResolver().openOutputStream(songUri);
        FileInputStream fis = new FileInputStream(tmp);
        byte[] buf = new byte[40960];
        int nReadCount = 0;
        while ((nReadCount = fis.read(buf)) != -1) {
            os.write(buf, 0, nReadCount);
        }
        os.close();
        fis.close();
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
                    } catch (IOException ex) {
                        new AlertDialog.Builder(SongRecodeActivity.this).
                                setMessage("Failed to update media").
                                create().
                                show();
                    }
                }
            };
            showMessage(sMessage, listener);
        }
    }
}
