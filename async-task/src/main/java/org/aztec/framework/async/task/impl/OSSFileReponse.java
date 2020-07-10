package org.aztec.framework.async.task.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.aztec.framework.async.task.AsyncTaskConst;
import org.aztec.framework.async.task.ResultFileUploader;
import org.aztec.framework.disconf.items.AsyncTaskConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class OSSFileReponse implements HttpServletResponse {
	
	private static Logger logger = LoggerFactory.getLogger(AsyncTaskConst.TASK_LOG_NAME);
	private LocalFileServletOutputStream localFileOutputStream = null;
	//private ExportTaskConfigure expConfig = ExportTaskConfigure.getInstance();
	private long taskId = 0l;
	private long warehouseId = 0l;
	private long userId = 0l;
	private String filePrefix = "wmp_data_export_";
	private int bufferSize = 0;
	private PrintWriter writer;
	private String ossFileUrl;
	private Map<String,String> additionalHeadInfo = new ConcurrentHashMap<>();
	@Autowired
	private AsyncTaskConfigure expConfig;
	
	
	public OSSFileReponse(long warehouseId,long userId,long taskId,String description) throws IOException {
		super();
		this.warehouseId = warehouseId;
		this.userId = userId;
		this.taskId = taskId;
		//this.filePrefix = StringUtils.isBlank(description) ? filePrefix : description ;
		localFileOutputStream = new LocalFileServletOutputStream();
	}

	private class LocalFileServletOutputStream extends ServletOutputStream{
		
		private File localTmpFile;
		private OutputStream os;
		private long writeCount = 0;
		private String fileSuffix = ".xls";
		@Override
		public void write(int b) throws IOException {
			// TODO Auto-generated method stub
			if(os == null){
				os = new BufferedOutputStream(new FileOutputStream(localTmpFile), AsyncTaskConfigure.toLong(expConfig.getTempFileBufferSize()).intValue());
				//os = new FileOutputStream(localTmpFile);
			}
			writeCount ++;
			if ((writeCount * 4) >= expConfig.toLong(expConfig.getTempFileMaxSize())) {
				os.close();
				localTmpFile.delete();
				throw new IOException(
						"The generated file is too larger[max acceptable size:"
								+ expConfig.getTempFileMaxSize() + "]!" + AsyncTaskConst.EXPORT_TASK_INNER_ERR_CODE.FILE_MAX_SIZE_EXCEED.getErrorMsg());
			}
			os.write(b);
		}
		
		public LocalFileServletOutputStream() throws IOException{
			init();
		}
		
		public void init() throws IOException{

			
			localTmpFile = File.createTempFile(filePrefix,userId + "-" + warehouseId + "_" + taskId + fileSuffix);
			os = new FileOutputStream(localTmpFile);
			writeCount = 0;
			//os = new BufferedOutputStream(new FileOutputStream(localTmpFile), expConfig.getTempFileBufferSize().intValue());
		}
		
		public void reset() throws IOException{
			if(os != null){
				os.flush();
				os.close();
			}
			writeCount = 0;
			if(localTmpFile != null && localTmpFile.exists()){
				//localTmpFile.delete();
				init();
			}
		}

		public File getLocalTmpFile() {
			return localTmpFile;
		}

		public void setFileSuffix(String suffix){
			this.fileSuffix = suffix;
		}

        @Override
        public boolean isReady() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
            // TODO Auto-generated method stub
            
        }
	}
	

	@Override
	public String getCharacterEncoding() {
		// TODO Auto-generated method stub
		return "UTF-8";
	}

	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return localFileOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		// TODO Auto-generated method stub
		if(writer == null){
			File tmpFile = File.createTempFile("wmp_data_export_", userId + "_" + warehouseId + "_" + taskId + ".html");
			writer = new PrintWriter(new FileOutputStream(tmpFile));
		}
		return writer;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentLength(int len) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentType(String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBufferSize(int size) {
		// TODO Auto-generated method stub
		this.bufferSize = size;
	}

	@Override
	public int getBufferSize() {
		// TODO Auto-generated method stub
		return bufferSize;
	}

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub
		localFileOutputStream.flush();
		//OSSImageService
		try {
			ResultFileUploader uploader = FileUploaderFactory.getDefaultUploader();
			String fileUrl = uploader.upload2OSS(localFileOutputStream.getLocalTmpFile());
			this.ossFileUrl = fileUrl;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error(e.getMessage(),e);
			throw new IOException(e.getMessage(),e);
		}
	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub
		try {
			localFileOutputStream.reset();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		
	}

	@Override
	public boolean isCommitted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		//resetBuffer();
	}

	@Override
	public void setLocale(Locale loc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCookie(Cookie cookie) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsHeader(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String encodeURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectUrl(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendError(int sc) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDateHeader(String name, long date) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDateHeader(String name, long date) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHeader(String name, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addHeader(String name, String value) {
		// TODO Auto-generated method stub
		additionalHeadInfo.put(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addIntHeader(String name, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStatus(int sc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStatus(int sc, String sm) {
		// TODO Auto-generated method stub
		
	}

	public File getLocalResultFile(){
		return localFileOutputStream.getLocalTmpFile();
	}
	
	public String getOSSFileUrl(){
		return ossFileUrl;
	}
	
	public void setOssFileSuffix(String fileSuffix){
		if(localFileOutputStream != null){
			localFileOutputStream.setFileSuffix(fileSuffix);
			try {
				localFileOutputStream.init();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e.getMessage(),e);
			}
		}
	}

    @Override
    public void setContentLengthLong(long length) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getStatus() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getHeader(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        // TODO Auto-generated method stub
        return null;
    }
}
