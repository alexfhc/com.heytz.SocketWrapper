package com.heytz.socketwrapper;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This class starts transmit to activation
 */
public class socketwrapper extends CordovaPlugin {

    private static String TAG = "=====mxsdkwrapper.class====";
    private CallbackContext easyLinkCallbackContext;
    private Context context;
    //    private FTC_Service ftcService;
    private String mac;
    private String deviceIP;
    private String uid;
    private String token;
    private String APPId;
    private String productKey;
    private String deviceLoginID;
    private String devicePassword;
    //  private String appSecretKey;
    //  private int easylinkVersion;
    private int activateTimeout;
    private String activatePort;
    private Socket socket = null;


    /**
     * Step 2.1 FTC Call back, process the response from MXChip Model.
     */
//    private FTC_Listener ftcListener;//new FTCLisenerExtension(easyLinkCallbackContext);
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        context = cordova.getActivity().getApplicationContext();
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("disconnect")) {
            disconnectSocket();
            return true;
        }
        if (action.equals("sendCMD")) {
            String ip = args.getString(0);
            String message = args.getString(1);
            sendSocketCMD(ip, message);
            return true;
        }
        return false;
    }

    private void disconnectSocket() {
        try {
             if (socket != null) {
               if (socket.isConnected()) {

               }
             }
        } catch (Exception e) {
            easyLinkCallbackContext.error("Device activate failed.");
            Log.e(TAG, e.getMessage());
        }
    }

    private void sendSocketCMD(final String ip, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socket == null) {
                        socket = new Socket(ip, 8080);
                    }
                    if (socket.isConnected()) {
                        final OutputStream os = socket.getOutputStream();
                        os.flush();
                        byte[] buffer = new byte[message.length() / 2];
                        buffer = message.getBytes();
                        buffer[0] = 0x03;
                        buffer[1] = 0x00;
                        os.write(buffer);
                    }
                } catch (Exception se) {
                    Log.e(TAG, se.toString());
                }
            }
        }).start();
    }

    private static byte[] readStream(InputStream inStream) throws Exception {
        int count = 0;
        while (count == 0) {
            count = inStream.available();
            Log.i(TAG, String.valueOf(count));
        }
        byte[] b = new byte[count];
        inStream.read(b);
        return b;
    }



}
