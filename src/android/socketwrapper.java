package com.heytz.socketwrapper;

import android.content.Context;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.OutputStream;
import java.net.Socket;

/**
 * This class starts transmit to activation
 */
public class socketwrapper extends CordovaPlugin {

    private static String TAG = "=====socketwrapper.class====";
    private CallbackContext socketCallbackContext;
    private Context context;
    private String mac;
    private String deviceIP;
    private String uid;
    private String token;
    private String APPId;
    private String productKey;
    private String deviceLoginID;
    private String devicePassword;
    private int activateTimeout;
    private String activatePort;
    private Socket socket = null;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        context = cordova.getActivity().getApplicationContext();
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        socketCallbackContext = callbackContext;
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
                    socketCallbackContext.success();
                }
            }
        } catch (Exception e) {
            socketCallbackContext.error("Device activate failed.");
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
                        byte[] bs = message.getBytes();
                        for (int i = 0; i < (bs.length); i = i + 2) {
                            int bit = Integer.parseInt(Integer.toString(bs[i] - 48) + Integer.toString(bs[i + 1] - 48));
                            buffer[i / 2] = new Integer(bit).byteValue();
                        }
                        os.write(buffer);
                    }
                } catch (Exception se) {
                    Log.e(TAG, se.toString());
                }
            }
        }).start();
        socketCallbackContext.success();
    }

    private byte[] str2HexString(String str) {
        byte[] bs = str.getBytes();
        byte[] result = new byte[str.length() / 2];
        for (int i = 0; i < (bs.length); i = i + 2) {
            int bit = Integer.parseInt(Integer.toString(bs[i] - 48) + Integer.toString(bs[i + 1] - 48));
            result[i / 2] = new Integer(bit).byteValue();
        }
        return result;
    }


}
