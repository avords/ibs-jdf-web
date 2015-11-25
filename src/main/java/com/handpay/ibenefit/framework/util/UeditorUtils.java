package com.handpay.ibenefit.framework.util;

/**
 * 百度编辑器工具类
 * @author bob.pu
 *
 */
public final class UeditorUtils {
	
	/**
	 * 去掉包含在富文本中的特殊字符
	 * http://www.cnblogs.com/pmars/archive/2011/12/27/2303742.html
	 * @param source
	 * @return
	 */
	public static String convertSpace(String source) {
		if(source!=null){
			byte[] bytes = new byte[]{(byte) 0xc2,(byte) 0xa0};
			try{
				String space = new String(bytes,"utf-8");
				return source.replace(space, "&nbsp;");
			}catch (Exception e){
			}
		}
		return source;
	}
}
