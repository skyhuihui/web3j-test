package com.ethjava.we3jUtil;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainId;
import org.web3j.utils.Numeric;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: autumn
 * @Package: com.zx.token
 * @ClassName: ${TYPE_NAME}
 * @Description: 代币空投 一笔交易一个转账
 * @Author: skyhuihui
 * @CreateDate: 2018/7/15 15:37
 * @UpdateUser: Neil.Zhou
 * @UpdateDate: 2018/7/15 15:37
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public class OneAirdrop {
    //正式环境
    //private static Web3j web3j = Web3j.build(new HttpService(""));
    //	https://ropsten.infura.io 测试环境
    private static Web3j web3j = Web3j.build(new HttpService(""));
    //导出信息
    private static String info="";
    //账户地址
    private static String fromAddress="";

    //币地址
    private static String contractAddress="0xd5c0f4e10801717f683f546fad3e4481d2b324ba";

    private static String privateKey="";

    public static void main(String [] str) {

        readTxt();
        System.out.println(info);
    }
    /**
     * 代币转账
     */
    private static void testTokenTransaction(Web3j web3j, String privateKey, String contractAddress, String toAddress, double amount, int decimals, BigInteger nonce) throws IOException {
        //gasPraice 手动设置
        //BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(10), Convert.Unit.GWEI).toBigInteger();
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        BigInteger gasPrice = ethGasPrice.getGasPrice().multiply(new BigInteger("100")).divide(new BigInteger("100"));
        //gasLimit
        BigInteger gasLimit = BigInteger.valueOf(60000);
        BigInteger value = BigInteger.ZERO;
        //token转账参数
        String methodName = "transfer";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        Address tAddress = new Address(toAddress);
        Uint256 tokenValue = new Uint256(BigDecimal.valueOf(amount).multiply(BigDecimal.TEN.pow(decimals)).toBigInteger());
        inputParameters.add(tAddress);
        inputParameters.add(tokenValue);
        TypeReference<Bool> typeReference = new TypeReference<Bool>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        byte chainId = ChainId.NONE;
        String signedData;
        String str="nonce:"+nonce+" gasPrice"+gasPrice+"   gasLimit:"+gasLimit+"   接受地址："+toAddress+"   总量："+value;
        info+=str;
        try {
            signedData = OneAirdrop.signTransaction(nonce, gasPrice, gasLimit, contractAddress, value, data, chainId, privateKey);
            if (signedData != null) {
                EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedData).send();
                System.out.println(ethSendTransaction.getTransactionHash());
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
        System.out.println("参数：gasPrice:"+gasPrice+"   gaslimit:"+gasLimit+"   nonce:"+nonce+"   value"+value+"   data:"+data);
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
    public  static  void  readTxt(){
        FileInputStream fis = null;
        InputStreamReader isr = null;
        //用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
        BufferedReader br = null;
        try {
            String str = "";
            String str1 = "";
            fis = new FileInputStream("E:\\空投\\空投测试.txt");
            // 从文件系统中的某个文件中获取字节
            // InputStreamReader 是字节流通向字符流的桥梁,
            isr = new InputStreamReader(fis);
            // 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
            br = new BufferedReader(isr);
            BigInteger nonce=getNonce(fromAddress);
            while ((str = br.readLine()) != null) {
                System.out.println("nonce " + nonce);
                str = str.replaceAll("\\s*", "");
                String [] strings=str.split(",");
                testTokenTransaction(web3j, privateKey
                        , contractAddress, strings[0], Double.parseDouble(strings[1]), 18, nonce);
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
