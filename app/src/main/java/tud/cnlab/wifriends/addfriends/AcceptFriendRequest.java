package tud.cnlab.wifriends.addfriends;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import tud.cnlab.wifriends.R;
import tud.cnlab.wifriends.castle.AESKeyGenerator;

public class AcceptFriendRequest extends Activity {

    private String friendsMac;
    private String friendsName;

    private SharedPreferences sharedPref;
    private String MY_MAC_ADDRESS;
    private int MY_ADD_FRIENDS_PIN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_friend_request);

        TextView friendRequest = (TextView) findViewById(R.id.read_friend_request);
        Button acceptFriend = (Button) findViewById(R.id.accept);
        Button declineFriend = (Button) findViewById(R.id.decline);

        Intent intent = getIntent();

        friendsMac = intent.getStringArrayExtra("FriendRequestInfo")[0];
        friendsName = intent.getStringArrayExtra("FriendRequestInfo")[1];

        friendRequest.setText(friendsName + " has sent you a Friend Request. \n " +
                "Would you like to Add? ");

        sharedPref = this.getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        MY_MAC_ADDRESS = sharedPref.getString(getString(R.string.sp_field_my_mac), null);

        declineFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        acceptFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MY_MAC_ADDRESS == null) {
                    Toast.makeText(AcceptFriendRequest.this, "Your WiFi P2P Mac_id is not set. " +
                            "\n Please enable WiFi Direct and Retry", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ((sharedPref.getString("NickName", "")).equals("")) {
                    System.out.println("No Nick Name");
                    promptNewNickName();
                } else {
                    checkPin();
                }
            }
        });
    }

    private void checkPin() {
        if ((MY_ADD_FRIENDS_PIN = sharedPref.getInt("PIN", 0)) == 0) {
            System.out.println("PIN is 0");
            promptNewPin();
        } else {
            promptPin();
        }
    }

    private void promptNewNickName() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View viewPromptNewNickName = layoutInflater.inflate(R.layout.prompt_new_nick_name, null);

        AlertDialog.Builder alertNewNickName = new AlertDialog.Builder(this);

        alertNewNickName.setView(viewPromptNewNickName);

        final EditText newNickName = (EditText) viewPromptNewNickName.findViewById(R.id.new_nick_name_field);

        alertNewNickName
                .setTitle("Your Nick Name")
                .setTitle("Enter a Name that your Friend knows")
                .setCancelable(false)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = newNickName.getText().toString();
                        if (newName.isEmpty()) {
                            Toast.makeText(AcceptFriendRequest.this, "Name cannot be Empty", Toast.LENGTH_SHORT).show();
                            promptNewNickName();
                        } else {
                            saveSharedPreference("NickName", newName);
                            checkPin();
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create().show();
    }

    private void promptNewPin() {

        System.out.println("Inside prompt new pin");

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View viewPromptNewPin = layoutInflater.inflate(R.layout.prompt_new_pin, null);

        AlertDialog.Builder alertNewPin = new AlertDialog.Builder(this);

        alertNewPin.setView(viewPromptNewPin);

        final EditText newPin = (EditText) viewPromptNewPin.findViewById(R.id.new_pin_field);
        final EditText retryPin = (EditText) viewPromptNewPin.findViewById(R.id.retype_pin_field);

        alertNewPin
                .setTitle("New PIN for Add Friends")
                .setCancelable(false)
                .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPinVal = newPin.getText().toString();
                        String retryPinVal = retryPin.getText().toString();
                        if (newPinVal.isEmpty() || retryPinVal.isEmpty()) {
                            Toast.makeText(AcceptFriendRequest.this, "PINs cannot be Empty", Toast.LENGTH_SHORT).show();
                            promptNewPin();
                        } else {

                            int newPinNo = Integer.parseInt(newPinVal);
                            int retypePinNo = Integer.parseInt(retryPinVal);
                            if (newPinNo != retypePinNo) {

                                Toast.makeText(AcceptFriendRequest.this, "PINs do not match or cannot be Empty", Toast.LENGTH_SHORT).show();
                                promptNewPin();

                            } else {
                                saveSharedPreference("PIN", Integer.parseInt(newPin.getText().toString()));
                                promptPin();
                            }
                        }

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertNewPin.create().show();
    }

    private void promptPin() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View viewPromptPin = layoutInflater.inflate(R.layout.prompt_pin, null);

        AlertDialog.Builder alertPin = new AlertDialog.Builder(this);

        alertPin.setView(viewPromptPin);
        final String proceed = "Key in the PIN to Add Friend Request";
        final EditText myPin = (EditText) viewPromptPin.findViewById(R.id.pin_field);

        alertPin
                .setTitle("PIN for Add Friends")
                .setCancelable(false)
                .setMessage(proceed)
                .setPositiveButton("Add Friend", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (myPin.getText().toString().isEmpty()) {
                            Toast.makeText(AcceptFriendRequest.this, "PINs cannot be left Empty", Toast.LENGTH_SHORT).show();
                        } else {
                            int myPinNo = Integer.parseInt(myPin.getText().toString());
                            MY_ADD_FRIENDS_PIN = sharedPref.getInt("PIN", 0);
                            if (myPinNo != MY_ADD_FRIENDS_PIN) {
                                final String error = "You have entered an Invalid PIN";

                                AlertDialog.Builder alertRetryPin = new AlertDialog.Builder(AcceptFriendRequest.this);

                                alertRetryPin
                                        .setTitle("Error")
                                        .setCancelable(false)
                                        .setMessage(error)
                                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                promptPin();
                                            }
                                        })
                                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .show();
                            } else {
                                acceptFriends();

                            }
                        }
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create().show();

    }

    private void saveSharedPreference(String key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private void saveSharedPreference(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void acceptFriends() {

        SharedPreferences sharedPref = AcceptFriendRequest.this.getSharedPreferences(
                getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        String myMacAddress = sharedPref.getString(
                getResources().getString(R.string.sp_field_my_mac), null);

        Toast.makeText(AcceptFriendRequest.this, R.string.add_friends_start_beam,
                Toast.LENGTH_SHORT).show();

        String[] beamMacInfo = {myMacAddress, friendsMac, friendsName};

        Intent beamAddFriends = new Intent(AcceptFriendRequest.this, AddFriendsBeam.class);
        beamAddFriends.putExtra("MACInfo", beamMacInfo);
        beamAddFriends.putExtra("AESKeyInfo", generateKey());
        startActivity(beamAddFriends);
    }

    public byte[] generateKey() {

        return AESKeyGenerator.GenerateKey();
    }

}
