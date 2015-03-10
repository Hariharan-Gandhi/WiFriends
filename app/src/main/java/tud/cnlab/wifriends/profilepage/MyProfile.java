package tud.cnlab.wifriends.profilepage;

import android.content.Context;
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

public class MyProfile extends BaseHome {

    WiFriends wFriends = new WiFriends();
    Context context;

    MdlProfileDbHandler profileDbHandleRead;
    TblMyProfile profileRead;

    TextView USER_NAME;
    TextView USER_ID;
    TextView ABOUT;
    TextView STATUS;
    TextView WEEKLY_HAPPY_EVENTS;
    TextView WEEKLY_ANNOYED_EVENTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my_profile);
        context = getApplicationContext();
        profileDbHandleRead = new MdlProfileDbHandler();
        profileRead = new TblMyProfile(this);

        ImageView PROFILE_PIC = (ImageView) findViewById(R.id.profile_pic);

        File profilePic = new File(WiFriends.WIFRIENDS_PATH + "/" +
                WiFriends.MYPROFILE_FOLDER + "/" + "prof_pic.jpg");

        if (profilePic.exists()) {
            PROFILE_PIC.setImageBitmap(BitmapFactory.decodeFile(profilePic.getAbsolutePath()));
        } else {
            PROFILE_PIC.setImageResource(R.drawable.default_profile_pic);
        }

        USER_NAME = (TextView) findViewById(R.id.my_name_field);
        USER_ID = (TextView) findViewById(R.id.user_id_field);
        ABOUT = (TextView) findViewById(R.id.about_field);
        STATUS = (TextView) findViewById(R.id.status_field);
        WEEKLY_HAPPY_EVENTS = (TextView) findViewById(R.id.happy_field);
        WEEKLY_ANNOYED_EVENTS = (TextView) findViewById(R.id.annoy_field);

    }

    @Override
    public int getLayout() {
        return R.layout.activity_enhanced_my_profile;
    }

    @Override
    public void onResume() {
        profileDbHandleRead = profileRead.retrieveMyProfile();

        System.out.println("Printing Objects from MYProfile: " + profileDbHandleRead);

        USER_NAME.setText(profileDbHandleRead.getUSER_NAME());
        USER_ID.setText(profileDbHandleRead.getUSER_ID());
        ABOUT.setText(profileDbHandleRead.getABOUT());
        STATUS.setText(profileDbHandleRead.getSTATUS());
        WEEKLY_HAPPY_EVENTS.setText(profileDbHandleRead.getWEEKLY_HAPPY_EVENTS());
        WEEKLY_ANNOYED_EVENTS.setText(profileDbHandleRead.getWEEKLY_ANNOYED_EVENTS());
        super.onResume();

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
