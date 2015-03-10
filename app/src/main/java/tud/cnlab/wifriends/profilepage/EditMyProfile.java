package tud.cnlab.wifriends.profilepage;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import tud.cnlab.wifriends.R;
import tud.cnlab.wifriends.WiFriends;
import tud.cnlab.wifriends.datahandlers.MdlProfileDbHandler;
import tud.cnlab.wifriends.datahandlers.TblMyProfile;

public class EditMyProfile extends BaseHome {
    protected static final String WIFRIENDS_DESTN_FOLDER = "WiFriends";
    Context context;
    WiFriends wFriends = new WiFriends();
    MdlProfileDbHandler profileDbHandleWrite;
    MdlProfileDbHandler profileDbHandleRead;
    TblMyProfile profileRead;
    TblMyProfile profileEdit;
    private EditText USER_NAME;
    private EditText USER_ID;
    private EditText ABOUT;
    private EditText STATUS;
    private EditText WEEKLY_HAPPY_EVENTS;
    private EditText WEEKLY_ANNOYED_EVENTS;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_profile);
        context = getApplicationContext();
        profileEdit = new TblMyProfile(this);
        profileRead = new TblMyProfile(this);
        profileDbHandleRead = new MdlProfileDbHandler();
        Button upload_image = (Button) findViewById(R.id.uploadImage);
        Button btn_cancel = (Button) findViewById(R.id.cancel);
        Button btn_save = (Button) findViewById(R.id.save);
        USER_NAME = (EditText) findViewById(R.id.e_name_field);
        USER_ID = (EditText) findViewById(R.id.e_user_field);
        ABOUT = (EditText) findViewById(R.id.e_about_field);
        STATUS = (EditText) findViewById(R.id.e_status_field);
        WEEKLY_HAPPY_EVENTS = (EditText) findViewById(R.id.e_happy_field);
        WEEKLY_ANNOYED_EVENTS = (EditText) findViewById(R.id.e_annoy_field);

        profileDbHandleRead = profileRead.retrieveMyProfile();
        imageView = (ImageView) findViewById(R.id.imageView1);
        File profilePic = new File(WiFriends.WIFRIENDS_PATH + "/" +
                WiFriends.MYPROFILE_FOLDER + "/" + "prof_pic.jpg");

        if (profilePic.exists()) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(profilePic.getAbsolutePath()));
        } else {
            imageView.setImageResource(R.drawable.default_profile_pic);
        }

        USER_NAME.setText(profileDbHandleRead.getUSER_NAME());
        USER_ID.setText(profileDbHandleRead.getUSER_ID());
        ABOUT.setText(profileDbHandleRead.getABOUT());
        STATUS.setText(profileDbHandleRead.getSTATUS());
        WEEKLY_HAPPY_EVENTS.setText(profileDbHandleRead.getWEEKLY_HAPPY_EVENTS());
        WEEKLY_ANNOYED_EVENTS.setText(profileDbHandleRead.getWEEKLY_ANNOYED_EVENTS());

	/*	profileDbHandleWrite = new MdlProfileDbHandler("Hariharan", "@hari17",*/
        /*		"I am Student at TUD", "Feeling Happy", "Good Scores", "Exams");*/


        upload_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                chooseImage();
            }
        });

        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        btn_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                saveProfile();
            }
        });
    }

    public void saveProfile() {

          /* Setting own MAC address in the WiFriends Shared Preferences */
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        String myMAC = sharedPref.getString(context.getResources().getString(R.string.sp_field_my_mac), null);

        profileDbHandleWrite = new MdlProfileDbHandler(
                myMAC,
                USER_NAME.getText().toString(),
                USER_ID.getText().toString(),
                ABOUT.getText().toString(),
                STATUS.getText().toString(),
                WEEKLY_HAPPY_EVENTS.getText().toString(),
                WEEKLY_ANNOYED_EVENTS.getText().toString());
        System.out.println("Printing Saving DATA: \n" + profileDbHandleWrite);
        profileEdit.updateMyProfile(profileDbHandleWrite);
        Toast.makeText(EditMyProfile.this, "Profile Saved Successfully",
                Toast.LENGTH_SHORT).show();

        this.finish();
    }

    @Override
    public int getLayout() {
        return (R.layout.activity_edit_profile);
    }

    /******************************EDIT PROFILE PIC CODE************************
    ***************CREATED BY HARINI********************************
     ***************DO NOT REMOVE****************************************/

    private static final int REQ_CODE_PICK_IMAGE = 1;

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    // reference: http://stackoverflow.com/questions/19985286/convert-content-uri-to-actual-path-in-android-4-4

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        //super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == EditMyProfile.REQ_CODE_PICK_IMAGE && data != null
                && data.getData() != null) {
            Uri _uri = data.getData();

            String filePath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filePath, "WiFriends");
            try {
                String WIFRIENDS_PATH = file.getCanonicalPath();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            if (!file.exists()) {
                file.mkdirs();
            }

            File file1 = new File(file, "MyProfile/prof_pic.jpg");

            Context context = getApplicationContext();
            String path = getPath(context, _uri);
            File myFile = new File(path);
            Log.e("UploadImage", "Path of the image: " + path);
            Log.e("UploadImage", "Path of the image: " + myFile);
            Log.e("UploadImage", "Path of the image: " + file1);
            try {
                copyFile(myFile, file1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Bitmap profileBmp = MediaStore.Images.Media.getBitmap(getContentResolver(), _uri);
                if (profileBmp != null) {
                    Bitmap scaled = decodeUri(_uri);
                    
                    imageView.setImageBitmap(scaled);
                }
            } catch (OutOfMemoryError e) {
                System.out.println("Out of memory Error!");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("2nd Exception!");
                e.printStackTrace();
            }
        }
    }

    void copyFile(File src, File dst) throws IOException {
        Log.e("UploadImage", "Inside Copy File");
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }

}
