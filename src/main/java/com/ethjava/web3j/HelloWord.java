package com.ethjava.web3j;

import com.ethjava.utils.Environment;
import com.ethjava.we3jUtil.sol.AirdorpSol;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;

/**
 * @ProjectName: web3j-sample
 * @Package: com.ethjava.utils
 * @ClassName: ${HelloWord}
 * @Description: 部署合约
 * @Author: skyhuihui
 * @CreateDate: 2018/7/19 18:17
 * @UpdateUser: Neil.Zhou
 * @UpdateDate: 2018/7/19 18:17
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public class HelloWord {

    private static Web3j web3j;
    private static String privateKey="";
    private static String contractAddress="";

    public  static  void main(String [] str) throws Exception {
        web3j = Web3j.build(new HttpService(Environment.RPC_URL));

        //转账人私钥
        Credentials credentials = Credentials.create(privateKey);
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        BigInteger gasPrice = ethGasPrice.getGasPrice().multiply(new BigInteger("105")).divide(new BigInteger("100"));
        //gasLimit
        BigInteger gasLimit = BigInteger.valueOf(100000);
        //部署合约
        AirdorpSol contract=AirdorpSol.deploy(web3j, credentials, gasPrice, gasLimit).send();
        String contractAddress = contract.getContractAddress();
        System.out.println("合约地址："+contractAddress);
        System.out.println("合约是否有效"+contract.isValid());
//        //调用合约
//        Hello contract =Hello.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
//        TransactionReceipt transactionReceipt = contract.set("nihaoshijie").send();
//        String value=contract.get().send();
//        System.out.println("现在的值："+value);

    }

}
