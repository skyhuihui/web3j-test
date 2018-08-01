package com.ethjava.we3jUtil;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainId;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @ProjectName: autumn
 * @Package: com.zx.token
 * @ClassName: ${TYPE_NAME}
 * @Description: 代币空投 一笔交易多个空投  执行空头前应先授信
 * @Author: skyhuihui
 * @CreateDate: 2018/7/15 15:37
 * @UpdateUser: Neil.Zhou
 * @UpdateDate: 2018/7/15 15:37
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public class TestAirdrop {
    //正式环境
    private static Web3j web3j = Web3j.build(new HttpService(""));
    //	https://ropsten.infura.io 测试环境
    //private static Web3j web3j = Web3j.build(new HttpService(""));

    //账户地址
    private static String fromAddress="";

    //合约地址正式
    private static String abi="0xd8d783e4094be5c76a080c31d16235d1b0499e09";

    //合约地址测试
    //private static String abi="0xcd7fab5f0ae977deee5e47106303d9c6eb93e1f5";

    //币地址(KT)
    private static String contractAddress="0xddb5bfae38ce17f3ad300dd635f30acbcc275287";

    private static String privateKey="";

    public static void main(String [] str) throws Exception {

        readTxt();

    }
    /**
     * 代币转账
     */
    private static void testTokenTransaction(Web3j web3j, String privateKey, String contractAddress, String toAddress, String amounts, int decimals, BigInteger nonce) throws Exception {
        //gasPraice 手动设置
        BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(3), Convert.Unit.GWEI).toBigInteger();
//        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
//        BigInteger gasPrice = ethGasPrice.getGasPrice().multiply(new BigInteger("120")).divide(new BigInteger("100"));
        //gasLimit
        BigInteger gasLimit = BigInteger.valueOf(7000000);
        BigInteger value = BigInteger.ZERO;
        //循环取出地址 金额
        List<String> allAddresses= Arrays.asList(toAddress.split(","));
        List<String> allAmount= Arrays.asList(amounts.split(","));
        List<BigInteger> bigIntegers =new ArrayList<>();
        for(int i=0; i<allAmount.size();i++){
            BigDecimal ddd= BigDecimal.TEN.pow(decimals);
            BigDecimal num = new BigDecimal(allAmount.get(i));
            num=num.multiply(ddd);
            String [] num2=num.toString().split("\\.");
            BigInteger amout = new BigInteger(num2[0]);
            bigIntegers.add(amout);
        }
        Function function = new Function(
                "deliverTokens",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(contractAddress),
                        new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                                org.web3j.abi.Utils.typeMap(allAddresses, org.web3j.abi.datatypes.Address.class)),
                        new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                                org.web3j.abi.Utils.typeMap(bigIntegers, org.web3j.abi.datatypes.generated.Uint256.class))),
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        byte chainId = ChainId.NONE;
        String signedData;
        try {
            signedData = TestAirdrop.signTransaction(nonce, gasPrice, gasLimit, abi, value, data, chainId, privateKey);
            if (signedData != null) {
                EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();
                System.out.println("hash:"+ethSendTransaction.getTransactionHash());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 签名交易
     */
    public static String signTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
                                         BigInteger value, String data, byte chainId, String privateKey) throws IOException {
        byte[] signedMessage;
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                to,
                value,
                data);

        if (privateKey.startsWith("0x")) {
            privateKey = privateKey.substring(2);
        }
        ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(privateKey, 16));
        Credentials credentials = Credentials.create(ecKeyPair);

        if (chainId > ChainId.NONE) {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        } else {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        }

        String hexValue = Numeric.toHexString(signedMessage);
        return hexValue;
    }

    //读文件数据 发转账
    public  static  void  readTxt() throws IOException {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        //用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
        BufferedReader br = null;
        try {
            String str = "";
            //总地址
            String str1 = "";
            //总转账金额
            String str2 = "";
            fis = new FileInputStream("E:\\空投\\mchain空投.txt");
            // 从文件系统中的某个文件中获取字节
            // InputStreamReader 是字节流通向字符流的桥梁,
            isr = new InputStreamReader(fis);
            // 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
            br = new BufferedReader(isr);
            int i=0,n=1;
            BigInteger nonce=getNonce(fromAddress);
            while ((str = br.readLine()) != null) {
                str = str.replaceAll("\\s*", "");
                String [] strings=str.split(",");
                str1 += strings[0]+",";
                str2 += strings[1]+",";
                if(i == 154){
                    System.out.println("第几批："+n);
                    String strsub1 = str1.substring(0,str1.length()-1);
                    String strsub2 = str2.substring(0,str2.length()-1);
                    testTokenTransaction(web3j, privateKey
                            , contractAddress, strsub1, strsub2, 18, nonce);
                    nonce=BigInteger.ONE.add(nonce);
                    str1="";str2="";i=0;n++;
                }
                i++;
            }
            System.out.println("最后一次空投");
            String strsub1 = str1.substring(0,str1.length()-1);
            String strsub2 = str2.substring(0,str2.length()-1);
            testTokenTransaction(web3j, privateKey
                    , contractAddress, strsub1, strsub2, 18, nonce);
            // 当读取的一行不为空时,把读到的str的值赋给str1
        } catch (FileNotFoundException e) {
            System.out.println("找不到指定文件");
        } catch (IOException e) {
            System.out.println("读取文件失败");
        } catch (Exception e) {
            System.out.println("空投失败");
        } finally {
            br.close();
            isr.close();
            fis.close();
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
