package com.handpay.ibenefit.framework.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.handpay.ibenefit.framework.util.ThemeUtils;

public class ToLeftImageTag extends TagSupport{
	private static final Logger LOGGER = Logger.getLogger(ToLeftImageTag.class);
	private String path;
	private String className;
	public int doStartTag() throws JspTagException {
		try {
			JspWriter out = pageContext.getOut();
			StringBuilder builder = new StringBuilder(100);
			builder.append("<img src=\"");
			builder.append(ThemeUtils.getFullCssThemePath(pageContext.getSession())).append(path);
			builder.append("\"");
			if(StringUtils.isNotBlank(className)){
				builder.append(" class=\"").append(className).append("\"");
			}
			builder.append(" align=\"middle\"></img>");
			out.print(builder.toString());
		} catch (IOException e) {
			LOGGER.error("doStartTag", e);
		}
		return EVAL_PAGE;
	}
	public String getPath() {
    	return path;
    }
	public void setPath(String path) {
    	this.path = path;
    }
	public String getClassName() {
    	return className;
    }
	public void setClassName(String className) {
    	this.className = className;
    }
}
