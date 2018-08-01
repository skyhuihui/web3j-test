package com.ethjava.we3jUtil;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

/**
 * @ProjectName: autumn
 * @Package: com.zx.token
 * @ClassName: ${TYPE_NAME}
 * @Description: 空投以太币 一笔交易一个转账
 * @Author: skyhuihui
 * @CreateDate: 2018/7/15 15:37
 * @UpdateUser: Neil.Zhou
 * @UpdateDate: 2018/7/15 15:37
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public class ETHAirdrop {
    //正式环境
    private static Web3j web3j = Web3j.build(new HttpService(""));
    //	https://ropsten.infura.io 测试环境
    //private static Web3j web3j = Web3j.build(new HttpService(""));
    //导出信息
    private static String info="";

    //账户地址
    private static String fromAddress="0xBeD05734BB3718b295ED1484B6b06461b4B4Ada8";

    private static String privateKey="";
    //文件位置
    private static String file="E:\\空投\\托管账户地址1.txt";

    public static void main(String [] str) {

        readTxt();


    }

    /**
     * 代币转账
     */
    public static void testTokenTransaction(Web3j web3j, String privateKey, String toAddress, double amount, BigInteger nonce) throws IOException {
        //gasPraice 手动设置
        BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(2), Convert.Unit.GWEI).toBigInteger();
//        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
//        BigInteger gasPrice = ethGasPrice.getGasPrice().multiply(new BigInteger("100")).divide(new BigInteger("100"));
        //gasLimit
        BigInteger gasLimit = BigInteger.valueOf(21000);
        //转账人私钥
        Credentials credentials = Credentials.create(privateKey);
        //创建交易，这里是转0.5个以太币
        BigInteger value = Convert.toWei(Double.toString(amount), Convert.Unit.ETHER).toBigInteger();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, gasPrice, gasLimit, toAddress, value);
         //签名Transaction，这里要对交易做签名
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        //发送交易
        EthSendTransaction ethSendTransaction = null;
        try {
            ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String transactionHash = ethSendTransaction.getTransactionHash();

        //获得到transactionHash后就可以到以太坊的网站上查询这笔交易的状态了
        String str="nonce:"+nonce+" gasPrice"+gasPrice+"   gasLimit:"+gasLimit+"   接受地址："+toAddress+"   总量："+value+"  Hash:"+transactionHash;
        System.out.println(str);
    }

    //读文件数据 发转账
    public  static  void  readTxt(){
        FileInputStream fis = null;
        InputStreamReader isr = null;
        //用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
        BufferedReader br = null;
        try {
            String str = "";
            String str1 = "";
            fis = new FileInputStream(file);
            // 从文件系统中的某个文件中获取字节
            // InputStreamReader 是字节流通向字符流的桥梁,
            isr = new InputStreamReader(fis);
            // 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
            br = new BufferedReader(isr);
            BigInteger nonce=getNonce(fromAddress);
            while ((str = br.readLine()) != null) {
                str = str.replaceAll("\\s*", "");
                //String [] strings=str.split("，");
                testTokenTransaction(web3j, privateKey, str, Double.parseDouble("0.00033"),  nonce);
                nonce=BigInteger.ONE.add(nonce);
                str1 += str + "\n";
            }
            // 当读取的一行不为空时,把读到的str的值赋给str1
        } catch (FileNotFoundException e) {
            System.out.println("找不到指定文件");
        } catch (IOException e) {
            System.out.println("读取文件失败");
        } finally {
            try {
                br.close();
                isr.close();
                fis.close();
                // 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //获取地址nonce
    public static BigInteger getNonce(String address){
        BigInteger nonce;
        EthGetTransactionCount ethGetTransactionCount = null;
        try {
            ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ethGetTransactionCount == null) return new BigInteger("0");
        nonce = ethGetTransactionCount.getTransactionCount();
        System.out.println("nonce " + nonce);
        if(nonce.equals(BigInteger.ZERO))
             return new BigInteger("1");
        else return nonce;
    }
}
