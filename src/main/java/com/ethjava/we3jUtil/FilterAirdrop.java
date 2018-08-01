package com.ethjava.we3jUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.http.HttpService;

import java.io.*;
import java.util.*;

/**
 * @ProjectName: autumn
 * @Package: com.zx.token
 * @ClassName:
 * @Description: 94eth工具站过滤空投
 * @Author: skyhuihui
 * @CreateDate: 2018/7/16 17:58
 * @UpdateUser: Neil.Zhou
 * @UpdateDate: 2018/7/16 17:58
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public class FilterAirdrop {

    //正式环境
    private static Web3j web3j = Web3j.build(new HttpService(""));
    //	https://ropsten.infura.io 测试环境
    //private static Web3j web3j = Web3j.build(new HttpService(""));

    public static void main(String [] str) throws IOException {

        filterAirdrop();

    }

    //读文件数据 发查询请求
    public  static  void  filterAirdrop() throws IOException {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            String str = "";
            List<String> str1 =new ArrayList<String>();
            fis = new FileInputStream("E:\\空投\\mchain空投.txt");
            // 从文件系统中的某个文件中获取字节
            isr = new InputStreamReader(fis);
            // 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
            br = new BufferedReader(isr);
            while ((str = br.readLine()) != null) {
                str = str.replaceAll("\\s*", "");
                String[] strings = str.split(",");
                EthGetCode code = web3j.ethGetCode(strings[0], DefaultBlockParameterName.PENDING).send();
                try{
                    if(code.getCode().length() > 3){
                        System.out.println("合约地址："+strings[0]);
                    }
                }catch (NullPointerException n){
                    System.out.println("错误地址："+strings[0]);
                }

                str1.add(str);
            }
            Set<String> set = new HashSet<>(str1);
            System.out.println("---------------------------------------");
            Collection rs = CollectionUtils.disjunction(str1,set);
            List<String> list1 = new ArrayList<>(rs);
            for(String s:list1){
                System.out.println("重复地址:"+s);
            }
        } catch (FileNotFoundException e) {
            System.out.println("找不到指定文件");
        } catch (IOException e) {
            System.out.println("读取文件失败");
        } finally {
            br.close();
            isr.close();
            fis.close();
        }
    }
}
