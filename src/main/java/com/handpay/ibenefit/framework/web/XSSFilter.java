package com.handpay.ibenefit.framework.web;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringUtils;
import org.owasp.esapi.ESAPI;

/**
 * XSS filter
 */

public final class XSSFilter implements Filter {

	private static String[] excludePaths;

	public void doFilter(ServletRequest reg, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) reg;
		String requestUrl = request.getRequestURI();
		if (!excludeUrl(requestUrl)) {
			chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
		}else{
			chain.doFilter(request, response);
		}
	}

	public void destroy() {

	}

	public void init(FilterConfig config) throws ServletException {
		String excludePath = config.getInitParameter("excludePaths");
		if (StringUtils.isNotBlank(excludePath)) {
			excludePaths = excludePath.split(",");
		}
	}

	private boolean excludeUrl(String url) {
		if (excludePaths != null && excludePaths.length > 0) {
			for (String path : excludePaths) {
				if (url.indexOf(path)!=-1) {
					return true;
				}
			}
		}
		return false;
	}
}
 

class XSSRequestWrapper extends HttpServletRequestWrapper {
	 
    private static Pattern[] patterns = new Pattern[]{
        // Script fragments
        Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
        // src='...'
        Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // lonely script tags
        Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // eval(...)
        Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // expression(...)
        Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // javascript:...
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        // vbscript:...
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        // onload(...)=...
        Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };
 
    public XSSRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }
 
    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
 
        if (values == null) {
            return null;
        }
 
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = stripXSS(values[i]);
        }
 
        return encodedValues;
    }
 
    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
 
        return stripXSS(value);
    }
 
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return stripXSS(value);
    }
 
    private String stripXSS(String value) {
        if (value != null) {
            // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
            // avoid encoded attacks.
        	try{
        		value = ESAPI.encoder().canonicalize(value);
        	}catch(Exception e){
        		
        	}
            // Avoid null characters
            value = value.replaceAll("\0", "");
 
            // Remove all sections that match a pattern
            for (Pattern scriptPattern : patterns){
                value = scriptPattern.matcher(value).replaceAll("");
            }
        }
        return value;
    }
}