package tud.cnlab.wifriends.profilepage;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import tud.cnlab.wifriends.R;
import tud.cnlab.wifriends.addfriends.AddFriends;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {

    public static final String PREF_FILE_NAME = "testpref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    ListView listView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View containerView;
    private boolean isDrawerOpened = false;


    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Locate:", "OnCreate");
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Locate:", "OnCreateView");

        View layout;

        layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        ImageView coverPic = (ImageView) layout.findViewById(R.id.coverPic);
        coverPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                if (!(getActivity().getClass().getSimpleName().equals("HomeScreen"))) {
                    Log.e("TEST", "SAME");
                    startActivity(new Intent(getActivity(), HomeScreen.class));
                }

            }
        });

        listView = (ListView) layout.findViewById(R.id.list);
        listView.setAdapter(new NavigatorListViewAdapter(getActivity()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mDrawerLayout.closeDrawers();
                        if (!(getActivity().getClass().getSimpleName().equals("MyProfile"))) {
                            Log.e("TEST", "SAME");
                            startActivity(new Intent(getActivity(), MyProfile.class));
                        }
                        break;
                    case 1:
                        mDrawerLayout.closeDrawers();
                        if (!(getActivity().getClass().getSimpleName().equals("EditMyProfile"))) {
                            Log.e("TEST", "SAME");
                            startActivity(new Intent(getActivity(), EditMyProfile.class));
                        }
                        break;
                    case 2:
                        mDrawerLayout.closeDrawers();
                        if (!(getActivity().getClass().getSimpleName().equals("FriendsList"))) {
                            Log.e("TEST", "SAME");
                            startActivity(new Intent(getActivity(), FriendsList.class));
                        }
                        break;
                    case 3:
                        mDrawerLayout.closeDrawers();
                        if (!(getActivity().getClass().getSimpleName().equals("AddFriends"))) {
                            startActivity(new Intent(getActivity(), AddFriends.class));
                        }
                        break;
                    case 4:
                        Toast.makeText(getActivity(), "Settings under development", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(getActivity(), Settings.class));
                        break;
                    case 5:
                        mDrawerLayout.closeDrawers();
                        if (!(getActivity().getClass().getSimpleName().equals("about_help"))) {
                            startActivity(new Intent(getActivity(), about_help.class));
                        }
                        break;
                    case 6:
                        mDrawerLayout.closeDrawers();
                        if (!(getActivity().getClass().getSimpleName().equals("about"))) {
                            startActivity(new Intent(getActivity(), about.class));
                        }
                        break;
                    default:
                        mDrawerLayout.closeDrawers();
                        break;
                }
            }
        });
        return layout;
    }

    public boolean closeDrawer() {
        if (isDrawerOpened) {
            mDrawerLayout.closeDrawers();
            return true;
        }
        return false;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                isDrawerOpened = true;
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);
                isDrawerOpened = false;
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
//                if(slideOffset<0.6) {
//                    toolbar.setAlpha(1 - slideOffset);
//                }
            }
        };
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(containerView);
        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }
}
