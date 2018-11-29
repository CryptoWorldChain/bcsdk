package org.fc.bc.sdk.test;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.brewchain.bcapi.gens.Oentity.KeyStoreValue;
import org.fc.bc.sdk.BcSDK;

public class Test {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, IOException {

		//KeyStore的密码
		String password = "123456";
		
		System.out.println("###### 新建账户（不创建KeyStore文件） ######");

		System.out.println("生成KeyStore内容（创建账户，并且不创建KeyStore文件）密码与生成的私钥没有关系");
		//TODO 注：密码与生成的私钥没有关系
		String keyStoreContent = BcSDK.genKeyStoreContent(password); 
		System.out.println("密码="+password);
		System.out.println("keyStoreContent="+keyStoreContent);
		System.out.println();
		
		System.out.println("###### 读取已有KeyStore内容 ######");
		KeyStoreValue from = BcSDK.readKeyStoreContent(password, keyStoreContent);
		System.out.println("地址"+from.getAddress());
		System.out.println("私钥"+from.getPrikey());
		System.out.println("公钥"+from.getPubkey());
		System.out.println();
		

		System.out.println("###### 助记词（注：助记词词典在本工程的src/main/resources目录中） ######");
		//生成一个助记词作为 作为KeyStore的密码
		String seed = BcSDK.genBip39Seed(); 
		System.out.println("助记词="+seed);
		System.out.println();
			
		System.out.println("生成KeyStore内容（创建账户，并且不创建KeyStore文件）seed相同时生成的私钥也相同");
		System.out.println("助记词="+seed);
		System.out.println("密码="+password);
		// TODO 注：seed相同时生成的私钥也相同
		keyStoreContent = BcSDK.genKeyStoreContentBySeed(seed,password);
		System.out.println("keyStoreContent="+keyStoreContent);
		System.out.println();
		System.out.println("###### 读取已有KeyStore内容 ######");
		from = BcSDK.readKeyStoreContent(password, keyStoreContent);
		System.out.println("地址"+from.getAddress());
		System.out.println("私钥"+from.getPrikey());
		System.out.println("公钥"+from.getPubkey());
		System.out.println();
		
		System.out.println("助记词="+seed);
		System.out.println("密码=abcxyz");
		keyStoreContent = BcSDK.genKeyStoreContentBySeed(seed,"abcxyz");
		System.out.println("keyStoreContent="+keyStoreContent);
		System.out.println();
		System.out.println("###### 读取已有KeyStore内容 ######");
		from = BcSDK.readKeyStoreContent("abcxyz", keyStoreContent);
		System.out.println("地址"+from.getAddress());
		System.out.println("私钥"+from.getPrikey());
		System.out.println("公钥"+from.getPubkey());
		System.out.println();
		

		System.out.println("###### 创建账户（通过已知私钥创建账户,不创建KeyStore文件） ######");
		keyStoreContent = BcSDK.genKeyStoreContentByKey(from.getPrikey(),password);
		System.out.println("keyStoreContent="+keyStoreContent);
		System.out.println();

		System.out.println("###### 生成转账交易签名，服务器端 ETH转账 接口所需要的签名后字符串参数 ######");
		BigInteger fromAddressNonce = new BigInteger("0");//Nonce通过接口获取from地址最新的Nonce值
		BigInteger amonut = BigInteger.valueOf(1000000000000000000L);//
		String to  =  "0x5D999604F7F16076199728a62698ad453a4A9965";//接收方地址
		//生成CWV转账签名字符串
		String signedMessage_ETH = BcSDK.genSignedMessage(fromAddressNonce, from, to, amonut);
		System.out.println("CWV转账SignedMessage="+signedMessage_ETH);
		System.out.println();
		
		System.out.println("###### 生成转账交易签名，服务器端 Token转账 接口所需要的签名后字符串参数 ######");
		fromAddressNonce = new BigInteger("1");//Nonce通过接口获取from地址最新的Nonce值
		BigInteger tokenAmonut = new BigInteger("10000000000000000000000000000");//10000000000个Token币（假设这个18位小数）
		String tokenContractAddress = "0xed494c9e2f8e34e53bdd0ea9b4d80305cb15c5c2";//创建Token的合约地址
		//生成Token转账签名字符串
		String signedMessage_Token = BcSDK.genSignedMessage(fromAddressNonce, from, to,tokenContractAddress, amonut);
		System.out.println("Token转账SignedMessage="+signedMessage_Token);
		
		System.out.println();
		
		// TODO
//		System.out.println("调用服务器的转账接口（发送到到链上） 将signedMessage作为ETH转账接口或者转账接口的参数");
		
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("###### 新建账户（创建KeyStore文件） ######");
		String keyStoreFileName = "";//文件名称
		String keyStorePath = "/GIT/KSTest/";//文件保存的本地路径
		try {
			//新账户，keyStore文件（地址、私钥、公钥）
			keyStoreFileName = BcSDK.genKeyStoreFile(password, new File(keyStorePath),null);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchProviderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidAlgorithmParameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("创建KeyStore文件名="+keyStoreFileName);
		System.out.println();
		
		try {
			System.out.println("###### 读取账户 ######");
			String keyStoreFilePath = keyStorePath + keyStoreFileName;
			System.out.println("创建KeyStore路径及文件名="+keyStoreFilePath);
			// 读取已有账户信息（读取keyStore文件）
			from = BcSDK.readKeyStoreFile(password, keyStoreFilePath);
			System.out.println("地址"+from.getAddress());
			System.out.println("私钥"+from.getPrikey());
			System.out.println("公钥"+from.getPubkey());
			System.out.println();
			
			System.out.println("###### 创建账户（通过已知私钥创建账户，创建KeyStore文件） ######");
			keyStoreFileName = BcSDK.genKeyStoreFile(
					from.getPrikey()
					, password
					, new File(keyStorePath)
					, null);
			keyStoreFilePath = keyStorePath + keyStoreFileName;
			System.out.println("keyStore路径及文件名="+keyStoreFilePath);
			System.out.println();
			
			System.out.println("###### 读取账户（通过已知私钥创建的账户，创建KeyStore文件） ######");
			// 读取已有账户信息（读取keyStore文件）
			from = BcSDK.readKeyStoreFile(password, keyStoreFilePath);
			System.out.println("地址"+from.getAddress());
			System.out.println("私钥"+from.getPrikey());
			System.out.println("公钥"+from.getPubkey());
			System.out.println();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
