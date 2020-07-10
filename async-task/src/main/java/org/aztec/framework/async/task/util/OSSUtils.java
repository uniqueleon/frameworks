/**
 *  
 */
package org.aztec.framework.async.task.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;


/**
 * ��������Դ�������ϴ�������
 * 
 * @author ���� 2017��9��15��
 * 
 */
public class OSSUtils {

	/**
	 * 
	 * @param path
	 *            �ϴ�����Դ���������ļ�Ŀ¼���������ļ���
	 * @param b
	 *            �ļ����ֽ�����
	 * @param destFileName
	 *            �ϴ�����Դ���������ļ������������ļ������������ļ�Ŀ¼��������Ĭ��Ϊʹ���ϴ��ı����ļ���
	 * @return
	 * @throws IOException
	 */
	public static UploadResultInfo upload(String path, byte[] b,
			String destFileName, OSSParam ossParam) throws Exception {
		// У�����
		ParameterUtils.assertNotBlank(path, "�ϴ�����Դ���������ļ�Ŀ¼����Ϊ��");
		// ParameterUtils.assertNotBlank(localFileName, "�����ļ�������·������Ϊ��");

		InputStream input = null;
		try {
			/*OSSClient client = new OSSClient(ossParam.getEndPoint(),
					ossParam.getAccessId(), ossParam.getAccessKey());

			ObjectMetadata objectMeta = new ObjectMetadata();
			objectMeta.setContentLength(b.length);*/

			UploadResultInfo result = new UploadResultInfo();

			result.setSuccessfull(false);
			if (StringUtils.isNotBlank(destFileName)) {
				result.setFileName(destFileName);
			}
			String key = path + result.getFileName();

			result.setFileUrl(ossParam.getUrlBase() + key);

			input = new ByteArrayInputStream(b);
			//client.putObject(ossParam.getBucketName(), key, input, objectMeta);
			result.setSuccessfull(true);
			return result;
		} catch (Throwable e) {
			throw new IOException("�ϴ��ļ�����Դ����������", e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	/**
	 * ���ļ�����Դ��������ɾ��
	 * 
	 * @param fileName
	 *            ��Դ���������ļ�������·���������ļ���
	 */
	public static void deleteFile(String fileName, OSSParam ossParam)
			throws IOException {
		// У�����
		ParameterUtils.assertNotBlank(fileName, "ɾ�����ļ�������Ϊ��");
		// ��ʼ��OSSClient
//		OSSClient client = new OSSClient(ossParam.getEndPoint(),
//				ossParam.getAccessId(), ossParam.getAccessKey());

		try {
//			client.deleteObject(ossParam.getBucketName(),
//					fileName.replace(ossParam.getUrlBase(), ""));
		} catch (Exception e) {
			throw new IOException("����Դ��������ɾ���ļ�����fileName=" + fileName, e);
		}
	}

	/**
	 * ��ȡ��Դ�������ļ����е���������·���ļ�������������Ŀ¼
	 * 
	 * @return �����ļ�·���б�
	 */
	public static List<String> listFile(String folder, OSSParam ossParam)
			throws IOException {
		// У�����
		ParameterUtils.assertNotBlank(folder, "�ļ���������Ϊ��");

		List<String> files = new ArrayList<String>();
		// ��ʼ��OSSClient
//		OSSClient client = new OSSClient(ossParam.getUrlBase(),
//				ossParam.getAccessId(), ossParam.getAccessKey());
//		try {
//			// ����ListObjectsRequest����
//			ListObjectsRequest listObjectsRequest = new ListObjectsRequest(
//					ossParam.getBucketName());
//
//			// �ݹ��г�Ŀ¼�µ������ļ�
//			listObjectsRequest.setDelimiter("/");
//			listObjectsRequest.setPrefix(folder);
//
//			// ��ȡָ��bucket�µ�����Object��Ϣ
//			ObjectListing listing = client.listObjects(listObjectsRequest);
//			for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
//				files.add(ossParam.getUrlBase() + objectSummary.getKey());
//			}
//		} catch (Exception e) {
//			throw new IOException("��ȡ��Դ�������е��ļ��б����folder=" + folder, e);
//		}

		return files;
	}

	public static UploadResultInfo uploadInBreakpointMode(File localFile,
			String path, int taskNum, long partSize, String destFileName,
			OSSParam ossParam) throws Throwable {
//		OSSClient ossClient = new OSSClient(ossParam.getEndPoint(),
//				ossParam.getAccessId(), ossParam.getAccessKey());
//
//		UploadFileRequest uploadFileRequest = new UploadFileRequest(
//				ossParam.getBucketName(), path + destFileName);
//		// ָ���ϴ��ı����ļ�
//		uploadFileRequest.setUploadFile(localFile.getPath());
//		// ָ���ϴ������߳���
//		uploadFileRequest.setTaskNum(taskNum);
//		// ָ���ϴ��ķ�Ƭ��С
//		uploadFileRequest.setPartSize(partSize);
//		// �����ϵ�����
//		uploadFileRequest.setEnableCheckpoint(true);
//		// �ϵ������ϴ�
//		ossClient.uploadFile(uploadFileRequest);
//		// �ر�client
//		ossClient.shutdown();
		UploadResultInfo result = new UploadResultInfo();
		result.setFileName(destFileName);
		result.setSuccessfull(true);
		String key = path + result.getFileName();
		result.setFileUrl(key);
		return result;
	}

	public static File downloadInBreakpointMode(String key, OSSParam param,
			int taskNum, String targetFile) throws Throwable {
		// endpoint�Ժ���Ϊ��������region�밴ʵ�������д
		String endpoint = param.getEndPoint();
		// ���˺�AccessKey������API����Ȩ�ޣ�������ѭ�����ư�ȫ���ʵ����������ʹ��RAM���˺Ž���API���ʻ��ճ���ά�����¼
		// https://ram.console.aliyun.com ����
		String accessKeyId = param.getAccessId();
		String accessKeySecret = param.getAccessKey();
		// ����OSSClientʵ��
//		OSSClient ossClient = new OSSClient(endpoint, accessKeyId,
//				accessKeySecret);
//		// ��������10�����񲢷����أ������ϵ�����
//		DownloadFileRequest downloadFileRequest = new DownloadFileRequest(
//				param.getBucketName(), key);
//		downloadFileRequest.setDownloadFile(targetFile);
//		downloadFileRequest.setTaskNum(taskNum);
//		downloadFileRequest.setEnableCheckpoint(true);
//		// �����ļ�
//		DownloadFileResult downloadRes = ossClient
//				.downloadFile(downloadFileRequest);
//		// ���سɹ�ʱ���᷵���ļ���Ԫ��Ϣ
//		downloadRes.getObjectMetadata();
//		// �ر�client
//		ossClient.shutdown();
		return new File(targetFile);
	}

	public static String getAccessURL(String key, OSSParam param,Date expiredDate) {
		// endpoint�Ժ���Ϊ��������region�밴ʵ�������д
		String endpoint = param.getEndPoint();
		// ���˺�AccessKey������API����Ȩ�ޣ�������ѭ�����ư�ȫ���ʵ����������ʹ��RAM���˺Ž���API���ʻ��ճ���ά�����¼
		// https://ram.console.aliyun.com ����
		String accessKeyId = param.getAccessId();
		String accessKeySecret = param.getAccessKey();
		String bucketName = param.getBucketName();
		// ����OSSClientʵ��
//		OSSClient ossClient = new OSSClient(endpoint, accessKeyId,
//				accessKeySecret);
		/**/

//		return ossClient.generatePresignedUrl(bucketName, key,
//				expiredDate != null ? expiredDate : getDefaultExpiredDate()).toString();
		return null;
	}
	
	private static Date getDefaultExpiredDate(){
		Date nowDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(nowDate);
		calendar.add(Calendar.MINUTE, 1);
		return nowDate;
	}

	public static void main(String[] args) {
		try {
			OSSParam param = new OSSParam("LTAItn3KhtQWkZ3L",
					"idZjlyp4PxgAPYhpgSCoIuoSdaWmDD",
					"http://alogsym.oss-cn-hangzhou.aliyuncs.com/",
					"testalogexptask");
			param.setEndPoint("http://oss-cn-hangzhou.aliyuncs.com/");
			// OSSUtils.downloadInBreakpointMode("data/export/wmp_data_export_8520972834990463221-75800_1.xls",
			// param, 10, "test.xls");
			Date nowDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nowDate);
			calendar.add(Calendar.DATE, 30);
			System.out
					.println(getAccessURL(
							"data/export/wmp_data_export_75635118889589977701-75800_5.xls",
							param,calendar.getTime()));
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
