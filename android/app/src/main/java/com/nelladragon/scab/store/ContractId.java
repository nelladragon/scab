package com.nelladragon.scab.store;

import android.util.Base64;

import com.nelladragon.common.crypto.Rand;

/**
 * A 256 bit number which is the id for the contract.
 *
 * Use a 256 bit number so that they can be generated randomly and be (almost) guarenteed not to clash.
 *
 *
 * TODO: Maybe the string format would be better as 0xHexValue
 */

public class ContractId {
    public static final int CONTRACT_ID_LENGTH_IN_BYTES = 32;


    String b64Id;



    public ContractId(byte[] id) {
        this.b64Id = Base64.encodeToString(id, Base64.URL_SAFE);
    }


    public ContractId(String b64Id) {
        this.b64Id = b64Id;
    }


    public static ContractId generateRandomContractId() {
        byte[] rand = Rand.generateRandom(CONTRACT_ID_LENGTH_IN_BYTES);
        return new ContractId(rand);
    }


    public String getId() {
        return this.b64Id;
    }


    public String toString() {
        return this.b64Id;
    }

}
