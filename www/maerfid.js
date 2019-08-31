var argscheck = require('cordova/argscheck');
var channel = require('cordova/channel');
var utils = require('cordova/utils');
var exec = require('cordova/exec');
var cordova = require('cordova');

channel.createSticky('onCordovaInfoReady');
// Tell cordova channel to wait on the CordovaInfoReady event
channel.waitForInitialization('onCordovaInfoReady');


function MaeRfid () {
    this.available = false;
    this.dio = 'santo';
    this.cordova = null;

    var me = this;

    channel.onCordovaReady.subscribe(function () {
        me.getInfo(function (info) {
            me.available = true;
            me.dio = 'porco';
            channel.onCordovaInfoReady.fire();
        }, function (e) {
            me.available = false;
            me.dio = 'sbudellato';
            utils.alert('[ERROR] Error initializing Cordova: ' + e);
        });
    });
}

/**
 * Get device info
 *
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
MaeRfid.prototype.bestemmia = function (successCallback, errorCallback) {
    argscheck.checkArgs('fF', 'MaeRfid.getInfo', arguments);
    exec(successCallback, errorCallback, 'MaeRfid', 'bestemmia', []);
};

module.exports = new MaeRfid();