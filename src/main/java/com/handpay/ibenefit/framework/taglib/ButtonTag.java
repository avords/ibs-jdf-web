package com.handpay.ibenefit.framework.taglib;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.handpay.ibenefit.security.entity.MenuLink;
import com.handpay.ibenefit.security.service.IMenuLinkManager;

public class ButtonTag extends SimpleTagSupport {

	private static final Logger LOGGER = Logger.getLogger(RadioTag.class);
	
	private static IMenuLinkManager menuLinkManager;
	private String operationId; //按钮Id
	private String type;//button type : submit, button
	
	private String name;//
	private String id;//
	private String className;//
	private String style;
	private String onclick;//用户自定义要调用的方法 : 例如 delete(id);
	 
	 
	
	public void doTag() throws JspException, IOException {
       Long objectId = Long.MIN_VALUE ;
		
		try {
			objectId = Long.parseLong(operationId);
		} catch (Exception e) {
			
			LOGGER.error("operationId: " + "为非数字", e );
		}
		
        String bodyText = this.invokeBody();
        
        MenuLink menuLink = menuLinkManager.getByObjectId(objectId);
		if (null == menuLink) {
			LOGGER.error("根据operationId: " + "查询不到 MenuLink" );
			 
		}
		
        //如果用户为在标签体重写内容则查询数据库中的名称
        if (StringUtils.isBlank(bodyText)) {
    		bodyText = menuLink.getButtonName();
        }
        
        try {
        	getJspContext().getOut().write(createButtonLabel(bodyText, menuLink));
		} catch (IOException e) {
			 
		}
        
	}
	
	 
	
	public String createButtonLabel(String bodyText, MenuLink menuLink) {
		 StringBuilder sb = new StringBuilder("");
		 
		 sb.append("<button type=\"").append(type).append("\" ");
		 
		 if (StringUtils.isNotBlank(id)) {
		   sb.append("  id=\"").append(id).append("\" ");
		 }
		 
		 if (StringUtils.isNotBlank(className)) {
			   sb.append(" class=\"").append(className).append("\" ");
		 }
		 
		 if (StringUtils.isNotBlank(style)) {
			   sb.append("  style=\"").append(style).append("\" ");
		 }
		 
		 sb.append("  onclick=\" return logOperation( '").append(menuLink.getMenuLink());
		 sb.append("' ," ).append(operationId);
		 
		 if (StringUtils.isNotBlank(onclick)) {
			 sb.append(",").append(onclick);
		 }
		 
		 sb.append(" )\" > ").append(bodyText);
		 sb.append(" </button>");
		 return sb.toString();
	}

	 private String invokeBody() {
		  JspFragment body=this.getJspBody();
		  StringWriter sw=new StringWriter();
		  
		  if(body!=null){
			  
		   try {
		      body.invoke(sw);
		   } catch (JspException e) {
			   LOGGER.error("得到标签体内容失败: ",e );
		   } catch (IOException e) {
			   LOGGER.error("得到标签体内容失败: ",e);
		   }
		   
		  }
		  
		  return sw.toString();
		 
	 }

	public static IMenuLinkManager getMenuLinkManager() {
		return menuLinkManager;
	}

	public void setMenuLinkManager(IMenuLinkManager menuLinkManager) {
		ButtonTag.menuLinkManager = menuLinkManager;
	}

	public String getOperationId() {
		return operationId;
	}


	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public String getStyle() {
		return style;
	}


	public void setStyle(String style) {
		this.style = style;
	}


	public String getOnclick() {
		return onclick;
	}


	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}
	
	
	
	
}
