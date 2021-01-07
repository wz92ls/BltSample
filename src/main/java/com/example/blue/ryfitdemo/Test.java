package com.example.blue.ryfitdemo;
/**
 * @author Mars E_mail:HouJianChao1204@163
 * @version CareteTime:2014-3-12 下午2:14:07
 * @description Class description
 */
public class Test {

	/**
	 * @description Method description
	 * @author Mars 2014-3-12 下午2:14:07
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Test test=new Test();
		test.hexStringToBytes("A5");
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	public byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
}
