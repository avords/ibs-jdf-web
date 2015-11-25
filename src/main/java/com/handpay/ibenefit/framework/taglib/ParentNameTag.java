package com.handpay.ibenefit.framework.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

import com.handpay.ibenefit.framework.entity.Dictionary;
import com.handpay.ibenefit.framework.web.DictionaryFacade;
public class ParentNameTag extends TagSupport {

	private static final Logger LOGGER = Logger.getLogger(ParentNameTag.class);
	private String parentId = null;
	private String value = null;
	private static DictionaryFacade dictionaryFacade;

	/**
	 * @return Returns the enumValue.
	 */
	public String getEnumValue() {
		return value;
	}

	/**
	 * @param enumValue
	 *            The enumValue to set.
	 */
	public void setEnumValue(String enumValue) {
		this.value = enumValue;
	}

	public int doStartTag() throws JspException {
		Object elResultID = ExpressionEvaluatorManager.evaluate("parentId", parentId, Long.class, this,
		        pageContext);
		Object elResult = ExpressionEvaluatorManager.evaluate("value", value, String.class, this, pageContext);
		long eid = -1;
		try {
			eid = Long.parseLong(elResultID.toString());
		} catch (NumberFormatException e) {
			LOGGER.error("The numId of Parent Name is not a number", e);
		}
		String enumName = getName(elResult, eid);
		try {
			if (enumName != null) {
				pageContext.getOut().write(enumName);
			} else {
				pageContext.getOut().write(elResult.toString());
			}
		} catch (IOException e) {
			LOGGER.error("doStartTag()", e);
		}
		return EVAL_PAGE;
	}

	private String getName(Object elResult, long eid) {
		String enumName = null;
		if (eid >= 0) {
			Integer enumValueResult = null;
			try {
				enumValueResult = Integer.parseInt((String) elResult);
			} catch (NumberFormatException e1) {
				LOGGER.error("The value of Parent Name is not a number", e1);
			}
			if (enumValueResult != null) {
				Dictionary dictionary = dictionaryFacade.getDictionaryByParentIdAndValue(Integer.valueOf(parentId), Integer.valueOf(value));
				if(null!=dictionary){
					enumName = dictionary.getName();
				}
			}
		}
		return enumName;
	}
	public void setDictionaryFacade(DictionaryFacade dictionaryFacade) {
		ParentNameTag.dictionaryFacade = dictionaryFacade;
    }
	public String getParentId() {
    	return parentId;
    }

	public void setParentId(String parentId) {
    	this.parentId = parentId;
    }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
