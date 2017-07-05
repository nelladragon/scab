var Web3 = require('web3');
var util = require('ethereumjs-util');
var tx = require('ethereumjs-tx');
var lightwallet = require('eth-lightwallet');
var txutils = lightwallet.txutils;

var web3 = new Web3(
    new Web3.providers.HttpProvider('https://rinkeby.infura.io/RN5ouaGxbPB4wrYNgM2Q')
);

var user1 = require('../../../user1')
var user2 = require('../../../user2')
var user1 = require('../../../user3')
var con = require('./contract')



var contract = web3.eth.contract(con.interface1);
var instance = contract.at(con.address);


// Debug
console.log(" Connected: " + web3.isConnected() );
console.log(" Code: " + web3.eth.getCode(con.address));
console.log(" Check Value (should be 5): " + instance.check());


console.log("User 1 information");
showInfo(user1.address, instance);







function showInfo(userAddress, instance) {
    console.log("------------------");

    console.log(" Get information for: inboxIsEmpty(address _recipient) constant returns (bool)");
    instance.inboxIsEmpty.call(userAddress, function(err, result) {
        console.log(" Result: inboxIsEmpty(address _recipient) constant returns (bool)");
        if(err) {
            console.log(err);
        } else {
            console.log("  isEmpty: " + result);
        }
    });

    console.log(" Get information for: inboxSize(address _recipient) constant returns (uint)");
    instance.inboxSize.call(userAddress, function(err, result) {
        console.log(" Result: inboxSize(address _recipient) constant returns (uint)");
        if(err) {
            console.log(err);
        } else {
            console.log("  Number of messages: " + result);
        }
    });


/*    console.log(" Get information for: getMsg(address _recipient) constant returns (address, uint, string)");
    instance.getMsg.call(userAddress, function(err, result) {
        console.log(" Result: getMsg(address _recipient) constant returns (address, uint, string)");
        if(err) {
            console.log(err);
        } else {
            console.log("  Address: " + result[0]);
            console.log("  Timestamp: " + result[1]);
            console.log("  Message: " + result[2]);
        }
    });
*/

/*
    console.log(" Get information for: getSignedPublicEncKey(address addr) constant returns (string)");
    instance.getSignedPublicEncKey.call(userAddress, function(err, result) {
    	console.log(" Result: getSignedPublicEncKey(address addr) constant returns (string)");
        if(err) {
            console.log(err);
        } else {
            console.log("  Signed public key: " + String(result));
        }
    });

*/
}


