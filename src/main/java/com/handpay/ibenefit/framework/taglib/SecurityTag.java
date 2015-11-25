package com.handpay.ibenefit.framework.taglib;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.handpay.ibenefit.security.SecurityConstants;
import com.handpay.ibenefit.security.entity.Operation;

public class SecurityTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	private String code;

	public void setCode(String code) {
		this.code = code;
	}
	public int doStartTag() throws JspException{
		if(code==null||"".equals(code)){
			return SKIP_BODY;
		}
		HttpSession session = pageContext.getSession();
		List <Operation> operationList = (List<Operation>)session.getAttribute(SecurityConstants.OPERATION_PERMISSION);
		if (operationList==null||operationList.size()==0) {
			return SKIP_BODY;
		}
		boolean isComplex = false;
		String[] codes = null;
		if(code.indexOf("|")>0){
			isComplex = true;
			codes = code.split("\\|");
		}
		for(int i=0,n=operationList.size();i<n;i++){
			if(!isComplex){
				if(code.equals(operationList.get(i).getCode())){
					return EVAL_BODY_INCLUDE;
				}
			}else{
				for(int j=0;j<codes.length;j++){
					if(codes[j].equals(operationList.get(i).getCode())){
						return EVAL_BODY_INCLUDE;
					}
				}
			}
		}
		return SKIP_BODY;
	}
}