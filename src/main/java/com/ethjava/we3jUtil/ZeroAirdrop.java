package com.ethjava.we3jUtil;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: autumn
 * @Package: com.zx.token
 * @ClassName: ${TYPE_NAME}
 * @Description: 查询指定代币的账户余额（Imtoken添加即空投 ）
 * @Author: skyhuihui
 * @CreateDate: 2018/7/15 11:06
 * @UpdateUser: Neil.Zhou
 * @UpdateDate: 2018/7/15 11:06
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public class ZeroAirdrop {

    //正式环境
    private static Web3j web3j = Web3j.build(new HttpService(""));
    //	https://ropsten.infura.io 测试环境
    //private static Web3j web3j = Web3j.build(new HttpService(""));


    //代币合约地址
    private static String contractAddress = "0x1a28efaa62ddc7636df515c6a16136ab2b5e9814";

    public static void main(String [] str){

        readTxt();

    }

    //读文件数据 发查询请求
    public  static  void  readTxt(){
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null; //用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
        try {
            String str = "";
            String str1 = "";
            fis = new FileInputStream("E:\\Wallet\\总账户地址.txt");// FileInputStream
            // 从文件系统中的某个文件中获取字节
            isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
            br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
            while ((str = br.readLine()) != null) {
                System.out.println("特定币种地址余额为："+getTokenBalance(web3j, str, contractAddress));
                str1 += str + "\n";
            }
            // 当读取的一行不为空时,把读到的str的值赋给str1
           // System.out.println("查询账户地址:"+str1  );
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


    /**
     * 查询代币余额
     */
    public static BigInteger getTokenBalance(Web3j web3j, String fromAddress, String contractAddress) {

        String methodName = "balanceOf";
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();
        Address address = new Address(fromAddress);
        inputParameters.add(address);

        TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {
        };
        outputParameters.add(typeReference);
        Function function = new Function(methodName, inputParameters, outputParameters);
        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);

        EthCall ethCall;
        BigInteger balanceValue = BigInteger.ZERO;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            if (results.size()==0){
                return new BigInteger("0");
            }
            balanceValue = (BigInteger) results.get(0).getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return balanceValue;
    }
}
