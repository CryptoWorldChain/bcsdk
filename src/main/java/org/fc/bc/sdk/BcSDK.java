package org.fc.bc.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import org.apache.commons.lang3.StringUtils;
import org.brewchain.bcapi.gens.Oentity.KeyStoreValue;
import org.brewchain.ecrypto.impl.EncInstance;
import org.fc.bc.sdk.enums.TransTypeEnum;
import org.fc.brewchain.bcapi.KeyPairs;
import org.fc.brewchain.bcapi.KeyStoreFile;
import org.fc.brewchain.bcapi.KeyStoreHelper;
import org.fc.sdk.pbgens.Bcsdk.MultiTransactionBody;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.SecureRandomUtils;

public class BcSDK {
	
	private static EncInstance enc;
	private static KeyStoreHelper keyStoreHelper;
	static{
		enc = new EncInstance();
		enc.startup();
		keyStoreHelper = new KeyStoreHelper(enc);
	}
	
	/**
	 * 随机生成助记词
	 * @return seed 助记词字符串
	 */
	public static String genBip39Seed(){
		byte[] initialEntropy = new byte[16];
		SecureRandom secureRandom = SecureRandomUtils.secureRandom();
        secureRandom.nextBytes(initialEntropy);
        return MnemonicUtils.generateMnemonic(initialEntropy);
	}
	
	/**
	 * 生成BC转账交易签名
	 * @param nonce 交易Nonce（注：需要请求服务器接口查询到发送方地址最新的Nonce）
	 * @param from 发送方(发送方KeyStore文件读取的KeyStoreValue)
	 * @param to 接收方地址
	 * @param amonut 交易金额
	 * @return signedMessage BC转账签名字符串
	 * @throws IOException
	 * @throws CipherException
	 */
	public static String genSignedMessage(
			BigInteger nonce, KeyStoreValue from, String to, BigInteger amonut) {
		
		// TODO  
		MultiTransactionBody.Builder oBody = MultiTransactionBody.newBuilder();
		
		oBody.setType(TransTypeEnum.TYPE_DEFAULT.value());
		
		byte[] sign = enc.ecSign(from.getPrikey(), oBody.build().toByteArray());
		return enc.hexEnc(sign);
	}
	
	/**
	 * 生成Token转账交易签名
	 * @param nonce 发送方地址Nonce（注：需要请求服务器接口查询到发送方地址最新的Nonce）
	 * @param from 发送方(发送方KeyStore文件读取的KeyStoreValue)
	 * @param to 接收方地址
	 * @param contractAddress Token合约的地址
	 * @param amonut 交易金额
	 * @return signedMessage Token转账签名字符串
	 * @throws IOException
	 * @throws CipherException
	 */
	public static String genSignedMessage(BigInteger nonce, KeyStoreValue from, String to, String contractAddress, BigInteger amonut) {

		// TODO  
		MultiTransactionBody.Builder oBody = MultiTransactionBody.newBuilder();
		
		oBody.setType(TransTypeEnum.TYPE_TokenTransaction.value());
		
		byte[] sign = enc.ecSign(from.getPrikey(), oBody.build().toByteArray());
		return enc.hexEnc(sign);
	}
	
	/**
	 * 生成KeyStore内容（创建新账户）
	 * @param password KeyStore内容的密码
	 * @return KeyStore内容
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IOException
	 */
	public static String genKeyStoreContent(String password) 
					throws NoSuchAlgorithmException, NoSuchProviderException
					, InvalidAlgorithmParameterException, IOException{
		KeyPairs key = enc.genKeys();
		KeyStoreHelper keyStoreHelper = new KeyStoreHelper(enc);
		KeyStoreFile oKeyStoreFile = keyStoreHelper.generate(key, password);
		return keyStoreHelper.parseToJsonStr(oKeyStoreFile);
	}
	
	/**
	 * 生成KeyStore内容（创建新账户）
	 * @param seed 助记词(生成私钥的种子)
	 * @param password KeyStore内容的密码
	 * @return KeyStore内容
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidAlgorithmParameterException
	 * @throws CipherException
	 * @throws IOException
	 */
	public static String genKeyStoreContentBySeed(String seed, String password) 
					throws NoSuchAlgorithmException, NoSuchProviderException
					, InvalidAlgorithmParameterException, IOException{
		KeyPairs key = enc.genKeys(seed);
		KeyStoreHelper keyStoreHelper = new KeyStoreHelper(enc);
		KeyStoreFile oKeyStoreFile = keyStoreHelper.generate(key, password);
		return keyStoreHelper.parseToJsonStr(oKeyStoreFile);
	}

	/**
	 * 生成KeyStore内容（根据已知私钥创建账户）
	 * @param privKey 已知私钥
	 * @param password KeyStore文件的密码
	 * @return KeyStore内容
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidAlgorithmParameterException
	 * @throws CipherException
	 * @throws IOException
	 */
	public static String genKeyStoreContentByKey(String privKey, String password) 
					throws NoSuchAlgorithmException, NoSuchProviderException
					, InvalidAlgorithmParameterException, IOException{
		KeyPairs key = enc.priKeyToKey(privKey);
		KeyStoreHelper keyStoreHelper = new KeyStoreHelper(enc);
		KeyStoreFile oKeyStoreFile = keyStoreHelper.generate(key, password);
		return keyStoreHelper.parseToJsonStr(oKeyStoreFile);
	}
	
	/**
	 * 生成KeyStore文件（创建新账户）
	 * @param password KeyStore文件的密码
	 * @param destinationDirectory KeyStore文件生成的目标路径
	 * @param fixName KeyStoreFile文件前缀
	 * @return KeyStore文件名
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidAlgorithmParameterException
	 * @throws CipherException
	 * @throws IOException
	 */
	public static String genKeyStoreFile(
			String password, File destinationDirectory, String fixName) 
					throws NoSuchAlgorithmException, NoSuchProviderException
					, InvalidAlgorithmParameterException, IOException{
		
		String keyStoreContent = genKeyStoreContent(password);
		String keyStoreFilePath = getFilePath(destinationDirectory,fixName,readKeyStoreContent(password, keyStoreContent));
		fileWriter(keyStoreContent, keyStoreFilePath);
		return keyStoreFilePath;
	}
	
	/**
	 * 生成KeyStore文件（创建新账户）
	 * @param seed 助记词(生成私钥的种子)
	 * @param password KeyStore文件的密码
	 * @param destinationDirectory KeyStore文件生成的目标路径
	 * @param fixName KeyStoreFile文件前缀
	 * @return KeyStore文件名
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidAlgorithmParameterException
	 * @throws CipherException
	 * @throws IOException
	 */
	public static String genKeyStoreFileBySeed(
			String seed, String password, File destinationDirectory, String fixName) 
					throws NoSuchAlgorithmException, NoSuchProviderException
					, InvalidAlgorithmParameterException, IOException{
		String keyStoreContent = genKeyStoreContentBySeed(seed,password);
		String keyStoreFilePath = getFilePath(destinationDirectory,fixName,readKeyStoreContent(password, keyStoreContent));
		fileWriter(keyStoreContent, keyStoreFilePath);
		return keyStoreFilePath;
	}
	
	/**
	 * 生成KeyStore文件（根据已知私钥创建账户）
	 * @param privKey 已知私钥
	 * @param password KeyStore文件的密码
	 * @param destinationDirectory KeyStore文件生成的目标路径
	 * @param fixName KeyStoreFile文件前缀
	 * @return KeyStore文件名
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidAlgorithmParameterException
	 * @throws CipherException
	 * @throws IOException
	 */
	public static String genKeyStoreFile(
			String privKey, String password, File destinationDirectory, String fixName) 
					throws NoSuchAlgorithmException, NoSuchProviderException
					, InvalidAlgorithmParameterException, IOException{
		String keyStoreContent = genKeyStoreContentByKey(privKey,password);
		String keyStoreFilePath = getFilePath(destinationDirectory,fixName,readKeyStoreContent(password, keyStoreContent));
		fileWriter(keyStoreContent, keyStoreFilePath);
		return keyStoreFilePath;
	}

	/**
	 * 读取KeyStore内容
	 * @param keyStoreContent KeyStore内容
	 * @param password KeyStore内容的密码
	 * @return 
	 * @throws CipherException 
	 * @throws IOException 
	 */
	public static KeyStoreValue readKeyStoreContent(String password,String keyStoreContent) throws IOException {
		return keyStoreHelper.getKeyStore(keyStoreContent, password);
	}
	
	/**
	 * 读取KeyStore文件（获取已有账户信息）
	 * @param keyStoreFile KeyStore文件
	 * @param password KeyStore文件密码
	 * @return 
	 * @throws CipherException 
	 * @throws IOException 
	 */
	public static KeyStoreValue readKeyStoreFile(String password,File keyStoreFile) throws IOException{
		String keyStoreContent = fileRead(keyStoreFile);
		return readKeyStoreContent(password, keyStoreContent);
	}
	
	/**
	 * 读取KeyStore文件（获取已有账户信息）
	 * @param keyStoreFilePath KeyStore文件绝对路径及文件名
	 * @param password KeyStore文件密码
	 * @return 
	 * @throws CipherException 
	 * @throws IOException 
	 */
	public static KeyStoreValue readKeyStoreFile(String password,String keyStoreFilePath) throws IOException{
		File keyStoreFile = new File(keyStoreFilePath);
		return readKeyStoreFile(password, keyStoreFile);
	}
	

	private static String getFilePath(File destinationDirectory, String fixName, KeyStoreValue key){
		if(StringUtils.isBlank(fixName)){
			fixName = "BC-";
		}else{
			fixName += "-";
		}
		if(destinationDirectory != null){
			if(!destinationDirectory.exists()){
				destinationDirectory.mkdirs();
			}
			fixName = destinationDirectory.getPath() + "/" + fixName;
		}
		fixName = fixName + key.getAddress() + "-" + System.currentTimeMillis()+".keystore";
		return fixName;
	}
	
	private static void fileWriter(String keyStoreContent, String keyStoreFilePath) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(keyStoreFilePath, false);
			writer.write(keyStoreContent);
		} catch (IOException e) {
			throw e;
        } finally {
            if (writer != null) {
                try {
                	writer.close();
                } catch (IOException e1) {
                }
            }
        }
	}
	
	private static String fileRead(File keyStoreFile) throws IOException {
		String keyStoreContent = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(keyStoreFile));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	keyStoreContent += tempString;
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return keyStoreContent;
    }

}
