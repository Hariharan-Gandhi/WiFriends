
package tud.cnlab.wifriends;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import tud.cnlab.wifriends.datahandlers.MdlFriendListDbHandler;

/**
 * Group Owner Socket - Used when connected as Group Owner.
 * Creates Socket and threads for accepting connections from Client*
 */
public class GroupOwnerSocketHandler extends Thread {

    private static final String TAG = "GroupOwnerSocketHandler";
    private final int THREAD_COUNT = 10;
    /**
     * A ThreadPool for client sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    ServerSocket socket = null;
    MdlFriendListDbHandler oFriendInfo;
    private Handler handler;

    public GroupOwnerSocketHandler(Handler handler, MdlFriendListDbHandler oFriendInfo) throws IOException {
        try {


            socket = new ServerSocket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(4545));
            this.handler = handler;
            this.oFriendInfo = oFriendInfo;
            Log.d(TAG, "Socket Started");
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                // A blocking operation. Initiate a ProfileExchanger instance when
                // there is a new connection

                pool.execute(new ProfileExchanger(socket.accept(), handler, oFriendInfo));
                Log.d(TAG, "GO: Launching the I/O handler");

            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {

                }
                e.printStackTrace();
                pool.shutdownNow();
                break;
            }
        }
    }

}
