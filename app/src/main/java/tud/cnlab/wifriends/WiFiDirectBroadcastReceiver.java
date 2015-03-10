
package tud.cnlab.wifriends;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    public SharedPreferences sharedPref;
    private WifiP2pManager manager;
    private WifiManager wfmanager;
    private Channel channel;
    //private Activity activity;

    /**
     * @param manager  WifiP2pManager system service
     * @param channel  Wifi p2p channel
     * //@param activity activity associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel) {
                                      // ,Activity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
       // this.activity = activity;
    }

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(WiFriends.TAG, action);

        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // we are connected with the other device, request connection
                // info to find group owner IP
                Log.d(WiFriends.TAG,
                        "Connected to p2p network. Requesting network details");
                
                WiFriendsService.runFromBroadcastReceiver();
                /*manager.requestConnectionInfo(channel,
                        (ConnectionInfoListener) activity);*/
            } else {
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
                .equals(action)) {

            WifiP2pDevice device = (WifiP2pDevice) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

            String myMac = device.deviceAddress;
            Log.d(WiFriends.TAG, "Device status: " + device.status);
            Log.d(WiFriends.TAG, "Device Address: " + myMac);
            //WF.setMyMac(myMac);

            sharedPref = context.getSharedPreferences(context.getString(R.string.sp_file_name), Context.MODE_PRIVATE);
            String MY_MAC_ADDRESS = sharedPref.getString(context.getString(R.string.sp_field_my_mac), null);
            Log.d("TRY MAC~~", "My MAC is : " + MY_MAC_ADDRESS);


            if (MY_MAC_ADDRESS == null || MY_MAC_ADDRESS != myMac) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(context.getString(R.string.sp_field_my_mac), myMac);
                editor.commit();
            }
        }
    }
}
