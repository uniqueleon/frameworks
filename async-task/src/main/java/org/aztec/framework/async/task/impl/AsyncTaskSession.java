package org.aztec.framework.async.task.impl;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.alibaba.fastjson.JSON;


@SuppressWarnings("deprecation")
public class AsyncTaskSession implements HttpSession {
	
	private Map<String,Object> attrMap = new HashMap<>();
	private long creationTime = new Date().getTime();
	private long lastAccessTime = new Date().getTime();
	private int interval = 10000;
	private String id = UUID.randomUUID().toString();
	
	@SuppressWarnings("unchecked")
	public AsyncTaskSession(String attrJson){
		attrMap = JSON.parseObject(attrJson, Map.class);
	}

	@Override
	public long getCreationTime() {
		// TODO Auto-generated method stub
		return creationTime;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		return lastAccessTime;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
	    this.interval = interval;
	}

	@Override
	public int getMaxInactiveInterval() {
		return interval;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return attrMap.get(name);
	}

	@Override
	public Object getValue(String name) {
		// TODO Auto-generated method stub
		return attrMap.get(name);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		Hashtable hashTable = new Hashtable<>();
		for(String key : attrMap.keySet()){
			hashTable.put(key.hashCode(), key);
		}
		return hashTable.elements();
	}

	@Override
	public String[] getValueNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String name, Object value) {
		// TODO Auto-generated method stub
		attrMap.put(name, value);
	}

	@Override
	public void putValue(String name, Object value) {
		// TODO Auto-generated method stub
	    attrMap.put(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		// TODO Auto-generated method stub
	    attrMap.remove(name);
	}

	@Override
	public void removeValue(String name) {
		// TODO Auto-generated method stub
	    attrMap.remove(name);
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

}
