package com.handpay.ibenefit.framework.taglib;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;

import com.handpay.ibenefit.framework.ProjectConfig;
import com.handpay.ibenefit.framework.util.LocaleUtils;
import com.handpay.ibenefit.framework.util.MessageUtils;
import com.handpay.ibenefit.framework.validate.FieldProperty;
import com.handpay.ibenefit.framework.validate.ValidateContainer;
/**
 * Table label validation
 */
public class BootstrapDomainValidatorTag extends DomainValidateTag {
	private static final int DEFAULT_SCRIPT_LENGTH = 200;
	private static final long serialVersionUID = -1951060861357104605L;
	private static final Logger LOGGER = Logger.getLogger(BootstrapDomainValidatorTag.class);
	private static final String GET_ELEMENT_BY_ID_BEGIN = "$('#";
	private static final String GET_ELEMENT_BY_ID_END = "')";
	private static final String GET_ELEMENT_BY_NAME_BEGIN = "$('[name=\"";
	private static final String GET_ELEMENT_BY_NAME_END = "\"]'";
	private static final String FIELD_PREFFIX = "f_";
	private static final String FORM_NAME_PREFFIX = "valid_form_0";
	private static final String DECORATOR_FORM_NAME_PREFFIX = "d_f";
	private static final String FIELD_LABEL = "f_l_";

	private static final ConcurrentHashMap<String,String> VALIDATE_SCRIPTS = new ConcurrentHashMap<String, String>();
	
	private String formId = null;
	private int index = 0;

	private static String getCacheKey(String domainName,Locale locale){
		if(locale==null){
			return domainName;
		}
		return domainName + "_" + locale.getLanguage();
	}
	
	public static String getValidateScripts(String domainName,String formId,int index, Locale locale){
		String key = getCacheKey(domainName, locale);
		String result =  VALIDATE_SCRIPTS.get(key);
		if(null==result){
			result = generateScripts(domainName,formId,index,locale);
			VALIDATE_SCRIPTS.put(key, result);
		}
		return result;
	}

	private static String generateScripts(String domainName, String formId, int index,Locale locale){
		String formGroup = ProjectConfig.getFormGroup();
		String decoratorFormName = DECORATOR_FORM_NAME_PREFFIX + index + "()";
		String formName = FORM_NAME_PREFFIX + index ;
		List<FieldProperty> fieldProperties = new ArrayList<FieldProperty>();
		domainName = domainName.trim();
		if(!"".equals(domainName)){
			String[] domains = domainName.split(",");
			domainName = domains[0];
			for(String entity : domains){
				List<FieldProperty> properties = ValidateContainer.getAllFieldsOfTheDomain(entity);
				if(null!=properties){
					fieldProperties.addAll(properties);
				}
			}
		}
		String simpleName = domainName.substring(domainName.lastIndexOf(".") + 1);
		if (fieldProperties.size() > 0) {
			
			StringBuilder decorator = new StringBuilder(DEFAULT_SCRIPT_LENGTH);
			decorator.append("function ").append(decoratorFormName).append("{\n");
			
			StringBuilder rules = new StringBuilder();
			for (int i=0,n=fieldProperties.size();i<n;i++) {
				FieldProperty fieldProperty = fieldProperties.get(i);
				String currFieldName = fieldProperty.getFieldName();
				Field field = fieldProperty.getField();
				//Not null
				StringBuilder fieldValidate = new StringBuilder();
				Column column = field.getAnnotation(Column.class);
				if (field.isAnnotationPresent(NotNull.class)||field.isAnnotationPresent(Id.class) || (column!=null && !column.nullable())) {
					decorator.append("var ").append(FIELD_PREFFIX).append(i).append("=")
					     .append(GET_ELEMENT_BY_NAME_BEGIN).append(currFieldName).append(GET_ELEMENT_BY_NAME_END).append(",").append(formName).append(");\n");
//					decorator.append("if(isNeedValidate(").append(FIELD_PREFFIX).append(i).append(")){\n");
					decorator.append("var ").append(FIELD_LABEL).append(i).append("=$(\"label[for='").append(currFieldName).append("']\");\n");
//					decorator.append(FIELD_LABEL).append(i).append(".closest('").append(formGroup).append("').addClass('has-error');");
					decorator.append(FIELD_LABEL).append(i).append(".append('<span class=\"not-null\">*：</span>');");
//					decorator.append("}\n");
					fieldValidate.append("required: true,");
				}else{
					decorator.append("var ").append(FIELD_PREFFIX).append(i).append("=")
					     .append(GET_ELEMENT_BY_NAME_BEGIN).append(currFieldName).append(GET_ELEMENT_BY_NAME_END).append(",").append(formName).append(");\n");
					decorator.append("var ").append(FIELD_LABEL).append(i).append("=$(\"label[for='").append(currFieldName).append("']\");\n");
					decorator.append(FIELD_LABEL).append(i).append(".append('<span class=\"can-null\">：</span>');");
				}
				//Length
				
				if (null!=column) {
					int maxWidth = column.length();
					if (maxWidth < Integer.MAX_VALUE) {
						fieldValidate.append("maxlength: ").append(maxWidth).append(",");
					}
				}
				//Type
				Class type = field.getType();
				if(type == Integer.class||type == Long.class){
					fieldValidate.append("integer:true,");
					minAndMax(field, fieldValidate);
				}else if(type == Double.class ||type==Float.class){
					fieldValidate.append("number:true,");
					minAndMax(field, fieldValidate);
				}
				if(fieldValidate.length()>0){
					rules.append(currFieldName).append(":{").append(fieldValidate.substring(0, fieldValidate.length()-1)).append("},");
				}
			}
			StringBuilder validate = new StringBuilder(DEFAULT_SCRIPT_LENGTH);
			if(rules.length()>0){
				validate.append("").append(formName).append("=null;\n");
				validate.append("$(document).ready(function(){\n");
				validate.append("").append(formName).append("=").append(GET_ELEMENT_BY_ID_BEGIN).append(simpleName).append(GET_ELEMENT_BY_ID_END).append(";\n");
				validate.append("if(").append(formName).append(".val() == undefined){\n");
				validate.append(formName);
				if(formId != null &&!"".equals(formId)){
					validate.append("=$(\"#").append(formId).append("\");\n");
				} else{
					validate.append("=$(\"form:first\");}\n");
				}
				validate.append("if(").append(formName).append(".val() != undefined){\n");
				validate.append(formName).append(".validate({rules:{").append(rules.substring(0, rules.length()-1)).append("},");
				validate.append("highlight: function(label) {$(label).closest('").append(formGroup).append("').addClass('has-error');}")
				.append("})\n");
				validate.append("if(").append(formName).append(" != undefined){\n");
				validate.append(decoratorFormName).append(";}}\n");
				validate.append("});");
			}
			decorator.append("}\n");
			return validate.toString() + decorator.toString();
		}
		return "";
	}

	private static void minAndMax(Field field, StringBuilder fieldValidate) {
		Min min = field.getAnnotation(Min.class);
		if(null!=min){
			fieldValidate.append("min:").append(min.value()).append(",");
		}
		Max max = field.getAnnotation(Max.class);
		if(null!=max){
			fieldValidate.append("max:").append(max.value()).append(",");
		}
	}
	
	protected static String noLaterThenToday(Locale locale) {
		if(locale==null){
			 return "No later than today.";
		}
	    return MessageUtils.getMessage("common.validator.noLaterThanToday",locale);
    }

	protected static String mustBeNumber(Locale locale) {
		if(locale==null){
			 return "must be number.";
		}
	    return MessageUtils.getMessage("common.validator.mustBeNumber",locale);
    }

	protected static String mustBeInteger(Locale locale) {
		if(locale==null){
			 return "must be integer.";
		}
	    return MessageUtils.getMessage("common.validator.mustBeInteger",locale);
    }

	protected static String widthTooLong(Locale locale) {
		if(locale==null){
			 return "too long.";
		}
	    return MessageUtils.getMessage("common.validator.widthTooLong",locale);
    }

	protected static String widthNotEnough(Locale locale) {
		if(locale==null){
			 return "width is not enough.";
		}
	    return MessageUtils.getMessage("common.validator.widthNotEnough",locale);
    }

	protected static String cannontBeBull(Locale locale) {
		if(locale==null){
			 return "cannot be null.";
		}
	    return MessageUtils.getMessage("common.validator.cannotBeNull",locale);
    }

	int validate() throws JspTagException {
		if(null!=domain){
			Locale locale = LocaleUtils.getLocale(pageContext.getSession());
			String scripts = getValidateScripts(domain,formId,index,locale);
			try {
				JspWriter out = pageContext.getOut();
				out.print("<script type=\"text/javascript\">\n");
				out.write(scripts);
				out.print("</script>\n");
			} catch (IOException e) {
				LOGGER.error("validate()", e);
			}
		}
		return EVAL_PAGE;
	}

	public String getFormId() {
    	return formId;
    }

	public void setFormId(String formId) {
    	this.formId = formId;
    }

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
