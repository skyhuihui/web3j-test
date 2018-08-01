package com.ethjava.we3jUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: autumn
 * @Package: com.zx.token
 * @ClassName: BaWool
 * @Description: 褥羊毛
 * @Author: skyhuihui
 * @CreateDate: 2018/7/18 11:05
 * @UpdateUser: Neil.Zhou
 * @UpdateDate: 2018/7/18 11:05
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public class BaWool {

    //正式环境
    //private static Web3j web3j = Web3j.build(new HttpService(""));
    //	https://ropsten.infura.io 测试环境
    private static Web3j web3j = Web3j.build(new HttpService(""));
    //导出信息
    private static String info="";

    //账户地址
    private static String fromAddress="";

    private static List<String> privateKey=new ArrayList<>();
    //文件位置
    private static String file="E:\\空投\\托管账户地址.txt";

    public static void main(String [] str) {

    }

    /**
     * 解密keystore 得到私钥
     *
     * @param keystore
     * @param password
     */
    public static String decryptWallet(String keystore, String password) {
        String privateKey = null;
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        try {
            WalletFile walletFile = objectMapper.readValue(keystore, WalletFile.class);
            ECKeyPair ecKeyPair = null;
            ecKeyPair = Wallet.decrypt(password, walletFile);
            privateKey = ecKeyPair.getPrivateKey().toString(16);
            System.out.println(privateKey);
        } catch (CipherException e) {
            if ("Invalid password provided".equals(e.getMessage())) {
                System.out.println("密码错误");
            }
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return privateKey;
    }
}
