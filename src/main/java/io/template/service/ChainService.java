package io.template.service;

import java.math.BigInteger;

public interface ChainService {

    /**
     * get token total supply
     *
     * @param contract token address
     * @return
     * @throws Exception
     */
    BigInteger getTotalSupply(String contract) throws Exception;

    /**
     * get owner from id
     *
     * @param contract
     * @param tokenId
     * @return
     * @throws Exception
     */
    String getOwnerOf(String contract, long tokenId) throws Exception;

    /**
     * if token of the id is minted
     *
     * @param contract
     * @param tokenId
     * @return
     */
    boolean isIdMinted(String contract, long tokenId);

    /**
     * mint new nft
     * 
     * @param contract token address
     * @param tokenId nft-id
     * @return txHash
     * @throws Exception
     */
    String sendMint(String contract, long tokenId) throws Exception;


}
