package io.template;

import io.template.client.Web3jClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import io.template.service.ChainService;

import javax.annotation.Resource;
import java.math.BigInteger;

@Slf4j
@SpringBootTest(classes = Application.class)
class ChainServiceTest {

    @Resource
    private ChainService chainService;

    @Resource
    private Web3jClient web3jClient;

    @Value("${chain.contract}")
    private String contract;

    @Test
    void totalSupply() throws Exception {
        BigInteger totalSupply = chainService.getTotalSupply(contract);
        log.info("totalSupply:" + totalSupply);
        Assertions.assertTrue(totalSupply.compareTo(BigInteger.ZERO) > 0);
    }

    @Test
    public void txHash() {
        web3jClient.txStatus("0xa00ba9837db7f7b987541b77ee8fe3e238ca86e1b443375f2f28047fb8645467");
    }

}
