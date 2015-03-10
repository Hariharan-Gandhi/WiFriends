package tud.cnlab.wifriends.profilepage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

import tud.cnlab.wifriends.Constants;
import tud.cnlab.wifriends.R;
import tud.cnlab.wifriends.WiFriends;
import tud.cnlab.wifriends.WiFriendsService;
import tud.cnlab.wifriends.datahandlers.MdlProfileDbHandler;
import tud.cnlab.wifriends.datahandlers.TblMyProfile;


public class HomeScreen extends BaseHome {

    TblMyProfile profileRead;
    MdlProfileDbHandler profileDbHandleRead;
    private EditText STATUS;
    private ImageView STATUS_SAVE_EDIT;
    private ImageView MY_PROFILE_PIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        STATUS = (EditText) findViewById(R.id.statusView);
        STATUS_SAVE_EDIT = (ImageView) findViewById(R.id.editStatus);
        MY_PROFILE_PIC = (ImageView) findViewById(R.id.profilePic);

        profileRead = new TblMyProfile(this);
        profileDbHandleRead = new MdlProfileDbHandler();
        profileDbHandleRead = profileRead.retrieveMyProfile();

        MY_PROFILE_PIC.setPadding(8, 8, 8, 8);

        Intent startIntent = new Intent(this,
                WiFriendsService.class);
        startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(startIntent);
        
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        Boolean isServiceActive = sharedPref.getBoolean(getString(R.string.sp_field_service_state), false);
        if (isServiceActive) {
            Toast.makeText(this,
                    "Master Service is already running.",
                    Toast.LENGTH_LONG).show();

        } else {

            //startService(startIntent);
        }

        
    }
    
    @Override
    public void onResume(){
        super.onResume();
        File profilePic = new File(WiFriends.WIFRIENDS_PATH + "/" +
                WiFriends.MYPROFILE_FOLDER + "/" + "prof_pic.jpg");

        if (profilePic.exists()) {
            MY_PROFILE_PIC.setImageBitmap(BitmapFactory.decodeFile(profilePic.getAbsolutePath()));
        } else {
            MY_PROFILE_PIC.setImageResource(R.drawable.ic_default_profile_pic);
        }


        String formatStatus = "\"" + profileDbHandleRead.getSTATUS() + "\"";
        STATUS.setText(formatStatus);
        STATUS.setEnabled(false);

        STATUS_SAVE_EDIT.setTag("savedMode");
        STATUS_SAVE_EDIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Listener", "Clicked EditText");

                if ((STATUS_SAVE_EDIT.getTag() == "savedMode")) {
                    STATUS_SAVE_EDIT.setImageResource(android.R.drawable.ic_menu_save);
                    STATUS_SAVE_EDIT.setTag("editMode");
                    STATUS.setEnabled(true);
                    STATUS.getText().clear();
                    STATUS.setAlpha(0.8f);
                } else {
                    STATUS.setEnabled(false);
                    STATUS_SAVE_EDIT.setTag("savedMode");
                    STATUS_SAVE_EDIT.setImageResource(android.R.drawable.ic_menu_edit);
                    STATUS.setAlpha(1.0f);

                    if (STATUS.getText().toString().isEmpty()) {
                        String formatStatus = "\"" + profileDbHandleRead.getSTATUS() + "\"";
                        STATUS.setText(formatStatus);
                        Toast.makeText(HomeScreen.this, "No change to status", Toast.LENGTH_SHORT).show();
                    } else {
                        saveProfile();
                        String formatStatus = "\"" + STATUS.getText() + "\"";
                        STATUS.setText(formatStatus);
                    }
                }
            }
        });
        displayFriendsStatus();
        
    }

    public void saveProfile() {

        profileDbHandleRead.setSTATUS(STATUS.getText().toString());
        profileRead.updateMyProfile(profileDbHandleRead);
        Toast.makeText(HomeScreen.this, "Status Updated",
                Toast.LENGTH_SHORT).show();

    }

    private void displayFriendsStatus() {

        Cursor friendStatusCursor = profileRead.RetrieveFriends();

        FriendStatusCursorAdapter friendStatusCursorAdapter = new FriendStatusCursorAdapter(this, friendStatusCursor, 0);

        ListView listView = (ListView) findViewById(R.id.friends_status_list);

        listView.setAdapter(friendStatusCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent displayFriend = new Intent(HomeScreen.this, FriendsProfile.class);

                Cursor cursor1 = (Cursor) parent.getItemAtPosition(position);
                String friendsMac = cursor1.getString(cursor1.getColumnIndex(profileRead.fMac));

                displayFriend.putExtra("MACInfo", friendsMac);
                startActivity(displayFriend);


            }
        });


    }

    @Override
    public int getLayout() {
        return R.layout.activity_home_screen;

    }
}
