# Cordova Plugin MaeRfid
================================

Android Cordova plugin to connect CAEN HADRON Rfid reader to Android device through USB OTG cable. Tested on android 8.
Note: This plugin does not expose all the methods provided by the CAEN device. 
It contains some methods that allow the reading of RFID tags and the use of GPIO pins.

## Installation

    cordova plugin add https://github.com/scrabionau77/cordova-plugin-maerfid.git

It is also possible to install via repo url directly ( unstable )

    cordova plugin add https://github.com/scrabionau77/cordova-plugin-maerfid.git


### Supported Platforms

- Android

The plugin creates the object `maerfid`


## HOW IT WORKS ##
There are some essential steps to enable RFID reading:
1) Enable serial USB communication (method `requestPermission` show below);
2) Enable connection to the CAEN Reader device (method `connect`);
3) Set the operating parameters of the CAEN device (method `configCaen`)

After that it is possible to proceed in two ways:
- Use the automatic method that waits for RFID reads when a GPIO is activated (method `waitRfid`);
- Use the other methods available;



## METHODS ##

### REQUEST USB CONNECTION PERMISSION
This is the first required operation to communicate with the device.
The plugin exposes method `requestPermission` for this. This method needs `options` object that contains vid, pid e driver configurations (these data are provided by the manufacturer. For Caen Hadron they are show below).

```js
maerfid.rerequestPermissionadGpio({
        vid: '21E1',
        pid: '0089',
        driver: 'CdcAcmSerialDriver'
    },
    function(success){
        // authorization granted
    },
    function(error){}
);
```


### CONNECT TO CAEN RFID READER
The `connect` method establishes communication with the CAEN Reader device. You'll can use the `disconnect` method to close the connection.

```js
maerfid.connect({},
    function(success){
        // establishes communication
    },
    function(error){}
);
```



### CONFIG CAEN RFID READER
You can configure the device using `configCaen` method. It needs `options` object that contains GPIO pins `direction` (input and output settings) and `value` configurations (set high/low value for GPIO pins set as output). Note: the value setting is ignored for bits configured as input.
By default, all GPIO are set as input (equivalent to options.gpioConfig: 0x0).

```js
var options = {
    gpioConfig: 0x3, // Hex value. 0 = INPUT, 1 = OUTPUT
    outputVal: 0x0  // Hex value. 0 = Low output value, 1 = High output value
};
maerfid.configCaen(options, function(success){}, function(error){});
```

The following table show `gpioConfig` value and I/O configuration pins:

| gpioConfig | GPIO3    | GPIO2    | GPIO1    | GPIO0    |
|------------|:--------:|:--------:|:--------:|:--------:|
| 0x0        |  0 = IN  |  0 = IN  |  0 = IN  |  0 = IN  |
| 0x1        |  0 = IN  |  0 = IN  |  0 = IN  |  1 = OUT |
| 0x2        |  0 = IN  |  0 = IN  |  1 = OUT |  0 = IN  |
| 0x3        |  0 = IN  |  0 = IN  |  1 = OUT |  1 = OUT |
| 0x4        |  0 = IN  |  1 = OUT |  0 = IN  |  0 = IN  |
| 0x5        |  0 = IN  |  1 = OUT |  0 = IN  |  1 = OUT |
| 0x6        |  0 = IN  |  1 = OUT |  1 = OUT |  0 = IN  |
| 0x7        |  0 = IN  |  1 = OUT |  1 = OUT |  1 = OUT |
| 0x8        |  1 = OUT |  0 = IN  |  0 = IN  |  0 = IN  |
| 0x9        |  1 = OUT |  0 = IN  |  0 = IN  |  1 = OUT |
| 0xA        |  1 = OUT |  0 = IN  |  1 = OUT |  0 = IN  |
| 0xB        |  1 = OUT |  0 = IN  |  1 = OUT |  1 = OUT |
| 0xC        |  1 = OUT |  1 = OUT |  0 = IN  |  0 = IN  |
| 0xD        |  1 = OUT |  1 = OUT |  0 = IN  |  1 = OUT |
| 0xE        |  1 = OUT |  1 = OUT |  1 = OUT |  0 = IN  |
| 0xF        |  1 = OUT |  1 = OUT |  1 = OUT |  1 = OUT |


The following table show `outputVal` variable value and corresponding Output pin value (remember: the value setting is ignored for pins configured as input):

| gpioConfig | GPIO3     | GPIO2     | GPIO1     | GPIO0     |
|------------|:---------:|:---------:|:---------:|:---------:|
| 0x0        |  0 = LOW  |  0 = LOW  |  0 = LOW  |  0 = LOW  |
| 0x1        |  0 = LOW  |  0 = LOW  |  0 = LOW  |  1 = HIGH |
| 0x2        |  0 = LOW  |  0 = LOW  |  1 = HIGH |  0 = LOW  |
| 0x3        |  0 = LOW  |  0 = LOW  |  1 = HIGH |  1 = HIGH |
| 0x4        |  0 = LOW  |  1 = HIGH |  0 = LOW  |  0 = LOW  |
| 0x5        |  0 = LOW  |  1 = HIGH |  0 = LOW  |  1 = HIGH |
| 0x6        |  0 = LOW  |  1 = HIGH |  1 = HIGH |  0 = LOW  |
| 0x7        |  0 = LOW  |  1 = HIGH |  1 = HIGH |  1 = HIGH |
| 0x8        |  1 = HIGH |  0 = LOW  |  0 = LOW  |  0 = LOW  |
| 0x9        |  1 = HIGH |  0 = LOW  |  0 = LOW  |  1 = HIGH |
| 0xA        |  1 = HIGH |  0 = LOW  |  1 = HIGH |  0 = LOW  |
| 0xB        |  1 = HIGH |  0 = LOW  |  1 = HIGH |  1 = HIGH |
| 0xC        |  1 = HIGH |  1 = HIGH |  0 = LOW  |  0 = LOW  |
| 0xD        |  1 = HIGH |  1 = HIGH |  0 = LOW  |  1 = HIGH |
| 0xE        |  1 = HIGH |  1 = HIGH |  1 = HIGH |  0 = LOW  |
| 0xF        |  1 = HIGH |  1 = HIGH |  1 = HIGH |  1 = HIGH |





### WAIT FOR RFID
The HADRON Reader device allows you to connect up to 4 antennas for reading RFID tags.
This method involves reading the RFID tags when activating one of the GPIO pins. In other words, when a positive voltage (>= 5V) is applied to one of the GPIO pins set as input, the reading of the RFID tags starts.
This method requires the `options` parameter. With this parameter we can choose:
1) Which antennas correspond to each GPIO input (for each input it is possible to choose one or more antennas from which to read the RFID);
2) How long to read the RFIDs after applying the positive voltage to the GPIO input.
3) When the reading is complete you can use an output GPIO to activate lights or other devices. The option parameter contains 3 dedicated keys (activation, duration of activation, GPIO pin to be activated)

The reading is repeated several times in the time frame set in the options parameter. When finished, the list of read tags is returned.
The list of read tags is filtered to prevent the same tag from being repeated in the returned list.
The method stops when the result is returned. Therefore it must be called back to start further reading.

Note: only the GPIOs set as input will be read.

IMPORTANT: Activate the reading only from the antennas actually connected to the HADRON Reader to avoid possible damage to the device.

```js
var options = {
    Input0Antennas: 0x3, // see below table for details
    Input1Antennas: 0x3,
    Input2Antennas: 0x3,
    Input3Antennas: 0x3,
    readRfidDuration: 50000,    // milliseconds
    activeBuzzer: true,
    buzzerDuration: 1000        // milliseconds
    buzzerPin: 3                // 0 to 3
};

maerfid.waitRfid(options, function(success){}, function(error){});
```


The following table show `Input0Antennas`, `Input1Antennas`, `Input2Antennas`, `Input3Antennas` values and corresponding antennas readed.
Note: value must be greater than 0.

| Value      | ANT 3    | ANT 2    | ANT 1    | ANT 0    |
|------------|:--------:|:--------:|:--------:|:--------:|
| 0x1        |          |          |          |   ✔     |
| 0x2        |    |    |   ✔     |       |
| 0x3        |    |    |  ✔ |  ✔ |
| 0x4        |    |  ✔ |    |    |
| 0x5        |    |  ✔ |    |  ✔ |
| 0x6        |    |  ✔ |  ✔ |    |
| 0x7        |    |  ✔ |  ✔ |  ✔ |
| 0x8        |  ✔ |    |    |    |
| 0x9        |  ✔ |    |    |  ✔ |
| 0xA        |  ✔ |    |  ✔ |    |
| 0xB        |  ✔ |    |  ✔ |  ✔ |
| 0xC        |  ✔ |  ✔ |    |    |
| 0xD        |  ✔ |  ✔ |    |  ✔ |
| 0xE        |  ✔ |  ✔ |  ✔ |    |
| 0xF        |  ✔ |  ✔ |  ✔ |  ✔ |






### SETUP GPIO
You can configure the GPIO pin. The plugin exposes method `configGpioCaen` for this. This method needs `options` object that contains pins `direction` (input and output settings) and `value` configurations (set high/low value for output pins). Note: the value setting is ignored for bits configured as input

```js
var options = {
    gpioConfig: 0x3, // Hex value. 0 = INPUT, 1 = OUTPUT
    outputVal: 0x0  // Hex value. 0 = Low output value, 1 = High output value
};
maerfid.configGpioCaen(options, function(success){}, function(error){});
```



The following table show `gpioConfig` value and I/O configuration pins:

| gpioConfig | GPIO3    | GPIO2    | GPIO1    | GPIO0    |
|------------|:--------:|:--------:|:--------:|:--------:|
| 0x0        |  0 = IN  |  0 = IN  |  0 = IN  |  0 = IN  |
| 0x1        |  0 = IN  |  0 = IN  |  0 = IN  |  1 = OUT |
| 0x2        |  0 = IN  |  0 = IN  |  1 = OUT |  0 = IN  |
| 0x3        |  0 = IN  |  0 = IN  |  1 = OUT |  1 = OUT |
| 0x4        |  0 = IN  |  1 = OUT |  0 = IN  |  0 = IN  |
| 0x5        |  0 = IN  |  1 = OUT |  0 = IN  |  1 = OUT |
| 0x6        |  0 = IN  |  1 = OUT |  1 = OUT |  0 = IN  |
| 0x7        |  0 = IN  |  1 = OUT |  1 = OUT |  1 = OUT |
| 0x8        |  1 = OUT |  0 = IN  |  0 = IN  |  0 = IN  |
| 0x9        |  1 = OUT |  0 = IN  |  0 = IN  |  1 = OUT |
| 0xA        |  1 = OUT |  0 = IN  |  1 = OUT |  0 = IN  |
| 0xB        |  1 = OUT |  0 = IN  |  1 = OUT |  1 = OUT |
| 0xC        |  1 = OUT |  1 = OUT |  0 = IN  |  0 = IN  |
| 0xD        |  1 = OUT |  1 = OUT |  0 = IN  |  1 = OUT |
| 0xE        |  1 = OUT |  1 = OUT |  1 = OUT |  0 = IN  |
| 0xF        |  1 = OUT |  1 = OUT |  1 = OUT |  1 = OUT |


The following table show `outputVal` value and Output pin value (remember: the value setting is ignored for bits configured as input):

| gpioConfig | GPIO3     | GPIO2     | GPIO1     | GPIO0     |
|------------|:---------:|:---------:|:---------:|:---------:|
| 0x0        |  0 = LOW  |  0 = LOW  |  0 = LOW  |  0 = LOW  |
| 0x1        |  0 = LOW  |  0 = LOW  |  0 = LOW  |  1 = HIGH |
| 0x2        |  0 = LOW  |  0 = LOW  |  1 = HIGH |  0 = LOW  |
| 0x3        |  0 = LOW  |  0 = LOW  |  1 = HIGH |  1 = HIGH |
| 0x4        |  0 = LOW  |  1 = HIGH |  0 = LOW  |  0 = LOW  |
| 0x5        |  0 = LOW  |  1 = HIGH |  0 = LOW  |  1 = HIGH |
| 0x6        |  0 = LOW  |  1 = HIGH |  1 = HIGH |  0 = LOW  |
| 0x7        |  0 = LOW  |  1 = HIGH |  1 = HIGH |  1 = HIGH |
| 0x8        |  1 = HIGH |  0 = LOW  |  0 = LOW  |  0 = LOW  |
| 0x9        |  1 = HIGH |  0 = LOW  |  0 = LOW  |  1 = HIGH |
| 0xA        |  1 = HIGH |  0 = LOW  |  1 = HIGH |  0 = LOW  |
| 0xB        |  1 = HIGH |  0 = LOW  |  1 = HIGH |  1 = HIGH |
| 0xC        |  1 = HIGH |  1 = HIGH |  0 = LOW  |  0 = LOW  |
| 0xD        |  1 = HIGH |  1 = HIGH |  0 = LOW  |  1 = HIGH |
| 0xE        |  1 = HIGH |  1 = HIGH |  1 = HIGH |  0 = LOW  |
| 0xF        |  1 = HIGH |  1 = HIGH |  1 = HIGH |  1 = HIGH |





### DISCONNECT
Close HADRON device connection.

```js
maerfid.disconnect({}, function(success){}, function(error){});
```




### READ GPIO CONFIG
You can read GPIO direction settings. The plugin exposes method `readConfig` for this. Note: by default, all GPIO pins are set as input.

```js
maerfid.readGpioConfig({}, function(success){}, function(error){});
```



### READ GPIO VALUES
You can read GPIO pin value. The plugin exposes method `readGpioValue` for this. Note: during reading, the pins configured as output are read and their value corresponds to the one set previously with the `configGpioCaen` method.

```js
maerfid.readGpioValue({}, function(success){}, function(error){});
```


### RFID READING
After obtaining the communication permission, you can call this method to read the RFIDs picked up by the antenna. The plugin esposes `readRfid` method for this. This method needs `options` object that contains `src` number (indicating the antenna to read from).

```js
maerfid.readRfid({
        src: 0 // 0 to 3 (Caen Hadron has up to 4 antennas)
    },
    function(success){
        
    },
    function(error){}
);
```



THIS IS OLD CONTENT!!!!!!!!!!!!!!!!!!!!
| AZTEC         |    ✔    |  ✔  |     ✔    |

`success` and `fail` are callback functions. Success is passed an object with data, type and cancelled properties. Data is the text representation of the barcode data, type is the type of barcode detected and cancelled is whether or not the user cancelled the scan.

A full example could be:
```js
   cordova.plugins.barcodeScanner.scan(
      function (result) {
          alert("We got a barcode\n" +
                "Result: " + result.text + "\n" +
                "Format: " + result.format + "\n" +
                "Cancelled: " + result.cancelled);
      },
      function (error) {
          alert("Scanning failed: " + error);
      },
      {
          preferFrontCamera : true, // iOS and Android
          showFlipCameraButton : true, // iOS and Android
          showTorchButton : true, // iOS and Android
          torchOn: true, // Android, launch with the torch switched on (if available)
          saveHistory: true, // Android, save scan history (default false)
          prompt : "Place a barcode inside the scan area", // Android
          resultDisplayDuration: 500, // Android, display scanned text for X ms. 0 suppresses it entirely, default 1500
          formats : "QR_CODE,PDF_417", // default: all but PDF_417 and RSS_EXPANDED
          orientation : "landscape", // Android only (portrait|landscape), default unset so it rotates with the device
          disableAnimations : true, // iOS
          disableSuccessBeep: false // iOS and Android
      }
   );
```

## Encoding a Barcode ##

The plugin creates the object `cordova.plugins.barcodeScanner` with the method `encode(type, data, success, fail)`.

Supported encoding types:

* TEXT_TYPE
* EMAIL_TYPE
* PHONE_TYPE
* SMS_TYPE

```
A full example could be:

   cordova.plugins.barcodeScanner.encode(cordova.plugins.barcodeScanner.Encode.TEXT_TYPE, "http://www.nytimes.com", function(success) {
            alert("encode success: " + success);
          }, function(fail) {
            alert("encoding failed: " + fail);
          }
        );
```

## iOS quirks ##

Since iOS 10 it's mandatory to add a `NSCameraUsageDescription` in the `Info.plist`.

`NSCameraUsageDescription` describes the reason that the app accesses the user's camera.
When the system prompts the user to allow access, this string is displayed as part of the dialog box. If you didn't provide the usage description, the app will crash before showing the dialog. Also, Apple will reject apps that access private data but don't provide an usage description.

To add this entry you can use the `edit-config` tag in the `config.xml` like this:

```
<edit-config target="NSCameraUsageDescription" file="*-Info.plist" mode="merge">
    <string>To scan barcodes</string>
</edit-config>
```

## Windows quirks ##

* Windows implementation currently doesn't support encode functionality.

* On Windows 10 desktop ensure that you have Windows Media Player and Media Feature pack installed.

## Thanks on Github ##

So many -- check out the original [iOS](https://github.com/phonegap/phonegap-plugins/tree/DEPRECATED/iOS/BarcodeScanner),  [Android](https://github.com/phonegap/phonegap-plugins/tree/DEPRECATED/Android/BarcodeScanner) and
[BlackBerry 10](https://github.com/blackberry/WebWorks-Community-APIs/tree/master/BB10-Cordova/BarcodeScanner) repos.

## Licence ##

The MIT License

Copyright (c) 2010 Matt Kane

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
