/**
 * Reference: http://lukieb.blogspot.de/2013/11/aes-encryptiondecryption-in-android.html*
 */

package tud.cnlab.wifriends.BluetoothKeyExchange;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import tud.cnlab.wifriends.R;
import tud.cnlab.wifriends.WiFriendsService;
import tud.cnlab.wifriends.datahandlers.MdlFriendListDbHandler;
import tud.cnlab.wifriends.datahandlers.TblFriendList;

public class GenerateStoreKey extends ActionBarActivity {

    SecretKey key;
    Context context;

    EditText mac;

    private static final String KEY_FILE = "Local.key";
    
    File profilePic = new File(WiFriendsService.WIFRIENDS_PATH + "/" + "Local.key");

    MdlFriendListDbHandler oAddFriend_send;
    TblFriendList oFriendInfo_send;

    public static SecretKey GenerateKey() {

        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGenerator.init(256);
        SecretKey key = keyGenerator.generateKey();

        return key;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_store_key);
        context = getApplicationContext();
        mac = (EditText) findViewById(R.id.mac);

        oAddFriend_send = new MdlFriendListDbHandler();
        oFriendInfo_send = new TblFriendList(this);
    }

    public void generateKey(View view) {

        System.out.println("Generating Key");

        key = GenerateKey();

        System.out.println("Generate Key: " + key.getEncoded().toString());
        StoreMyData(key);

    }

    public Boolean StoreMyData(SecretKey key) {

        System.out.println("Storing the Key in file");

        if (key == null) {
            System.out.println("Null key not storing");
            return false;
        }

        WriteData(key.getEncoded(), "mydata.key");

        return true;
    }

    /**
     * Write the given data to private storage
     *
     * @param data     The data to store
     * @param fileName The filename to store the data in
     */
    private void WriteData(byte[] data, String fileName) {
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(profilePic);
            if(fOut==null){
                System.out.println("fout is null");
            }else{
                System.out.println("fout is not null");
            }
            
            fOut.write(data);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDB(View view) {

        String mMAC = mac.getText().toString();


        
        try {
            byte[] loclKey = ReadData("mydata.key");
            
            if (loclKey != null) {
                System.out.println("local Key: " + Arrays.toString(loclKey));
            } else {
                System.out.println("Local key is null");
            }

            oAddFriend_send.setUSER_MAC(mMAC);
            oAddFriend_send.setAES_KEY(loclKey);

            System.out.println("Friend record:\n" + oAddFriend_send);
            
            oFriendInfo_send.CreateFriendRecord(oAddFriend_send);
        } catch (IOException e) {
            e.printStackTrace();
        }




    }


    private byte[] ReadData(String filename) throws IOException {
        System.out.println("inside read data");
        
        byte[] key = new byte[5096];
        Arrays.fill(key, (byte) 0);
        FileInputStream fOut = null;
        
        try {
            fOut = new FileInputStream(profilePic);
            /*fOut = context.openFileInput(filename);*/

            if(fOut==null){
                System.out.println("fout read is null");
            }else{
                System.out.println("fout read is not null");
            }
            
            int length = fOut.read(key);

            System.out.println("key read length:" + length);
            
            byte[] key2 = new byte[length];
            System.arraycopy(key, 0, key2, 0, length);
            fOut.close();
            return key2;
        } catch (FileNotFoundException e) {

            System.out.println("inside the catch");
            return null;
        }
    }
}
