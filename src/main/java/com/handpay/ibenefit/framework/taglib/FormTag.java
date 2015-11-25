package com.handpay.ibenefit.framework.taglib;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.handpay.ibenefit.framework.util.CodecHtml;
import com.handpay.ibenefit.framework.util.HtmlUtil;

public class FormTag extends BodyTagSupport {

	private static final Logger LOGGER = Logger.getLogger(FormTag.class);

	private String beanName = null;

	private String scope = null;

	private Object bean = null;

	private boolean fromRequest = false;

	/**
	 * Sets bean names with value of the "bean" attribute.
	 *
	 * @param v
	 *            bean names
	 */
	public void setBean(String v) {
		beanName = v;
	}

	/**
	 * Sets the value of "scope" attribute, that represent beans scope.
	 *
	 * @param v
	 */
	public void setScope(String v) {
		scope = v;
	}

	/**
	 * Copies properties of all specified bean into one map.
	 *
	 * @return EVAL_BODY_AGAIN
	 */
	public int doStartTag() {
		getBean();
		return EVAL_BODY_AGAIN;
	}

	/**
	 * Performs smart form population.
	 *
	 * @return SKIP_BODY
	 */
	public int doAfterBody() {
		BodyContent body = getBodyContent();
		try {
			JspWriter out = body.getEnclosingWriter();
			String bodytext = body.getString();
			if (bean != null || fromRequest == true) {
				bodytext = populateForm(bodytext, bean);
			}
			out.print(bodytext);
		} catch (Exception ex) {
			LOGGER.error("doAfterBody",ex);
		}
		return SKIP_BODY;
	}

	/**
	 * End of tag.
	 *
	 * @return EVAL_PAGE
	 */
	public int doEndTag() {
		return EVAL_PAGE;
	}

	private String populateForm(String html, Object bean) {
		int i = 0;
		int s = 0;
		StringBuilder result = new StringBuilder(html.length());
		String currentSelectName = null;
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		while (true) {
			// find starting tag
			i = html.indexOf('<', s);
			if (i == -1) {
				result.append(html.substring(s));
				break; // input tag not found
			}
			result.append(html.substring(s, i)); // tag found, all before tag
			// is stored
			s = i;
			// find closing tag
			i = html.indexOf('>', i);
			if (i == -1) {
				result.append(html.substring(s));
				break; // closing tag not found
			}
			i++;
			// match tags
			String tag = html.substring(s, i);
			String tagName = HtmlUtil.getTagName(tag);

			if (tagName.equalsIgnoreCase("input") == true) {
				String tagType = HtmlUtil.getAttribute(tag, "type");
				String format = HtmlUtil.getAttribute(tag, "format");
				if (tagType != null) {
					String name = HtmlUtil.getAttribute(tag, "name");
					List<String> valueList = getValueList(bean, request, name,format);
					if (valueList.size() > 0) {
						tagType = tagType.toLowerCase();
						if (tagType.equals("text")) {
							tag = HtmlUtil.addAttribute(tag, "value", valueList.get(0));
						}else if (tagType.equals("hidden")) {
							tag = HtmlUtil.addAttribute(tag, "value", valueList.get(0));
						}else  if (tagType.equals("image")) {
							tag = HtmlUtil.addAttribute(tag, "value", valueList.get(0));
						}else if (tagType.equals("password")) {
							tag = HtmlUtil.addAttribute(tag, "value", valueList.get(0));
						}else if (tagType.equals("checkbox")) {
							String tagValue = HtmlUtil.getAttribute(tag, "value");
							// Multiple parameters
							for (String value : valueList) {
								if (tagValue != null && tagValue.equals(value)) {
									tag = HtmlUtil.addAttribute(tag, "checked");
									break;
								}
							}
						}else if (tagType.equals("radio")) {
							String tagValue = HtmlUtil.getAttribute(tag, "value");
							if (tagValue != null) {
								if (tagValue.equals(valueList.get(0))) {
									tag = HtmlUtil.addAttribute(tag, "checked");
								}
							}
						}
					}
				}
			} else if (tagName.equalsIgnoreCase("textarea") == true) {
				String name = HtmlUtil.getAttribute(tag, "name");
				List<String> valueList = getValueList(bean, request, name,null);
				if (valueList.size() > 0) {
					tag += new CodecHtml().encode(valueList.get(0));
				}
			} else if (tagName.equalsIgnoreCase("select") == true) {
				currentSelectName = HtmlUtil.getAttribute(tag, "name");
			} else if (tagName.equalsIgnoreCase("/select") == true) {
				currentSelectName = null;
			} else if (tagName.equalsIgnoreCase("option") == true) {
				if (currentSelectName != null) {
					String tagValue = HtmlUtil.getAttribute(tag, "value");
					List<String> valueList = getValueList(bean, request, currentSelectName,null);
					// Multiple parameters
					if (valueList.size() > 0) {
						for (Object value : valueList) {
							Integer intValue = TagUtils.tryGetIntegerFromObject(value);
							String val = null;
							if(intValue!=null){
								val = String.valueOf(intValue);
							} else {
								val = String.valueOf(value);
							}
							if (tagValue != null && tagValue.equals(val)) {
								tag = HtmlUtil.addAttribute(tag, "selected");
								break;
							}
						}
					}
				}
			}
			result.append(tag);
			s = i;
		}
		return result.toString();
	}

	/**
	 * Get the bean
	 */
	private void getBean() {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpSession session = (HttpSession) pageContext.getSession();
		if ("request".equals(beanName)) {
			fromRequest = true;
		} else {
			if ((scope.length() == 0) || (scope.equalsIgnoreCase("page"))) {
				bean = pageContext.getAttribute(beanName);
			} else if (scope.equalsIgnoreCase("request")) {
				bean = request.getAttribute(beanName);
			} else if (scope.equalsIgnoreCase("session")) {
				bean = session.getAttribute(beanName);
			}
		}

	}

	/**
	 *
	 * @param bean
	 * @param request
	 * @param name
	 * @return
	 */
	private List<String> getValueList(Object bean, HttpServletRequest request, String name,String format) {
		HttpSession session = request.getSession();
		List<String> valueList = new ArrayList<String>();
		if (name == null || name.length() == 0) {
			return valueList;
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DecimalFormat df = new DecimalFormat("0.##");    
		try {
			if (!fromRequest && bean != null) {
				Object obj = null;
				try {
					obj = PropertyUtils.getProperty(bean, name);
				} catch (Exception e) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("getValueList(" + name + ") parameter errror, get value from " + scope );
					}
					if ("page".equals(scope)) {
						obj = pageContext.getAttribute(name);
					} else if ("request".equals(scope)) {
						obj = request.getAttribute(name);
					} else if ("session".equals(scope)) {
						obj = session.getAttribute(name);
					}
				}

				if (obj != null) {
					if (Date.class.isInstance(obj)) {
						addDateValue(format, valueList, dateFormat, obj);
					} else if (List.class.isInstance(obj)) {
						valueList = (List) obj;
					} else {
						if(Number.class.isInstance(obj)){
							obj = df.format(obj);
						}
						valueList.add(obj.toString());
					}
				}
			} else {
				// request.parameter
				String[] paramArray = request.getParameterValues(name);
				if (paramArray != null && paramArray.length > 0) {
					valueList = Arrays.asList(paramArray);
				} else {
					// request.getAtttibute
					Object obj = null;
					if ("page".equals(scope)) {
						obj = pageContext.getAttribute(name);
					} else if ("request".equals(scope)) {
						obj = request.getAttribute(name);
					} else if ("session".equals(scope)) {
						obj = session.getAttribute(name);
					}
					if (obj != null) {
						if (Date.class.isInstance(obj)) {
							addDateValue(format, valueList, dateFormat, obj);
						} else if (List.class.isInstance(obj)) {
							valueList = (List) obj;
						} else {
							if(Number.class.isInstance(obj)){
								obj = df.format(obj);
							}
							valueList.add(obj.toString());
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("getValueList",e);
		}
		return valueList;
	}

	private void addDateValue(String format, List<String> valueList, DateFormat dateFormat, Object obj) {
	    if(null!=format&&!"".equals(format)){
	    	DateFormat dateFormat2 = new SimpleDateFormat(format);
	    	valueList.add(dateFormat2.format((Date) obj));
	    }else {
	    	valueList.add(dateFormat.format((Date) obj));
	    }
    }
}