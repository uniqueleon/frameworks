package org.aztec.framework.async.task.impl;

import java.io.File;

import org.aztec.framework.async.task.ResultFileUploader;
import org.aztec.framework.disconf.items.AsyncTaskConfigure;
import org.springframework.beans.factory.annotation.Autowired;

public class FileAppendUploader implements ResultFileUploader {
    
    @Autowired
    AsyncTaskConfigure config;


	@Override
	public String upload2OSS(File file) throws Throwable {
		/*UploadResultInfo resultInfo = OSSUtils.uploadInBreakpointMode(file, config.getOssFilePath(), 5, config.getOssUploadBufferSize(), file.getName(), new OSSParam(config.getOssAccessID(), config
				.getOssAccessKey(), config.getOssUrlBase(), config
				.getOssBucketName(), config.getOssEndPoint()));*/
	    
		//return resultInfo.getFileUrl();
	    return null;
		/*InputStream fis = new FileInputStream(file);
		byte[] bufferedByte = new byte[config.getOssUploadBufferSize()];
		UploadResultInfo result = null;
		while (fis.available() > 0) {
			if (fis.available() < config.getOssUploadBufferSize()) {
				bufferedByte = new byte[fis.available()];
			}
			int readSize = fis.read(bufferedByte);
			result = OSSUtils.appendUpload(
					config.getOssFilePath(),
					result == null ? 0 : result.getNextPosition(),
					bufferedByte,
					file.getName(),
					new OSSParam(config.getOssAccessID(), config
							.getOssAccessKey(), config.getOssUrlBase(), config
							.getOssBucketName(), config.getOssEndPoint()));
			if (readSize == -1)
				break;
		}*/
	}

}
