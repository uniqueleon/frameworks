package org.aztec.framework.async.task.util;

/**
 * ����OSS�������������������������system_property��key������
 * 
 * @author jack_liang 2015��7��23��
 *
 */
public class OSSParam {
	/**
	 * ��Ӧ��key��oss_access_id
	 */
	private String accessId;

	/**
	 * ��Ӧ��key��oss_access_key
	 */
	private String accessKey;

	/**
	 * ��Ӧ��key��oss_url_base
	 */
	private String urlBase;

	/**
	 * ��Ӧkey��oss_bucket_name
	 */
	private String bucketName;
	
	/**
	 *  ��Ӧkey��oss_end_point
	 */
	private String endPoint;

	public OSSParam(String accessId, String accessKey, String urlBase, String bucketName) {
		this.accessId = accessId;
		this.accessKey = accessKey;
		this.urlBase = urlBase;
		this.bucketName = bucketName;
	}
	
	public OSSParam(String accessId, String accessKey, String urlBase, String bucketName,String endPoint) {
		this.accessId = accessId;
		this.accessKey = accessKey;
		this.urlBase = urlBase;
		this.bucketName = bucketName;
		this.endPoint = endPoint;
	}

	public String getAccessId() {
		return accessId;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getUrlBase() {
		return urlBase;
	}

	public void setUrlBase(String urlBase) {
		this.urlBase = urlBase;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}
	
}
