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
import android.widget.EditText;
import android.widget.TextView;

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
    }
    
    public void fillData(String title, String album, String artist) {
        EditText etTitle = (EditText)findViewById(R.id.Title);
        EditText etAlbum = (EditText)findViewById(R.id.Album);
        EditText etArtist = (EditText)findViewById(R.id.Artist);
        
        etTitle.setText(title);
        etAlbum.setText(album);
        etArtist.setText(artist);
    }
}
