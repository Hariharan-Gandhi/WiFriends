package tud.cnlab.wifriends.profilepage;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import tud.cnlab.wifriends.R;
import tud.cnlab.wifriends.datahandlers.TblMyProfile;

public class FriendsList extends BaseHome {

    TblMyProfile tblMyProfile;

    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tblMyProfile = new TblMyProfile(this);

        displayFriendList();

    }

    @Override
    public int getLayout() {
        return (R.layout.activity_friends_list);

    }

    public void displayFriendList() {

        Cursor friendListCursor = tblMyProfile.RetrieveFriends();

        FriendListCursorAdapter friendListCursorAdapter = new FriendListCursorAdapter(this, friendListCursor, 0);

        ListView listView = (ListView) findViewById(R.id.friendList);

        listView.setAdapter(friendListCursorAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent displayFriend = new Intent(FriendsList.this, FriendsProfile.class);

                Cursor cursor1 = (Cursor) parent.getItemAtPosition(position);
                String friendsMac = cursor1.getString(cursor1.getColumnIndex(tblMyProfile.fMac));

                displayFriend.putExtra("MACInfo", friendsMac);
                startActivity(displayFriend);


            }
        });


    }


}
