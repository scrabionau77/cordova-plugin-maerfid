function requestPermission(success, error) {
    var code = window.prompt("Type Y to success response, any other to fail response");
    if(code == "Y") {
        var result = {
            success: true
        };
        success(result);
    } else {
        error("No permission");
    }
}

function connect(type, data, success, errorCallback) {
    success();
}

function configCaen(type, data, success, errorCallback) {
    success();
}

function configCaen(type, data, success, errorCallback) {
    var now = new Date();
    var result = {
        NumberTags: 1,
        TagList: [
            { Antenna: 0, Id: "E28011700000020F71C535F1", TimeStamp: now.getTime() }
        ]
    }
    success(result);
}

function disconnect(type, data, success, errorCallback) {
    success();
}

function readGpioConfig(type, data, success, errorCallback) {
    success("1000");
}

function readGpioValue(type, data, success, errorCallback) {
    success("0000");
}

function configCaen(type, data, success, errorCallback) {
    var now = new Date();
    var result = {
        NumberTags: 1,
        TagList: [
            { Antenna: 0, Id: "E28011700000020F71C535F1", TimeStamp: now.getTime() }
        ]
    }
    success(result);
}

module.exports = {
    requestPermission: requestPermission,
    connect: connect,
    configCaen: configCaen,
    waitRfid: waitRfid,
    disconnect: disconnect,
    readGpioConfig: readGpioConfig,
    readGpioValue: readGpioValue,
    readRfid: readRfid
};

require("cordova/exec/proxy").add("maerfid",module.exports);