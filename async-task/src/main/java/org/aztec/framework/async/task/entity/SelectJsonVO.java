package org.aztec.framework.async.task.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.aztec.framework.async.task.util.StringUtilsProperties;

/** 
 * select������Json���ݴ�����
 */
public class SelectJsonVO {
	
	private String text;
	
	private String value;
	
	public SelectJsonVO(){}
	
	public SelectJsonVO(String text, String value) {
		this.text = StringUtilsProperties.getSesseionByProt(text);
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
