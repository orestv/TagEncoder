/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ovoloshchuk
 */
public class MainMenuActivity extends Activity implements OnClickListener, Thread.UncaughtExceptionHandler{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        
        ((Button)findViewById(R.id.btnSongList)).setOnClickListener(this);
        
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void onClick(View arg0) {
        Intent i = new Intent();
        switch(arg0.getId()) {
            case R.id.btnSongList:
                i.setClass(this, SongListActivity.class);
                break;
        }
        startActivity(i);
    }

    public void uncaughtException(Thread arg0, Throwable arg1) {
        Toast t = new Toast(this);
        t.setText(arg1.toString());
        t.show();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainMenuActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.finish();        
    }

}
