package com.nelladragon.scab.nameservice;

import com.nelladragon.common.crypto.Keccak;
import com.nelladragon.common.crypto.digest.Keccak256;

import java.nio.charset.Charset;
import java.util.StringTokenizer;

/**
 * Name services.
 */

public class NameService {
    private static final Charset UTF8 = Charset.forName("UTF8");

    // Ethereum Name Service main net address.
    public static final String ETHEREUM_MAIN_ADDRESS = "0x314159265dd8dbb310642f98f50c066173c1259b,";
    // Ethereum Name Service Rinkeby address.
    public static final String ETHEREUM_RINKEBY_ADDRESS = "0xe7410170f87102df0055eb195163a03b7f2bff4a";


    //
//    var node = namehash('myname.eth');
//    Ask the ENS registry for the resolver responsible for that node:
//
//    var resolverAddress = ens.resolver(node);
//    Create an instance of a resolver contract at that address:
//
//    var resolver = resolverContract.at(resolverAddress);
//    Finally, ask the resolver what the address is:
//
//            resolver.addr(node);







//    unction namehash (inputName) {
//        // Reject empty names:
//        var node = ''
//        for (var i = 0; i < 32; i++) {
//            node += '00'
//        }
//
//        name = normalize(inputName)
//
//        if (name) {
//            var labels = name.split('.')
//
//            for(var i = labels.length - 1; i >= 0; i--) {
//                var labelSha = sha3(labels[i])
//                node = sha3(new Buffer(node + labelSha, 'hex'))
//            }
//        }
//
//        return `0x${node}`
//    }
    public byte[] namehash(String ethereumDomainName) {
        if (ethereumDomainName == null) {
            return null;
        }
        StringTokenizer tok = new StringTokenizer(".");
        int numElements = tok.countTokens() + 1;
        String[] labels = new String[numElements];
        for (int i=0; i < numElements; i++) {
            labels[i] = (String) tok.nextElement();
        }

        byte[] node = new byte[32];
        for (int i=labels.length; i >= 0; i--) {
            byte[] labelSha = Keccak.keccak256(labels[i].getBytes(UTF8));
            node = Keccak.keccak256(node, labelSha);
        }
        return node;
    }
}
