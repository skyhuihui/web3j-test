package com.ethjava.we3jUtil;

import com.ethjava.utils.Environment;
import com.ethjava.we3jUtil.sol.Erc20Sol;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;

/**
 * @ProjectName: web3j-sample
 * @Package: com.ethjava.we3jUtil
 * @ClassName: ${TYPE_NAME}
 * @Description: 调用部署erc20 合约
 * @Author: skyhuihui
 * @CreateDate: 2018/7/25 18:37
 * @UpdateUser: Neil.Zhou
 * @UpdateDate: 2018/7/25 18:37
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public class Erc20 {

    private static Web3j web3j;
    //私钥
    private static String privateKey="";
    //币合约地址
    private static String contractAddress="0xa4dF8a11d5D66640E6084b1309fBE1cC5a84f52c";

    public  static  void main(String [] str) throws Exception {
        web3j = Web3j.build(new HttpService(Environment.RPC_URL));

        //转账人私钥
        Credentials credentials = Credentials.create(privateKey);
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        BigInteger gasPrice = ethGasPrice.getGasPrice().multiply(new BigInteger("105")).divide(new BigInteger("100"));
        //gasLimit
        BigInteger gasLimit = BigInteger.valueOf(100000);
        //调用合约
        Erc20Sol contract =Erc20Sol.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
        //授信合约地址，授信金额
        TransactionReceipt transactionReceipt = contract.approve("",new BigInteger("")).send();
        System.out.println("Hash："+transactionReceipt.getTransactionHash());

    }

}
