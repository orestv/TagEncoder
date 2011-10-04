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
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.MediaColumns;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author seth
 */
public class DataUpdater {
    
    private static long[] updateDatabase(Tag tag, long nID, String sValue, ContentResolver resolver) {
        String sValueColumnName = null;
        String sIDColumnName = null;
        switch(tag) {
            case Title:
                sValueColumnName = Media.TITLE;
                sIDColumnName = Media._ID;
                break;
            case Album:
                sValueColumnName = Media.ALBUM;
                sIDColumnName = Media.ALBUM_ID;
                break;
            case Artist:
                sValueColumnName = Media.ARTIST;
                sIDColumnName =  Media.ARTIST_ID;
                break;
        }
        ContentValues values = new ContentValues(1);
        values.put(sValueColumnName, sValue);
        String sWhere = sIDColumnName + " = ?";
        String[] arrArgs = new String[]{Long.toString(nID)};
        resolver.update(Media.EXTERNAL_CONTENT_URI, values, sWhere, arrArgs);
        
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
    
    public static void updateAlbum(long nAlbumId, String sAlbum, ContentResolver resolver) throws IOException, UnknownFormatException {
        long[] ids = updateDatabase(Tag.Album, nAlbumId, sAlbum, resolver);    
        for (int nIndex = 0; nIndex < ids.length; nIndex++) {
            updateTag(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, ids[nIndex]), Tag.Album, sAlbum, resolver);
        }
    }
    
    public static void updateArtist(long nArtistId, String sArtist, ContentResolver resolver) throws IOException, UnknownFormatException {
        long[] ids = updateDatabase(Tag.Artist, nArtistId, sArtist, resolver);
         
        for (int nIndex = 0; nIndex < ids.length; nIndex++) {
            updateTag(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, ids[nIndex]), BicycleTagEncoder.Tag.Artist, sArtist, resolver);            
        }
        
    }
    
    public static void updateTitle(long nSongId, String sTitle, ContentResolver resolver) throws IOException, UnknownFormatException {
        updateDatabase(Tag.Title, nSongId, sTitle, resolver);
        Uri uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, nSongId);
        updateTag(uri, BicycleTagEncoder.Tag.Title, sTitle, resolver);
    }
    
    public static void updateTag(Uri uri, BicycleTagEncoder.Tag tag, String value, ContentResolver resolver) throws IOException, UnknownFormatException {
        InputStream is = resolver.openInputStream(uri);
        OutputStream os = resolver.openOutputStream(uri);
        BicycleTagEncoder.updateTagValue(is, os, tag, value);
        is.close();
        os.close();
    }
    
}
