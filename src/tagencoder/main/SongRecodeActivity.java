/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import TagEncoderLib.BicycleTagEncoder;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author seth
 */
public class SongRecodeActivity extends Activity implements OnItemSelectedListener{

    private Uri songUri;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.songedit);
        Intent i = this.getIntent();
        this.songUri = i.getData();

        ArrayAdapter<CharSequence> encodingAdapter =
                ArrayAdapter.createFromResource(this, R.array.encodings, android.R.layout.simple_spinner_item);
        Spinner spinner = (Spinner) findViewById(R.id.Encoding);
        spinner.setAdapter(encodingAdapter);
        spinner.setOnItemSelectedListener(this);        
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

    public void fillData(String title, String album, String artist) {
        EditText etTitle = (EditText) findViewById(R.id.Title);
        EditText etAlbum = (EditText) findViewById(R.id.Album);
        EditText etArtist = (EditText) findViewById(R.id.Artist);
        etTitle.setText(title);
        etAlbum.setText(album);
        etArtist.setText(artist);
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        String sCharset = (String) arg0.getAdapter().getItem(arg2);
        setSongData(this.songUri, sCharset);
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
