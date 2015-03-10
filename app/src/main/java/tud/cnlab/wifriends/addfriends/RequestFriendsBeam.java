package tud.cnlab.wifriends.addfriends;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;

import tud.cnlab.wifriends.R;

public class RequestFriendsBeam extends Activity implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

    protected TextView successMessageView;
    NfcAdapter mNfcAdapter;
    private String[] beamRequestInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_friends_beam);


        Intent intent = getIntent();

        beamRequestInfo = intent.getStringArrayExtra("FriendRequestInfo");

        successMessageView = (TextView) findViewById(R.id.friend_request_sent);

        Log.e("NFC", beamRequestInfo[0] + "+" + beamRequestInfo[1]);
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
     * Called to provide a {@link android.nfc.NdefMessage} to push.
     * <p/>
     * <p>This callback is usually made on a binder thread (not the UI thread).
     * <p/>
     * <p>Called when this device is in range of another device
     * that might support NDEF push. It allows the application to
     * create the NDEF message only when it is required.
     * <p/>
     * <p>NDEF push cannot occur until this method returns, so do not
     * block for too long.
     * <p/>
     * <p>The Android operating system will usually show a system UI
     * on top of your activity during this time, so do not try to request
     * input from the user to complete the callback, or provide custom NDEF
     * push UI. The user probably will not see it.
     *
     * @param event {@link android.nfc.NfcEvent} with the {@link android.nfc.NfcEvent#nfcAdapter} field set
     * @return NDEF message to push, or null to not provide a message
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Log.e("NFC", "On Create NDEF MESSAGE");
        String beamType = "FriendRequest";
        String myMac = beamRequestInfo[0];
        String myNickName = beamRequestInfo[1];
        Log.e("NFC", myMac + "+" + myNickName);

        NdefMessage msg = new NdefMessage(
                new NdefRecord[]{
                        createMimeRecord("application/tud.cnlab.wifriends", beamType.getBytes()),
                        createMimeRecord("application/tud.cnlab.wifriends", myMac.getBytes()),
                        createMimeRecord("application/tud.cnlab.wifriends", myNickName.getBytes())
                });
        return msg;
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //stuff that updates ui
                successMessageView.setText(" Friend Request Sent Successfully. \n Waiting for Acceptance");
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
        Log.e("RECORD:", mimeRecord.toString());

        return mimeRecord;
    }
}
