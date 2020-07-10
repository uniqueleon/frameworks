package org.aztec.framework.async.task.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.aztec.framework.async.task.constant.LanguageConstant;

import com.sjsc.framework.redis.entity.UserSession;

public class StringUtilsProperties {

	/**
	 * ���ݴ����key��ȡ��Ӧ��value
	 * 
	 * @param key
	 * @return
	 */
	public static String getSesseionByProt(String key) {
		return getSesseionByProt(key, key,null);
	}
	public static String getSesseionByProt(String key,String defaultValue,Map<String,String> presetKV) {
		UserSession us = UserSession.getSesseionLanguage();
		String language = "";
		if (null != us && null != us.getSessionLg()) {
			language = us.getSessionLg();
		}
		Map<String, String> map = null;
		if (StringUtils.isBlank(key)) {
			return "";
		}else if (language.equals(LanguageConstant.USER_LANGUAGE_EN)) {// Ӣ��
			map = ResourceLoadUtils.enResMap;
		}else {// Ĭ������
			map = ResourceLoadUtils.zhResMap;
		}
		if (null != map) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (key.equals(entry.getKey())) {
					return entry.getValue();
				}
			}
		}
		if (null != presetKV) {
			for (Map.Entry<String, String> entry : presetKV.entrySet()) {
				if (key.equals(entry.getKey())) {
					return entry.getValue();
				}
			}
		}
		return defaultValue;
	}
	public static Map<String, String> getSesseionByProt() {
		UserSession us = UserSession.getSesseionLanguage();
		Map<String, String> map = null;
		String language = "";
		if (null != us && null != us.getSessionLg()) {
			language = us.getSessionLg();
		}
		if (language.equals(LanguageConstant.USER_LANGUAGE_EN)) {// Ӣ��
			map = ResourceLoadUtils.enResMap;
		}else {// Ĭ������
			map = ResourceLoadUtils.zhResMap;
		}
		return map;
	}

	/**
	 * ��������
	 * 
	 * @return
	 */
	public static String getCountryCode() {
		try {
			UserSession us = UserSession.getSesseionLanguage();
			String language = "";
			if (null != us && null != us.getSessionLg()) {
				language = us.getSessionLg();
			}
			if (language.equals(LanguageConstant.USER_LANGUAGE_EN)) {// Ӣ��
				return LanguageConstant.USER_LANGUAGE_EN;
			}else {// Ĭ������
				return LanguageConstant.User_LANGUAGE_ZH;
			}
		} catch (Exception e) {
			return LanguageConstant.User_LANGUAGE_ZH;
		}
	}
	
	/**����key�����Ի�ȡ��Ӧ��value
	 * 
	 * @param key
	 * @param countryCode
	 */
	public static String getValueByKeyAndCountryCode(String key,String countryCode){
		
		if(StringUtils.isBlank(key)){
			return "";
		}
		
		String language = LanguageConstant.User_LANGUAGE_ZH;
		if(StringUtils.isNotBlank(countryCode)){
			language=countryCode;
		}
		
		Map<String, String> map = null;
		if(language.equalsIgnoreCase(LanguageConstant.User_LANGUAGE_ZH)){
			map = ResourceLoadUtils.zhResMap;
		}else if(language.equalsIgnoreCase(LanguageConstant.USER_LANGUAGE_EN)){
			map = ResourceLoadUtils.enResMap;
		}
		
		String value=null;
		if(map!=null){
			value=map.get(key);
			if(StringUtils.isBlank(value)){
				value=key;
			}
		}else{
			return key;
		}
		
		
		
		return value;
		
	}
}
