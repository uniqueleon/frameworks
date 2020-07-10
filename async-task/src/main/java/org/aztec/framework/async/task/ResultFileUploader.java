package org.aztec.framework.async.task;

import java.io.File;

public interface ResultFileUploader {

	public String upload2OSS(File file) throws Throwable;
}
