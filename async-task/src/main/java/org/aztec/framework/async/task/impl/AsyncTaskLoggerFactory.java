package org.aztec.framework.async.task.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncTaskLoggerFactory {


	public static Logger getLogger(String taskSerialID)
			throws IllegalArgumentException, IOException {
		Logger logger = LoggerFactory.getLogger("WmpExportTask");
		Object loggerProxy = Proxy.newProxyInstance(
				Logger.class.getClassLoader(), new Class[] { Logger.class },
				new localTempFileLogger(logger, taskSerialID));
		return (Logger) loggerProxy;
	}

	private static class localTempFileLogger implements InvocationHandler {

		private static final String CMD_PREFIX = "cmd:";
		private Logger realLogger;
		private String taskSeqNo;
		private File logTempFile;

		public localTempFileLogger(Logger realLogger, String taskSeqNo)
				throws IOException {
			this.taskSeqNo = taskSeqNo;
			logTempFile = File.createTempFile(taskSeqNo + "_", "_tmp.log");
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			//FileUtils.writeLines(logTempFile, getWriteLines(args), true);
			method.invoke(realLogger, args);
			return null;
		}

		private List<String> getWriteLines(Object[] args) {
			List<String> writeLines = new ArrayList<>();
			for (int i = 0; i < args.length; i++) {
				Object arg = args[0];
				if (arg instanceof String) {
					writeLines.add((String) arg);
				} else if (Throwable.class.isAssignableFrom(arg.getClass())) {
					Throwable throwableArg = (Throwable) arg;
					writeLines.add(throwableArg.getClass().getName() + ":"
							+ throwableArg.getMessage());
					for (StackTraceElement traceEle : throwableArg
							.getStackTrace()) {
						writeLines.add(traceEle.getClassName() + "."
								+ traceEle.getMethodName() + ":lines "
								+ traceEle.getLineNumber() + "("
								+ traceEle.getFileName() + ")");
					}
				}
			}
			return writeLines;
		}

	}

	private static abstract class LogCommandInterpretor {
		public abstract boolean isExecutable(String cmd, File logFile);

		public abstract void execute(String cmd, File logFile);

		public static LogCommandInterpretor[] getInterpretors() {
			return new LogCommandInterpretor[] { new LogCommandInterpretor() {

				@Override
				public boolean isExecutable(String cmd, File logFile) {
					return cmd.equals("cmd:flush");
				}

				@Override
				public void execute(String cmd, File logFile) {
					/*ExportTaskConfigure expConfig = ExportTaskConfigure
							.getInstance();
					OSSParam ossParam = new OSSParam(
							expConfig.getOssAccessID(),
							expConfig.getOssAccessKey(),
							expConfig.getOssUrlBase(),
							expConfig.getOssBucketName());*/
					// OSSUtils.uploadInBreakpointMode(localFile, path, taskNum,
					// partSize, destFileName, ossParam);

				}

			} };
		}
	}
}
