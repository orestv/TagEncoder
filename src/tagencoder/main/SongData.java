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
    private String sTitle;
    private String sPath;
    
    public SongData(long nId, String sTitle, String sPath) {
        this.nId = nId;
        this.sTitle = sTitle;
        this.sPath = sPath;
    }
    
    public String getTitle() {
        return sTitle;
    }
    
    public long getId(){
        return nId;
    }
    
    public String getPath(){
        return sPath;
    }
    
}
