/*
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package com.nelladragon.common.crypto;


import com.nelladragon.common.crypto.digest.Keccak256;




import static java.util.Arrays.copyOfRange;

public class Keccak {

    private static Keccak256 k256Digest = new Keccak256();



    public static byte[] keccak256(byte[] input) {
        k256Digest.reset();
        return k256Digest.digest(input);
    }

    public static byte[] keccak256(byte[] input1, byte[] input2) {
        k256Digest.reset();
        k256Digest.update(input1);
        return k256Digest.digest(input2);
    }




    /**
     * Calculates RIGTMOST160(SHA3(input)). This is used in address
     * calculations. *
     * 
     * @param input
     *            - data
     * @return - 20 right bytes of the hash keccak of the data
     */
    public static byte[] addressHash(byte[] input) {
        byte[] hash = keccak256(input);
        return copyOfRange(hash, 12, hash.length);
    }

    /**
     * The way to calculate new address inside ethereum
     *
     * @param addr
     *            - creating addres
     * @param nonce
     *            - nonce of creating address
     * @return new address
     */
//    public static byte[] calcNewAddr(byte[] addr, byte[] nonce) {
//
//        byte[] encSender = RLP.encodeElement(addr);
//        byte[] encNonce = RLP.encodeBigInteger(new BigInteger(1, nonce));
//
//        return addressHash(RLP.encodeList(encSender, encNonce));

//    }

}
