/**
 * MAE CAEN PLUGIN
 * 
 * Copyright (c) 2019, Daniele Pellerucci @ Maestrale Information Technology
 * www.maestrale.it
 */

var maerfid = {
    configCaen: function (opts, successCallback, errorCallback) {
        if (typeof opts === 'function') {  //user did not pass opts
            console.log("MaeRfid.configCaen failure: options parameter is required!");
            return;
        }
        console.log('CONFIGURAZIONE');
        if (errorCallback == null) {
            errorCallback = function () {
            };
        }
        console.log('CONFIGURAZIONE2');
        if (typeof errorCallback != "function") {
            console.log("MaeRfid.configCaen failure: error callback parameter not a function");
            return;
        }
    
        if (typeof successCallback != "function") {
            console.log("MaeRfid.configCaen failure: success callback parameter must be a function");
            return;
        }
    
        cordova.exec(successCallback, errorCallback, 'MaeRfid', 'configCaen', [{'opts': opts}]);
    },
    readGpio: function (opts, successCallback, errorCallback) {
        opts = {}; // not used for now!
        if (errorCallback == null) {
            errorCallback = function () {
            };
        }
    
        if (typeof errorCallback != "function") {
            console.log("MaeRfid.readGpio failure: error callback parameter not a function");
            return;
        }
    
        if (typeof successCallback != "function") {
            console.log("MaeRfid.readGpio failure: success callback parameter must be a function");
            return;
        }
    
        cordova.exec(successCallback, errorCallback, 'MaeRfid', 'readGpio', [{'opts': opts}]);
    },
    requestPermission: function (opts, successCallback, errorCallback) {
        if (typeof opts === 'function') {  //user did not pass opts
            errorCallback = successCallback;
            successCallback = opts;
            opts = {};
        }
        if (errorCallback == null) {
            errorCallback = function () {
            };
        }
    
        if (typeof errorCallback != "function") {
            console.log("MaeRfid.requestPermission failure: error callback parameter not a function");
            return;
        }
    
        if (typeof successCallback != "function") {
            console.log("MaeRfid.requestPermission failure: success callback parameter must be a function");
            return;
        }
    
        cordova.exec(successCallback, errorCallback, 'MaeRfid', 'requestPermission', [{'opts': opts}]);
    },
    openSerial: function (opts, successCallback, errorCallback) {
        if (errorCallback == null) {
            errorCallback = function () {
            };
        }
        if (typeof opts === 'function') {  //user did not pass opts
            errorCallback = successCallback;
            successCallback = opts;
            opts = {};
        }
        
        if (typeof errorCallback != "function") {
            console.log("MaeRfid.openSerial failure: error callback parameter not a function");
            return;
        }
    
        if (typeof successCallback != "function") {
            console.log("MaeRfid.openSerial failure: success callback parameter must be a function");
            return;
        }
    
        cordova.exec(successCallback, errorCallback, 'MaeRfid', 'openSerial', [{'opts': opts}]);
    }

}


module.exports = maerfid;