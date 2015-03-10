package tud.cnlab.wifriends.datahandlers;

import java.util.Arrays;

/**
 * Model Class for the Friend List in the SQLite Database
 *
 * @author  Hariharan Gandhi
 * @author Harini Gunabalan
 * @version 1.0
 */
public class MdlFriendListDbHandler {

    private String USER_MAC;		// MAC Address of the friend
    private byte[] AES_KEY;			// AES Key to connect to the friend

    // Empty Constructor    
    public MdlFriendListDbHandler() {

    }

    // Constructor    
    public MdlFriendListDbHandler(String USER_MAC, byte[] AES_KEY) {
        this.USER_MAC = USER_MAC;
        this.AES_KEY = AES_KEY;
    }

	/**
	 * Getter Method to return MAC Address
	 *
	 * @return  MAC Address
	 */  
    public String getUSER_MAC() {
        return USER_MAC;
    }

	/**
	 * Setter Method to set the MAC Address
	 *
	 * @param  USER_MAC Address
	 */    
    public void setUSER_MAC(String USER_MAC) {
        this.USER_MAC = USER_MAC;
    }

	/**
	 * Getter Method to return AES Key to connect with the friend
	 *
	 * @return  MAC Address
	 */
    public byte[] getAES_KEY() {
        return AES_KEY;
    }

	/**
	 * Setter Method to set the AES Key
	 *
	 * @param  AES_KEY
	 */       
    public void setAES_KEY(byte[] AES_KEY) {
        this.AES_KEY = AES_KEY;
    }

    
	/**
	 * Method returns User MAC and corresponding AES Key 
	 *
	 * @return  Concatenated User MAC and AES Key as String
	 */    
    @Override
    public String toString() {
        return "MdlFriendListDbHandler{" +
                "USER_MAC='" + USER_MAC + '\'' +
                ", AES_KEY='" + Arrays.toString(AES_KEY) + '\'' +
                '}';
    }
}
