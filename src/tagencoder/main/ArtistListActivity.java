package tagencoder.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ArtistListActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);   
        Intent i = new Intent(this, SongListActivity.class);
        startActivity(i);                
    }
}
