package com.handpay.ibenefit.framework.taglib;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

public class SelectCollectionTag extends TagSupport {
	private static final Logger LOGGER = Logger.getLogger(SelectCollectionTag.class);
	private String items = null;
	private String optionValue = null;
	private String optionText = null;
	private String optionClass = null;

	/**
	 * @return Returns the optionClass.
	 */
	public String getOptionClass() {
		return optionClass;
	}

	/**
	 * @param optionClass
	 *            The optionClass to set.
	 */
	public void setOptionClass(String optionClass) {
		this.optionClass = optionClass;
	}

	/**
	 * @return Returns the items.
	 */
	public String getItems() {
		return items;
	}

	/**
	 * @param items
	 *            The items to set.
	 */
	public void setItems(String items) {
		this.items = items;
	}

	/**
	 * @return Returns the optionText.
	 */
	public String getOptionText() {
		return optionText;
	}

	/**
	 * @param optionText
	 *            The optionText to set.
	 */
	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}

	/**
	 * @return Returns the optionValue.
	 */
	public String getOptionValue() {
		return optionValue;
	}

	/**
	 * @param optionValue
	 *            The optionValue to set.
	 */
	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}

	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpSession session = (HttpSession) pageContext.getSession();
		String itemsvalue = (String) ExpressionEvaluatorManager.evaluate("items", items, String.class, this,
		        pageContext);
		if (itemsvalue == null) {
			LOGGER.error("SelectOptionCollectionTag的items参数<" + items + ">不正确，el无法解析!");
		} else {
			Object allItem = request.getAttribute(itemsvalue);
			if (allItem == null) {
				allItem = session.getAttribute(itemsvalue);
			}
			if (allItem == null) {
				allItem = pageContext.getAttribute(itemsvalue);
			}
			if (allItem == null) {
				LOGGER.error("无法取得SelectOptionCollectionTag的items参数<" + items + ">，无此对象!");
			} else {
				if (Map.class.isInstance(allItem)) {
					forMap((Map) allItem);
				} else if (Collection.class.isInstance(allItem)) {
					forCollection((Collection) allItem, optionValue, optionText);
				} else {
					LOGGER.error("SelectOptionCollectionTag的items类型<" + allItem.getClass() + ">不正确，必须是Map或者List!");
				}
			}
		}
		return EVAL_PAGE;
	}

	/**
	 * 取Map中的值，key为text，value为value
	 *
	 * @param items
	 * @throws JspException
	 * @throws IOException
	 */
	private void forMap(Map map) throws JspException {
		Set<Map.Entry> x = map.entrySet();
		for (Map.Entry entry : x) {
			if (entry != null && entry.getValue() != null && entry.getKey() != null) {
				try {
					pageContext.getOut().write(
					        "<option value='" + entry.getValue() + "'>" + entry.getKey() + "</option>");
				} catch (IOException e) {
					LOGGER.error("doStartTag()", e);
				}
			}
		}
	}

	/**
	 * @param items
	 * @param optionValue
	 * @param optionText
	 * @throws JspException
	 */
	private void forCollection(Collection items, String optionValue, String optionText) throws JspException {
		if (optionValue == null || optionText == null) {
			LOGGER.error("The optionValue、optionText not configuration");

		} else {
			StringBuilder result = new StringBuilder(items.size()*40);
			for (Object item : items) {
				if (item != null) {
					try {
						Object value = null;
						if (String.class.isInstance(item) || Long.class.isInstance(item)
						        || Integer.class.isInstance(item)) {
							value = item;
						} else {
							value = PropertyUtils.getProperty(item, optionValue);
						}
						Object text = null;
						if (String.class.isInstance(item) || Long.class.isInstance(item)
						        || Integer.class.isInstance(item)) {
							text = item;
						} else {
							text = PropertyUtils.getProperty(item, optionText);
						}
						if (value != null && text != null) {
							result.append("<option value=\"").append(value).append("\"");
							if (optionClass != null) {
								Object classname = PropertyUtils.getProperty(item, optionClass);
								if (classname != null) {
									result.append(" class=\"").append(classname).append("\"");
								}

							}
							result.append(">").append(text).append("</option>");
						}
					} catch (Exception e) {
						LOGGER.error("Fail to get value from SelectOptionCollectionTag.optionValue<" + optionValue + ">、optionText<"
						        + optionText + ">");
					}
				}
			}
			try {
				pageContext.getOut().write(result.toString());
            } catch (Exception e) {
            }
		}
	}
}
