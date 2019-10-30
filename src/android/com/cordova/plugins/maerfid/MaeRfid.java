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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;

import java.util.List;

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
    private static final String ACTION_REQUEST_PERMISSION = "requestPermission";
    private static final String ACTION_READ_TAG = "readTag";
    private static final String ACTION_READ_GPIO = "readGpio";
    private static final String ACTION_CONNECT = "connect";
    private static final String ACTION_CONFIG_CAEN = "configCaen";
    private static final String ACTION_WAIT_RFID = "waitRfid";
    private static final String ACTION_DISCONNECT = "disconnect";
    private static final String ACTION_RESET = "resetReader";


    private UsbManager manager; // UsbManager instance to deal with permission and opening
    private UsbSerialDriver driver; // The current driver that handle the serial port
    private UsbSerialPort port; // The serial port that will be used in this plugin

    private JSONArray requestArgs;
    private CallbackContext callbackContext;
    private CAENRFIDReader reader = new CAENRFIDReader();

    // setting variables
    private Integer Input0Antennas = 0;
    private Integer Input1Antennas = 0;
    private Integer Input2Antennas = 0;
    private Integer Input3Antennas = 0;
    private Integer readRfidDuration = 5000; // milliseconds
    private Boolean isConnected = false;
    private Integer triggeredInput = 0;
    private Boolean activeBuzzer = false;
    private Integer buzzerPin = 3;

    private Integer buzzerDuration = 1000; // Per avvenuta lettura
    private Integer buzzerRepeat = 1;
    private Integer buzzerStartDuration = 300; // Per inizio lettura (rilevata attivazione GPIO)
    private Integer buzzerStartRepeat = 1;
    private Integer buzzerFailDuration = 150; // Per inizio lettura (rilevata attivazione GPIO)
    private Integer buzzerFailRepeat = 3;

    // utility variables
    private Boolean BuzzerIsActive = false;
    private Boolean BuzzeredInThisReading = false;
    private Integer BuzzerRealDuration = 0;
    private Integer BuzzerRepeat = 1;


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

        if(ACTION_REQUEST_PERMISSION.equals(action)){
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
        } else if(ACTION_CONFIG_CAEN.equals(action)){
            JSONObject opts = arg_object.has("opts")? arg_object.getJSONObject("opts") : new JSONObject();
            configCaen(opts, callbackContext);
        } else if(ACTION_WAIT_RFID.equals(action)){
            JSONObject opts = arg_object.has("opts")? arg_object.getJSONObject("opts") : new JSONObject();
            waitRfid(opts, callbackContext);
        } else if(ACTION_DISCONNECT.equals(action)){
            JSONObject opts = arg_object.has("opts")? arg_object.getJSONObject("opts") : new JSONObject();
            disconnect(opts, callbackContext);
        } else if(ACTION_RESET.equals(action)) {
            JSONObject opts = arg_object.has("opts")? arg_object.getJSONObject("opts") : new JSONObject();
            resetConnection(opts, callbackContext);
        } else {
            return false;
        }
        return true;
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
                        //callbackContext.error("Unknown driver!");

                        org.json.JSONObject res = new org.json.JSONObject();
                        try{
                            res.put("success", false);
                            res.put("code", 1);
                            res.put("message", "Unknown driver!");
                        } catch(Exception e){}
                        PluginResult result = new PluginResult(PluginResult.Status.ERROR, res); // ListArr.toString()
                        callbackContext.sendPluginResult(result);
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

                    isConnected = false;
                }
                else {
                    // no available drivers
                    Log.d(TAG, "No device found!");
                    //callbackContext.error("No device found!");

                    org.json.JSONObject res = new org.json.JSONObject();
                    try{
                        res.put("success", false);
                        res.put("code", 0);
                        res.put("message", "No device found!");
                    } catch(Exception e){}
                    PluginResult result = new PluginResult(PluginResult.Status.ERROR, res); // ListArr.toString()
                    callbackContext.sendPluginResult(result);
                }
            }
        });
    }




    /**
     * CONNESSIONE AL DISPOSITIVO CAEN
     */
    private void connect(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                /*
                int src = 0;
                if (opts.has("source")) {
                    Object o_src = opts.opt("source"); //can be an integer Number or a hex String
                    src = o_src instanceof Number ? ((Number) o_src).intValue() : Integer.parseInt((String) o_src, 16);

                    if(src < 0 || src > 3){
                        src = 0;
                    }
                }
                */

                try {
                    Log.d(TAG, "Avvio apertura porta seriale!");
                    List<VCPSerialPort> ports = VCPSerialPort.findVCPDevice(cordova.getActivity().getApplication().getApplicationContext()); // Global.getAppContext()

                    VCPSerialPort port = ports.get(0);

                    reader.Connect(port);

                    PluginResult.Status status = PluginResult.Status.OK;
                    PluginResult result = new PluginResult(PluginResult.Status.OK, "Reader Connesso"); // ListArr.toString()
                    callbackContext.sendPluginResult(result);

                } catch (Exception ex){
                    callbackContext.error(ex.getMessage());
                }
            }
        });
    }






    /**
     * CONFIGURAZIONE DISPOSITIVO CAEN
     * (Direzione delle GPIO, Valori delle GPIO)
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
     * WAIT RFID
     * Attende l'attivazione di una GPIO e legge i tag RFID
     */
    private void waitRfid(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                // Impostazione variabili parametriche
                if (opts.has("readRfidDuration")) {
                    Object o_dur = opts.opt("readRfidDuration");
                    readRfidDuration = ((Number) o_dur).intValue();

                    if(readRfidDuration <= 0){
                        readRfidDuration = 1000;
                    }
                }

                if (opts.has("Input0Antennas")) {
                    Object o_in0 = opts.opt("Input0Antennas"); //can be an integer Number or a hex String
                    Input0Antennas = o_in0 instanceof Number ? ((Number) o_in0).intValue() : Integer.parseInt((String) o_in0, 16);

                    if(Input0Antennas <= 0 || Input0Antennas > 15){
                        callbackContext.error("Input0Antennas out of range. Passed:" + Input0Antennas);
                    }
                }

                if (opts.has("Input1Antennas")) {
                    Object o_in1 = opts.opt("Input1Antennas"); //can be an integer Number or a hex String
                    Input1Antennas = o_in1 instanceof Number ? ((Number) o_in1).intValue() : Integer.parseInt((String) o_in1, 16);

                    if(Input1Antennas <= 0 || Input1Antennas > 15){
                        callbackContext.error("Input1Antennas out of range. Passed:" + Input1Antennas);
                    }
                }

                if (opts.has("Input2Antennas")) {
                    Object o_in2 = opts.opt("Input2Antennas"); //can be an integer Number or a hex String
                    Input2Antennas = o_in2 instanceof Number ? ((Number) o_in2).intValue() : Integer.parseInt((String) o_in2, 16);

                    if(Input2Antennas <= 0 || Input2Antennas > 15){
                        callbackContext.error("Input2Antennas out of range. Passed:" + Input2Antennas);
                    }
                }

                if (opts.has("Input3Antennas")) {
                    Object o_in3 = opts.opt("Input3Antennas"); //can be an integer Number or a hex String
                    Input3Antennas = o_in3 instanceof Number ? ((Number) o_in3).intValue() : Integer.parseInt((String) o_in3, 16);

                    if(Input3Antennas <= 0 || Input3Antennas > 15){
                        callbackContext.error("Input3Antennas out of range. Passed:" + Input3Antennas);
                    }
                }

                if (opts.has("activeBuzzer")) {
                    Object bz = opts.opt("activeBuzzer"); // boolean
                    activeBuzzer = Boolean.valueOf(bz.toString());
                }

                if (opts.has("buzzerDuration")) {
                    Object b_dur = opts.opt("buzzerDuration");
                    buzzerDuration = ((Number) b_dur).intValue();

                    if(buzzerDuration <= 0){
                        buzzerDuration = 1000;
                    }
                }

                if (opts.has("buzzerRepeat")) {
                    Object b_r = opts.opt("buzzerRepeat");
                    buzzerRepeat = ((Number) b_r).intValue();

                    if(buzzerRepeat <= 0){
                        buzzerRepeat = 1;
                    }
                }

                if (opts.has("buzzerStartDuration")) {
                    Object b_durS = opts.opt("buzzerStartDuration");
                    buzzerStartDuration = ((Number) b_durS).intValue();

                    if(buzzerStartDuration <= 0){
                        buzzerStartDuration = 300;
                    }
                }

                if (opts.has("buzzerStartRepeat")) {
                    Object b_rS = opts.opt("buzzerStartRepeat");
                    buzzerStartRepeat = ((Number) b_rS).intValue();

                    if(buzzerStartRepeat <= 0){
                        buzzerStartRepeat = 1;
                    }
                }

                if (opts.has("buzzerFailDuration")) {
                    Object b_durF = opts.opt("buzzerFailDuration");
                    buzzerFailDuration = ((Number) b_durF).intValue();

                    if(buzzerFailDuration <= 0){
                        buzzerFailDuration = 150;
                    }
                }

                if (opts.has("buzzerFailRepeat")) {
                    Object b_rF = opts.opt("buzzerFailRepeat");
                    buzzerFailRepeat = ((Number) b_rF).intValue();

                    if(buzzerFailRepeat <= 0){
                        buzzerFailRepeat = 3;
                    }
                }

                if (opts.has("buzzerPin")) {
                    Object b_pin = opts.opt("buzzerPin");
                    buzzerPin = ((Number) b_pin).intValue();

                    if(buzzerPin <= 0 || buzzerPin > 3){
                        buzzerPin = 3;
                    }
                }



                try {
                    //Log.d(TAG, "Avvio la lettura delle GPIO!");

                    // avvio il loop di lettura delle GPIO
                    new GpioPolling().execute();

                } catch (Exception ex){
                    PluginResult result = new PluginResult(PluginResult.Status.ERROR, ex.getMessage());
                    callbackContext.sendPluginResult(result);
                }
            }
        });
    }




    /**
     * LETTURA TAG IN LOOP
     */
    private void readTagLoop(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                try {
                    //Log.d(TAG, "Avvio lettura tag!");
                    BuzzeredInThisReading = false;

                    // Loop
                    long t= System.currentTimeMillis();
                    long end = t + readRfidDuration;
                    Integer numberTags = 0;


                    org.json.JSONObject JsonOut = new org.json.JSONObject();
                    org.json.JSONObject JsonTagList = new org.json.JSONObject();
                    while(System.currentTimeMillis() < end) {

                        // devo misurare da tutte le antenne previste per l'ingresso GPIO a cui è stato applicato un livello logico alto
                        Integer choicedIn = 0;
                        switch (triggeredInput) {
                            case 0:
                                choicedIn = Input0Antennas;
                                break;
                            case 1:
                                choicedIn = Input1Antennas;
                                break;
                            case 2:
                                choicedIn = Input2Antennas;
                                break;
                            case 3:
                                choicedIn = Input3Antennas;
                                break;
                        }

                        String choicedInBin = Integer.toString(choicedIn, 2); // converto in binario
                        String cIB = "0000" + choicedInBin;
                        choicedInBin =  cIB.substring(cIB.length() - 4); // Antenne da leggere (binario)

                        Log.d(TAG, "AAAA INPUT: "+ triggeredInput +", InputAntenna associato: "+choicedIn+ " con binario: " + choicedInBin);

                        for(int a = 0; a <= 3; a++){
                            Log.d(TAG, "*** ANTENNA: " + a);
                            char antIsActive = choicedInBin.charAt(invertIndex(a)); // Valore (leggere/non leggere) per antenna attuale (intero)
                            Log.d(TAG, "*** Valore: " + antIsActive);
                            if(antIsActive == '1'){

                                // Leggo i tag di questa antenna
                                String caen_src = "Source_" + a; // ex: Source_0 for antenna 0
                                CAENRFIDLogicalSource mySource = reader.GetSource(caen_src); // seleziono l'antenna
                                Log.d(TAG, "AAAA ANtenna selezionata");

                                CAENRFIDTag[] myTags = mySource.InventoryTag(); // leggo i tag
                                Log.d(TAG, "ANTENNA LETTA");

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

                                        JsonTagList.put("tag_"+x, obj);
                                        numberTags++;
                                    }

                                    if(BuzzeredInThisReading == false && BuzzerIsActive == false){ // avvio subito il buzzer
                                        BuzzerRepeat = 1;
                                        BuzzeredInThisReading = true;
                                        BuzzerRealDuration = buzzerDuration;
                                        new Buzzer().execute(); // Attivo la segnalazione di avvenuta lettura
                                    }

                                }

                            }
                        }

                        Thread.sleep( 10 );
                    }

                    if(numberTags == 0){
                        if(BuzzerIsActive == false){
                            BuzzerRepeat = buzzerFailRepeat;
                            BuzzerRealDuration = buzzerFailDuration;
                            new Buzzer().execute(); // Attivo la segnalazione di lettura senza tag
                        }
                    } else {
                        JsonOut.put("TagList", JsonTagList);
                    }

                    // manca il FILTER "ARRAY" x togliere i doppioni


                    JsonOut.put("NumberTags", numberTags);
                    // Aggiungo dati utili per la verifica:
                    /*
                    JsonOut.put("Input0Antennas", Input0Antennas);
                    JsonOut.put("Input1Antennas", Input1Antennas);
                    JsonOut.put("Input2Antennas", Input2Antennas);
                    JsonOut.put("Input3Antennas", Input3Antennas);
                    JsonOut.put("readRfidDuration", readRfidDuration);
                    JsonOut.put("isConnected", isConnected);
                    JsonOut.put("triggeredInput", triggeredInput);
                    JsonOut.put("activeBuzzer", activeBuzzer);
                    JsonOut.put("buzzerDuration", buzzerDuration);
                    JsonOut.put("buzzerPin", buzzerPin);
                    */


                    PluginResult.Status status = PluginResult.Status.OK;
                    PluginResult result = new PluginResult(PluginResult.Status.OK, JsonOut.toString()); // ListArr.toString()
                    callbackContext.sendPluginResult(result);

                } catch (Exception ex){
                    callbackContext.error(ex.getMessage());
                }
            }
        });
    }



    /**
     * DISCONNESSIONE
     */
    private void disconnect(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                try {
                    Log.d(TAG, "Tento disconnessione!");
                    reader.Disconnect();

                    PluginResult result = new PluginResult(PluginResult.Status.OK, "Disconnesso"); // ListArr.toString()
                    callbackContext.sendPluginResult(result);

                } catch (Exception ex){
                    reader = new CAENRFIDReader();
                    PluginResult result = new PluginResult(PluginResult.Status.ERROR, ex.getMessage()); // ListArr.toString()
                    callbackContext.sendPluginResult(result);
                }
            }
        });
    }




    private void resetConnection(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                try {
                    reader = new CAENRFIDReader();
                    PluginResult result = new PluginResult(PluginResult.Status.OK, "Reset avvenuto"); // ListArr.toString()
                    callbackContext.sendPluginResult(result);

                } catch (Exception ex){
                    PluginResult result = new PluginResult(PluginResult.Status.ERROR, ex.getMessage()); // ListArr.toString()
                    callbackContext.sendPluginResult(result);
                }
            }
        });
    }








    // OTHER METHOD


    /**
     * LETTURA STATO GPIO
     */
    private void readGpio(final JSONObject opts, final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {

                try {
                    Log.d(TAG, "Avvio la lettura delle GPIO!");
                    //reader.Connect(port);

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





    /**
     * LETTURA TAG
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



    /*
     * Java algorithm to convert bite to hex
     */
    private static String bytesToHex(byte[] hashInBytes) {

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();

    }






    /*
     * Loop to detect trigger GPIO
     */
    private class GpioPolling extends AsyncTask<Void, Integer, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Log.d(TAG, "AAAAA PRE EXECUTE");
        }
        @Override
        protected String doInBackground(Void... arg0)
        {
            //Log.d(TAG, "AAAAA DO IN BG");

            // estraggo la configurazione (direzione) dei GPIO
            Integer InputSetting = 0xF;
            try{
                InputSetting = reader.GetIODirection();
            } catch(Exception e){
            }
            String InputString = Integer.toString(InputSetting, 2); // Direzione dei GPIO (in binario)
            String Is = "0000" + InputString;
            InputString =  Is.substring(Is.length() - 4);

            Boolean iterate = true;
            int InputVal = 0x0;
            try {
                Integer n = 3, m = 0;
                while(iterate){
                    //Log.d(TAG, "AAAAA ANCORA A ZERO!");

                    char str_index = InputString.charAt(n); // Direzione del GPIO n (formato stringa)
                    Integer index = Integer.parseInt(String.valueOf(str_index)); // Direzione del GPIO n (intero)

                    InputVal = reader.GetIO(); // Valore applicato agli Ingressi
                    String InputValString = Integer.toString(InputVal, 2); // Valore applicato agli Ingressi (in binario)
                    // mi assicuro che abbia lunghezza pari a 4 bit
                    String Ivs = ("0000" + InputValString);
                    InputValString =  Ivs.substring(Ivs.length() - 4);

                    char str_val_index = InputValString.charAt(n); // Valore applicato all'Ingresso n (stringa)
                    Integer value = Integer.parseInt(String.valueOf(str_val_index)); // Valore applicato all'Ingresso n (intero)

                    //Log.d(TAG, "AAAAA STATO: STEP N="+n+" ConfigDirection:="+InputString+" InputValString:"+InputValString);
                    //Log.d(TAG, "AAAAA QUINDI: index="+index+" value:="+value);
                    if(index == 0 && value > 0){ // se il GPIO n-esimo è settato come ingresso e c'è una tensione d'ingresso positiva
                        iterate = false;
                        triggeredInput = m;
                        //Log.d(TAG, "AAAAA ESCO");

                        BuzzerRepeat = buzzerStartRepeat;
                        BuzzerRealDuration = buzzerStartDuration;
                        new Buzzer().execute();
                        // verrà chiamato il metodo onPostExecute che a sua volta richiamerà readTagLoop
                    }

                    n--;
                    m ++;
                    if(m > 3) {
                        m = 0;
                        n = 3;
                    }
                }
            }
            catch (Exception e) {
                Log.d(TAG, "AAAAA ECCEZIONE" + e.toString());
            }

            //Log.d(TAG, "AAAAA CICLO TERMINATO");
            return "Valore estratto: " +  InputVal;
        }
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            //Log.d(TAG, "AAAAA ON UPDATE");
            super.onProgressUpdate(values);

        }
        @Override
        protected void onPostExecute(String result)
        {
            //Log.d(TAG, "AAAAA ON POST EXECUTE");

            JSONObject jnull = new JSONObject();
            try{
                Thread.sleep(1000); // ritardo necessario per evitare che il timing del lampeggiamento venga impostato dalla funzione di lettura tag, laddove un tag sia subito disponibile!
                readTagLoop(jnull, callbackContext);
            }
            catch(Exception e){}



            super.onPostExecute(result);
        }
    }





    /*
     * Loop to detect trigger GPIO
     */
    private class Buzzer extends AsyncTask<Void, Integer, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "AAAAA SEZIONE BUZZER. ActiveBuzzer="+activeBuzzer.toString());

        }
        @Override
        protected String doInBackground(Void... arg0)
        {
            if(activeBuzzer && BuzzerIsActive == false){
                // estraggo la configurazione (direzione) dei GPIO
                Integer InputSetting = 0xF;
                try{
                    InputSetting = reader.GetIODirection();
                } catch(Exception e){}
                String InputString = Integer.toString(InputSetting, 2); // Direzione dei GPIO (binario)
                String is = ("0000" + InputString);
                InputString = is.substring(is.length() - 4); // Direzione dei GPIO (stringa)
                Log.d(TAG, "AAAAA Direzione GPIO="+InputString);

                // verifico se il pin a cui è assegnato il buzzer è effettivamente di uscita
                char valueBuzzerActivation = InputString.charAt(invertIndex(buzzerPin));
                Log.d(TAG, "AAAAA Pin designato per il buzzer (invertito): "+invertIndex(buzzerPin)+" - Valore di direzione: "+valueBuzzerActivation);

                if(valueBuzzerActivation == '1'){

                    for(int y = 0; y < BuzzerRepeat; y++){
                        try{
                            Log.d(TAG, "AAAAA ATTIVO LA SEGNALAZIONE D'USCITA");
                            Integer actualValue = reader.GetIO();
                            String actualValueString = Integer.toString(actualValue, 2); // Valore attualmente applicato agli ingressi (binario)

                            String avs = ("0000" + actualValueString);
                            actualValueString = avs.substring(avs.length() - 4); // Valore attualmente applicato agli ingressi (stringa)


                            StringBuilder plainText = new StringBuilder(actualValueString);
                            plainText.setCharAt(invertIndex(buzzerPin), '1'); // Nuovo
                            actualValue = Integer.parseInt((String) plainText.toString(), 2);

                            reader.SetIO(actualValue);
                            BuzzerIsActive = true;
                        }
                        catch(Exception e){}

                        try{
                            Thread.sleep(BuzzerRealDuration);

                            // recupero l'attuale valore degli ingressi e poi modifico il valore del pin di buzzer
                            Integer actualValue = reader.GetIO();

                            String actualValueString = Integer.toString(actualValue, 2); // Attuale valore applicato agli ingressi (binario)
                            String aVs = "0000" + actualValueString;
                            actualValueString =  aVs.substring(aVs.length() - 4); // Attuale valore applicato agli ingressi (stringa)

                            StringBuilder plainText = new StringBuilder(actualValueString);

                            plainText.setCharAt(invertIndex(buzzerPin), '0');
                            actualValue = Integer.parseInt((String) plainText.toString(), 2);

                            reader.SetIO(actualValue);
                            BuzzerIsActive = false;
                        }
                        catch(Exception e){}

                        if(y > 0){ // ritardo per gli eventuali cicli di ripetizione del buzzer
                            try{
                                Thread.sleep(10);
                            }
                            catch(Exception e){};
                        }
                    }
                }
            }


            if(activeBuzzer && BuzzerIsActive == true){

            }


            Log.d(TAG, "AAAAA DISATTIVO LA SEGNALAZIONE D'USCITA");
            return "";
        }
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
        }
    }



    /*
     * Java algorithm to "invert" index order
     */
    private static Integer invertIndex(Integer Index) {
        if(Index == 0){
            return 3;
        } else if(Index == 1){
            return 2;
        } else if(Index == 2){
            return 1;
        } else return 0;
    }


}