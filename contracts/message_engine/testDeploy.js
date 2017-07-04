var Web3 = require('web3');
var util = require('ethereumjs-util');
var tx = require('ethereumjs-tx');
var lightwallet = require('eth-lightwallet');
var txutils = lightwallet.txutils;

var web3 = new Web3(
    new Web3.providers.HttpProvider('https://rinkeby.infura.io/')
);


var user1 = require('../../../user1')
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
    var privateKey = new Buffer(user1.key, 'hex');
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
    nonce: web3.toHex(web3.eth.getTransactionCount(user1.address)),
    gasLimit: web3.toHex(800000),
    gasPrice: web3.toHex(200000000000),
    data: '0x' + contract.bytecode
};


sendRaw(rawTx);


