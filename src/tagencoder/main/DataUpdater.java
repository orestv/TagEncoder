/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import TagEncoderLib.BicycleTagEncoder;
import TagEncoderLib.BicycleTagEncoder.Tag;
import TagEncoderLib.BicycleTagEncoder.UnknownFormatException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author seth
 */
public class DataUpdater {
    
    private static void updateDatabase(long[] IDs, Context context) {
        MediaScannerConnection conn = new MediaScannerConnection(context, new MediaScannerConnectionClient() {

            public void onMediaScannerConnected() {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void onScanCompleted(String arg0, Uri arg1) {
                Log.d("MediaScanner", "Scan complete: " + arg0);
            }
        });
        conn.connect();
        for (int i = 0; i < IDs.length; i++) {
            long id = IDs[i];
            Uri uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, id);
            conn.scanFile(uri.getEncodedPath(), "*/*");
        }
    }
    
    private static long[] getIDs(Tag tag, long nID, String sValue, ContentResolver resolver) {
        String sIDColumnName = null;
        switch(tag) {
            case Title:
                sIDColumnName = Media._ID;
                break;
            case Album:
                sIDColumnName = Media.ALBUM_ID;
                break;
            case Artist:
                sIDColumnName =  Media.ARTIST_ID;
                break;
        }
        String sWhere = sIDColumnName + " = ?";
        String[] arrArgs = new String[]{Long.toString(nID)};
        
        String[] projection = new String[]{Media._ID};
        Cursor c = resolver.query(Media.EXTERNAL_CONTENT_URI, projection, sWhere, arrArgs, null);
        long[] ids = new long[c.getCount()];
        int nIndex = 0;
        while (c.moveToNext()) {
            ids[nIndex] = c.getLong(c.getColumnIndex(Media._ID));
            nIndex++;
        }
        c.close();
        return ids;
    }
    
    public static void updateAlbum(long nAlbumId, String sAlbum, Context context) throws IOException, UnknownFormatException {
        ContentResolver resolver = context.getContentResolver();
        long[] ids = getIDs(Tag.Album, nAlbumId, sAlbum, resolver);    
        for (int nIndex = 0; nIndex < ids.length; nIndex++) {
            updateTag(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, ids[nIndex]), Tag.Album, sAlbum, resolver);
        }
    }
    
    public static void updateArtist(long nArtistId, String sArtist, Context context) throws IOException, UnknownFormatException {
        ContentResolver resolver = context.getContentResolver();
        long[] ids = getIDs(Tag.Artist, nArtistId, sArtist, resolver);
         
        for (int nIndex = 0; nIndex < ids.length; nIndex++) {
            updateTag(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, ids[nIndex]), BicycleTagEncoder.Tag.Artist, sArtist, resolver);            
        }
        
    }
    
    public static void updateTitle(long nSongId, String sTitle, Context context) throws IOException, UnknownFormatException {
        ContentResolver resolver = context.getContentResolver();
        //updateDatabase(Tag.Title, nSongId, sTitle, resolver);
        Uri uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, nSongId);
        updateTag(uri, BicycleTagEncoder.Tag.Title, sTitle, resolver);
        updateDatabase(new long[]{nSongId}, context);
    }
    
    public static void updateTag(Uri uri, BicycleTagEncoder.Tag tag, String value, ContentResolver resolver) throws IOException, UnknownFormatException {
        InputStream is = resolver.openInputStream(uri);
        byte[] newData = BicycleTagEncoder.updateTagValue(is, tag, value);
        is.close();
        OutputStream os = resolver.openOutputStream(uri);
        os.write(newData);
        os.close();
    }    
}
