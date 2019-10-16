/**
 * PhoneGap is available under *either* the terms of the modified BSD license *or* the
 * MIT License (2008). See http://opensource.org/licenses/alphabetical for full text.
 *
 * Copyright (c) 2019, Daniele Pellerucci @ Maestrale Information Technology
 * www.maestrale.it
 */
package com.cordova.plugins.maerfid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.Ch34xSerialDriver;
import com.hoho.android.usbserial.driver.Cp21xxSerialDriver;
import com.hoho.android.usbserial.driver.FtdiSerialDriver;
import com.hoho.android.usbserial.driver.ProbeTable;
import com.hoho.android.usbserial.driver.ProlificSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.app.Activity;
import android.os.Bundle;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.caen.RFIDLibrary.*;
import com.caen.VCPSerialPort.VCPSerialPort;



public class MaeRfid extends CordovaPlugin {

    public static final int REQUEST_CODE = 0x0ba7c;
    private static final String CANCELLED = "cancelled";
    private static final String FORMAT = "format";
    private static final String TEXT = "text";


    private static final String TAG = "MaeRfid";

    // actions definitions
    private static final String ACTION_CONFIG = "configCaen";
    private static final String ACTION_REQUEST_PERMISSION = "requestPermission";
    private static final String ACTION_READ_TAG = "readTag";
    private static final String ACTION_READ_GPIO = "readGpio";
    private static final String ACTION_CONNECT = "connect";
    private static final String ACTION_CONFIG_ASYNC = "configCaenAsync";
    private static final String ACTION_READ_GPIO_ASYNC = "readGpioAsync";



    // NUOVA IMPLEMENTAZIONE
    // UsbManager instance to deal with permission and opening
    private UsbManager manager;
    // The current driver that handle the serial port
    private UsbSerialDriver driver;
    // The serial port that will be used in this plugin
    private UsbSerialPort port;

    private JSONArray requestArgs;
    private CallbackContext callbackContext;
    private CAENRFIDReader reader = new CAENRFIDReader();

    /**
     * Constructor.
     */
    public MaeRfid() {
    }





    /**
     * Overridden execute method
     * @param action the string representation of the action to execute
     * @param args
     * @param callbackContext the cordova {@link CallbackContext}
     * @return true if the action exists, false otherwise
     * @throws JSONException if the args parsing fails
     */
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "ACTION: " + action);
        JSONObject arg_object = args.optJSONObject(0);
        this.callbackContext = callbackContext;

        if(ACTION_CONFIG.equals(action)){
            JSONObject opts = arg_object.has("opts")? arg_object.getJSONObject("opts") : new JSONObject();
            configCaen(opts, callbackContext);
        } else if(ACTION_REQUEST_PERMISSION.equals(action)){
            JSONObject opts = arg_object.has("opts")? arg_object.getJSONObject("opts") : new JSONObject();
            requestPermission(opts, callbackContext);
        } else if(ACTION_READ_TAG.equals(action)){
            JSONObject opts = arg_object.has("opts")? arg_object.getJSONObject("opts") : new JSONObject();
            readTag(opts, callbackContext);
        } else if(ACTION_READ_GPIO.equals(action)){
            JSONObject opts = arg_object.has("opts")? arg_object.getJSONObject("opts") : new JSONObject();
            readGpio(opts, callbackContext);
        } else if(ACTION_CONNECT.equals(action)){
            JSONObject opts = arg_object.has("opts")? arg_object.getJSONObject("opts") : new JSONObject();
            connect(opts, callbackContext);
        } else if(ACTION_CONFIG_ASYNC.equals(action)){
            JSONObject opts = arg_object.has("opts")? arg_object.getJSONObject("opts") : new JSONObject();
            configCaenAsync(opts, callbackContext);
        } else if(ACTION_READ_GPIO_ASYNC.equals(action)){
            JSONObject opts = arg_object.has("opts")? arg_object.getJSONObject("opts") : new JSONObject();
            readGpioAsync(opts, callbackContext);
        } else {
            return false;
        }
        return true;
    }






    /**
     * CONFIGURAZIONE DISPOSITIVO CAEN
     */
    private void configCaen(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                int GpioConfig = 0x0; // 0 = INPUT, 1 = OUTPUT
                int OutputVal = 0xf;

                if (opts.has("gpioConfig")) {
                    Object o_gpio = opts.opt("gpioConfig"); //can be an integer Number or a hex String
                    GpioConfig = o_gpio instanceof Number ? ((Number) o_gpio).intValue() : Integer.parseInt((String) o_gpio, 16);
                }

                if (opts.has("outputVal")) {
                    Object o_ov = opts.opt("outputVal"); //can be an integer Number or a hex String
                    OutputVal = o_ov instanceof Number ? ((Number) o_ov).intValue() : Integer.parseInt((String) o_ov, 16);
                }

                try {
                    Log.d(TAG, "Avvio il settaggio del CAEN!");
                    List<VCPSerialPort> ports = VCPSerialPort.findVCPDevice(cordova.getActivity().getApplication().getApplicationContext());
                    VCPSerialPort port = ports.get(0);

                    CAENRFIDReader reader = new CAENRFIDReader();

                    reader.Connect(port);

                    // Definisco quali GPIO sono di ingresso e quali di uscita
                    reader.SetIODIRECTION(GpioConfig);

                    // Definisco il livello logico per i pin di uscita
                    reader.SetIO(OutputVal);



                    PluginResult result = new PluginResult(PluginResult.Status.OK, "Settaggio terminato"); // ListArr.toString()
                    callbackContext.sendPluginResult(result);

                } catch (Exception ex){
                    Log.d(TAG, "Errore settaggio CAEN!");
                    callbackContext.error(ex.getMessage());
                }
            }
        });
    }

    private void configCaenAsync(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                int GpioConfig = 0x0; // 0 = INPUT, 1 = OUTPUT
                int OutputVal = 0xf;

                if (opts.has("gpioConfig")) {
                    Object o_gpio = opts.opt("gpioConfig"); //can be an integer Number or a hex String
                    GpioConfig = o_gpio instanceof Number ? ((Number) o_gpio).intValue() : Integer.parseInt((String) o_gpio, 16);
                }

                if (opts.has("outputVal")) {
                    Object o_ov = opts.opt("outputVal"); //can be an integer Number or a hex String
                    OutputVal = o_ov instanceof Number ? ((Number) o_ov).intValue() : Integer.parseInt((String) o_ov, 16);
                }

                try {
                    Log.d(TAG, "Avvio il settaggio del CAEN!");

                    // Definisco quali GPIO sono di ingresso e quali di uscita
                    reader.SetIODIRECTION(GpioConfig);

                    // Definisco il livello logico per i pin di uscita
                    reader.SetIO(OutputVal);

                    PluginResult result = new PluginResult(PluginResult.Status.OK, "Settaggio terminato"); // ListArr.toString()
                    callbackContext.sendPluginResult(result);

                } catch (Exception ex){
                    Log.d(TAG, "Errore settaggio CAEN!");
                    callbackContext.error(ex.getMessage());
                }
            }
        });
    }


    /**
     * LETTURA STATO GPIO
     */
    private void readGpio(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                try {
                    Log.d(TAG, "Avvio la lettura delle GPIO!");
                    List<VCPSerialPort> ports = VCPSerialPort.findVCPDevice(cordova.getActivity().getApplication().getApplicationContext());
                    VCPSerialPort port = ports.get(0);
                    CAENRFIDReader reader = new CAENRFIDReader();
                    reader.Connect(port);

                    // Leggo il valore dei GPIO
                    int InputVal = 0x0;
                    InputVal = reader.GetIO();

                    PluginResult result = new PluginResult(PluginResult.Status.OK, InputVal); // ListArr.toString()
                    callbackContext.sendPluginResult(result);

                } catch (Exception ex){
                    //callbackContext.error(ex); // .getMessage()

                    PluginResult result = new PluginResult(PluginResult.Status.ERROR, ex.getMessage()); // ListArr.toString()
                    callbackContext.sendPluginResult(result);
                }
            }
        });
    }





    private void readGpioAsync(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                try {
                    Log.d(TAG, "Avvio la lettura delle GPIO!");

                    // Leggo il valore dei GPIO
                    int InputVal = 0x0;
                    InputVal = reader.GetIO();

                    new GpioPollong().execute();

                    
                    //PluginResult result = new PluginResult(PluginResult.Status.OK, InputVal); // ListArr.toString()
                    //callbackContext.sendPluginResult(result);

                } catch (Exception ex){
                    //callbackContext.error(ex); // .getMessage()

                    PluginResult result = new PluginResult(PluginResult.Status.ERROR, ex.getMessage()); // ListArr.toString()
                    callbackContext.sendPluginResult(result);
                }
            }
        });
    }




    /**
     * RICHIESTA PERMESSI CONNESSIONE USB
     */
    private void requestPermission(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                // get UsbManager from Android
                manager = (UsbManager) cordova.getActivity().getSystemService(Context.USB_SERVICE);
                UsbSerialProber prober;

                if (opts.has("vid") && opts.has("pid")) {
                    ProbeTable customTable = new ProbeTable();
                    Object o_vid = opts.opt("vid"); //can be an integer Number or a hex String
                    Object o_pid = opts.opt("pid"); //can be an integer Number or a hex String
                    int vid = o_vid instanceof Number ? ((Number) o_vid).intValue() : Integer.parseInt((String) o_vid,16);
                    int pid = o_pid instanceof Number ? ((Number) o_pid).intValue() : Integer.parseInt((String) o_pid,16);
                    String driver = opts.has("driver") ? (String) opts.opt("driver") : "CdcAcmSerialDriver";

                    if (driver.equals("FtdiSerialDriver")) {
                        customTable.addProduct(vid, pid, FtdiSerialDriver.class);
                    }
                    else if (driver.equals("CdcAcmSerialDriver")) {
                        customTable.addProduct(vid, pid, CdcAcmSerialDriver.class);
                    }
                    else if (driver.equals("Cp21xxSerialDriver")) {
                        customTable.addProduct(vid, pid, Cp21xxSerialDriver.class);
                    }
                    else if (driver.equals("ProlificSerialDriver")) {
                        customTable.addProduct(vid, pid, ProlificSerialDriver.class);
                    }
                    else if (driver.equals("Ch34xSerialDriver")) {
                        customTable.addProduct(vid, pid, Ch34xSerialDriver.class);
                    }
                    else {
                        Log.d(TAG, "Unknown driver!");
                        callbackContext.error("Unknown driver!");
                    }

                    prober = new UsbSerialProber(customTable);

                }
                else {
                    // find all available drivers from attached devices.
                    prober = UsbSerialProber.getDefaultProber();
                }

                List<UsbSerialDriver> availableDrivers = prober.findAllDrivers(manager);

                if (!availableDrivers.isEmpty()) {
                    // get the first one as there is a high chance that there is no more than one usb device attached to your android
                    driver = availableDrivers.get(0);
                    UsbDevice device = driver.getDevice();
                    // create the intent that will be used to get the permission
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(cordova.getActivity(), 0, new Intent(UsbBroadcastReceiver2.USB_PERMISSION), 0);
                    // and a filter on the permission we ask
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(UsbBroadcastReceiver2.USB_PERMISSION);
                    // this broadcast receiver will handle the permission results
                    UsbBroadcastReceiver2 usbReceiver = new UsbBroadcastReceiver2(callbackContext, cordova.getActivity());
                    cordova.getActivity().registerReceiver(usbReceiver, filter);
                    // finally ask for the permission
                    manager.requestPermission(device, pendingIntent);
                }
                else {
                    // no available drivers
                    Log.d(TAG, "No device found!");
                    callbackContext.error("No device found!");
                }
            }
        });
    }




    /**
     * APERTURA CONNESSIONE PORTA
     */
    private void readTag(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                int src = 0;
                if (opts.has("source")) {
                    Object o_src = opts.opt("source"); //can be an integer Number or a hex String
                    src = o_src instanceof Number ? ((Number) o_src).intValue() : Integer.parseInt((String) o_src, 16);

                    if(src < 0 || src > 3){
                        src = 0;
                    }
                }

                String caen_src = "Source_" + src; // ex: Source_0 for antenna 0


                try {
                    Log.d(TAG, "Avvio apertura porta seriale!");

                    CAENRFIDLogicalSource mySource = reader.GetSource(caen_src);

                    CAENRFIDTag[] myTags = mySource.InventoryTag();
                    //reader.Disconnect();

                    PluginResult.Status status = PluginResult.Status.OK;

                    if(myTags != null && myTags.length > 0){
                        String list = "";

                        org.json.JSONObject JsonOut = new org.json.JSONObject();



                        for (int x= 0; x< myTags.length; x++) {
                            CAENRFIDTag tag = myTags[x];

                            org.json.JSONObject obj = new org.json.JSONObject();
                            obj.put("Antenna", tag.GetAntenna());
                            obj.put("Id", bytesToHex(tag.GetId()));
                            obj.put("Length", tag.GetLength());
                            obj.put("RSSI", tag.GetRSSI());
                            obj.put("TID", tag.GetTID());
                            obj.put("TimeStamp", tag.GetTimeStamp());

                            JsonOut.put("tag_"+x, obj);
                        }

                        //final byte[] data = new byte[myTags.length];
                        //callbackContext.success(ListArr.); // + myTags.length);
                        //callbackContext.sendPluginResult(new PluginResult(status,data));

                        PluginResult result = new PluginResult(PluginResult.Status.OK, JsonOut.toString()); // ListArr.toString()
                        callbackContext.sendPluginResult(result);


                    } else {
                        final byte[] data = new byte[0];

                        //PluginResult result = new PluginResult(PluginResult.Status.OK, "Non ci sono tag");


                        callbackContext.success("NON Ci sono tag");
                        //callbackContext.sendPluginResult(new PluginResult(status, data));
                    }

                } catch (Exception ex){
                    callbackContext.error(ex.getMessage());
                }
            }
        });
    }



    private void readTagLoop(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                int src = 0;
                if (opts.has("source")) {
                    Object o_src = opts.opt("source"); //can be an integer Number or a hex String
                    src = o_src instanceof Number ? ((Number) o_src).intValue() : Integer.parseInt((String) o_src, 16);

                    if(src < 0 || src > 3){
                        src = 0;
                    }
                }

                String caen_src = "Source_" + src; // ex: Source_0 for antenna 0


                try {
                    Log.d(TAG, "Avvio apertura porta seriale!");

                    CAENRFIDLogicalSource mySource = reader.GetSource(caen_src);

                    // Loop
                    long t= System.currentTimeMillis();
                    long end = t + 5000;

                    org.json.JSONObject JsonOut = new org.json.JSONObject();
                    while(System.currentTimeMillis() < end) {
                        CAENRFIDTag[] myTags = mySource.InventoryTag();

                        if(myTags != null && myTags.length > 0){
                            for (int x= 0; x< myTags.length; x++) {
                                CAENRFIDTag tag = myTags[x];

                                org.json.JSONObject obj = new org.json.JSONObject();
                                obj.put("Antenna", tag.GetAntenna());
                                obj.put("Id", bytesToHex(tag.GetId()));
                                obj.put("Length", tag.GetLength());
                                obj.put("RSSI", tag.GetRSSI());
                                obj.put("TID", tag.GetTID());
                                obj.put("TimeStamp", tag.GetTimeStamp());
    
                                JsonOut.put("tag_"+x, obj);
                            }
                        }


                        Thread.sleep( 10 );
                    }

                    // manca il FILTER "ARRAY" x togliere i doppioni

                    PluginResult.Status status = PluginResult.Status.OK;
                    PluginResult result = new PluginResult(PluginResult.Status.OK, JsonOut.toString()); // ListArr.toString()
                    callbackContext.sendPluginResult(result);

                } catch (Exception ex){
                    callbackContext.error(ex.getMessage());
                }
            }
        });
    }

    private void connect(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                int src = 0;
                if (opts.has("source")) {
                    Object o_src = opts.opt("source"); //can be an integer Number or a hex String
                    src = o_src instanceof Number ? ((Number) o_src).intValue() : Integer.parseInt((String) o_src, 16);

                    if(src < 0 || src > 3){
                        src = 0;
                    }
                }

                String caen_src = "Source_" + src; // ex: Source_0 for antenna 0

                try {
                    Log.d(TAG, "Avvio apertura porta seriale!");
                    List<VCPSerialPort> ports = VCPSerialPort.findVCPDevice(cordova.getActivity().getApplication().getApplicationContext()); // Global.getAppContext()

                    VCPSerialPort port = ports.get(0);

                    reader.Connect(port);
                    CAENRFIDLogicalSource mySource = reader.GetSource(caen_src);

                    PluginResult.Status status = PluginResult.Status.OK;

                    PluginResult result = new PluginResult(PluginResult.Status.OK, "Reader Connesso"); // ListArr.toString()
                    callbackContext.sendPluginResult(result);

                } catch (Exception ex){
                    callbackContext.error(ex.getMessage());
                }
            }
        });
    }


    /*
     * Java algorithm to convert binary to decimal format
     */
    public static int binaryToDecimal(int number) {
        int decimal = 0;
        int binary = number;
        int power = 0;

        while (binary != 0) {
            int lastDigit = binary % 10;
            decimal += lastDigit * Math.pow(2, power);
            power++;
            binary = binary / 10;
        }
        return decimal;
    }




    private static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }




    /**
     * Convert a given string of hexadecimal numbers
     * into a byte[] array where every 2 hex chars get packed into
     * a single byte.
     *
     * E.g. "ffaa55" results in a 3 byte long byte array
     *
     * @param s
     * @return
     */
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }




    /**
     * Called when the barcode scanner intent completes.
     *
     * @param requestCode The request code originally supplied to startActivityForResult(),
     *                       allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param intent      An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE && this.callbackContext != null) {
            if (resultCode == Activity.RESULT_OK) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put(TEXT, intent.getStringExtra("SCAN_RESULT"));
                    obj.put(FORMAT, intent.getStringExtra("SCAN_RESULT_FORMAT"));
                    obj.put(CANCELLED, false);
                } catch (JSONException e) {
                    Log.d(TAG, "This should never happen");
                }
                //this.success(new PluginResult(PluginResult.Status.OK, obj), this.callback);
                this.callbackContext.success(obj);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put(TEXT, "");
                    obj.put(FORMAT, "");
                    obj.put(CANCELLED, true);
                } catch (JSONException e) {
                    Log.d(TAG, "This should never happen");
                }
                //this.success(new PluginResult(PluginResult.Status.OK, obj), this.callback);
                this.callbackContext.success(obj);
            } else {
                //this.error(new PluginResult(PluginResult.Status.ERROR), this.callback);
                this.callbackContext.error("Unexpected error");
            }
        }
    }







    private class GpioPollong extends AsyncTask<Void, Integer, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "AAAAA PRE EXECUTE");
            //progress.setProgress(0);
            //progress.show();
        }
        @Override
        protected String doInBackground(Void... arg0)
        {
            Log.d(TAG, "AAAAA DO IN BG");
            Boolean iterate = true;
            int InputVal = 0x0;
            try {
                
                while(iterate){
                    Log.d(TAG, "AAAAA ANCORA A ZERO!");
                    InputVal = reader.GetIO();
                    if(InputVal > 0){
                        iterate = false;
                        Log.d(TAG, "AAAAA ESCO");
                    }
                }
            }
            catch (Exception e) {}


            return "Valore estratto: " +  InputVal;
        }
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            Log.d(TAG, "AAAAA ON UPDATE");
            super.onProgressUpdate(values);
            //progress.setProgress(values[0].intValue());
        }
        @Override
        protected void onPostExecute(String result)
        {
            Log.d(TAG, "AAAAA ON POST EXECUTE");

            JSONObject jnull = new JSONObject();
            readTagLoop(jnull, callbackContext);


            super.onPostExecute(result);
            //progress.dismiss();
            //Toast.makeText(MainActivity.this, result,	Toast.LENGTH_SHORT).show();
        }
    }







}







