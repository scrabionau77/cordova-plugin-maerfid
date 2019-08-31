var browser = require('cordova/platform');

function myfun () {
    return 'porchiddio';
}


module.exports = {
    bestemmia: function (success, error) {
        setTimeout(function () {
            success({
                esempio: myfun(),
                madonna: "porca"
            });
        }, 0);
    }
};

require('cordova/exec/proxy').add('MaeRfid', module.exports);