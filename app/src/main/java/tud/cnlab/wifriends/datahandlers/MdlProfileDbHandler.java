package tud.cnlab.wifriends.datahandlers;

/**
 * Model Class for the User Profile in the SQLite Database
 *
 * @author  Hariharan Gandhi
 * @author Harini Gunabalan
 * @version 1.0
 */
public class MdlProfileDbHandler {

    private String USER_MAC;					// User MAC Address
	private String USER_NAME;					// User Name
	private String USER_ID;						// User ID
	private String ABOUT;						// About Me 
	private String STATUS;						// Current Status
	private String WEEKLY_HAPPY_EVENTS;			// Happy Event of the week
	private String WEEKLY_ANNOYED_EVENTS;		// Annoying event of the week
		
	// Empty Constructor	
	public MdlProfileDbHandler(){
		
	}
	
	// Constructor	
	public MdlProfileDbHandler(String uSER_MAC, String uSER_NAME, String uSER_ID, String aBOUT,
                               String sTATUS, String wEEKLY_HAPPY_EVENTS,
                               String wEEKLY_ANNOYED_EVENTS) {
        this.USER_MAC = uSER_MAC;
        this.USER_NAME = uSER_NAME;
        this.USER_ID = uSER_ID;
        this.ABOUT = aBOUT;
        this.STATUS = sTATUS;
        this.WEEKLY_HAPPY_EVENTS = wEEKLY_HAPPY_EVENTS;
        this.WEEKLY_ANNOYED_EVENTS = wEEKLY_ANNOYED_EVENTS;
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
	 * @param  MAC Address
	 */     
    public void setUSER_MAC(String uSER_MAC) {
        this.USER_MAC = uSER_MAC;
    }

	/**
	 * Getter Method to return User Name
	 *
	 * @return  USER_NAME
	 */
    public String getUSER_NAME() {
        return USER_NAME;
    }

	/**
	 * Setter Method to set the User NAme
	 *
	 * @param  USER_NAME
	 */ 
    public void setUSER_NAME(String uSER_NAME) {
        this.USER_NAME = uSER_NAME;
    }

	/**
	 * Getter Method to return User ID
	 *
	 * @return  USER_ID
	 */    
	public String getUSER_ID() {
		return USER_ID;
	}

	/**
	 * Setter Method to set the User ID
	 *
	 * @param  USER_ID
	 */ 
	public void setUSER_ID(String uSER_ID) {
		this.USER_ID = uSER_ID;
	}

	/**
	 * Getter Method to return About me
	 *
	 * @return  ABOUT 
	 */	
	public String getABOUT() {
		return ABOUT;
	}
	
	/**
	 * Setter Method to set About me 
	 *
	 * @param  ABOUT
	 */ 
	public void setABOUT(String aBOUT) {
		this.ABOUT = aBOUT;
	}

	/**
	 * Getter Method to return Current Status
	 *
	 * @return  STATUS
	 */
	public String getSTATUS() {
		return STATUS;
	}

	/**
	 * Setter Method to set the Current Status
	 *
	 * @param  STATUS
	 */ 
	public void setSTATUS(String sTATUS) {
		this.STATUS = sTATUS;
	}
	
	/**
	 * Getter Method to return Happy event of week
	 *
	 * @return  WEEKLY_HAPPY_EVENTS
	 */
	public String getWEEKLY_HAPPY_EVENTS() {
		return WEEKLY_HAPPY_EVENTS;
	}

	/**
	 * Setter Method to set Happy event of week
	 *
	 * @return  WEEKLY_HAPPY_EVENTS
	 */
	public void setWEEKLY_HAPPY_EVENTS(String wEEKLY_HAPPY_EVENTS) {
		this.WEEKLY_HAPPY_EVENTS = wEEKLY_HAPPY_EVENTS;
	}

	/**
	 * Getter Method to return Annoying event of the week
	 *
	 * @return  WEEKLY_ANNOYING_EVENT
	 */
	public String getWEEKLY_ANNOYED_EVENTS() {
		return WEEKLY_ANNOYED_EVENTS;
	}

	/**
	 * Setter Method to set Annoying event of the week
	 *
	 * @return  WEEKLY_ANNOYING_EVENT
	 */
	public void setWEEKLY_ANNOYED_EVENTS(String wEEKLY_ANNOYED_EVENTS) {
		this.WEEKLY_ANNOYED_EVENTS = wEEKLY_ANNOYED_EVENTS;
	}
	
	 /**
	  * Method returns All fields of this user profile class as String 
	  *
	  * @return  Concatenated fields as String
	  */ 
	  @Override
	   public String toString() {
		  String Profile = 
			"\n USER_MAC: " + this.USER_MAC +
            "\n USER_NAME: " + this.USER_NAME +
			"\n USER_ID: " + this.USER_ID+ 
			"\n ABOUT: " + this.ABOUT +
			"\n STATUS: " + this.STATUS +
			"\n WEEKLY_HAPPY_EVENTS: " + this.WEEKLY_HAPPY_EVENTS +
			"\n WEEKLY_ANNOYED_EVENTS: " + this.WEEKLY_ANNOYED_EVENTS; 
	       return Profile;
	   }
}
