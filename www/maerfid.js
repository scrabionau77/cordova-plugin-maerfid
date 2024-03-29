/**
 * MAE CAEN PLUGIN
 * 
 * Copyright (c) 2019, Daniele Pellerucci @ Maestrale Information Technology
 * www.maestrale.it
 */

var maerfid = {
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
    connect: function (opts, successCallback, errorCallback) {
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
            console.log("MaeRfid.connect failure: error callback parameter not a function");
            return;
        }
    
        if (typeof successCallback != "function") {
            console.log("MaeRfid.connect failure: success callback parameter must be a function");
            return;
        }
    
        cordova.exec(successCallback, errorCallback, 'MaeRfid', 'connect', [{'opts': opts}]);
    },
    configCaen: function (opts, successCallback, errorCallback) {
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
            console.log("MaeRfid.configCaen failure: error callback parameter not a function");
            return;
        }
    
        if (typeof successCallback != "function") {
            console.log("MaeRfid.configCaen failure: success callback parameter must be a function");
            return;
        }
    
        cordova.exec(successCallback, errorCallback, 'MaeRfid', 'configCaen', [{'opts': opts}]);
    },
    waitRfid: function (opts, successCallback, errorCallback) {
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
            console.log("MaeRfid.waitRfid failure: error callback parameter not a function");
            return;
        }
    
        if (typeof successCallback != "function") {
            console.log("MaeRfid.waitRfid failure: success callback parameter must be a function");
            return;
        }
    
        cordova.exec(successCallback, errorCallback, 'MaeRfid', 'waitRfid', [{'opts': opts}]);
    },
    disconnect: function (opts, successCallback, errorCallback) {
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
            console.log("MaeRfid.disconnect failure: error callback parameter not a function");
            return;
        }
    
        if (typeof successCallback != "function") {
            console.log("MaeRfid.disconnect failure: success callback parameter must be a function");
            return;
        }
    
        cordova.exec(successCallback, errorCallback, 'MaeRfid', 'disconnect', [{'opts': opts}]);
    },
    reset: function (opts, successCallback, errorCallback) {
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
            console.log("MaeRfid.reset failure: error callback parameter not a function");
            return;
        }
    
        if (typeof successCallback != "function") {
            console.log("MaeRfid.reset failure: success callback parameter must be a function");
            return;
        }
    
        cordova.exec(successCallback, errorCallback, 'MaeRfid', 'resetReader', [{'opts': opts}]);
    },

    readGpioConfig: function (opts, successCallback, errorCallback) {
        opts = {}; // not used for now!
        if (errorCallback == null) {
            errorCallback = function () {
            };
        }
    
        if (typeof errorCallback != "function") {
            console.log("MaeRfid.readConfig failure: error callback parameter not a function");
            return;
        }
    
        if (typeof successCallback != "function") {
            console.log("MaeRfid.readConfig failure: success callback parameter must be a function");
            return;
        }
    
        cordova.exec(successCallback, errorCallback, 'MaeRfid', 'readGpio', [{'opts': opts}]);
    },
    readGpioValue: function (opts, successCallback, errorCallback) {
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
    readRfid: function (opts, successCallback, errorCallback) {
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
            console.log("MaeRfid.readTag failure: error callback parameter not a function");
            return;
        }
    
        if (typeof successCallback != "function") {
            console.log("MaeRfid.readTag failure: success callback parameter must be a function");
            return;
        }
    
        cordova.exec(successCallback, errorCallback, 'MaeRfid', 'readTag', [{'opts': opts}]);
    },

    setupBluetooth: function (opts, successCallback, errorCallback) {
        opts = {}; // not used for now!
        if (errorCallback == null) {
            errorCallback = function () {
            };
        }
    
        if (typeof errorCallback != "function") {
            console.log("MaeRfid.setupBluetooth failure: error callback parameter not a function");
            return;
        }
    
        if (typeof successCallback != "function") {
            console.log("MaeRfid.setupBluetooth failure: success callback parameter must be a function");
            return;
        }

        cordova.exec(successCallback, errorCallback, 'MaeRfid', 'setupBluetooth', [{'opts': opts}]);
    },

}


module.exports = maerfid;