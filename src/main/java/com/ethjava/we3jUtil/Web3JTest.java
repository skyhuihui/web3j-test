package com.ethjava.we3jUtil;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @ProjectName: autumn
 * @Package: com.zx.token
 * @ClassName: ${TYPE_NAME}
 * @Description: web3J测试方法
 * @Author: skyhuihui
 * @CreateDate: 2018/7/15 13:22
 * @UpdateUser: Neil.Zhou
 * @UpdateDate: 2018/7/15 13:22
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public class Web3JTest {

    //正式环境
    private static Web3j web3j = Web3j.build(new HttpService(""));
    //	https://ropsten.infura.io 测试环境
    //private static Web3j web3j = Web3j.build(new HttpService(""));



    public static void main(String [] str) throws Exception {
        //获取指定钱包的以太币余额

        BigInteger integer=web3j.ethGetBalance("0x2207358972E37F663a5480dBaa09715E8b0FC4FF",DefaultBlockParameterName.LATEST).send().getBalance();
        System.out.println("以太币余额为："+ integer);

        //获取指定地址的交易串
        Function function = new Function(
                "查询余额方法名称",
                Arrays.asList(new Address("0x2207358972E37F663a5480dBaa09715E8b0FC4FF")),
                Arrays.asList(new TypeReference<Address>(){})
        );
        String string= FunctionEncoder.encode(function);
        System.out.println("交易串为:"+string);

        //获取NONCE
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount("0x2207358972E37F663a5480dBaa09715E8b0FC4FF", DefaultBlockParameterName.LATEST).sendAsync().get();
        System.out.println("Nonce为："+ethGetTransactionCount.getTransactionCount());


    }



}
