package com.handpay.ibenefit.framework.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

import com.handpay.ibenefit.framework.entity.Dictionary;
import com.handpay.ibenefit.framework.web.DictionaryFacade;
public class ColumnValueTag extends TagSupport {
	private static final Logger LOGGER = Logger.getLogger(ColumnValueTag.class);
	private String dictionaryId = null;
	private String type = "standard";
	private String value = null;

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

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}

	private static DictionaryFacade dictionaryFacade;

	public int doStartTag() throws JspException {
		Object elResultID = ExpressionEvaluatorManager.evaluate("dictionaryId", dictionaryId, Long.class, this,
		        pageContext);
		Object elResult = ExpressionEvaluatorManager.evaluate("value", value, String.class, this, pageContext);
		int eid = -1;
		try {
			eid = Integer.parseInt(elResultID.toString());
		} catch (NumberFormatException e) {
			LOGGER.error("ColunmValueTag的EnumId值不是数字", e);
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

	private String getName(Object elResult, int eid) {
		String enumName = null;
		if (eid >= 0 && elResult!=null) {
			Integer enumValueResult = null;
			if(elResult!=null){
				try {
					enumValueResult = Integer.parseInt((String) elResult);
				} catch (NumberFormatException e1) {
					//LOGGER.debug("ColunmValueTag的EnumValue is not a number", e1);
				}
			}
			if(enumValueResult==null){
				enumValueResult = TagUtils.tryGetIntegerFromObject(elResult);
			}
			if (enumValueResult != null) {
				Dictionary dictionary = dictionaryFacade.getDictionaryByDictionaryIdAndValue(eid, enumValueResult);
				if(null!=dictionary){
					enumName = dictionary.getName();
				}
			}
		}
		return enumName;
	}

	public void setDictionaryFacade(DictionaryFacade dictionaryFacade) {
    	ColumnValueTag.dictionaryFacade = dictionaryFacade;
    }

	public String getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(String dictionaryId) {
		this.dictionaryId = dictionaryId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
