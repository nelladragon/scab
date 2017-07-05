var Web3 = require('web3');
var util = require('ethereumjs-util');
var tx = require('ethereumjs-tx');
var lightwallet = require('eth-lightwallet');
var txutils = lightwallet.txutils;

var web3 = new Web3(
    new Web3.providers.HttpProvider('https://rinkeby.infura.io/RN5ouaGxbPB4wrYNgM2Q')
);


var user = require('../../../user2')
var contract = require('./contract')

/*
web3.eth.getGasPrice(function(err, result) {
        if(err) {
            console.log(err);
        } else {
            // Display the transaction number.
            console.log("Gas Price: " + result);
        }
});
*/


function sendRaw(rawTx) {
    var privateKey = new Buffer(user.key, 'hex');
    var transaction = new tx(rawTx);
    transaction.sign(privateKey);
    var serializedTx = transaction.serialize().toString('hex');
    web3.eth.sendRawTransaction(
    '0x' + serializedTx, function(err, result) {
        if(err) {
            console.log(err);
        } else {
            // Display the transaction number.
            console.log(result);
        }
    });
}


var rawTx = {
    nonce: web3.toHex(web3.eth.getTransactionCount(user.address)),
    gasLimit: web3.toHex(800000),
//    gasPrice: web3.toHex(20000000000),
    gasPrice: web3.toHex(50000000000),
    data: '0x' + contract.bytecode + '0000000000000000000000000000000000000000000000000000000000000005'

};


sendRaw(rawTx);


