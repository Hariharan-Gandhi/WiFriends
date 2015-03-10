package tud.cnlab.wifriends;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import tud.cnlab.wifriends.castle.AESCastle;
import tud.cnlab.wifriends.datahandlers.MdlFriendListDbHandler;
import tud.cnlab.wifriends.datahandlers.TblFriendList;
import tud.cnlab.wifriends.profilepage.HomeScreen;


/**
 * <p>Starting Point of the Application. This service registers a Wi-Fi P2p service and
 * perform discovery over Wi-Fi p2p network for the same service broadcast. When a service from
 * our app is discovered, it verifies if he/she is a my friend. If a friend, then it initiates a
 * connection request. Meanwhile it also checks for any invitation for connect and authorizes if
 * the invitation came from a Friend. Once connected(either as a Server or a Client), app exchanges
 * the Encrypted Version of profile information and profile picture. The app also hosts several
 * activities for viewing profile, friends profile, friends list.</p>
 * <p/>
 * <p/>
 * References:http://developer.android.com/training/connect-devices-wirelessly/nsd-wifi-direct.html
 * https://android.googlesource.com/platform/development/+/master/samples/WiFiDirectServiceDiscovery/src/com/example/android/wifidirect?autodive=0
 * http://lukieb.blogspot.de/2013/11/aes-encryptiondecryption-in-android.html* * *
 */


public class WiFriendsService extends Service
        implements
        Handler.Callback {

    public static final String TAG = "WiFriendsDebug";
    // TXT RECORD properties
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_WiFriends";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    public static final String WIFRIENDS_DESTN_FOLDER = "WiFriends";
    public static final String MYPROFILE_FOLDER = "MyProfile";
    public static final String FRIENDS_FOLDER = "Friends";
    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MESSAGE_SENT = 0x400 + 3;
    public static final int MY_HANDLE = 0x400 + 2;
    static final int SERVER_PORT = 4545;
    public static String TXTRECORD_PROP_LASTUPDATED;
    public static String WIFRIENDS_PATH;
    public static String MYPROFILE_PATH;

    /* Static way to get application Context in NON-ACTIVITY Class*/
    public static WiFriendsService wf;
    public static Boolean isServiceActive = false;
    static TblFriendList oMyFriend;
    static MdlFriendListDbHandler oIsMyFriend;
    static WifiP2pInfo p2pInfo_temp;
    private static WifiP2pManager manager;
    private static WifiP2pManager.Channel channel;
    private static Handler handler = new Handler();
    private static boolean INITIATED_BY_ME = false;
    private final IntentFilter intentFilter = new IntentFilter();
    public WifiManager wfManager;
    public LinkedHashSet<WiFiP2pService> discoveredItems = new LinkedHashSet<WiFiP2pService>();
    public SharedPreferences sharedPref;
    AESCastle aesC = new AESCastle();
    SharedPreferences.Editor editor;
    private BroadcastReceiver receiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    private TextView statusTxtView;

    public static void runFromBroadcastReceiver() {
        manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                runOnConnectionInfoAvailable(info);
            }
        });

    }

    public static Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public static void runOnConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        p2pInfo_temp = p2pInfo;

        boolean check = p2pInfo.groupFormed;

        /*
         * The group owner accepts connections using a server socket and then spawns a
         * client socket for every client. This is handled by {@code GroupOwnerSocketHandler}
         *
         */

        /* TODO Add logic to restrict incoming connections if they are not your friend */
        /**
         * INITIATED_BY_ME : used to check if the connection request was initiated by self or is it an Invitation from other
         */
        if (!INITIATED_BY_ME) {
            //appendStatus("Connected to incoming service");

            //oMyFriend = new TblFriendList(WiFriendsService.this);
            System.out.println("I did not Initiate");
            manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    System.out.println("Group Info must be available");

                    if (group.isGroupOwner()) {
                        System.out.println("Yes I am the Group Owner");

                        Collection<WifiP2pDevice> groupClients = group.getClientList();
                        WifiP2pDevice groupClient = groupClients.iterator().next();
                        oIsMyFriend = oMyFriend.CheckFriendship(groupClient.deviceAddress);
                        if (!(oIsMyFriend.getUSER_MAC() == null)) {
                            callAsGroupOwner();
                        } else {
                            Log.d(TAG, "Sorry..! You are not my friend yet");
                        }
                    } else {
                        WifiP2pDevice groupOwner = group.getOwner();
                        oIsMyFriend = oMyFriend.CheckFriendship(groupOwner.deviceAddress);
                        callAsClient();
                    }
                }
            });

        } else {
            //appendStatus("Connected to service");
            if (p2pInfo.isGroupOwner) {
                callAsGroupOwner();

            } else {
                callAsClient();
            }

        }
    }

    public static void callAsGroupOwner() {
        Thread handler = null;

        Log.d(TAG, "Connected as group owner");
        //appendStatus("Connected as group owner");
        try {
            System.out.println("Life: " + oIsMyFriend);
            handler = new GroupOwnerSocketHandler(
                    getHandler(), oIsMyFriend);
            handler.start();
        } catch (IOException e) {
            Log.d(TAG,
                    "Failed to create a server thread - " + e.getMessage());
            return;
        }
    }

    public static void callAsClient() {
        Thread handler = null;
        Log.d(TAG, "Connected as peer");
        //appendStatus("Connected as Peer");
        //Log.d(TAG, "GO Address: " + p2pInfo.groupOwnerAddress.toString()+'\n'+p2pInfo.groupOwnerAddress.getHostAddress());

        Log.i(TAG, "Sleep before launching client thread to avoid race conditions...");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        handler = new ClientSocketHandler(
                getHandler(),
                p2pInfo_temp.groupOwnerAddress,
                oIsMyFriend);
        handler.start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wf = this;

        sharedPref = this.getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.sp_field_service_state), isServiceActive);
        editor.commit();

        //intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        String filePath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filePath, WIFRIENDS_DESTN_FOLDER);
        try {
            WIFRIENDS_PATH = file.getCanonicalPath();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if (!file.exists()) {
            file.mkdirs();
        }

        File file1 = new File(file, MYPROFILE_FOLDER);
        if (!file1.exists()) {
            file1.mkdirs();
            File temp = new File(file1, "temp");
            temp.mkdirs();
        }

        File file2 = new File(file, FRIENDS_FOLDER);
        if (!file2.exists()) {
            file2.mkdirs();
        }

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        wfManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        oMyFriend = new TblFriendList(this);

    }

    /**
     * Registers a local Wi-Fi P2p Service and then initiates a service discovery throught the method <b>discoverService()</b>
     */
    private void startRegistrationAndDiscovery() {
        Map<String, String> record = new HashMap<>();

        record.put(TXTRECORD_PROP_AVAILABLE, "visible");

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
                SERVICE_INSTANCE, SERVICE_REG_TYPE, record);

        manager.addLocalService(channel, service, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //appendStatus("Added Local Service");
            }

            @Override
            public void onFailure(int error) {
                //appendStatus("Failed to add a service");
            }
        });

   /*     Log.d(TAG, "Start: Testing RSA Pub/Pri Key");
        Log.d(TAG, "AES: Calling Key Manager");
        aesC.AESKeyGenerator();
        Log.d(TAG, "End: Testing RSA Pub/Pri Key");*/

        discoverService();

    }

    /**
     *
     */
    public void discoverService() {


        if (manager == null) {
            Log.e(TAG, "Manager is null");
            return;
        }
        
        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */
        manager.setDnsSdResponseListeners(channel,
                new WifiP2pManager.DnsSdServiceResponseListener() {

                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, WifiP2pDevice srcDevice) {

                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {

                            Log.d(TAG, "onWifriensServiceAvailable " + instanceName);
                            Log.d(TAG, "onWifriendsServiceAvailable " + srcDevice.deviceAddress);
                            // update the List of Peers
                            //TODO

                            WiFiP2pService service = new WiFiP2pService();
                            service.device = srcDevice;
                            service.instanceName = instanceName;
                            service.serviceRegistrationType = registrationType;

                            discoveredItems.add(service);
                            Log.d(TAG, "List Count " + discoveredItems.size());

                            if (discoveredItems.isEmpty()) {
                                Log.d(TAG, "No devices found");
                                return;
                            } else {
                                Log.d(TAG, "Starting foundAnyFriend");
                                System.out.println("Printing List of Devices:" + discoveredItems.toString());

                                Iterator<WiFiP2pService> friendsNearby = discoveredItems.iterator();
                                //while (friendsNearby.hasNext()) {
//                                    foundAnyFriend(friendsNearby.next());
                                //  friendsNearby.remove();
                                //}
                                foundAnyFriend(service);
                                Log.d("TAG", "Finished work on all nearby devices. Calling Discover Service Again");
                                //discoverService();

                                Log.d(TAG, "Ending foundAnyFriend");

                            }

                        }

                    }
                }, new WifiP2pManager.DnsSdTxtRecordListener() {

                    /**
                     * A new TXT record is available. Pick up the advertised
                     * buddy name.
                     */
                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> record,
                            WifiP2pDevice device) {
                        Log.d(TAG,
                                device.deviceName + " is "
                                        + record.get(TXTRECORD_PROP_AVAILABLE));

                        //TODO Logic to include "TXTRECORD_PROP_LASTUPDATED" to validate
                    }
                });

        /**
         *  After attaching listeners, create a service request and initiate discovery.
         */

        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,
                new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        //appendStatus("Added service discovery request");
                    }

                    @Override
                    public void onFailure(int arg0) {
                        // appendStatus("Failed adding service discovery request");
                    }
                });

        /**
         * Start the Service Discovery*
         */
        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //appendStatus("Service discovery initiated");
            }

            @Override
            public void onFailure(int reason) {
                String failReason = "Failed";
                if (reason == WifiP2pManager.P2P_UNSUPPORTED) {
                    failReason = "P2P_UNSUPPORTED";
                    Log.d(TAG, "P2P isn't supported on this device.");
                } else if (reason == WifiP2pManager.BUSY) {
                    failReason = "P2P_BUSY";
                    Log.d(TAG, "WiFi P2p is Busy");
                } else if (reason == WifiP2pManager.ERROR) {
                    failReason = "P2P_ERROR";
                    Log.d(TAG, "Internal Error in WiFi P2p");
                }
                //appendStatus("Resetting Wifi Adapter " + failReason);

                //TODO Change the WIFI reset pattern

                if (wfManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                    Log.d("WiFi State:", "WIFI_STATE_ENABLED");

                    //TODO Check for User Shared preferences to see if user permits
                    // Wifi adapter reset
                    wfManager.setWifiEnabled(false);

                    wfManager.setWifiEnabled(true);
                    discoverService();
                } else if (wfManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING
                        || wfManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                    Log.d("WiFi State:", "WIFI_STATE_ENABLING/DISABLING");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    discoverService();
                }
            }
        });
    }

    /**
     * The Method that verifies if the scanned Service is already in our Friendlist*
     *
     * @param friend type WiFiP2pService. Contains info about the device MAC
     */
    public void foundAnyFriend(WiFiP2pService friend) {

        /* TODO Use the below segment to QUEUE next friend*/
        //for (int i = 0; i < discoveredItems.size(); i++) {
        // friend = discoveredItems.get(i);
        int count = 0;
        Log.d(TAG, "Current Device Address: " + friend.device.deviceAddress);

        String MAC = friend.device.deviceAddress;


        oIsMyFriend = oMyFriend.CheckFriendship(MAC);

        if (!(oIsMyFriend.getUSER_MAC() == null)) {
            if (count < 1) {
                Log.d(TAG, "Calling Connect Method: " + oIsMyFriend.getUSER_MAC());
                count = +1;

                INITIATED_BY_ME = true;
                connectP2p(friend);
            } else {
                Log.d(TAG, "trying to call more than once to: " + friend.device.deviceAddress);
                return;
            }
        } else {
            Log.d(TAG, "Sorry..! You are not my friend yet: " + friend.device.deviceAddress);
            return;
        }

    }

    /**
     * Called when identified as Friend, for establishing connection *
     *
     * @param service
     */
    public void connectP2p(WiFiP2pService service) {

        Log.e("TAG", "Inside ConnectP2p Method");

        WifiP2pConfig config = new WifiP2pConfig();

        config.deviceAddress = service.device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        if (serviceRequest != null)
            manager.removeServiceRequest(channel, serviceRequest,
                    new WifiP2pManager.ActionListener() {

                        @Override
                        public void onSuccess() {
                            Log.e("TAG", "Successfully removed Discover Service");
                        }

                        @Override
                        public void onFailure(int arg0) {
                            Log.e("TAG", "Failed in removing Discover Service");
                        }
                    });

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //appendStatus("Connecting to Service");
            }

            @Override
            public void onFailure(int errorCode) {
                // appendStatus("Failed connecting to service: " + errorCode);
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_READ:
                Log.d(TAG, "Message is Received and I am trying to display");
                byte[] readBuf = (byte[]) msg.obj;
                Log.d(TAG, "Data Length = " + readBuf.length);
                Log.d(TAG, "Sent Length = " + msg.arg1);
                try {
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, readBuf.length);
                    Log.d(TAG, readMessage);

                    //appendStatus("Msg Received from Friend: " + '\n' + readMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case MESSAGE_SENT:
                //appendStatus("Successfully Sent My Profile to my Friend");
                break;
        }
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        isServiceActive = sharedPref.getBoolean(getString(R.string.sp_field_service_state), false);

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Log.i(TAG, "Received Start Foreground Intent ");

            Intent notificationIntent = new Intent(this, HomeScreen.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Intent passivateIntent = new Intent(this, WiFriendsService.class);
            passivateIntent.setAction(Constants.ACTION.PASSIVATE_ACTION);
            PendingIntent ppassivateIntent = PendingIntent.getService(this, 0,
                    passivateIntent, 0);

            Intent activateIntent = new Intent(this, WiFriendsService.class);
            activateIntent.setAction(Constants.ACTION.ACTIVATE_ACTION);
            PendingIntent pactivateIntent = PendingIntent.getService(this, 0,
                    activateIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("WiFriends")
                    .setTicker("WiFriends(Service Started Running)")
                    .setContentText("You are never alone")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setLargeIcon(
                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)

                    .setOngoing(true)
                    .addAction(android.R.drawable.btn_star_big_off,
                            "Passivate", ppassivateIntent)
                    .addAction(android.R.drawable.ic_menu_help, "Activate",
                            pactivateIntent).build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notification);

            if (!isServiceActive) {
                editor.putBoolean(getString(R.string.sp_field_service_state), true);
                editor.commit();
            }

            receiver = new WiFiDirectBroadcastReceiver(manager, channel);
            registerReceiver(receiver, intentFilter);

            startRegistrationAndDiscovery();

        } else if (intent.getAction().equals(Constants.ACTION.PASSIVATE_ACTION)) {
            Log.i(TAG, "Deactivate Service");

        } else if (intent.getAction().equals(Constants.ACTION.ACTIVATE_ACTION)) {
            Log.i(TAG, "Re-activate Service");

        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(TAG, "Received Stop Foreground Intent");

            stopForeground(true);
            stopSelf();

        }

//        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "CHECK:Calling On Stop");
        if (manager != null && channel != null) {
            manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "Remove group Success");
                }

            });
        }
        unregisterReceiver(receiver);
        editor.putBoolean(getString(R.string.sp_field_service_state), false);
        editor.commit();
        super.onDestroy();

    }
}
