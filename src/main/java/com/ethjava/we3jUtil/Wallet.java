package com.ethjava.we3jUtil;

import org.web3j.crypto.WalletUtils;

import java.io.*;

/**
 * @ProjectName: autumn
 * @Package: com.autumnframework.common.model.bo
 * @ClassName: Test
 * @Description: 批量生成以太坊账户 修改名字 储存地址
 * @Author: skyhuihui
 * @CreateDate: 2018/7/14 15:26
 * @UpdateUser: skyhuihui
 * @UpdateDate: 2018/7/14 15:26
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public class Wallet {

    public static void main(String [] are){

        craetAddress();

        //修改文件名
        //updateFileName();

        //创建账户
//        for (int i=1;i<10000;i++){
//            savePic();
//        }

//       System.out.println( new BigInteger("200000000000000")
//               .subtract(BigDecimal.valueOf(Double.parseDouble("0.0001"))
//                       .multiply(BigDecimal.TEN.pow(18)).toBigInteger())
//               .toString());
//       System.out.println(BigDecimal.valueOf(Double.parseDouble("0.0001"))
//               .multiply(BigDecimal.TEN.pow(18)).toBigInteger());
//
//        System.out.println(BigDecimal.valueOf(new BigInteger(new BigInteger("200000000000000")
//                .subtract(BigDecimal.valueOf(Double.parseDouble("0.0001"))
//                        .multiply(BigDecimal.TEN.pow(18)).toBigInteger())
//                .toString()).compareTo(BigInteger.ZERO)));


    }

    //创建钱包
    private static void savePic() {
        try {
            String path = "E:\\Wallet\\";
            // 2、保存到临时文件
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流保存到本地文件

            File tempFile = new File(path);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            String wallet_file = WalletUtils.generateLightNewWalletFile("tian550345681", tempFile);
            System.out.println("钱包输出："+wallet_file);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 完毕，关闭所有链接
        }
    }

    //批量修改文件名
    public  static  void updateFileName(){
        File file1=new File("E:\\Wallet\\");

        File[] f=file1.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if(name.endsWith(".json"))
                    return true;
                return false;
            }
        });
        int n=0;
        for(File tmp:f){
            n++;
            String str=tmp.getName();
            int i;
            for(i=0;i<str.length()-1;i++)
                if(str.charAt(i)=='-'&&Character.isDigit(str.charAt(i+1)) )
                    break;
            String s=n+"";
            s+=".json";
            File ft=new File(file1,s);
            System.out.println(tmp.renameTo(ft));

        }
    }

    //集合所有钱包地址生成文件
    public static void craetAddress(){
        //创建总地址
        String allAddress="";
        // 2.创建一个流，指向目标文件
        InputStream is = null;
        try {
            for (int i=1 ; i<=5027;i++){
                // 1.定义目标文件
                File srcFile = new File("E:\\Wallet\\"+i+".json");

                //创建地址
                String address="";

                is = new FileInputStream(srcFile);
                // 3.循环往外流
                int content = is.read();
                // 4.循环打印
                while (content != -1) {
                    address+=(char)content;
                    content = is.read();
                }
                allAddress+="0x"+address.substring(12,52)+"\n";
            }
            System.out.println("总地址:"+allAddress);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭io流
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
