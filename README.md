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
var options = {
        vid: '21E1',
        pid: '0089',
        driver: 'CdcAcmSerialDriver'
    };

maerfid.rerequestPermissionadGpio(options,
    function(success){
        // authorization granted
    },
    function(error){}
);
```


### CONNECT TO CAEN RFID READER
The `connect` method establishes communication with the CAEN Reader device on USB port. You'll can use the `disconnect` method to close the connection.

```js
maerfid.connect({},
    function(success){
        // established communication
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
3) When the reading is complete you can use an output GPIO to activate lights or other devices. The option parameter contains 3 dedicated keys (enable/disable, duration of activation, GPIO pin to be activated)

The Rfid reading is repeated several times in the time frame set in the options parameter. When finished, the list of read tags is returned.
The list of read tags is filtered to prevent the same tag from being repeated in the returned list.
The method stops when the result is returned. Therefore it must be called back to start further reading.

Note: only the GPIOs set as input will be read.

IMPORTANT: Activate the reading only from the antennas actually connected to the HADRON Reader to avoid possible damage to the device.

```js
var options = {
    Input0Antennas: 0x3,        // see below table for details
    Input1Antennas: 0x3,
    Input2Antennas: 0x3,
    Input3Antennas: 0x3,
    readRfidDuration: 5000,     // milliseconds
    activeBuzzer: true,
    buzzerDuration: 1000,       // milliseconds
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


### DISCONNECT
Close HADRON device connection.

```js
maerfid.disconnect({}, function(success){}, function(error){});
```


## OTHER METHODS ##


### READ GPIO CONFIG
You can read GPIO direction settings. The plugin exposes method `readGpioConfig` for this. Note: by default, all GPIO pins are set as input.

```js
maerfid.readGpioConfig({}, function(success){}, function(error){});
```



### READ GPIO VALUES
You can read GPIO pin value. The plugin exposes method `readGpioValue` for this. Note: during reading, the pins configured as output are read and their value corresponds to the one set previously with the `configCaen` method.

```js
maerfid.readGpioValue({}, function(success){}, function(error){});
```


### RFID READING
After first 3 essential steps you can call this method to read the RFIDs picked up by a single antenna. The plugin esposes `readRfid` method for this. This method needs `options` object that contains `src` number (indicating the antenna to read from).

```js
maerfid.readRfid({
        src: 0 // 0 to 3 (Caen Hadron has up to 4 antennas)
    },
    function(success){
        
    },
    function(error){}
);
```



________

A full example could be:
```js
    maerfid.requestPermission({
            vid: '21E1',
            pid: '0089',
            driver: 'CdcAcmSerialDriver'
        }, //'FtdiSerialDriver' // or any other
        function(success){
            console.log(success);

            maerfid.connect({}, function(success){
                var options = {
                    gpioConfig: 0x8, // Hex value. 0 = INPUT, 1 = OUTPUT
                    outputVal: 0  // Hex value. 0 = Low output value, 1 = High output value
                };

                maerfid.configCaen(options, function(success){
                    // Device ready
                    
                }, function(error){
                    // ...
                });


            }, function(error){
                // ...
            });
        },
        function(error){
            // ...
        }
    );



    $('#button').on('tap', function(e){
        e.preventDefault();

        var options = {
            Input0Antennas: 0x1,
            Input1Antennas: 0x2,
            Input2Antennas: 0x4,
            readRfidDuration: 5000,
            activeBuzzer: true,
            buzzerDuration: 1500,
            buzzerPin: 3
        }
        maerfid.waitRfid(options, function(success){
            console.log(success); // list of tag
            
        }, function(error){
            // ...
        });
    });
```


## Thanks ##

Developed by Daniele Pellerucci and Roberto Vitali for Maestrale Information Technology (https://www.maestrale.it/).
Special thanks to CAEN RFID (https://www.caenrfid.com/)

## Licence ##

The MIT License

Copyright (c) 2019 Maestrale Information Technology (https://www.maestrale.it/)

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
