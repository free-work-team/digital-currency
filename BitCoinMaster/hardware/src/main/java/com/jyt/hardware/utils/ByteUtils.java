package com.jyt.hardware.utils;

import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;


public class ByteUtils {

	/**
	 * convert Hex String to bytes
	 * @param hexString
	 * @return
	 * @throws ByteUtilException
	 */
	public static byte[] hex2Byte(String hexString) throws ByteUtilException {

		if(hexString==null || hexString.length()==0) return new byte[]{};

		if(hexString.length()%2!=0) throw new ByteUtilException("the hex String is not illegal it's length must required length%2 = 0 case by it's length is "+hexString.length());

		char[] hexCharArrays = hexString.toCharArray();

		byte[] bytes = new byte[hexCharArrays.length/2];

		for(int i = 0; i < hexCharArrays.length/2; i++){
			bytes[i] = hex2Byte(hexCharArrays[i*2], hexCharArrays[i*2+1]);
		}

		return bytes;
	}


	public static String getEigthBitsStringFromByte(int b)
	{
	// if this is a positive number its bits number will be less
	// than 8
	// so we have to fill it to be a 8 digit binary string
	// b=b+100000000(2^8=256) then only get the lower 8 digit
	b |= 256; // mark the 9th digit as 1 to make sure the string
	// has at
	// least 8 digits
	String str = Integer.toBinaryString (b);
	int len = str.length ();
	return str.substring (len - 8,len);
	}

	public static byte getByteFromEigthBitsString(String str)
	{
	// if(str.length()!=8)
	// throw new Exception("It's not a 8 length string");
	byte b;
	// check if it's a minus number
	if(str.substring (0,1).equals ("1"))
	{
	// get lower 7 digits original code
	str = "0" + str.substring (1);
	b = Byte.valueOf (str,2);
	// then recover the 8th digit as 1 equal to plus
	// 1000000
	b |= 128;
	}
	else
	{
	b = Byte.valueOf (str,2);
	}
	return b;
	}

	/**
	* 将一个16字节数组转成128二进制数组
	* 
	* @param b
	* @return
	*/
	public static boolean [] getBinaryFromByte(byte [] b)
	{
	boolean [] binary = new boolean [b.length * 8 + 1];
	String strsum = "";
	for (int i = 0;i < b.length;i++ )
	{
	strsum += getEigthBitsStringFromByte (b [i]);
	}
	for (int i = 0;i < strsum.length ();i++ )
	{
        binary[i + 1] = strsum.substring(i, i + 1).equalsIgnoreCase("1");
    }
	return binary;
	}

	/**
	* 将一个128二进制数组转成16字节数组
	* 
	* @param binary
	* @return
	*/
	public static byte [] getByteFromBinary(boolean [] binary)
	{

	int num = (binary.length - 1) / 8;
	if((binary.length - 1) % 8 != 0)
	{
	num = num + 1;
	}
	byte [] b = new byte [num];
	String s = "";
	for (int i = 1;i < binary.length;i++ )
	{
	if(binary [i])
	{
	s += "1";
	}
	else
	{
	s += "0";
	}
	}
	String tmpstr;
	int j = 0;
	for (int i = 0;i < s.length ();i = i + 8)
	{
	tmpstr = s.substring (i,i + 8);
	b [j] = getByteFromEigthBitsString (tmpstr);
	j = j + 1;
	}
	return b;
	}

	/**
	* 将一个byte位图转成字符串
	* 
	* @param b
	* @return
	*/
	public static String getStrFromBitMap(byte [] b)
	{
	String strsum = "";
	for (int i = 0;i < b.length;i++ )
	{
	strsum += getEigthBitsStringFromByte (b [i]);
	}
	return strsum;
	}

	/**
	* bytes转换成十六进制字符串
	* 
	* @param b
	* @return
	*/
	public static String byte2HexStr(byte [] b)
	{
	String hs = "";
	String stmp = "";
	for (int n = 0;n < b.length;n++ )
	{
	stmp = (Integer.toHexString (b [n] & 0XFF));
	if(stmp.length () == 1)
	hs = hs + "0" + stmp;
	else
	hs = hs + stmp;
	}
	return hs.toUpperCase ();
	}

    public static String byte2HexWithIndex(byte[] bytes, int startIndex) {
        int len = bytes.length - startIndex;
        StringBuilder sb = new StringBuilder(len * 2);
        for (int i = startIndex; i < len + startIndex; i++) {
            sb.append(byte2Hex(bytes[i]));
        }
        return sb.toString();
    }


    public static String byte2Hex(byte[] bytes, int start, int len) {
        if (bytes.length < len) len = bytes.length;
        StringBuilder sb = new StringBuilder(len * 2);
        for (int i = start; i < len + start; i++) {
            sb.append(byte2Hex(bytes[i]));
        }
        return sb.toString();
    }

    public static String byte2Hex(byte[] bytes, int len) {
        if (bytes.length < len) len = bytes.length;
        StringBuilder sb = new StringBuilder(len * 2);
        for (int i = 0; i < len; i++) {
            sb.append(byte2Hex(bytes[i]));
        }
        return sb.toString();
    }

	/**
	 * convert byte to Hex String
	 * @param b
	 * @return
	 */
	public static String byte2Hex(byte b) {
		String v = Integer.toHexString(b & 0xff).toUpperCase();
		if (v.length() == 1) {
			v = '0' + v;
		}
		return v;
	}
	public static String byte2Hex(byte[] bytes) {
		if(bytes==null)return null;
		StringBuilder sb = new StringBuilder(bytes.length*2);
		for(byte b : bytes){
			sb.append(byte2Hex(b));
		}
		return sb.toString();
	}
	private static byte uniteBytes(String src0, String src1)
	{
	byte b0 = Byte.decode ("0x" + src0).byteValue ();
	b0 = (byte) (b0 << 4);
	byte b1 = Byte.decode ("0x" + src1).byteValue ();
	byte ret = (byte) (b0 | b1);
	return ret;
	}

	/**
	* 十六进制字符串转换成bytes
	* 
	* @param src
	* @return
	*/
	public static byte [] hexStr2Bytes(String src)
	{
	int m = 0, n = 0;
	int l = src.length () / 2;
	byte [] ret = new byte [l];
	for (int i = 0;i < l;i++ )
	{
	m = i * 2 + 1;
	n = m + 1;
	ret [i] = uniteBytes (src.substring (i * 2,m),
	src.substring (m,n));
	}
	return ret;
	}

	/**
	* 将String转成BCD码
	* 
	* @param s
	* @return
	*/
	public static byte [] StrToBCDBytes(String s)
	{

	if(s.length () % 2 != 0)
	{
	s = "0" + s;
	}
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	char [] cs = s.toCharArray ();
	for (int i = 0;i < cs.length;i += 2)
	{
	int high = cs [i] - 48;
	int low = cs [i + 1] - 48;
	baos.write (high << 4 | low);
	}
	return baos.toByteArray ();
	}

	/**
	* 将BCD码转成int
	* 
	* @param b
	* @return
	*/
	public static int bcdToint(byte [] b)
	{
	StringBuffer sb = new StringBuffer();
	for (int i = 0;i < b.length;i++ )
	{
	int h = ((b [i] & 0xff) >> 4) + 48;
	sb.append ((char) h);
	int l = (b [i] & 0x0f) + 48;
	sb.append ((char) l);
	}
	return Integer.parseInt (sb.toString ());
	}

	/**
	 * ascii转16进制字符串
	 * @param  str
	 * @return
	 */
	 public static String convertStringToHex(String str){
		  
	      char[] chars = str.toCharArray();  
	  
	      StringBuffer hex = new StringBuffer();
	      for(int i = 0; i < chars.length; i++){
	      	String hexStr= Integer.toHexString((int)chars[i]&0xff);
			  hexStr= StringUtils.leftPad(hexStr,2,"0");
	        hex.append(hexStr);
	      }  
	  
	      return hex.toString().toUpperCase();
	      }

	/**
	 * 16进制字符串转ascii
	 *
	 * @param hex
	 * @return
	 */
	public static String convertHexToString(String hex) {

		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();

		//49204c6f7665204a617661 split into two characters 49, 20, 4c...
		for (int i = 0; i < hex.length() - 1; i += 2) {

			//grab the hex in pairs
			String output = hex.substring(i, (i + 2));
			//convert hex to decimal
			int decimal = Integer.parseInt(output, 16);
			//convert the decimal to character
			sb.append((char) decimal);

			temp.append(decimal);
		}

		return sb.toString();
	}

	private static byte char2Byte(char c){
			return (byte)"0123456789ABCDEF".indexOf(c);
		}
		/**
		 * convert hex char to byte
		 * @param high
		 * @param low
		 * @return
		 */
		public static byte hex2Byte(char high, char low){
			return (byte)((char2Byte(high>='a'?(char)(high-32):high) << 4 | char2Byte(low>='a'?(char)(low-32):low)));
		}
		public static int bytes2Int(byte[] bytes) throws ByteUtilException {
			if(bytes.length>4) throw new ByteUtilException("the type int has byte less than 4");
			int temp = 0;
			for(int i = 0; i <bytes.length ; i++){
				temp = temp | ((bytes[i] & 0x000000ff) << ((bytes.length-i-1)*8));
			}
			
			return temp;
		}
	/**
	 * 将十六进制串转换为二进制
	 *
	 * */
	public static String hexString2binaryString(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString ="", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp ="0000"+ Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}

}
