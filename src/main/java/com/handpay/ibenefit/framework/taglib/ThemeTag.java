package com.handpay.ibenefit.framework.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.handpay.ibenefit.framework.util.ThemeUtils;

public class ThemeTag extends TagSupport {
    private static final long serialVersionUID = -7499960757189184728L;
	private static final Logger LOGGER = Logger.getLogger(ThemeTag.class);

	protected String getFullCssThemePath(){
		return ThemeUtils.getFullCssThemePath(pageContext.getSession());
	}

	public int doStartTag() throws JspTagException {
		try {
			pageContext.getOut().write(getFullCssThemePath());
		} catch (IOException e) {
			LOGGER.error("doStartTag()", e);
		}
		return EVAL_PAGE;
	}
}
