/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import TagEncoderLib.BicycleTagEncoder;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Media;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author seth
 */
public class SongRecodeActivity extends Activity {

    private long nSongId = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.songedit);
        Intent i = this.getIntent();
        Uri uri = i.getData();
        this.nSongId = ContentUris.parseId(uri);

        String title = "", album = "", artist = "";
        FileInputStream fis = null;
        try {
            fis = (FileInputStream) getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SongRecodeActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        HashMap<String, String> hmTags = BicycleTagEncoder.getTags(fis, "CP1251");
        title = hmTags.get("TIT2");
        album = hmTags.get("TALB");
        artist = hmTags.get("TPE1");
        fillData(title, album, artist);

        ArrayAdapter<CharSequence> encodingAdapter =
                ArrayAdapter.createFromResource(this, R.array.encodings, android.R.layout.simple_spinner_item);
        Spinner spinner = (Spinner) findViewById(R.id.Encoding);
        spinner.setAdapter(encodingAdapter);
    }

    public void fillData(String title, String album, String artist) {
        EditText etTitle = (EditText) findViewById(R.id.Title);
        EditText etAlbum = (EditText) findViewById(R.id.Album);
        EditText etArtist = (EditText) findViewById(R.id.Artist);
        etTitle.setText(title);
        etAlbum.setText(album);
        etArtist.setText(artist);
    }
}
