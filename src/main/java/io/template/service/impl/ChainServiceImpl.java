package io.template.service.impl;

import io.template.client.Web3jClient;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.RawTransaction;
import io.template.service.ChainService;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Service
public class ChainServiceImpl implements ChainService {

    private final String ZERO_ADDRESS = "0x0000000000000000000000000000000000000000";

    @Resource
    private Web3jClient web3jClient;

    @Override
    public BigInteger getTotalSupply(String contract) throws Exception {

        Function function = new Function("totalSupply", Collections.emptyList(), Collections.singletonList(new TypeReference<Uint256>() {
        }));
        List<Type> result = web3jClient.viewContractFunction(function, contract, ZERO_ADDRESS);
        return (BigInteger) result.get(0).getValue();
    }

    @Override
    public String getOwnerOf(String contract, long tokenId) throws Exception {
        Function function = new Function("ownerOf", Collections.singletonList(new Uint256(tokenId)), Collections.singletonList(new TypeReference<Address>() {
        }));
        List<Type> result = web3jClient.viewContractFunction(function, contract, ZERO_ADDRESS);
        return (String) result.get(0).getValue();
    }

    @Override
    public boolean isIdMinted(String contract, long tokenId) {
        try {
            String owner = getOwnerOf(contract, tokenId);
            return !owner.equals(ZERO_ADDRESS);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String sendMint(String contract, long tokenId) throws Exception {

        // default recipient user
        Function function = new Function("mint", Arrays.asList(new Address(web3jClient.userAddress()), new Uint256(tokenId)), Collections.emptyList());
        String encodedFunction = FunctionEncoder.encode(function);

        // create transaction
        RawTransaction tx = RawTransaction.createTransaction(web3jClient.userNonce(), web3jClient.gasPrice(),
                BigInteger.valueOf(1_000_000), contract, new BigInteger("0"), encodedFunction);

        return web3jClient.sendContractFunction(tx);
    }


}
