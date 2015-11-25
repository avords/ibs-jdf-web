package com.handpay.ibenefit.framework.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.handpay.ibenefit.base.area.entity.Area;
import com.handpay.ibenefit.base.area.entity.AreaInfo;
import com.handpay.ibenefit.base.area.service.IAreaManager;

/**
 * 地区填充工具类
 * @author bob.pu
 *
 */
public final class AreaUtils {
	
	public static final String CACHE_KEY_GET_ALL_AREA = IAreaManager.class.getName() + ".getAll()";

	private AreaUtils() {
	}

	public static void setAreaInfo(AreaInfo areaInfo, List<Area> allAreas) {
		int level = areaInfo.getAreaLevel();
		if (level != AreaInfo.LEVEL_NULL) {
			if (AreaInfo.LEVEL_THREE == level) {
				setDistrict(areaInfo, allAreas);
			} else if (AreaInfo.LEVEL_TWO == level) {
				setCity(areaInfo, allAreas);
			} else if (AreaInfo.LEVEL_ONE == level) {
				setProvince(areaInfo, allAreas);
			}
		}
	}

	public static void setDistrict(AreaInfo areaInfo, List<Area> allAreas) {
		if(areaInfo.getAreaLevel()== areaInfo.LEVEL_THREE){
			Area area = getAreaByAreaCode(areaInfo.getAreaCode(), allAreas);
			if (area != null) {
				areaInfo.setDistrict(area.getName());
			}
		}
		setCity(areaInfo, allAreas);
	}

	public static void setCity(AreaInfo areaInfo, List<Area> allAreas) {
		if(areaInfo.getAreaLevel()>=areaInfo.LEVEL_TWO){
			Area area = getAreaByAreaCode(areaInfo.getAreaCode().substring(0, 4),allAreas);
			if (area != null) {
				areaInfo.setCity(area.getName());
			}
		}
		
		setProvince(areaInfo, allAreas);
	}

	public static void setProvince(AreaInfo areaInfo, List<Area> allAreas) {
		if(areaInfo.getAreaLevel()>=areaInfo.LEVEL_ONE){
			Area parentArea = getAreaByAreaCode(areaInfo.getAreaCode().substring(0, 2), allAreas);
			if (parentArea != null) {
				areaInfo.setProvince(parentArea.getName());
			}
		}
	}

	public static Area getAreaByAreaCode(String areaCode, List<Area> allAreas) {
		if (StringUtils.isNotBlank(areaCode)) {
			for (Area area : allAreas) {
				if (area.getAreaCode().equals(areaCode)) {
					return area;
				}
			}
		}
		return null;
	}

}
