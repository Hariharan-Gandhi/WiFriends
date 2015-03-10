package tud.cnlab.wifriends.addfriends;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;

import tud.cnlab.wifriends.R;
import tud.cnlab.wifriends.datahandlers.MdlFriendListDbHandler;
import tud.cnlab.wifriends.datahandlers.TblFriendList;

/**
 * Created by Hariharan Gandhi, DSS Master Student, TU Darmstadt on "12/23/2014"
 * for the project "WiFriends"
 */
public class AddFriendsBeam extends Activity implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {
    protected TextView successMessageView;
    NfcAdapter mNfcAdapter;
    TextView textView;
    MdlFriendListDbHandler oAddFriend_send;
    MdlFriendListDbHandler oAddFriend_recv;
    TblFriendList oFriendInfo_send;
    TblFriendList oFriendInfo_recv;
    private String[] beamMacInfo;
    private String myMac;
    private String myFriendsMac;
    private String myFriendsName;

    private byte[] beamAesKeyInfo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends_beam);
        Intent intent = getIntent();

        beamMacInfo = intent.getStringArrayExtra("MACInfo");


        beamAesKeyInfo = intent.getByteArrayExtra("AESKeyInfo");

        oAddFriend_send = new MdlFriendListDbHandler();
        oFriendInfo_send = new TblFriendList(this);

        successMessageView = (TextView) findViewById(R.id.add_friends_friend_added);

        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
    }

    /**
     * Actual NDEF push
     * NDEF push cannot occur until this method returns, so do not block for too long.
     */

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        myMac = beamMacInfo[0];
        myFriendsMac = beamMacInfo[1];
        String beamType = "AcceptFriend";
        NdefMessage msg = new NdefMessage(
                new NdefRecord[]{
                        createMimeRecord("application/tud.cnlab.wifriends", beamType.getBytes()),
                        createMimeRecord("application/tud.cnlab.wifriends", myMac.getBytes()),
                        createMimeRecord("application/tud.cnlab.wifriends", beamAesKeyInfo)
                });
        return msg;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {

            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {


        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present

        String beamType = new String(msg.getRecords()[0].getPayload());

        switch (beamType) {
            case ("FriendRequest"):
                processFriendRequest(msg);
                break;

            case ("AcceptFriend"):
                acceptFriendRequest(msg);
                break;
        }


    }

    /**
     * Accept Friend Request and Get ready to beam*
     * @param msg
     */
    public void processFriendRequest(NdefMessage msg) {

        String[] beamMyInfo = {new String(msg.getRecords()[1].getPayload()),
                new String(msg.getRecords()[2].getPayload())};

        Intent beamFriendRequest = new Intent(this, AcceptFriendRequest.class);
        beamFriendRequest.putExtra("FriendRequestInfo", beamMyInfo);
        startActivity(beamFriendRequest);

    }

    /**
     * Process incoming msg with the MAC details and key
     * @param msg
     */
    public void acceptFriendRequest(NdefMessage msg) {

        successMessageView = (TextView) findViewById(R.id.add_friends_friend_added);
        oAddFriend_recv = new MdlFriendListDbHandler();
        oFriendInfo_recv = new TblFriendList(this);

        oAddFriend_recv.setUSER_MAC(new String(msg.getRecords()[1].getPayload()));
        oAddFriend_recv.setAES_KEY(msg.getRecords()[2].getPayload());
        if (oFriendInfo_recv.CreateFriendRecord(oAddFriend_recv)) {
            System.out.println("Successfully received and Added friend");
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //stuff that updates ui
                successMessageView.setText("Successfully Added Friend");
                successMessageView.append('\n' + "Friend's MAC : " + oAddFriend_recv.getUSER_MAC());
            }
        });
    }

    /**
     * Creates a custom MIME type encapsulated in an NDEF record
     *
     * @param mimeType
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    /**
     * Called on successful NDEF push.
     * <p/>
     * <p>This callback is usually made on a binder thread (not the UI thread).
     *
     * @param event {@link android.nfc.NfcEvent} with the {@link android.nfc.NfcEvent#nfcAdapter} field set
     */
    @Override
    public void onNdefPushComplete(NfcEvent event) {
        myFriendsName = beamMacInfo[2];
        //Save in your DB
        // Display the Saved New Friends Details

        oAddFriend_send.setUSER_MAC(myFriendsMac);
        oAddFriend_send.setAES_KEY(beamAesKeyInfo);
        oFriendInfo_send.CreateFriendRecord(oAddFriend_send);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //stuff that updates ui
                successMessageView.setText("Success...!! \n You are now Friend with " + "\"" + myFriendsName + "\"");
                successMessageView.append('\n' + "Friend's MAC : " + oAddFriend_send.getUSER_MAC());
            }
        });

    }


}
