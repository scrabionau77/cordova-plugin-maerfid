/**
 * MAE CAEN PLUGIN
 * 
 * Copyright (c) 209, Daniele Pellerucci @ Maestrale Information Technology
 */


var exec = cordova.require("cordova/exec");

/**
 * Constructor.
 *
 * @returns {MaeRfid}
 */
function MaeRfid() {
}








//-------------------------------------------------------------------
MaeRfid.prototype.requestPermission = function (opts, successCallback, errorCallback) {
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

    exec(successCallback, errorCallback, 'MaeRfid', 'requestPermission', [{'opts': opts}]);
};


var MaeRfid2 = {
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
    
        exec(successCallback, errorCallback, 'MaeRfid', 'requestPermission', [{'opts': opts}]);
    }
}

//var maeRfid = new MaeRfid();
//module.exports = maeRfid;
module.exports = MaeRfid2;