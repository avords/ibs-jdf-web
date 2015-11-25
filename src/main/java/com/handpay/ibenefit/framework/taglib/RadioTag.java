package com.handpay.ibenefit.framework.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.handpay.ibenefit.framework.entity.Dictionary;
import com.handpay.ibenefit.framework.web.DictionaryFacade;

public class RadioTag extends SimpleTagSupport {
	private static final Logger LOGGER = Logger.getLogger(RadioTag.class);
	private String dictionaryId = null;
	private String parentId = null;
	private static DictionaryFacade dictionaryFacade;
	private String sortProperty;
	private String sortOrder;
	private Boolean valid;
	private String name;

	public void doTag() throws JspException, IOException {
		Assert.notNull(dictionaryFacade);
		int eid = 0;
		try {
			eid = Integer.parseInt(dictionaryId);
		} catch (NumberFormatException e) {
			LOGGER.debug("doTag()", e);
		}
		if (eid <= 0) {
			int pId = 0;
			try {
				pId = Integer.parseInt(parentId);
			} catch (NumberFormatException e) {
				LOGGER.error("doTag()", e);
			}
			if (pId > 0) {
				parent(pId);
			}
		} else {
			standard(eid);
		}
	}

	/**
	 *
	 * @param eid
	 *            enumId
	 * @throws JspException
	 * @throws IOException
	 * 
	 */
	private void standard(int dictionaryId) throws JspException, IOException {
		List<Dictionary> dictionaries = null;
		if (sortProperty != null && "name".equals(sortProperty.toLowerCase())) {
			if (sortOrder == null || "asc".equals(sortOrder.toLowerCase())) {
				dictionaries = dictionaryFacade
						.getDictionariesByDictionaryIdOrderByNameAsc(dictionaryId);
			} else {
				dictionaries = dictionaryFacade
						.getDictionariesByDictionaryIdOrderByNameDesc(dictionaryId);
			}
		} else {
			if (valid != null && valid) {
				dictionaries = dictionaryFacade
						.getValidDictionariesByDictionaryId(dictionaryId);
			} else {
				dictionaries = dictionaryFacade
						.getDictionariesByDictionaryId(dictionaryId);
			}
		}
		int count = 0;
		for (Dictionary baseEnum : dictionaries) {
			if (baseEnum != null) {
				if(count == 0){
					getJspContext().getOut().write(
							"<label><input type=\"radio\" name=\"" + name + "\" id=\""
									+ name + baseEnum.getValue() + "\" value=\""
									+ baseEnum.getValue()
									+ "\" checked/>"
									+ baseEnum.getName()+"</label>");
				}else{
					getJspContext().getOut().write(
							"<label><input type=\"radio\" name=\"" + name + "\" id=\""
									+ name + baseEnum.getValue() + "\" value=\""
									+ baseEnum.getValue()
									+ "\"/>"
									+ baseEnum.getName()+"</label>");
				}
			}
			count ++;
		}
	}

	private void parent(int parentId) throws JspException, IOException {
		List<Dictionary> dictionaries = dictionaryFacade
				.getDirectChildrenByParentId(parentId);
		for (Dictionary baseEnum : dictionaries) {
			if (baseEnum != null) {
				getJspContext().getOut().write(
						"<label><input type=\"radio\" name=\"" + name + "\" id=\""
								+ name + baseEnum.getValue() + "\" value=\""
								+ baseEnum.getValue()
								+ "\" class=\"flat-red\"/>"
								+ baseEnum.getName()+"</label>");
			}
		}
	}

	public String getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(String dictionaryId) {
		this.dictionaryId = dictionaryId;
	}

	public void setDictionaryFacade(DictionaryFacade dictionaryFacade) {
		RadioTag.dictionaryFacade = dictionaryFacade;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSortProperty() {
		return sortProperty;
	}

	public void setSortProperty(String sortProperty) {
		this.sortProperty = sortProperty;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	/**
	 * getter method
	 * 
	 * @return the name
	 */

	public String getName() {
		return name;
	}

	/**
	 * setter method
	 * 
	 * @param name
	 *            the name to set
	 */

	public void setName(String name) {
		this.name = name;
	}
}