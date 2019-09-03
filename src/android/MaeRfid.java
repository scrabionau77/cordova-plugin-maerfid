/**
 */
package com.maestrale.maerfid;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

//import com.caen.RFIDLibrary.*;

import android.util.Log;
import java.util.Date;

public class MaeRfid extends CordovaPlugin {
    private static final String TAG = "MaeRfid";

    /**
       * Constructor.
       */
    public MaeRfid() {
    } 

    
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG, "Initializing MaeRfid");
    }

    public boolean execute(String action, JSONArray params, final CallbackContext callbackContext) throws JSONException {
      
        String args = params.getString(0);

        if(action.equals("echo")) {
            //String phrase = args.getString(0);
            // Echo back the first argument
            Log.d(TAG, args);

        } else if(action.equals("getDate")) {
            // An example of returning data back to the web layer
            final PluginResult result = new PluginResult(PluginResult.Status.OK, (new Date()).toString());
            callbackContext.sendPluginResult(result);

        } else if(action.equals("bestemmia")) {
            JSONObject r = new JSONObject();
            r.put("dio", "porco");
            callbackContext.success(r);

        } /*else if(action.equals("connect")) {

            // Execute in another thread to avoid blocking
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    // Qui va chiamata la libreria android CAEN RFID
                    CAENRFIDReader reader = new CAENRFIDReader();
                    try {
                        // Execute success callback
                        reader.Connect(CAENRFIDPort.CAENRFID_RS232, "");

                        callbackContext.success("qualcosa");
                    } catch (CAENRFIDException e) {
                        callbackContext.error(e.toString()); // Execute error callback
                    }
                }
            });

        }
        */
        return true;
    }

}
