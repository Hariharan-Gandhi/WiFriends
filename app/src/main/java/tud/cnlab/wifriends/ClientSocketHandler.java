
package tud.cnlab.wifriends;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import tud.cnlab.wifriends.datahandlers.MdlFriendListDbHandler;


/**
 * Client Socket - Used when connected as Client.
 * Connects to Group owner Socket *
 */
public class ClientSocketHandler extends Thread {

    private static final String TAG = "ClientSocketHandler";
    MdlFriendListDbHandler oFriendInfo;
    private Handler handler;
    private ProfileExchanger exchanger;
    private InetAddress mAddress;

    public ClientSocketHandler(Handler handler, InetAddress groupOwnerAddress, MdlFriendListDbHandler oFriendInfo) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
        this.oFriendInfo = oFriendInfo;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            String hostAddress = mAddress.getHostAddress();
            if (hostAddress == null) {
                return;
            }
            socket.connect(new InetSocketAddress(hostAddress,
                    WiFriends.SERVER_PORT), 5000);
            Log.d(TAG, "Launching the I/O handler");
            exchanger = new ProfileExchanger(socket, handler, oFriendInfo);
            new Thread(exchanger).start();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }

    public ProfileExchanger getExchanger() {
        return exchanger;
    }

}
