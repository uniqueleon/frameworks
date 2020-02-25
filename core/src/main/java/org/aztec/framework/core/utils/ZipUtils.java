package org.aztec.framework.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.google.common.collect.Maps;

public class ZipUtils {

    
    public static byte[] zip(byte[] content) throws IOException{
        

        Map<String,byte[]> rawDatas = Maps.newConcurrentMap();
        rawDatas.put("tmpEntry", content);
        
        return compress(rawDatas).toByteArray();
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ZipEntry entry = new ZipEntry("tmpEntry");
//        ZipOutputStream zos = new ZipOutputStream(bos);
//        zos.putNextEntry(entry);
//        zos.write(content);
//        zos.flush();
        //System.out.println("entry size:" + entry.getSize());
        //return bos.toByteArray();
    }
    
    public static ByteArrayOutputStream compress(Map<String, byte[]> map) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream);
        Set<Map.Entry<String, byte[]>> entries = map.entrySet();
        for (Map.Entry<String, byte[]> entry : entries) {
            zipOut.putNextEntry(new ZipEntry(entry.getKey()));
            zipOut.write(entry.getValue());
        }
        zipOut.close();
        return byteArrayOutputStream;
    }
    
    public static Map<String, byte[]> uncompress(InputStream inputStream) throws IOException {
        ZipInputStream zis = new ZipInputStream(inputStream);
        Map<String, byte[]> map = new HashMap<>();
        ZipEntry ze = null;
        while (((ze = zis.getNextEntry()) != null) && !ze.isDirectory()) {
            String name = ze.getName();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[10240];
            int length = -1;
            while ((length = zis.read(buffer, 0, buffer.length)) > -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            map.put(name, byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.close();
        }
        zis.close();
        return map;
    }
    
    public static byte[] unzip(byte[] content) throws IOException{
        ByteArrayInputStream bos = new ByteArrayInputStream(content);
//        ZipInputStream zos = new ZipInputStream(bos);
//        byte[] tmpBytes = new byte[zos.available()];
//        System.out.println(zos.available());
//        System.out.println(zos.read(tmpBytes));
//        ZipEntry entry = zos.getNextEntry();
//        while(entry != null){
//            System.out.println(entry.getName());
//            System.out.println(entry.getSize());
//            entry = zos.getNextEntry();
//            zos.read(tmpBytes);
//        }
//        return tmpBytes;
        Map<String,byte[]> bytes = uncompress(bos);
        return bytes.get("tmpEntry");
    }
}
