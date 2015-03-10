package tud.cnlab.wifriends.castle;

import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

/**
 * Created by Hariharan Gandhi, DSS Master Student, TU Darmstadt on "12/11/2014"
 * for the project "WiFriends"
 * Reference: http://lukieb.blogspot.de/2013/11/aes-encryptiondecryption-in-android.html*
 */
public class AESCastle {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Encrypting Own Profile with corresponding friends Key
     *
     * @param profile My Profile details
     * @param key     Corresponding Friend's Key
     * @return
     */
    public static byte[] EncryptProfile(byte[] profile, byte[] key) {

        try {
            System.out.println("Printing Key from Inside Encryptor: " + Arrays.toString(key));

            System.out.println("AES: Trying to Encrypt..!!!");
            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
            // Random iv
            SecureRandom rng = new SecureRandom();
            byte[] ivBytes = new byte[16];
            rng.nextBytes(ivBytes);

            cipher.init(true, new ParametersWithIV(new KeyParameter(key), ivBytes));
            byte[] outBuf = new byte[cipher.getOutputSize(profile.length)];

            int processed = cipher.processBytes(profile, 0, profile.length, outBuf, 0);
            processed += cipher.doFinal(outBuf, processed);

            byte[] outBuf2 = new byte[processed + 16];               // Make room for iv
            System.arraycopy(ivBytes, 0, outBuf2, 0, 16);            // Add iv
            System.arraycopy(outBuf, 0, outBuf2, 16, processed);     // Then the encrypted data
            System.out.println("AES: Encrypt Success..!!!");
            return outBuf2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Decrypt the given data with the given key
     *
     * @param friendProfile The data to decrypt
     * @param key           The key to decrypt with
     * @return The decrypted bytes
     */
    public static byte[] DecryptProfile(byte[] friendProfile, byte[] key) {

        // 16 bytes is the IV size for AES256
        try {

            System.out.println("AES: Trying to Decrypt..!!!");
            System.out.println("Printing Key from Inside Decryptor: " + Arrays.toString(key));

            PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
            byte[] ivBytes = new byte[16];
            System.arraycopy(friendProfile, 0, ivBytes, 0, ivBytes.length); // Get iv from data

            byte[] dataonly = new byte[friendProfile.length - ivBytes.length];
            System.arraycopy(friendProfile, ivBytes.length, dataonly, 0, friendProfile.length - ivBytes.length);


            System.out.println("Decrypt: Length of Byte[]: " + friendProfile.length);

            cipher.init(false, new ParametersWithIV(new KeyParameter(key), ivBytes));
            byte[] decrypted = new byte[cipher.getOutputSize(dataonly.length)];
            int len = cipher.processBytes(dataonly, 0, dataonly.length, decrypted, 0);
            len += cipher.doFinal(decrypted, len);
            System.out.println("AES: Decrypt Success..!!!");
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}




