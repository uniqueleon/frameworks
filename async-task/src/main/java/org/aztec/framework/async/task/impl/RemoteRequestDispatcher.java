package org.aztec.framework.async.task.impl;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.aztec.framework.async.task.AsyncTaskConst;
import org.aztec.framework.async.task.AsyncTaskDataService;
import org.aztec.framework.async.task.AsyncTaskException;
import org.aztec.framework.async.task.AsyncTaskServerBean;
import org.aztec.framework.async.task.entity.AsyncTaskDetailDO;
import org.aztec.framework.async.task.mapper.AsyncTaskDetailMapper;
import org.aztec.framework.disconf.items.AsyncTaskConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.sjsc.framework.api.restful.constant.AsyncDetailTaskStatus;
import com.sjsc.framework.api.restful.constant.AsyncTaskType;
import com.sjsc.framework.api.restful.entity.RestRequest;
import com.sjsc.framework.api.restful.entity.RestResult;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;
import com.sjsc.framework.api.restful.entity.async.SubTaskExecParam;
import com.sjsc.framework.api.restful.feign.AsyncTaskInvokeService;
import com.sjsc.framework.api.restful.util.DynamicFeignUtils;
import com.sjsc.framework.core.utils.MinioFileDownloader;
import com.sjsc.framework.core.utils.MinioFileUploader;
import com.sjsc.framework.core.utils.ZipUtils;
import com.sjsc.framework.heartbeat.HeartbeatManageService;
import com.sjsc.framework.heartbeat.entity.ServerNode;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidArgumentException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.NoResponseException;
import io.minio.errors.RegionConflictException;

@AsyncTaskServerBean
@Component
public class RemoteRequestDispatcher extends AbstractTaskExecutor {

    @Autowired
    AsyncTaskDataService dataService;

    @Autowired
    AsyncTaskConfigure config;

    @Autowired
    MinioFileDownloader downloader;

    @Autowired
    MinioFileUploader uploader;

    @Autowired
    HeartbeatManageService hbManager;

    @Autowired
    AsyncTaskDetailMapper<AsyncTaskDetailDO> detailMapper;

    private static Logger LOG = LoggerFactory.getLogger(RemoteRequestDispatcher.class);

    private static final ThreadLocal<List<String>> subFiles = new ThreadLocal<>();

    @Override
    public Integer count() {
        // TODO Auto-generated method stub
        LOG.info("counting sub task!");
        AsyncTaskDTO taskDto = super.taskInfo;
        AsyncTaskType type = AsyncTaskType.getTaskType(taskDto.getTaskType());
        if (AsyncTaskType.IMPORT.equals(type)) {

            try {
                File dlDir = new File(config.getTmpDownloadDir());
                if (!dlDir.exists()) {
                    dlDir.mkdirs();
                }
                File ossFile = downloader.download(taskDto.getOssFileUrl(),
                        config.getTmpDownloadDir() + "/async_task_dl_" + UUID.randomUUID() + ".zip");
                //fileEntries.set(ZipUtils.getFileEntries(ossFile));
                uploadUnzipFiles(taskDto.getOssFileUrl(),taskInfo, ossFile, dlDir);
                Integer num = 1;
                try {
                    if (request.getParameter(AsyncTaskConst.CONTEXT_REQUEST_NUM_PARAM_NAME) != null) {
                        num = Integer.parseInt(request.getParameter(AsyncTaskConst.CONTEXT_REQUEST_NUM_PARAM_NAME));
                    }
                } catch (Exception e) {
                    num = 1;
                }
                LOG.info("counting sub task finshed!");
                return num * subFiles.get().size();
            } catch (Exception e) {
                LOG.error(e.getMessage(),e);
                return 0;
            }

        }
        return 0;
    }

    private void uploadUnzipFiles(String ossPath,AsyncTaskDTO taskDto, File zipFile ,File dlDir)
            throws IOException, InvalidKeyException, InvalidBucketNameException, NoSuchAlgorithmException,
            NoResponseException, ErrorResponseException, InternalException, InvalidArgumentException,
            InsufficientDataException, InvalidResponseException, RegionConflictException, InvalidEndpointException,
            InvalidPortException, XmlPullParserException {

        //List<ZipEntry> entries = fileEntries.get();
        
        LOG.info("uploading sub files。。。。。");
        subFiles.set(Lists.newArrayList());
        String tmpPath = dlDir.getPath() + "/uplTmp/" + removeSuffix(zipFile.getName());
        File tmpDir = new File(tmpPath);
        if(!tmpDir.exists()){
            tmpDir.mkdirs();
        }
        else {
            for(File subFile : tmpDir.listFiles()){
                subFile.delete();
            }
        }
        String encoding = request.getParameter(AsyncTaskConst.CONTEXT_REQUEST_ZIP_FILE_ENCODING);
        File unzipDir = ZipUtils.extractZipByAnt(zipFile, tmpPath,encoding == null ? "GBK" : encoding);
        for(File subFile : unzipDir.listFiles()){
            if(subFile.isDirectory()){
                continue;
            }
            String objectName = removeSuffix(ossPath) + "/" + subFile.getName();
            LOG.info("uploading subfile [" + subFile.getName() + "] to object path[" + objectName + "]");
            uploader.uploadFile(subFile.getAbsolutePath(), objectName);
            subFiles.get().add(objectName);
            //fileEntries.get().a
        }
        LOG.info("upload finshed!");
//        for (int i = 0; i < entries.size(); i++) {
//            ZipEntry targetEntry = entries.get(i);
//            String fileUrl = taskDto.getOssFileUrl();
//            String objectName = getParentPath(fileUrl) + "/" + getEntryFileName(targetEntry.getName());
////            File targetFile = ZipUtils.unzipFile(zipFile, targetEntry,tmpPath + "/" + targetEntry.getName());
////            uploader.uploadFile(targetFile.getPath(), objectName);
//        }
    }
    
    
    
    private static String removeSuffix(String entryName){

        int dashIndex = entryName.lastIndexOf(".");
        return dashIndex == -1 ? entryName : entryName.substring(0, dashIndex);
    }

    /*private static String getSubfileName(String filePath) {
        int dashIndex = filePath.lastIndexOf("/");
        int suffix = filePath.indexOf(".zip");
        return filePath.substring(dashIndex != -1 ? dashIndex + 1 : 0, suffix);
    }*/

    /*private static String getParentPath(String filePath) {
        int dashIndex = filePath.lastIndexOf("/");

        return filePath.substring(0, dashIndex == -1 ? filePath.length() : dashIndex);
    }*/

    @Override
    public void doRealExport() throws Exception {
        // TODO Auto-generated method stub

        AsyncTaskDTO taskDto = super.taskInfo;

        List<ServerNode> nodes = hbManager.pickServers(taskDto.getModule());
        for (int i = 0; i < nodes.size(); i++) {
            ServerNode node = nodes.get(i);
            if (node == null) {
                throw new Exception("No task server available!");
            }
            AsyncTaskDetailDO detail = createDetailRecord(node);
            if (detail != null) {
                try {
                    if(invoke(node, detail)){

                        return ;
                    }
                } catch (Exception e) {
                    LOG.info("[WARN]:Invoke  subtask fail on Server[" + node.getHost() + ":" + node.getPort()
                            + "]!Try next!");
                }
            } else {
                LOG.info("[WARN]:Create subtask fail on Server[" + node.getHost() + ":" + node.getPort()
                        + "]!Try next!");
            }
        }

        throw new AsyncTaskException("Can submit subtask for task[" + taskDto.getSeqNo() + "]",
                AsyncTaskException.TASK_SUMBIT_ERROR, null);

    }

    private AsyncTaskDetailDO createDetailRecord(ServerNode node) {
        Integer seqNo = Integer.parseInt(request.getParameter(AsyncTaskConst.CONTEXT_REQUEST_SUB_TASK_NO));
        Integer num = Integer.parseInt(request.getParameter(AsyncTaskConst.CONTEXT_REQUEST_NUM_PARAM_NAME));
        AsyncTaskDetailDO detailDO = new AsyncTaskDetailDO();
        detailDO.setCurrentDataNo(new Long(0));
        detailDO.setDataSize(new Long(num));
        detailDO.setPrecent(0f);
        detailDO.setSeqNo(seqNo);
        detailDO.setServer(node.getHost() + ":" + node.getPort());
        detailDO.setStatus(AsyncDetailTaskStatus.CREATE.getDbcode());
        detailDO.setTaskId(taskInfo.getId());
        detailDO.setUpdateTime(new Date());
        detailDO.setStartTime(new Date());
        detailDO.setStatus(AsyncDetailTaskStatus.CREATE.getDbcode());
        AsyncTaskDetailDO oldData = detailMapper.findSubtask(taskInfo.getId(), seqNo);
        if (oldData == null) {
            return detailMapper.insert(detailDO) > 0 ? detailDO : null;
        } else {
            oldData.setServer(detailDO.getServer());
            oldData.setUpdateTime(new Date());
            return detailMapper.updateByPrimaryKey(oldData) > 0 ? detailDO : null;
        }
    }

    private boolean invoke(ServerNode node, AsyncTaskDetailDO detailDO) {
        String url = DynamicFeignUtils.DEFAULT_FEIGN_CLIENT_PREFIX + node.getHost()
                + DynamicFeignUtils.DEFAULT_SERVER_PORT_SEPERATOR + node.getPort();
        AsyncTaskInvokeService invokeService = DynamicFeignUtils.getFeignClient(AsyncTaskInvokeService.class, url);
        //
        Integer seqNo = Integer.parseInt(request.getParameter(AsyncTaskConst.CONTEXT_REQUEST_SUB_TASK_NO));
        HttpSession session = request.getSession();
        SubTaskExecParam taskParam = new SubTaskExecParam();
        Map paramMap = request.getParameterMap();
        Map<String, String> reqParamMap = Maps.newHashMap();
        for (Object paramKey : paramMap.keySet()) {
            reqParamMap.put((String) paramKey, paramMap.get(paramKey).toString());
        }
        Map<String, Object> sessionDatas = Maps.newHashMap();
        Enumeration<String> enumStrs  = session.getAttributeNames();
        while(enumStrs.hasMoreElements()){
            String key = enumStrs.nextElement();
            sessionDatas.put(key, session.getAttribute(key));
        }
        taskParam.setSessionDatas(sessionDatas);
        // taskParam.setDataFile(entry.getName());
        //taskParam.setDataFile(taskInfo.getOssFileUrl());
        taskParam.setDataFile(subFiles.get().get(seqNo));
        taskParam.setSeqNo(seqNo);
        taskParam.setId(detailDO.getId());
        taskParam.setTaskId(taskInfo.getId());
        taskParam.setRequestParam(reqParamMap);
        taskParam.setExecutor(taskInfo.getExecutor());
        RestResult<Boolean> rr = invokeService.invoke(new RestRequest<SubTaskExecParam>(taskParam));
        return rr != null ? rr.isSuccess() : false;
    }

    public static void main(String[] args) {
        String ossPath = "temp/双捷4月份账单.zip";
        String objectName =  removeSuffix(ossPath) + "/" + "1.xls";
        System.out.println(objectName);
    }
}
