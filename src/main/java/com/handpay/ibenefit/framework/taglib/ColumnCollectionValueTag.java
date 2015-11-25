package com.handpay.ibenefit.framework.taglib;

import java.lang.reflect.Field;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

import com.handpay.ibenefit.framework.entity.AbstractEntity;
public class ColumnCollectionValueTag extends TagSupport {
	private static final Logger LOGGER = Logger.getLogger(ColumnCollectionValueTag.class);
	private String items;
	private String valueProperty = null;
	private String nameProperty = null;
	private String value = null;

	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpSession session = (HttpSession) pageContext.getSession();
		String itemsvalue = (String) ExpressionEvaluatorManager.evaluate("items", items, String.class, this,pageContext);
		nameProperty =(String) ExpressionEvaluatorManager.evaluate("nameProperty", nameProperty, String.class, this, pageContext);
		if(null!=valueProperty){
			Object temp = ExpressionEvaluatorManager.evaluate("valueProperty", valueProperty, String.class, this, pageContext);
			if(null!=temp){
				valueProperty = temp.toString();
			}
		}
		value = (String)ExpressionEvaluatorManager.evaluate("value", value, String.class, this, pageContext);
		Object allItem = request.getAttribute(itemsvalue);
		if (allItem == null) {
			allItem = session.getAttribute(itemsvalue);
		}
		if (allItem == null) {
			allItem = pageContext.getAttribute(itemsvalue);
		}
		String eid =value.toString();
		String enumName = null;
		try {
			if(allItem!=null){
				for(Object object : (Collection)allItem){
					Object value = null;
					if(null!=valueProperty){
						Field fieldValue = object.getClass().getDeclaredField(valueProperty);
						if(fieldValue!=null){
							fieldValue.setAccessible(true);
							value = fieldValue.get(object).toString();
						}
					}else{
						if( object!=null && object instanceof AbstractEntity ){
							AbstractEntity baseEntity = (AbstractEntity)object;
							value = String.valueOf(baseEntity.getObjectId());
						}
					}
					if(eid.equals(value)){
						Field name = getNameField(object);
						if(name!=null){
							name.setAccessible(true);
							Object nameValue = name.get(object);
							enumName = nameValue==null?"":nameValue.toString();
							break;
						}
					}
				}
			}
			if (enumName != null) {
				pageContext.getOut().write(enumName);
			} else {
				pageContext.getOut().write(value);
			}
		} catch (Exception e) {
			LOGGER.error("doStartTag", e);
		}
		return EVAL_PAGE;
	}

	protected Field getNameField(Object object) throws NoSuchFieldException {
	    Field name = null;
	    try{
	    	name = object.getClass().getDeclaredField(nameProperty);
	    }catch (Exception e){
	    	name = object.getClass().getSuperclass().getDeclaredField(nameProperty);
	    }
	    return name;
    }

	public String getValueProperty() {
    	return valueProperty;
    }

	public void setValueProperty(String valueProperty) {
    	this.valueProperty = valueProperty;
    }

	public String getNameProperty() {
    	return nameProperty;
    }

	public void setNameProperty(String nameProperty) {
    	this.nameProperty = nameProperty;
    }

	public String getValue() {
    	return value;
    }

	public void setValue(String value) {
    	this.value = value;
    }

	public String getItems() {
    	return items;
    }

	public void setItems(String items) {
    	this.items = items;
    }
}
