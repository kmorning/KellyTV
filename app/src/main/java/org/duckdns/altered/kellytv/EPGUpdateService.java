package org.duckdns.altered.kellytv;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by kmorning on 2018-02-10.
 *
 * This service pulls xmltv data from a web site URL contained in the incoming Intent (see
 * onHandleIntent()).  As it runs, it broadcasts it's status using LocalBroadcast Manger; any
 * component that wants to see the staus should implement a subclass of BroadcastReceiver and
 * register to receive broadcast Intents with category = CATEGORY_DEFAULT and action
 * Constants.BROADCAST_ACTION.
 *
 * Note:  maybe URL should come from a saved data file somewhere instead of passed through
 * Intent.
 */

public class EPGUpdateService extends IntentService {
    // Used to write to the system log from this class.
    public static final String LOG_TAG = "EPGUpdateService";

    // Defines and instantiates an object for handling status updates.
    private BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);

    // HTTP InputStream read buffer size
    private static final int BUFFER_SIZE = 4096;

    /**
     * An IntentService must always have a constructor that calls the super constructor. The
     * string supplied to the super constructor is used to give a name to the IntentService's
     * background thread.
     */
    public EPGUpdateService() {
        // Worker thread name which is only important for debugging
        super("EPGUpdateService");
    }

    /**
     * In an IntentService, onHandleIntent is run on a background thread.  As it
     * runs, it broadcasts its current status using the LocalBroadcastManager.
     * @param workIntent The Intent that starts the IntentService. This Intent contains the
     * URL of the web site from which the RSS parser gets data.
     */
    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets a URL to read from the incoming Intent's "data" value
        String urlString = workIntent.getDataString();

        // URL to download from
        URL url;

        //block that tries to connect to xml data, and throws an IOException if one occurs
        try {
            // Convert the incoming data string to a URL.
            url = new URL(urlString);

           /**
            * Tries to open a connection to the URL. If an IO error occurs, this throws an
            * IOException
            */
            URLConnection urlConnection = url.openConnection();

            // If the connection is an HTTP connection, continue
            if (urlConnection instanceof HttpURLConnection) {
                // Broadcasts an Intent indicating that processing has started.
                mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_STARTED);

                // Casts the connection to a HTTP connection
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

                // Reports that the service is about to connect to the RSS feed
                mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_CONNECTING);

                // Get response code from site
                int responseCode = httpURLConnection.getResponseCode();

                // Continue if response is OK
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Get content length (in bytes) to calculate progress from
                    int contentLength = httpURLConnection.getContentLength();

                    InputStream inputStream = urlConnection.getInputStream();

                    // Save to file
                    //FileOutputStream outputStream = new FileOutputStream(Constants.XML_SAVE_FILE_PATH);
                    // Save to internal storage instead
                    File file = new File(getFilesDir(), Constants.XML_SAVE_FILE_NAME);
                    FileOutputStream outputStream = new FileOutputStream(file);

                    // keep track of how much is downloaded
                    int totalBytesRead = 0;
                    int progressDone = 0;
                    // Report downloading state
                    mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_DOWNLOADING);

                    int bytesRead = -1;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                        progressDone = totalBytesRead/contentLength * 100;
                        // TODO: broadcast progress
                    }

                    outputStream.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    // Report download complete
                    mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_DOWNLOAD_COMPLETE);
                }

            }
            else {
                // Report that action failed
                mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);
                return;
            }

            // TODO: make sure tvheadend is running
            // Open sockect connection to xmltv.sock
            String socketName = getFilesDir() + "/xmltv.sock";
            Log.d("socketConnection", "Connecting to " + socketName);
            LocalSocketAddress socketAddress = new LocalSocketAddress(socketName,
                    LocalSocketAddress.Namespace.FILESYSTEM);
            LocalSocket socket = new LocalSocket();
            socket.connect(socketAddress);
            if (socket.isConnected()) {
                mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_SOCKET_CONNECTED);

                // open xml file
                File file = new File(getFilesDir(), Constants.XML_SAVE_FILE_NAME);
                FileInputStream inputStream = new FileInputStream(file);

                // get socket output stream
                OutputStream outputStream = socket.getOutputStream();

                // write xml data to xmltv socket
                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();;
                inputStream.close();
                socket.close();
            }
            else {
                // Report that action failed
                mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);
                return;
            }
            // Done successfully
            mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_COMPLETE);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            // Report that action failed
            mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);

        }
        catch (IOException e) {
            e.printStackTrace();
            // Report that action failed
            mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
            // Report that action failed
            mBroadcaster.broadcastIntentWithState(Constants.STATE_ACTION_FAILED);
        }
    }
}
