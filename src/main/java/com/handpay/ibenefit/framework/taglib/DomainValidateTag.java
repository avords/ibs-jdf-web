package com.handpay.ibenefit.framework.taglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

public abstract class DomainValidateTag extends TagSupport {
	protected String domain;

	abstract int validate() throws JspTagException;

	public int doStartTag() throws JspTagException {
		return validate();
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}
