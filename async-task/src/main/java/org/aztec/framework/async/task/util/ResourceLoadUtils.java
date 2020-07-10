package org.aztec.framework.async.task.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * 
 * @author ningzj
 * @deprecated ���Է����ļ����ع�����
 */
public class ResourceLoadUtils {
	public final static Logger logger = LoggerFactory
			.getLogger(ResourceLoadUtils.class);
	// ��ȡ��Դ�ļ�(����)
	public static Map<String, String> zhResMap = new HashMap<String, String>();
	// ��ȡ��Դ�ļ�����(Ӣ��)
	public static Map<String, String> enResMap = new HashMap<String, String>();
	// ��Դ�ļ����·��
	private static String RESOURCE_PATH = "classpath*:**/local/?*.properties";

	/**
	 * ��ȡ��Դ�ļ�
	 * 
	 * @return
	 */
	public static String[] getFilePath() {
		// ����ָ����·�����������Դ
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = null;
		String names[] = null;
		try {
			resources = resolver.getResources(RESOURCE_PATH);
			names = new String[resources.length];
			for (int i = 0; i < resources.length; i++) {
				names[i] = resources[i].getFile().getAbsolutePath();
			}
		} catch (IOException e) {
			logger.error("���ع��ʻ������ļ��쳣:", e);
		}
		return names;
	}

	public static void loadResFile() {
		String[] fileNames = getFilePath();
		if (null != fileNames && fileNames.length > 0) {
			try {
				for (int i = 0; i < fileNames.length; i++) {
					// ���ʻ���Դ�ļ�����resource_��ͷ,�ļ�Ϊproperties�ļ�
					if (!fileNames[i].contains("resource_")
							|| !fileNames[i].contains(".properties")) {
						logger.error("���ʻ������ļ������õ��ļ��������Ϲ淶!");
						continue;
					}
					Properties props = new Properties();
					InputStream in = null;
					in = new BufferedInputStream(new FileInputStream(
							fileNames[i]));
					props.load(in);
					Set keySet = props.keySet();
					String key = null;
					for (Iterator it = (Iterator) keySet.iterator(); it
							.hasNext();) {
						key = (String) it.next();
						if (fileNames[i].contains("zh_CN.properties")) {
							zhResMap.put(key, props.getProperty(key));
						} else if (fileNames[i].contains("en_US.properties")) {
							enResMap.put(key, props.getProperty(key));
						}
					}
				}
			} catch (Exception e) {
				logger.error("���ع��ʻ����������ļ��쳣:", e);
			}
		}
	}
}
