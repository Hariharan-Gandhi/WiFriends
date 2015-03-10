package tud.cnlab.wifriends.profilepage;

import android.content.Context;
import android.os.Bundle;

import tud.cnlab.wifriends.R;

public class about_help extends BaseHome {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        //setContentView(R.layout.activity_about_help);
    }


    @Override
    public int getLayout() {
        return R.layout.activity_about_help;
    }


}
