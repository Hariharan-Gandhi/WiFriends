package tud.cnlab.wifriends.profilepage;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import tud.cnlab.wifriends.R;


public class BaseHome extends ActionBarActivity {

    public Toolbar toolbar;
    NavigationDrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
    }

    public int getLayout() {
        return R.layout.activity_base_home;
    }

    @Override
    public void onBackPressed() {
        if (!(drawerFragment.closeDrawer())) {
            super.onBackPressed();
        }
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
        // as you specify a parent activity in AndroidManifest.xml..
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
