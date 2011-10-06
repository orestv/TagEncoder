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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author seth
 */
public class DataUpdater {

    private final static class ConnectionClient implements MediaScannerConnectionClient {

        MediaScannerConnection conn = null;
        private String[] paths = null;
        private int nProcessedFileCount = 0;
        private Object FileCountLock = new Object();

        public ConnectionClient(String[] ids) {
            this.paths = ids;
        }

        private void fileProcessed() {
            synchronized (FileCountLock) {
                nProcessedFileCount++;
                if (nProcessedFileCount == paths.length) {
                    conn.disconnect();
                }
            }
        }

        public void onMediaScannerConnected() {
            for (int i = 0; i < paths.length; i++) {
                String path = paths[i];
                conn.scanFile(path, null);
            }
        }

        public void onScanCompleted(String arg0, Uri arg1) {
            fileProcessed();
        }
    }

    private static void updateDatabase(long[] IDs, Context context) {
        ConnectionClient client = new ConnectionClient(getPaths(IDs, context.getContentResolver()));
        MediaScannerConnection conn = new MediaScannerConnection(context, client);
        client.conn = conn;
        conn.connect();
    }

    private static String[] getPaths(long[] IDs, ContentResolver resolver) {
        String[] projection = new String[]{Media._ID, Media.DATA};
        StringBuilder sbSelection = new StringBuilder();
        String[] selectionArgs = new String[IDs.length];
        for (int i = 0; i < IDs.length; i++) {
            sbSelection.append(Media._ID + " = ?");
            if (i < IDs.length - 1) {
                sbSelection.append(" OR ");
            }

            selectionArgs[i] = Long.toString(IDs[i]);
        }
        Cursor c = resolver.query(Media.EXTERNAL_CONTENT_URI,
                projection,
                sbSelection.toString(),
                selectionArgs,
                null);
        String[] paths = new String[IDs.length];
        int nIndex = 0;
        while (c.moveToNext()) {
            paths[nIndex] = c.getString(c.getColumnIndex(Media.DATA));
            nIndex++;
        }
        c.close();
        return paths;
    }

    public static long[] getIDs(Tag tag, long nID, ContentResolver resolver) {
        String sIDColumnName = null;
        switch (tag) {
            case Title:
                sIDColumnName = Media._ID;
                break;
            case Album:
                sIDColumnName = Media.ALBUM_ID;
                break;
            case Artist:
                sIDColumnName = Media.ARTIST_ID;
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
        long[] ids = getIDs(Tag.Album, nAlbumId, resolver);
        for (int nIndex = 0; nIndex < ids.length; nIndex++) {
            updateTag(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, ids[nIndex]), Tag.Album, sAlbum, context);
        }
        updateDatabase(ids, context);
    }

    public static void updateArtist(long nArtistId, String sArtist, Context context) throws IOException, UnknownFormatException {
        ContentResolver resolver = context.getContentResolver();
        long[] ids = getIDs(Tag.Artist, nArtistId, resolver);

        for (int nIndex = 0; nIndex < ids.length; nIndex++) {
            updateTag(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, ids[nIndex]), BicycleTagEncoder.Tag.Artist, sArtist, context);
        }
        updateDatabase(ids, context);
    }

    public static void updateTitle(long nSongId, String sTitle, Context context) throws IOException, UnknownFormatException {
        ContentResolver resolver = context.getContentResolver();
        //updateDatabase(Tag.Title, nSongId, sTitle, resolver);
        Uri uri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, nSongId);
        updateTag(uri, BicycleTagEncoder.Tag.Title, sTitle, context);
        updateDatabase(new long[]{nSongId}, context);
    }

    public static void updateTag(Uri uri, BicycleTagEncoder.Tag tag, String value, Context context) throws IOException, UnknownFormatException {
        InputStream is = context.getContentResolver().openInputStream(uri);
        File f = File.createTempFile("tagEncoder", "temp");
        OutputStream os = new FileOutputStream(f);
        BicycleTagEncoder.updateTagValue(is, os, tag, value);
        is.close();
        os.close();

        try {
            is = new FileInputStream(f);
            os = context.getContentResolver().openOutputStream(uri);

            byte[] buf = new byte[1<<19];
            int nReadCount = -1;
            while ((nReadCount = is.read(buf)) != -1) {
                os.write(buf, 0, nReadCount);
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            is.close();
            os.close();   
            f.delete();
        }
        
        updateDatabase(new long[]{ContentUris.parseId(uri)}, context);
    }
}
