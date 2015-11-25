package com.handpay.ibenefit.framework.taglib;

public final class TagUtils {
	private TagUtils(){
	}
	
	public static Boolean getBooleanValue(Object obj){
		if(obj instanceof Boolean){
			return ((Boolean) obj).booleanValue();
		}else{
			String val = String.valueOf(obj);
			if(val.equals("true")){
				return Boolean.TRUE;
			}else if(val.equals("false")){
				return Boolean.FALSE;
			}
		}
		return null;
	}
	
	public static Integer convertBooleanToInteger(boolean bool){
		if(bool){
			return 1;
		}else {
			return 0;
		}
	}
	
	public static Integer tryGetIntegerFromObject(Object obj){
		Boolean bool = getBooleanValue(obj);
		if(bool!=null){
			return convertBooleanToInteger(bool);
		}
		return null;
	}
}
