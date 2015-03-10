package tud.cnlab.wifriends;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import tud.cnlab.wifriends.castle.AESCastle;
import tud.cnlab.wifriends.datahandlers.MdlFriendListDbHandler;
import tud.cnlab.wifriends.datahandlers.MdlProfileDbHandler;
import tud.cnlab.wifriends.datahandlers.TblMyProfile;

/**
 * The Class which manages the Exchange of Profile information and the Photo albums
 */
public class ProfileExchanger implements Runnable {

    private static final String TAG = "ProfileExchanger";

    String passPhrase = "Dick Beck";

    char[] passArray = passPhrase.toCharArray();
    MdlFriendListDbHandler oFriendInfo;
    AESCastle aesCastle;
    static MdlProfileDbHandler profileDbHandleRead;
    static TblMyProfile profileRead;
    static MdlProfileDbHandler profileJsonWrite;
    static TblMyProfile profileEdit;
    String friendPath;
    Context context = WiFriendsService.wf.getApplicationContext();
    private Socket socket = null;
    private Handler handler;
    private OutputStream oStream;


    public ProfileExchanger(Socket socket, Handler handler, MdlFriendListDbHandler oFriendInfo) {
        this.socket = socket;
        this.handler = handler;
        this.oFriendInfo = oFriendInfo;
    }

    @Override
    public void run() {


        try {

            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            DataInputStream dis = new DataInputStream(bis);

            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());  //BOS
            DataOutputStream dos = new DataOutputStream(bos);                               //DOS

            friendPath = WiFriendsService.WIFRIENDS_PATH + "/" + WiFriendsService.FRIENDS_FOLDER + "/" + oFriendInfo.getUSER_MAC();

            try {

                /*******************Encrypting My Profile JSON**********************/
                Log.d(TAG, "Starting Encryption of JSON");
                byte[] myProfile;
                profileDbHandleRead = new MdlProfileDbHandler();
                profileRead = new TblMyProfile(context);
                //profileRead = TblMyProfile.getInstance();
                profileDbHandleRead = profileRead.retrieveMyProfile();
                String JSON = createJSON(profileDbHandleRead);
                System.out.println("Before Encryption \n Priniting AESKEYobject : " + oFriendInfo);
                myProfile = AESCastle.EncryptProfile(JSON.getBytes("UTF-8"), oFriendInfo.getAES_KEY());
                System.out.println("Length after Encryption:" + myProfile.length);
                //sendMyProfile(myProfile);
                String myEncrptPath = WiFriendsService.WIFRIENDS_PATH + "/" +
                        WiFriendsService.MYPROFILE_FOLDER + "/temp/" +
                        "MyProfile";
                FileOutputStream fos = new FileOutputStream(myEncrptPath);
                fos.write(myProfile);
                fos.close();
                Log.d(TAG, "Encrypted File written to local My Profile Folder");

                /************************Encrypting Images***************************/

                String myPath = WiFriendsService.WIFRIENDS_PATH + "/" + WiFriendsService.MYPROFILE_FOLDER;
                File[] files = new File(myPath).listFiles();

                try {
                    for (File file : files) {
                        if (!file.isDirectory()) {
                            String name = file.getName();
                            System.out.println("File name being Encrypted:" + name);
                            //if (!"MyProfile".equals(name)) {                                        // All other image files
                            byte[] bFile = new byte[(int) file.length()];
                            byte[] bEncFile;
                            FileInputStream fis = new FileInputStream(file);
                            fis.read(bFile);                                            // Image file is read in bFile as Bytes
                            fis.close();
                            bEncFile = AESCastle.EncryptProfile(bFile, oFriendInfo.getAES_KEY());
                            System.out.println("Length of image after Encryption:" + bEncFile.length);

                            String myImagePath = WiFriendsService.WIFRIENDS_PATH + "/" +
                                    WiFriendsService.MYPROFILE_FOLDER + "/temp/" + name;
                            FileOutputStream fos1 = new FileOutputStream(myImagePath);
                            fos1.write(bEncFile);
                            fos1.close();
                            Log.d(TAG, "Encrypted File written to local temp folder with same name");

                        }
                    }

                    profileRead.close();
                } catch (Exception e) {
                    Log.d(TAG, "Error in encrypting image files");
                    e.printStackTrace();
                }

                /**
                 * Sending Encrypted Files*
                 *
                 */

                String sendPath = WiFriendsService.WIFRIENDS_PATH + "/" +
                        WiFriendsService.MYPROFILE_FOLDER + "/temp/";
                File[] sendFiles = new File(sendPath).listFiles();
                System.out.println(sendFiles.length);
                dos.writeInt(sendFiles.length);
                Log.d(TAG, "No. of files to be sent: " + sendFiles.length);

                try {
                    for (File file : sendFiles) {

                        // Write Individual File Length using DOS
                        long length = file.length();
                        dos.writeLong(length);
                        System.out.println("Length of encrypted file being sent:" + length);

                        // Write Individual File name using DOS - as UTF
                        String name = file.getName();
                        dos.writeUTF(name);
                        System.out.println("Name of encrypted file being sent:" + name);

                        FileInputStream fis = new FileInputStream(file);                            //FIS
                        BufferedInputStream bisLocal = new BufferedInputStream(fis);                //BIS

                        int theByte = 0;
                        // Write the actual file using BOS
                        while ((theByte = bisLocal.read()) != -1) bos.write(theByte);

                        Log.d(TAG, "Sending a file...");

                        bisLocal.close();
                        fis.close();
                    }

                    dos.flush();
                    bos.flush();

                } catch (Exception e) {
                    Log.d(TAG, "Error in sending encrypted files");
                }

                Log.d(TAG, "All files sent successfully");

                // Sending part of temp directory Ends!!
            } catch (Exception e) {

                Log.d(TAG, "Error in Encrypting my profile / sending files");
                e.printStackTrace();
            }

            System.out.println("Sent all files. Now Trying to READ");


/***********************Receiving Friends Encrypted files begin *********************************/

            try {
                File friendFile = new File(friendPath);
                if (!friendFile.exists()) {
                    friendFile.mkdirs();
                }
            } catch (Exception e) {
                Log.d(TAG, "Error in Directory creation for the friend!");
                e.printStackTrace();
            }

            try {
                Log.d(TAG, "Trying to Read the File Count...");
                System.out.println("Checking for Data input Stream: "+dis.available());
                int filesCount = dis.readInt();
                Log.d(TAG, "No. of files to be received from the Socket: " + filesCount);
                File[] files = new File[filesCount];

                for (int i = 0; i < filesCount; i++) {
                    Log.d(TAG, "Inside For loop: " + i + " th iteration");
                    long fileLength = dis.readLong();
                    String fileName = dis.readUTF();

                    files[i] = new File(friendPath + "/" + fileName);

                    FileOutputStream fos = new FileOutputStream(files[i]);
                    BufferedOutputStream bosLocal = new BufferedOutputStream(fos);

                    for (int j = 0; j < fileLength; j++) bosLocal.write(bis.read());

                    bosLocal.close();
                }
            } catch (Exception e) {
                Log.d(TAG, "Error in Receiving");
                e.printStackTrace();
            }


            Log.d(TAG, "Read all incoming files");


/*************Reading data code ends*************************************************************************/

        } catch (IOException e) {
            Log.d("MASTER", "MASTER Error");
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                System.out.println("Trying to Decrypt the Profile and Images after closing the Socket");

                /*************Decryption of Received Files begin*************************/
                File[] decryptFiles = new File(friendPath).listFiles();
                Log.d(TAG, "Decryption of received files begins");
                for (File file : decryptFiles) {
                    // Write Individual File name using DOS - as UTF
                    String name = file.getName();
                    if (!"MyProfile".equals(name)) {
                        //Decrypt Images

                        byte[] bEncFile = new byte[(int) file.length()];
                        FileInputStream fis = new FileInputStream(file);
                        fis.read(bEncFile);
                        fis.close();
                        byte[] bFile = AESCastle.DecryptProfile(bEncFile, oFriendInfo.getAES_KEY());

                        System.out.println("Length of image after Decryption:" + bFile.length);
                        if (friendPath.isEmpty() || friendPath == null) {
                            System.out.println("Friend path isnull");
                        }
                        if (name.isEmpty() || name == null) {
                            System.out.println("Name isnull");
                        }
                        String friendImagePath = friendPath + "/" + name;
                        FileOutputStream fos1 = new FileOutputStream(friendImagePath);
                        fos1.write(bFile);
                        fos1.close();
                        Log.d(TAG, "Decrypted File written to local Friend Profile Folder with same name");

                    } else {
                        byte[] bEncFile = new byte[(int) file.length()];
                        FileInputStream fis = new FileInputStream(file);
                        fis.read(bEncFile);
                        fis.close();
                        persistFriendsProfile(bEncFile);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to save the Received friends profile*
     * @param bEncFile
     */
    public void persistFriendsProfile(byte[] bEncFile) {

        String decryptedJSONProfile;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bytesRead = -1;
        try {

            byte[] profileToDecrypt = bEncFile;
            System.out.println("Length received for Decryption: " + profileToDecrypt.length);

            byte[] decryptedProfile = AESCastle.DecryptProfile(profileToDecrypt, oFriendInfo.getAES_KEY());
            decryptedJSONProfile = new String(decryptedProfile, "UTF-8");
            decryptedJSONProfile = decryptedJSONProfile.trim();

            System.out.println("**************************");
            System.out.println("Received JSON before Parsing: \n " + decryptedJSONProfile);

            parseJSON(decryptedJSONProfile);
            profileEdit = new TblMyProfile(context);
            //profileEdit = TblMyProfile.getInstance();
            profileEdit.updateProfile(profileJsonWrite);

            Log.d(TAG, "Friend Profile Saved Successfully");
            profileEdit.close();
        } catch (Exception e) {
            Log.d(TAG, "Error during Decryption:");
            e.printStackTrace();
        }


    }

    /**
     * Create JSON file of the own profile
     * @param profileJsonWrite
     * @return
     */
    public String createJSON(MdlProfileDbHandler profileJsonWrite) {

        GsonBuilder profileJSON = new GsonBuilder();
        profileJSON.setPrettyPrinting().serializeNulls();
        Gson gson = profileJSON.create();

        String JSON = gson.toJson(profileJsonWrite);

        System.out.println("Printing the JSON");
        System.out.println(JSON);

        return JSON;
    }

    /**
     * Parse the JSON using Google GSON to save it in DB*
     * @param JSON
     */
    public void parseJSON(String JSON) {

        GsonBuilder profileJSON = new GsonBuilder();
        profileJSON.setPrettyPrinting().serializeNulls();
        Gson gson = profileJSON.create();
        //Gson gson = new Gson();
        //JsonReader.setLenient(true);

        profileJsonWrite = gson.fromJson(JSON, MdlProfileDbHandler.class);

        System.out.println("Printing the Object");
        System.out.println(profileJsonWrite);

    }

}
