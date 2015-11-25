package com.handpay.ibenefit.framework.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.config.annotation.Reference;
import com.handpay.ibenefit.framework.entity.BaseTree;
import com.handpay.ibenefit.framework.entity.Dictionary;
import com.handpay.ibenefit.framework.service.IDictionaryManager;
import com.handpay.ibenefit.framework.service.Manager;
import com.handpay.ibenefit.framework.util.AjaxUtils;

@Controller
@RequestMapping("/dict")
public class DictionaryController extends PageController<Dictionary> {
	
	private static final Logger LOGGER = Logger.getLogger(DictionaryController.class);
	
	private String fileBasePath = "framework/";
	
	@Reference(version="1.0")
	private IDictionaryManager dictionaryManager;

	@Override
    public Manager getEntityManager() {
	    return dictionaryManager;
    }

	@Override
    public String getFileBasePath() {
	    return fileBasePath;
    }

	protected String handleDelete(HttpServletRequest request, HttpServletResponse response, Long id) throws Exception {
		Dictionary dictionary = dictionaryManager.getByObjectId(id);
		dictionaryManager.delete(dictionary.getObjectId());
		updateDictionary(dictionary.getParentId());
		return "redirect:../page" + getMessage("删除成功");
	}
	@RequestMapping("dictTree")
	public String dictTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Dictionary> root=new ArrayList<Dictionary>(1);
		root.add(Dictionary.ROOT);
		String children = parseJsonTree(root);
		request.setAttribute("root", children);
		return getFileBasePath() + "dictTree";
	}
	
	@RequestMapping("dictTree/{dictionaryId}")
	public String dictTree(HttpServletRequest request, @PathVariable Integer dictionaryId) throws Exception {
		dictionaryManager.updateHook(dictionaryId);
		List<Dictionary> root=new ArrayList<Dictionary>(1);
		Dictionary dictionary = dictionaryManager.getDictionaryByDictionaryId(dictionaryId);
		root.add(dictionary);
		String children = parseJsonTree(root);
		request.setAttribute("root", children);
		return getFileBasePath() + "dictTree";
	}
	
	
	private String parseJsonTree(List<Dictionary> areas) {
		if(areas==null){
			return "";
		}
		StringBuilder children = new StringBuilder(areas.size()*80);
		children.append("[");
		int i=0;
		for ( Dictionary area : areas) {
			if(i>0){
				children.append(",");
			}
			//ID is the node identifier
			children.append("{\"id\":\"").append(area.getObjectId());
			if(area.getType()==BaseTree.FOLDER){
				children.append("\",\"rel\":\"folder");
			}else{
				children.append("\",\"rel\":\"file");
			}
			children.append("\",\"code\":\"").append(area.getCode())
			.append("\",\"dictionaryId\":\"").append(area.getDictionaryId()==null?"":area.getDictionaryId())
			.append("\",\"primaryKey\":\"").append(area.getObjectId())
			.append("\",\"parentId\":\"").append(area.getParentId())
			.append("\",\"realName\":\"").append(area.getName())
			.append("\",\"name\":\"");
			children.append(getDisplayName(area));
			children.append("\",\"value\":\"").append(area.getValue())
			.append("\",\"sortId\":\"").append(area.getSortId())
			.append("\",\"remark\":\"").append(area.getRemark())
			.append("\",\"status\":").append(area.getStatus())
			.append(",\"reserver1\":\"").append(area.getReserver1())
			.append("\",\"type\":\"").append(area.getType())
			.append("\",\"reserver2\":\"").append(area.getReserver2())
			.append("\",\"isParent\":").append(area.getType()==BaseTree.FOLDER)
			.append("}");
			i++;
		}
		children.append("]");
		return children.toString();
	}

	private String getDisplayName(Dictionary area) {
		StringBuilder result = new StringBuilder();
		result.append(area.getName());
	    if(area.getObjectId()!= Dictionary.ROOT.getObjectId().longValue()){
	    	if(area.getType()==BaseTree.FOLDER){
	    		if(area.getDictionaryId()!=null){
	    			result.append("(").append(area.getDictionaryId()).append(")");
	    		}
	    	}else{
	    		result.append(":").append(area.getValue()+"["+(area.getStatus()==true?"有效":"无效")+"]");
	    	}
	    }
	    return result.toString();
    }
	
	
	@RequestMapping(value="/getChildren")
	public String getChildren(HttpServletRequest request, HttpServletResponse response, Long id) throws Exception {
		if(id==null){
			id = Dictionary.ROOT.getObjectId();
		}
		List<Dictionary> areas = dictionaryManager.getDirectChildrenByParentId(id);
		String children = parseJsonTree(areas);
		AjaxUtils.doAjaxResponse(response, children);
		return null;
	}

	@RequestMapping("treeDelete/{objectId}")
	public String treeDelete(ModelMap modelMap, @PathVariable Long objectId) {
		Dictionary entity = dictionaryManager.getByObjectId(objectId);
		boolean result = false;
		if (entity != null) {
			dictionaryManager.delete(entity.getObjectId());
			updateDictionary(entity.getParentId());
			result = true;
		}
		modelMap.addAttribute("status", result);
		return "jsonView";
	}
	
	@RequestMapping("get/{dictionaryId}/{value}")
	public String get(ModelMap modelMap, @PathVariable Integer dictionaryId,@PathVariable Integer value) {
		Dictionary entity = getDictionaryByDictionaryIdAndValue(dictionaryId, value);
		modelMap.addAttribute("entity", entity);
		return "jsonView";
	}
	
	public Dictionary getDictionaryByDictionaryIdAndValue(int dictionaryId, int value) {
		List<Dictionary> dictionaries = dictionaryManager.getDictionariesByDictionaryId(dictionaryId);
		for (Dictionary dictionary : dictionaries) {
			if (value == dictionary.getValue()) {
				return dictionary;
			}
		}
		return null;
	}
	
	@RequestMapping("treeSave")
	public String treeSave(HttpServletRequest request, ModelMap modelMap,Dictionary entity) {
		Dictionary dictionary  = entity;
		if(entity.getType()==null){
			entity.setType(Dictionary.FILE);
		}
		if(entity.getParentId()==2||entity.getParentId()==1){
			entity.setType(Dictionary.FOLDER);
		}
		if(entity.getStatus()==null){
			entity.setStatus(true);
		}
		boolean result = false;
		try{
			entity.setSortId(entity.getValue());
			dictionary = dictionaryManager.save(entity);
			updateDictionary(entity.getParentId());
			result = true;
			if(dictionary.getDictionaryId()!=null){
				dictionaryManager.updateHook(dictionary.getDictionaryId());
			}
			updateDictionary(dictionary.getParentId());
		}catch (Exception e) {
			LOGGER.error("treeSave",e);
		}
		modelMap.addAttribute("entity",dictionary);
		modelMap.addAttribute("result", result);
		return "jsonView";
	}
	
	public void updateDictionary(Long parentId){
		if(parentId!=null){
			Dictionary dictionary = dictionaryManager.getByObjectId(parentId);
			if(dictionary!=null&&dictionary.getDictionaryId()!=null){
				dictionaryManager.updateHook(dictionary.getDictionaryId());
			}	
		}
	}
}
