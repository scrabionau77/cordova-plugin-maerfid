# Cordova Plugin MaeRfid
================================

[![Build Status](https://travis-ci.org/phonegap/phonegap-plugin-barcodescanner.svg)](https://travis-ci.org/phonegap/phonegap-plugin-barcodescanner)

Android Cordova plugin to connect CAEN Rfid reader

## Installation

This requires phonegap 7.1.0+ ( current stable v8.0.0 )

    cordova plugin add https://github.com/scrabionau77/cordova-plugin-maerfid.git

It is also possible to install via repo url directly ( unstable )

    cordova plugin add https://github.com/scrabionau77/cordova-plugin-maerfid.git


### Supported Platforms

- Android

The plugin creates the object `maerfid`


### METHODS


## SETUP GPIO ##
You can configure the GPIO pin. The plugin exposes method `configCaen` for this. This method needs `options` object that contains pins `direction` (input and output settings) and `value` configurations (set high/low value for output pins). Note: the value setting is ignored for bits configured as input

```js
options = {
    gpioConfig = 0x0; // Hex value. 0 = INPUT, 1 = OUTPUT
    outputVal = 0xf;  // Hex value. 0 = Low output value, 1 = High output value
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



## READ GPIO ##
You can read GPIO pin. The plugin exposes method `readGpio` for this. Note: during reading, the pins configured as output are read and their value corresponds to the one set previously with the `configCaen` method.

```js
maerfid.readGpio({}, function(success){}, function(error){});
```



## REQUEST CONNECTION PERMISSION ##
This is the first required operation to communicate with the device.
The plugin exposes method `requestPermission` for this. This method needs `options` object that contains vid, pid e driver configurations (these data are provided by the manufacturer. For Caen Hadron they are show below).

```js
maerfid.rerequestPermissionadGpio({
        vid: '21E1',
        pid: '0089',
        driver: 'CdcAcmSerialDriver'
    },
    function(success){
        // now you can call openSerial method
    },
    function(error){}
);
```



## RFID READING ##
After obtaining the communication permission, you can call this method to read the RFIDs picked up by the antenna. The plugin esposes `openSerial` method for this. This method needs `options` object that contains `src` number (indicating the antenna to read from).

```js
maerfid.openSerial({
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
