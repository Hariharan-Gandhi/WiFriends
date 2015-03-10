package tud.cnlab.wifriends.castle;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * AES Key Generator class: Generates AES Key of length 256
 *
 * @author  Hariharan Gandhi
 * @author Harini Gunabalan
 * @version 1.0
 */
public class AESKeyGenerator {

    private static final int outputKeyLength = 256;			// Key Length

    static {
        Security.addProvider(new BouncyCastleProvider());	// Initialize spongy castle API
    }

    
/**
 * Method to generate the AES Key
 *
 * @return  The AES key (as bytes) is returned
 */    
    public static byte[] GenerateKey() {

        KeyGenerator keyGenerator = null; 
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGenerator.init(outputKeyLength);
        SecretKey key = keyGenerator.generateKey();
        byte[] keyBytes = key.getEncoded();

        return keyBytes;

    }

}
