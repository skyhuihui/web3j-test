package com.ethjava.we3jUtil;

import com.ethjava.we3jUtil.sol.AirdorpSol;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.http.HttpService;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ProjectName: autumn
 * @Package: com.zx.token
 * @ClassName: ${TYPE_NAME}
 * @Description: 代币空投 一笔交易多个空投
 * @Author: skyhuihui
 * @CreateDate: 2018/7/15 15:37
 * @UpdateUser: Neil.Zhou
 * @UpdateDate: 2018/7/15 15:37
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public class Airdrop {
    //正式环境
    //private static Web3j web3j = Web3j.build(new HttpService(""));
    //	https://ropsten.infura.io 测试环境
    private static Web3j web3j = Web3j.build(new HttpService(""));

    //合约地址
    private static String abi="0xcd7fab5f0ae977deee5e47106303d9c6eb93e1f5";

    //币地址(KT)
    private static String contractAddress="0xd5c0f4e10801717f683f546fad3e4481d2b324ba";

    private static String privateKey="";

    public static void main(String [] str) throws Exception {

        readTxt();

    }
    /**
     * 代币转账
     */
    private static void testTokenTransaction(Web3j web3j, String privateKey, String contractAddress, String toAddress, String amounts, int decimals) throws Exception {
        //gasPraice 手动设置
        //BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(2), Convert.Unit.GWEI).toBigInteger();
        EthGasPrice ethGasPrice = web3j.ethGasPrice().send();
        BigInteger gasPrice = ethGasPrice.getGasPrice().multiply(new BigInteger("105")).divide(new BigInteger("100"));
        //gasLimit
        BigInteger gasLimit = BigInteger.valueOf(4600000);
        //web3j.ethEstimateGas();
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
        Credentials credentials = Credentials.create(privateKey);
        AirdorpSol contract =AirdorpSol.load(abi, web3j, credentials, gasPrice, gasLimit);
        TransactionReceipt transactionReceipt = contract.deliverTokens(contractAddress, allAddresses, bigIntegers).send();
        System.out.println("hash:"+transactionReceipt.getTransactionHash());
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
            fis = new FileInputStream("E:\\空投\\批量空头测试.txt");
            // 从文件系统中的某个文件中获取字节
            // InputStreamReader 是字节流通向字符流的桥梁,
            isr = new InputStreamReader(fis);
            // 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
            br = new BufferedReader(isr);
            int i=0,n=1;
            while ((str = br.readLine()) != null) {
                str = str.replaceAll("\\s*", "");
                String [] strings=str.split(",");
                str1 += strings[0]+",";
                str2 += strings[1]+",";
                if(i == 150){
                    System.out.println("第几批："+n);
                    String strsub1 = str1.substring(0,str1.length()-1);
                    String strsub2 = str2.substring(0,str2.length()-1);
                    testTokenTransaction(web3j, privateKey
                            , contractAddress, strsub1, strsub2, 18);
                    str1="";str2="";i=0;n++;
                }
                i++;
            }
            System.out.println("最后一次空投");
            String strsub1 = str1.substring(0,str1.length()-1);
            String strsub2 = str2.substring(0,str2.length()-1);
            testTokenTransaction(web3j, privateKey
                    , contractAddress, strsub1, strsub2, 18);
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
}
