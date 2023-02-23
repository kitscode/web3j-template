package io.template.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@Component
public class Web3jClient {

    @Value("${chain.rpc}")
    private String rpc;

    @Value("${chain.privateKey}")
    private String privateKey;

    private Web3j web3j;

    private void setupWeb3j() {
        if (web3j == null)
            web3j = Web3j.build(new HttpService(rpc));
    }


    /**
     * get user address from private key
     */
    public String userAddress() {
        Credentials credentials = Credentials.create(privateKey);
        return credentials.getAddress();
    }

    /**
     * get user nonce
     *
     * @param address
     * @return
     * @throws Exception
     */
    public BigInteger nonce(String address) throws Exception {
        setupWeb3j();
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameter.valueOf("latest")).send();
        return ethGetTransactionCount.getTransactionCount();
    }

    /**
     * get private key user nonce
     *
     * @return
     */
    public BigInteger userNonce() throws Exception {
        Credentials credentials = Credentials.create(privateKey);
        return nonce(credentials.getAddress());
    }

    /**
     * get current network gas price
     *
     * @return
     * @throws Exception
     */
    public BigInteger gasPrice() throws Exception {
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        return ethGasPrice.getGasPrice();
    }

    /**
     * call contract - view functions
     *
     * @param function callData
     * @param contract contract address
     * @param address  user address
     * @return
     * @throws Exception
     */
    public List<Type> viewContractFunction(Function function, String contract, String address) throws Exception {
        setupWeb3j();
        String encodedFunction = FunctionEncoder.encode(function);
        EthCall response = web3j.ethCall(Transaction.createEthCallTransaction(address, contract, encodedFunction),
                DefaultBlockParameterName.LATEST).sendAsync().get();
        return FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
    }

    /**
     * call contract - send functions
     *
     * @param transaction
     * @return txHash
     * @throws Exception
     */
    public String sendContractFunction(RawTransaction transaction) throws Exception {
        setupWeb3j();

        Credentials credentials = Credentials.create(privateKey);
        byte[] signMessage = TransactionEncoder.signMessage(transaction, credentials);
        String hexValue = Numeric.toHexString(signMessage);

        EthSendTransaction response = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        return response.getTransactionHash();
    }


    /**
     * get transaction status from tx hash
     *
     * @param hash
     * @return
     */
    public void txStatus(String hash) {
        setupWeb3j();
        try {
            EthGetTransactionReceipt resp = web3j.ethGetTransactionReceipt(hash).send();
            if (resp.getTransactionReceipt().isPresent()) {
                TransactionReceipt receipt = resp.getTransactionReceipt().get();
                log.info("receipt: " + receipt);
                log.info("status: " + receipt.getStatus());
                String status = StringUtils.equals(receipt.getStatus(), "0x1") ? "success" : "fail";
                log.info("res: " + status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
