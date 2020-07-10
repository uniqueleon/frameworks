package org.aztec.framework.async.task.impl;

import org.aztec.framework.async.task.ResultFileUploader;

public class FileUploaderFactory {

	public static ResultFileUploader getDefaultUploader(){
		return new FileAppendUploader();
	}
}
