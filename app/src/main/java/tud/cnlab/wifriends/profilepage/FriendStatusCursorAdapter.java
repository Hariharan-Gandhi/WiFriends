package tud.cnlab.wifriends.profilepage;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import tud.cnlab.wifriends.R;
import tud.cnlab.wifriends.WiFriends;
import tud.cnlab.wifriends.datahandlers.TblMyProfile;

/**
 * Created by Hariharan Gandhi, DSS Master Student, TU Darmstadt on "2/6/2015"
 * for the project "WiFriends"
 */
public class FriendStatusCursorAdapter extends CursorAdapter {
    /**
     * Recommended constructor.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     * @param flags   Flags used to determine the behavior of the adapter; may
     *                be any combination of {@link #FLAG_AUTO_REQUERY} and
     *                {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     */
    public FriendStatusCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_friend_status_list, parent, false);
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        {

            TextView viewUserName = (TextView) view.findViewById(R.id.userStatusHelper);
            TextView viewUserStatus = (TextView) view.findViewById(R.id.userStatus);
            ImageView viewUserPic = (ImageView) view.findViewById(R.id.status_profile_pic);

            String userName = cursor.getString(cursor.getColumnIndex(TblMyProfile.fUname));
            String userStatus = cursor.getString(cursor.getColumnIndex(TblMyProfile.fStatus));
            String friendsMac = cursor.getString(cursor.getColumnIndex(TblMyProfile.fMac));

            viewUserName.setText(userName + " says..");
            viewUserStatus.setText("\"" + userStatus + "\"");

            File profilePic = new File(WiFriends.WIFRIENDS_PATH + "/" +
                    WiFriends.FRIENDS_FOLDER + "/" + friendsMac + "/" + "prof_pic.jpg");

            if (profilePic.exists()) {
                viewUserPic.setImageBitmap(BitmapFactory.decodeFile(profilePic.getAbsolutePath()));
            } else {
                viewUserPic.setImageResource(R.drawable.default_profile_pic);
            }
        }

    }
}
