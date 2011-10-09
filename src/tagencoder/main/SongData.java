/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

/**
 *
 * @author seth
 */
public class SongData {
    private long nId;
    private String title;
    private String artist;
    private String album;

    public SongData(long nId, String title, String artist, String album) {
        this.nId = nId;
        this.title = title;
        this.artist = artist;
        this.album = album;
    }
    
    
    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }    
    
    public String getTitle() {
        return title;
    }
    
    public long getId(){
        return nId;
    }
    
}
