/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
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
        
        String[] projection = new String[] {AudioColumns._ID, 
            AudioColumns.TITLE, 
            AudioColumns.ALBUM_ID, AudioColumns.ALBUM, 
            AudioColumns.ARTIST_ID, AudioColumns.ARTIST};
        String selection = AudioColumns._ID + " = ?";
        String[] selectionArgs = new String[] {Long.toString(nSongId)};
                
        Cursor c = this.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, 
                projection,
                selection,
                selectionArgs,
                null);
        c.moveToFirst();
        String title, album, artist;
        title = c.getString(c.getColumnIndex(AudioColumns.TITLE));
        album = c.getString(c.getColumnIndex(AudioColumns.ALBUM));
        artist = c.getString(c.getColumnIndex(AudioColumns.ARTIST));
        c.close();
        fillData(title, album, artist);
        
        ArrayAdapter<CharSequence> encodingAdapter = 
                ArrayAdapter.createFromResource(this, R.array.encodings, android.R.layout.simple_spinner_item);
        Spinner spinner = (Spinner)findViewById(R.id.Encoding);
        spinner.setAdapter(encodingAdapter);        
    }
    
    public void fillData(String title, String album, String artist) {
        EditText etTitle = (EditText)findViewById(R.id.Title);
        EditText etAlbum = (EditText)findViewById(R.id.Album);
        EditText etArtist = (EditText)findViewById(R.id.Artist);
        try {
            etTitle.setText(decode(title, "windows-1251"));
            etAlbum.setText(decode(album, "Cp1251"));
            etArtist.setText(decode(artist, "utf-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SongRecodeActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String decode(String input, String charsetName) throws UnsupportedEncodingException {        
        Charset charset = Charset.forName(charsetName);
        CharsetDecoder decoder = charset.newDecoder();
        byte[] in = input.getBytes(charsetName);
        byte[] out = new byte[in.length/2];
        for (int i = 1; i < in.length; i+=2) {
            out[i/2] = in[i];
        }
        ByteBuffer bbuf = ByteBuffer.wrap(out);
        String sReturn = null;
        try {
            CharBuffer cbuf = decoder.decode(bbuf);
            sReturn = cbuf.toString();
        } catch (CharacterCodingException ex) {
            Logger.getLogger(SongRecodeActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sReturn;
    }
}
