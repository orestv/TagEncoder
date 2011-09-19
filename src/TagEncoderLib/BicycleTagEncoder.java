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

    private static HashMap<String, String> getTags(byte[] baTags, String sCharsetName) {
        HashMap<String, String> hmResult = new HashMap<String, String>();

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baTags));
        while (true) {
            try {
                if (dis.available() == 0) {
                    break;
                }
                byte[] baTagName = new byte[4];
                byte[] baTagLength = new byte[4];
                dis.read(baTagName);
                dis.read(baTagLength);
                dis.skip(3);
                int nTagLength = getSynchronizedIntegerValue(baTagLength) - 1;
                if (nTagLength < 0) {
                    break;
                }
                byte[] baTagValue = new byte[nTagLength];
                dis.read(baTagValue);

                String sName, sValue;
                sName = new String(baTagName);
                sValue = new String(baTagValue, sCharsetName);
                hmResult.put(sName, sValue);

            } catch (IOException ex) {
                System.out.println("asdfasdfasdf");
            }
        }

        return hmResult;
    }

    public static HashMap<String, String> getTags(InputStream fis, String sCharsetName) {
        try {

            byte[] baHeader = new byte[10];
            fis.read(baHeader);
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baHeader, 6, 4));
            byte[] baHeaderLength = new byte[4];
            dis.read(baHeaderLength);
            int nHeaderLength = BicycleTagEncoder.getSynchronizedIntegerValue(baHeaderLength);
            byte[] baTags = new byte[nHeaderLength];
            fis.read(baTags);
            fis.close();

            return BicycleTagEncoder.getTags(baTags, sCharsetName);

        } catch (IOException ex) {
            Logger.getLogger(BicycleTagEncoder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
