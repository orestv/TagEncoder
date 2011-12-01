/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tagencoder.main;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author ovoloshchuk
 */
public interface CharsetUpdateableAdapter {
    public void setCharset(String charset) throws UnsupportedEncodingException;
}
