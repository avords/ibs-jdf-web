package com.handpay.ibenefit.framework.web;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.handpay.ibenefit.framework.FrameworkConstants;
import com.handpay.ibenefit.framework.entity.AbstractEntity;
import com.handpay.ibenefit.framework.entity.AuditEntity;
import com.handpay.ibenefit.framework.entity.ForeverEntity;
import com.handpay.ibenefit.framework.service.Manager;
import com.handpay.ibenefit.framework.util.FrameworkContextUtils;
import com.handpay.ibenefit.framework.util.LocaleUtils;
import com.handpay.ibenefit.framework.util.MessageUtils;

/**
 * Base controller for CRUD operation The entrance is
 * create、edit、save、delete、view The subclass can override
 * handelEdit、handelSave、handelDelete
 * 
 * @author pubx 2010-3-29 02:28:33
 */
public abstract class BaseController<T> {
	
	private static final Logger LOGGER = Logger.getLogger(BaseController.class);
	
	private Class<T> actualArgumentType;
	
	private boolean isAssignableFromBaseEntity = false;
	
	private String entityName;
	
	public BaseController(){
		try{
			ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
			Type type = genericSuperclass.getActualTypeArguments()[0];
			if (type instanceof Class) {
				this.actualArgumentType = (Class) type;
			} else if (type instanceof ParameterizedType) {
				this.actualArgumentType = (Class) ((ParameterizedType) type).getRawType();
			}
		}catch(Exception e){
		}
		if(actualArgumentType!=null){
			isAssignableFromBaseEntity = AbstractEntity.class.isAssignableFrom(actualArgumentType);
		}
	}

	protected Class getActualArgumentType() {
		return actualArgumentType;
	}
	
	protected boolean isAssignableFromBaseEntity(){
		return isAssignableFromBaseEntity;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// CustomDateEditor dateEditor = new CustomDateEditor(new
		// SimpleDateFormat("yyyy-MM-dd"), true);
		// Date transformer
		binder.registerCustomEditor(Date.class, new JdfCustomDateEditor(true));
	}

	/**
	 * Go into the create page
	 * 
	 * @param request
	 * @param response
	 * @param t
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/create")
	public String create(HttpServletRequest request, HttpServletResponse response, T t) throws Exception {
		return handleEdit(request, response, null);
	}

	/**
	 * Go into the edit page
	 * 
	 * @param request
	 * @param response
	 * @param id
	 *            primary key
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit/{objectId}")
	public String edit(HttpServletRequest request, HttpServletResponse response, @PathVariable Long objectId)
			throws Exception {
		return handleEdit(request, response, objectId);
	}

	protected String handleEdit(HttpServletRequest request, HttpServletResponse response, Long objectId)
			throws Exception {
		if (null != objectId) {
			Object entity = getManager().getByObjectId(objectId);
			request.setAttribute("entity", entity);
		}
		return getFileBasePath() + "edit" + getActualArgumentType().getSimpleName();
	}

	/**
	 * Save the submit,and return to it's edit page
	 * 
	 * @param request
	 * @param modelMap
	 * @param t
	 *            Entity
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/save")
	public String save(HttpServletRequest request, ModelMap modelMap, @Valid T t, BindingResult result)
			throws Exception {
		return handleSave(request, modelMap, t);
	}

	protected String handleSave(HttpServletRequest request, ModelMap modelMap, T t) throws Exception {
		t = save(t);
		return "redirect:edit/" + ((AbstractEntity) t).getObjectId() + getMessage("common.base.success", request)
				+ "&" + appendAjaxParameter(request) + "&action=" + request.getParameter("action");
	}
	
	/**
	 * 保存对象，做一些基本赋值处理
	 * @param t
	 * @return 保存后的对象，如果没有主键的会包含创建的主键
	 */
	public T save(T t){
		if(t instanceof AuditEntity){
			AuditEntity baseEntity = (AuditEntity) t;
			if(baseEntity.getObjectId()==null){
				baseEntity.setCreatedBy(FrameworkContextUtils.getCurrentUserId());
				baseEntity.setCreatedOn(new Date());
			}else{
				baseEntity.setUpdatedBy(FrameworkContextUtils.getCurrentUserId());
				baseEntity.setUpdatedOn(new Date());
			}
			if(t instanceof ForeverEntity){
				((ForeverEntity)baseEntity).setDeleted(ForeverEntity.DELETED_NO);
			}
		}
		return getManager().save(t);
	}
	
	/**
	 * 根据主键删除对象，如果是逻辑删除的，做逻辑删除的赋值处理
	 * @param objectId
	 */
	public void delete(Long objectId){
		Class t = getActualArgumentType();
		if(ForeverEntity.class.isAssignableFrom(t)){
			try{
				ForeverEntity foreverEntity = (ForeverEntity)t.newInstance();
				foreverEntity.setObjectId(objectId);
				foreverEntity.setDeleted(ForeverEntity.DELETED_YES);
				foreverEntity.setUpdatedBy(FrameworkContextUtils.getCurrentUserId());
				foreverEntity.setUpdatedOn(new Date());
				getManager().delete(foreverEntity);
			}catch(Exception e){
				LOGGER.error("delete",e);
			}
		}else {
			getManager().delete(objectId);
		}
	}

	protected String getMessage(String message) {
		return com.handpay.ibenefit.framework.web.MessageUtils.getMessage(message);
	}

	protected String getMessage(String message, HttpServletRequest request) {
		Locale locale = LocaleUtils.getLocale(request);
		return com.handpay.ibenefit.framework.web.MessageUtils.getMessage(MessageUtils.getMessage(message, locale));
	}

	/**
	 * Save the submit,and return to query page
	 * 
	 * @param request
	 * @param modelMap
	 * @param t
	 *            Entity
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveToPage")
	public String saveToPage(HttpServletRequest request, ModelMap modelMap, T t) throws Exception {
		return handleSaveToPage(request, modelMap, t);
	}

	protected String handleSaveToPage(HttpServletRequest request, ModelMap modelMap, T t) throws Exception {
		save(t);
		return "redirect:page" + getMessage("common.base.success", request);
	}

	/**
	 * Save the submit,and return to create page
	 * 
	 * @param request
	 * @param modelMap
	 * @param t
	 *            Entity
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveToCreate")
	public String saveToCreate(HttpServletRequest request, ModelMap modelMap, T t) throws Exception {
		return handleSaveToCreate(request, modelMap, t);
	}

	protected String handleSaveToCreate(HttpServletRequest request, ModelMap modelMap, T t) throws Exception {
		save(t);
		return "redirect:create" + getMessage("common.base.success", request) + "&" + appendAjaxParameter(request);
	}

	/**
	 * Delete entity by primary key
	 * 
	 * @param request
	 * @param response
	 * @param id
	 *            Primary key
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete/{objectId}")
	public String delete(HttpServletRequest request, HttpServletResponse response, @PathVariable Long objectId)
			throws Exception {
		return handleDelete(request, response, objectId);
	}
	

	protected String handleDelete(HttpServletRequest request, HttpServletResponse response, Long objectId)
			throws Exception {
		delete(objectId);
		return "redirect:../page" + getMessage("common.base.deleted", request) + "&" + appendAjaxParameter(request);
	}

	@RequestMapping(value = "/jsonDelete/{objectId}")
	@ResponseBody
	public String jsonDelete(HttpServletRequest request, HttpServletResponse response, @PathVariable Long objectId)
			throws Exception {
		WebServiceResult result = new WebServiceResult();
		try{
			getManager().delete(objectId);
			result.setResult("true");
			result.setMessage(MessageUtils.getMessage("common.base.deleted", request));
		}catch(Exception e){
			result.setResult("false");
			result.setMessage(e.getLocalizedMessage());
		}
		return result.toJson();
	}
	
	/**
	 * Go into the view page
	 * 
	 * @param request
	 * @param response
	 * @param id
	 *            Primary key
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/view/{objectId}")
	public String view(HttpServletRequest request, HttpServletResponse response, @PathVariable Long objectId)
			throws Exception {
		return handleView(request, response, objectId);
	}

	protected String handleView(HttpServletRequest request, HttpServletResponse response, Long objectId)
			throws Exception {
		request.setAttribute(FrameworkConstants.VIEW, "1");
		return handleEdit(request, response, objectId);
	}
	
	public String appendAjaxParameter(HttpServletRequest request){
		return "ajax=" + request.getParameter("ajax");
	}
	
	@RequestMapping("isUnique")
	@ResponseBody
	public boolean isUnique(T t){
		if(t!=null){
			return getManager().isUnique(t);
		}else{
			return false;
		}
	}
	/**
	 * The lower first name of entity name: user(com.handpay.ibenefit.security.model.User.java), menuLink(com.handpay.ibenefit.security.model.MenuLink.java)
	 * @return entityName
	 */
	public String getEntityName(){
		return entityName;
	}

	/**
	 * Return the entity manager
	 * 
	 * @return
	 */
	public abstract Manager<T> getManager();

	/**
	 * Return the parent path of the JSP page
	 * 
	 * @return
	 */
	public abstract String getFileBasePath();
}
