/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TagEncoderLib;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ovoloshchuk
 */
public class BicycleTagEncoder {

    public static int getSynchronizedIntegerValue(byte[] bt) {
        int nResult = (int) bt[bt.length - 1];

        for (int i = 1; i < bt.length; i++) {
            int nIndex = (bt.length - i - 1);
            if (i > 0) {
                nResult += ((int) bt[nIndex]) << (i * 8 - 1);
            }
        }
        return nResult;
    }

    private static HashMap<String, String> getTags(byte[] baTags, String sCharsetName) throws IOException {
        HashMap<String, String> hmResult = new HashMap<String, String>();

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baTags));
        while (true) {
            if (dis.available() == 0) {
                break;
            }
            byte[] baTagName = new byte[4];
            byte[] baTagLength = new byte[4];
            dis.read(baTagName);
            dis.read(baTagLength);

            //Technical value - some flags and encoding specification
            dis.skip(3);
            int nTagLength = getSynchronizedIntegerValue(baTagLength) - 1;
            if (nTagLength <= 0) {
                break;
            }
            byte[] baTagValue = new byte[nTagLength];
            dis.read(baTagValue);

            String sName, sValue;
            sName = new String(baTagName);
            sValue = new String(baTagValue, sCharsetName);
            hmResult.put(sName, sValue);
        }

        return hmResult;
    }
    
    private static byte[] getHeaderBytes(InputStream is) throws IOException {
        //Get ID3 header - 10 bytes
        byte[] baHeader = new byte[10];
        is.read(baHeader);
        
        //Get last 4 bytes of the header - ID3 header length.
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baHeader, 6, 4));
        byte[] baHeaderLength = new byte[4];
        dis.read(baHeaderLength);
        //Parse synchronized int.
        int nHeaderLength = BicycleTagEncoder.getSynchronizedIntegerValue(baHeaderLength);
        //Read the whole header.
        byte[] baTags = new byte[nHeaderLength];
        is.read(baTags);
        return baTags;
    }

    public static HashMap<String, String> getTags(InputStream is, String sCharsetName) throws IOException {
        byte[] baTags = getHeaderBytes(is);
        return BicycleTagEncoder.getTags(baTags, sCharsetName);
    }
}
