package org.aztec.framework.core.common.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.collect.Lists;

public class FileScanUtils {
    
    
    public static List<String> scanSubFiles(ClassLoader loader,String baseDir) throws URISyntaxException, IOException{
        URL url = loader.getResource(baseDir);
        List<String> subFileNames = Lists.newArrayList();
        if(isJarInside(url)){
            String jarLocation = url.toURI().toString().replace("jar:file:", "").split("!")[0];
            jarLocation = jarLocation.replaceAll("//", "/");
            JarFile jarFile = new JarFile(new File(jarLocation));
            Enumeration<JarEntry> entries = jarFile.entries();
            JarEntry entry = entries.nextElement();
            while (entry != null) {
                // LOG.info("entry name:" + entry.getName());
                if (isUnderDirectory(entry.getName(),baseDir)) {
                    String fileName = getEntryFileName(entry);
                    subFileNames.add(fileName);
                }
                if (entries.hasMoreElements())
                    entry = entries.nextElement();
                else
                    entry = null;
            }
            jarFile.close();
        }
        else{
            File dir = new File(url.toURI());
            for(String subFile : dir.list()){
                subFileNames.add(subFile);
            }
        }
        return subFileNames;
    }
    
    public static String getEntryFileName(JarEntry entry){
        String rawName = entry.getName();
        int lastIndex = rawName.lastIndexOf("/");
        return rawName.substring(lastIndex + 1,rawName.length()); 
    }
    
    public static boolean isUnderDirectory(String entryName,String directory){
        String springBootPrefix = "BOOT-INF/classes";
        String subfix = (!directory.startsWith("/") ? "/" : "") + directory;
        String matchStr = springBootPrefix + subfix;
        if(!entryName.endsWith("/") && entryName.startsWith(matchStr)){
            System.out.println("spring boot match!");
            return true;
        }
        String simpleJarPrefix = "/";
        matchStr = simpleJarPrefix + subfix;
        if(!entryName.endsWith("/") && entryName.startsWith(matchStr)){
            return true;
        }
        return false;
    }
    
    public static String replaceSpringBootUnlessMark(String absolutePath){
        if(absolutePath.contains("/BOOT-INF/classes!")){
            absolutePath = absolutePath.replace("/BOOT-INF/classes!", "/BOOT-INF/classes");
        }
        return absolutePath;
    }

    public static boolean isJarInside(URL url) throws URISyntaxException {
        if (url.toURI().toString().contains("jar:file"))
            return true;
        return false;
    }
}
