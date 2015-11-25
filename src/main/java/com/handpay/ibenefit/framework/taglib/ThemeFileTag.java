package com.handpay.ibenefit.framework.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;

import org.apache.log4j.Logger;

import com.handpay.ibenefit.framework.FrameworkConstants;
import com.handpay.ibenefit.framework.config.GlobalConfig;

public final class ThemeFileTag extends ThemeTag {
    private static final long serialVersionUID = 3690317382673985348L;
	private static final Logger LOGGER = Logger.getLogger(ThemeFileTag.class);
	private static final String CSS_LINK_PREFIX = "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"";
	private static final String JS_LINK_PREFIX = "<script type=\"text/javascript\" src=\"";
	private static final String QUIK_HTML_SUFFIX = "\"/>";
	private static final String SCRIPT_SUFFIX = "\"></script>";
	private String file;

	public int doStartTag() throws JspTagException {
		//Handle with parameter condition
		String parameters = "";
		String[] arr = file.split("\\?");
		file = arr[0];
		if(arr.length==2){
			parameters = "?" + arr[1];
		}
		try {
			if(file.endsWith(".css")){
				pageContext.getOut().write(CSS_LINK_PREFIX + getFullCssThemePath() + file  + parameters + QUIK_HTML_SUFFIX);
			}else if (file.endsWith(".js")){
				pageContext.getOut().write(JS_LINK_PREFIX + getFullJsThemePath() + file + parameters  + SCRIPT_SUFFIX);
			}else if (file.endsWith(".swf")){
				pageContext.getOut().write(getFullFalshThemePath() + file + parameters );
			}else if(file.endsWith(".htc")){
				pageContext.getOut().write(getFullHtcThemePath() + file + parameters );
			//图片
			}else{
				pageContext.getOut().write(getFullImageThemePath() + file + parameters );
			}
		} catch (IOException e) {
			LOGGER.error("doStartTag()", e);
		}
		return SKIP_PAGE;
	}

	private String getFullJsThemePath(){
		return GlobalConfig.getStaticDomain() + FrameworkConstants.SCRIPT_ROOT;
	}

	private String getFullImageThemePath(){
		return getFullCssThemePath() + FrameworkConstants.IMAGE_ROOT;
	}

	private String getFullFalshThemePath(){
		return getFullCssThemePath() + FrameworkConstants.FLASH_ROOT;
	}

	private String getFullHtcThemePath(){
		return GlobalConfig.getStaticDomain() + FrameworkConstants.SCRIPT_ROOT;
	}

	public String getFile() {
    	return file;
    }

	public void setFile(String file) {
    	this.file = file;
    }
}
