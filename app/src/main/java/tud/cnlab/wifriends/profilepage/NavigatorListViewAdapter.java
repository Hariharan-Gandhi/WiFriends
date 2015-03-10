package tud.cnlab.wifriends.profilepage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import tud.cnlab.wifriends.R;

/**
 * Created by Hariharan Gandhi, DSS Master Student, TU Darmstadt on "1/27/2015"
 * for the project "Material-Design-Test-App-master"
 */
public class NavigatorListViewAdapter extends BaseAdapter {

    private Context mContext;
    // references to our icons
    private Integer[] mImageIds = {
            R.drawable.ic_my_profile,
            R.drawable.ic_edit_profile,
            R.drawable.ic_friend_list,
            R.drawable.ic_add_friend,
            R.drawable.ic_settings,
            R.drawable.ic_help,
            R.drawable.ic_about};
    // references to our Labels
    private Integer[] mLabelIds = {
            R.string.myprofile,
            R.string.editprofile,
            R.string.friendslist,
            R.string.addfriend,
            R.string.settings,
            R.string.help,
            R.string.about};

    public NavigatorListViewAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mImageIds.length;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View listView;

        if (convertView == null) {

            listView = new View(mContext);

            listView = inflater.inflate(R.layout.activity_navigator_row_list, null);

            ImageView imageView = (ImageView) listView
                    .findViewById(R.id.navigator_icon);
            TextView textView = (TextView) listView
                    .findViewById(R.id.navigator_name);

            textView.setText(mLabelIds[position]);
            imageView.setImageResource(mImageIds[position]);
            imageView.setPadding(8, 0, 8, 8);

        } else {
            listView = (View) convertView;
        }

        return listView;
    }
}
