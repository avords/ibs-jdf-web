package com.handpay.ibenefit.framework.web;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class JdfCustomDateEditor extends PropertyEditorSupport {
	private DateFormat dateFormat = null;
	private final boolean allowEmpty;

	public JdfCustomDateEditor(boolean allowEmpty){
		this.allowEmpty = allowEmpty;
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (allowEmpty && !StringUtils.isNotBlank(text)) {
			setValue(null);
		} else {
			if (text != null){
				dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try{
					setValue(dateFormat.parse(text));
				}catch (Exception e) {
					dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					try {
						setValue(dateFormat.parse(text));
                    } catch (Exception e2) {
                    	dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    	try {
                    		setValue(dateFormat.parse(text));
                        } catch (Exception e3) {
                        	dateFormat = new SimpleDateFormat("yyyy-MM");
                        	try {
                        		setValue(dateFormat.parse(text));
                            } catch (Exception e4) {
                            	dateFormat = new SimpleDateFormat("yyyy");
                            	try {
                            		setValue(dateFormat.parse(text));
                                } catch (Exception e5) {
                                	dateFormat = null;
                                }
                            }
                        }
                    }
				}
			}
		}
	}

	public String getAsText() {
		Date value = (Date) getValue();
		return null==value? "" : null==dateFormat?value.toLocaleString():dateFormat.format(value);
	}
}
