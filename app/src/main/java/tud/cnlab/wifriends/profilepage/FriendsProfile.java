package tud.cnlab.wifriends.profilepage;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import tud.cnlab.wifriends.R;
import tud.cnlab.wifriends.WiFriends;
import tud.cnlab.wifriends.datahandlers.MdlProfileDbHandler;
import tud.cnlab.wifriends.datahandlers.TblMyProfile;

public class FriendsProfile extends BaseHome {

    Context context;

    MdlProfileDbHandler profileDbHandleRead;
    TblMyProfile profileRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_friends_profile);

        context = getApplicationContext();
        profileDbHandleRead = new MdlProfileDbHandler();
        profileRead = new TblMyProfile(this);

        TextView USER_NAME = (TextView) findViewById(R.id.f_name_field);
        TextView USER_ID = (TextView) findViewById(R.id.f_user_id_field);
        TextView ABOUT = (TextView) findViewById(R.id.f_about_field);
        TextView STATUS = (TextView) findViewById(R.id.f_status_field);
        TextView WEEKLY_HAPPY_EVENTS = (TextView) findViewById(R.id.f_happy_field);
        TextView WEEKLY_ANNOYED_EVENTS = (TextView) findViewById(R.id.f_annoy_field);

        Intent intent = getIntent();

        String friendsMac = intent.getStringExtra("MACInfo");

        profileDbHandleRead = profileRead.retrieveProfile(friendsMac);

        ImageView PROFILE_PIC = (ImageView) findViewById(R.id.f_profile_pic);

        File profilePic = new File(WiFriends.WIFRIENDS_PATH + "/" +
                WiFriends.FRIENDS_FOLDER + "/" + friendsMac + "/" + "prof_pic.jpg");

        if (profilePic.exists()) {
            PROFILE_PIC.setImageBitmap(BitmapFactory.decodeFile(profilePic.getAbsolutePath()));
        } else {
            PROFILE_PIC.setImageResource(R.drawable.default_profile_pic);
        }

        System.out.println("Printing Profile Details: " + profileDbHandleRead);

        USER_NAME.setText(profileDbHandleRead.getUSER_NAME());
        USER_ID.setText(profileDbHandleRead.getUSER_ID());
        ABOUT.setText(profileDbHandleRead.getABOUT());
        STATUS.setText(profileDbHandleRead.getSTATUS());
        WEEKLY_HAPPY_EVENTS.setText(profileDbHandleRead.getWEEKLY_HAPPY_EVENTS());
        WEEKLY_ANNOYED_EVENTS.setText(profileDbHandleRead.getWEEKLY_ANNOYED_EVENTS());
    }

    @Override
    public int getLayout() {
        return R.layout.activity_enhanced_friends_profile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (id == R.id.action_settings) {
                Toast.makeText(this, "Hey you just hit " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
