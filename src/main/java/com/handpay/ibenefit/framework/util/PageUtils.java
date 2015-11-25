package com.handpay.ibenefit.framework.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.extremecomponents.table.context.Context;
import org.extremecomponents.table.context.HttpServletRequestContext;
import org.extremecomponents.table.core.TableConstants;
import org.extremecomponents.table.limit.Limit;
import org.extremecomponents.table.limit.LimitFactory;
import org.extremecomponents.table.limit.Sort;
import org.extremecomponents.table.limit.TableLimit;
import org.extremecomponents.table.limit.TableLimitFactory;
import org.springframework.web.util.WebUtils;

import com.handpay.ibenefit.framework.web.MessageUtils;

public final class PageUtils {
	public static final String PREFIX_WITH_TABLEID = "ec_";
	public static final int PAGE_SIZE_ALL = -999;
	public static final String CURRENT_ROWS_DISPLAYED = TableConstants.CURRENT_ROWS_DISPLAYED;
	public static final String PAGE = TableConstants.PAGE;
	private static final String REQUEST_PAGE_SIZE = PREFIX_WITH_TABLEID + TableConstants.CURRENT_ROWS_DISPLAYED;
	private static final String SESSION_PAGE_SIZE = "s_" + REQUEST_PAGE_SIZE;
	private static final Logger LOGGER = Logger.getLogger(PageUtils.class);
	public static final String LIST_ITEMS = "items";

	public static final String TOTAL_ROWS = "totalRows";

	public static final String SEARCH_PREFIX = "search_";
	
	public static final String RELATIONSHIP_PREFIX = "relation_";
	
	public static final String PAGE_SUFFIX = "_page";
	
	public static final int IS_NOT_BACK = 0;
	
	public static final int IS_BACK = 1;

	private PageUtils() {
	}

	public static PageSearch getPageSearch(HttpServletRequest request) {
		Limit limit = PageUtils.getLimit(request);
		PageSearch page = new PageSearch();
		if (request != null) {
			int pageSize = getSessionPageSize(request);
			boolean isExported = PageUtils.isExported(request);
			int requestPageSize=0;
			if(isExported){
				requestPageSize = setExportPageSize(request, requestPageSize);
			}
			if(requestPageSize==0){
				requestPageSize = getRequestPageSize(request);
			}
			if(requestPageSize!=pageSize){
				pageSize = requestPageSize;
				if(!isExported&&pageSize!=0){
					request.getSession().setAttribute(SESSION_PAGE_SIZE, pageSize);
				}
			}
			pageSize = (pageSize == PAGE_SIZE_ALL)? 0 : pageSize;
			if (pageSize != 0) {
				page.setPageSize(pageSize);
			}
			String requestCurrentPage = request.getParameter(PREFIX_WITH_TABLEID + PAGE);
			int cureentPage = 1;
			if (!StringUtils.isEmpty(requestCurrentPage)) {
				cureentPage = Integer.parseInt(requestCurrentPage);
			}
			page.setCurrentPage(cureentPage);
			Sort sort = limit.getSort();
			//Only support single property 
			if (sort != null && sort.getProperty() != null && sort.getSortOrder() != null) {
				page.setSortProperty(sort.getProperty());
				page.setSortOrder(sort.getSortOrder());
			}
		}
		return page;
	}
	
	public static PageSearch preparePage(HttpServletRequest request,Class entityClass,boolean isAssignableFromBaseEntity) {
		PageSearch page = PageUtils.getPageSearch(request);
	    List<PropertyFilter> propertyFilters = buildPropertyFilters(request, SEARCH_PREFIX, lowerFirstName(entityClass.getSimpleName()));
	    page.setEntityClass(entityClass);
	    try{
	    	if(isAssignableFromBaseEntity&&null==page.getSortProperty()){
				page.setSortProperty("objectId");
				page.setSortOrder("desc");
		    }
	    }catch (Exception e) {
	    	LOGGER.error("preparePage",e);
		}
	    Map<String, Object> filterParamMap = WebUtils.getParametersStartingWith(request, RELATIONSHIP_PREFIX);
		//add relationship
		for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
			page.getRelationships().add(entry.getKey());
		}
	    page.setFilters(propertyFilters);
	    return page;
	}
	
	public static PageSearch preparePageWithoutSort(HttpServletRequest request,Class entityClass) {
		PageSearch page = PageUtils.getPageSearch(request);
	    List<PropertyFilter> propertyFilters = buildPropertyFilters(request, SEARCH_PREFIX, lowerFirstName(entityClass.getSimpleName()));
	    page.setEntityClass(entityClass);
	    Map<String, Object> filterParamMap = WebUtils.getParametersStartingWith(request, RELATIONSHIP_PREFIX);
		//add relationship
		for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
			page.getRelationships().add(entry.getKey());
		}
	    page.setFilters(propertyFilters);
	    return page;
	}
	
	public static List<PropertyFilter> buildPropertyFilters(final HttpServletRequest request,
			final String filterPrefix, final String defaultObjectName) {
		List<PropertyFilter> filterList = new ArrayList<PropertyFilter>();
		// Get request parameter
		Map<String, Object> filterParamMap = WebUtils.getParametersStartingWith(request, filterPrefix);
		// Construct PropertyFilter list
		for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
			String filterName = entry.getKey();
			String value = "";
			//handle array parameter
			if(entry.getValue() instanceof String[]){
				for(String s : (String[])entry.getValue()){
					value += s + ",";
				}
				value = value.substring(0,value.length()-1);
			}else{
				value = entry.getValue().toString();
			}
			//去掉前后空格
			if(value!=null){
				value = value.trim();
			}
			// Ignore when parameter value is blank
			if (StringUtils.isNotBlank(value)) {
				//转义特殊字符  '%'
				value = value.replace("%", "\\%");
				PropertyFilter filter = new PropertyFilter(defaultObjectName, filterName, value);
				filterList.add(filter);
			}
		}
		return filterList;
	}
	
	public static String upperFirstName(String objectName) {
		return objectName.substring(0, 1).toUpperCase() + objectName.substring(1);
	}

	public static String lowerFirstName(String objectName) {
		return objectName.substring(0, 1).toLowerCase() + objectName.substring(1);
	}
	
	public static void afterPage(HttpServletRequest request, PageSearch page, Integer backPage) {
	    request.setAttribute(LIST_ITEMS, page.getList());
		request.setAttribute(TOTAL_ROWS, page.getTotalCount());
		// save query parameters
		if(backPage==null||IS_BACK!=backPage||!PageUtils.isExported(request)){
			StringBuilder query = new StringBuilder(50);
			Iterator it = request.getParameterMap().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				query.append(entry.getKey()).append("=").append(MessageUtils.urlEncodeWithUtf8(((String[])entry.getValue())[0].toString())).append("&");
			}
			request.getSession().setAttribute(request.getRequestURI() + PAGE_SUFFIX, query.toString());
		}
    }

	protected static int getRequestPageSize(HttpServletRequest request) {
		int requestPageSize = 0;
	    try{
	    	requestPageSize = Integer.parseInt(request.getParameter(REQUEST_PAGE_SIZE));
	    }catch (NumberFormatException e) {
	    }
	    return requestPageSize;
    }

	protected static int setExportPageSize(HttpServletRequest request, int requestPageSize) {
	    String isALl = request.getParameter(TableConstants.EXTREME_COMPONENTS_INSTANCE);
	    try{
	    	requestPageSize = Integer.parseInt(isALl);
	    	if(requestPageSize==PAGE_SIZE_ALL){
	    		requestPageSize = Integer.MAX_VALUE;
	    	}
	    }catch (Exception e) {
	    }
	    return requestPageSize;
    }

	protected static int getSessionPageSize(HttpServletRequest request) {
	    Object sessionPageSize = request.getSession().getAttribute(SESSION_PAGE_SIZE);
	    int pageSize = 0;
	    if(sessionPageSize!=null){
	    	pageSize = (Integer)sessionPageSize;
	    }
	    return pageSize;
    }

	public static void setPageParameter(HttpServletRequest request,PageSearch page){
		request.setAttribute(PREFIX_WITH_TABLEID + TableConstants.CURRENT_ROWS_DISPLAYED,page.getPageSize());
		request.setAttribute(PREFIX_WITH_TABLEID + TableConstants.PAGE,page.getCurrentPage());
	}
	public static Limit getLimit(HttpServletRequest request) {
		Context context = new HttpServletRequestContext(request);
		LimitFactory limitFactory = new TableLimitFactory(context);
		return new TableLimit(limitFactory);
	}
	
	public static boolean isExported(HttpServletRequest request){
		String isExported = request.getParameter(TableConstants.EXPORT_TABLE_ID);
		return StringUtils.isNotBlank(isExported);
	}

}
