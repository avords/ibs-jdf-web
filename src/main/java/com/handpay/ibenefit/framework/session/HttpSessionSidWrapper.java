package com.handpay.ibenefit.framework.session;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.handpay.ibenefit.framework.cache.ICacheManager;
import com.handpay.ibenefit.framework.cache.SerializeUtil;

public class HttpSessionSidWrapper extends HttpSessionWrapper {

	private String sid = "";
	private ICacheManager cacheService;
	//Session timeout seconds
	private static final int SESSION_TIMEOUT = 3600;

	
	private Map<String,Object> map = null;

	public HttpSessionSidWrapper(String sid, HttpSession session, ICacheManager cacheService) {
		super(session);
		this.sid = sid;
		this.cacheService = cacheService;
		Map<String,String> data = cacheService.hgetAll(sid);
		map = SerializeUtil.unserializeEachElement(data);
		//当有用户使用该SESSION时，延长其生存周期
		cacheService.expire(this.sid,SESSION_TIMEOUT);
	}

	public Object getAttribute(String key) {
		return this.map.get(key);
	}

	public String getId() {
		return sid;
	}

	public Enumeration getAttributeNames() {
		return (new Enumerator(this.map.keySet(), true));
	}

	public void invalidate() {
		//不删除缓存的SESSION，此时可能正被其它节点使用
		this.map.clear();
	}

	public void removeAttribute(String key) {
		this.map.remove(key);
		cacheService.hdel(sid, key);
	}

	@SuppressWarnings("unchecked")
	public void setAttribute(String key, Object value) {
		this.map.put(key, value);
		cacheService.hset(this.sid, key, SerializeUtil.serializeToString(value));
	}
}
