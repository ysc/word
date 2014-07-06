/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.word.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 资源变化自动检测
 * @author 杨尚川
 */
public class AutoDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoDetector.class);
    //已经被监控的文件
    private static final Set<String> fileWatchers = new HashSet<>();
    private static final Map<DirectoryWatcher, String> resources = new HashMap<>();
    private static final Map<DirectoryWatcher, ResourceLoader> resourceLoaders = new HashMap<>();
    private static final Map<DirectoryWatcher.WatcherCallback, DirectoryWatcher> watcherCallbacks = new HashMap<>();
    
    /**
     * 加载资源并自动检测资源变化
     * 当资源发生变化的时候重新自动加载
     * @param resourceLoader 资源加载逻辑
     * @param resourcePaths 多个资源路径，用逗号分隔
     */
    public static void loadAndWatch(ResourceLoader resourceLoader, String resourcePaths) {
        LOGGER.info("开始加载资源");
        LOGGER.info(resourcePaths);
        long start = System.currentTimeMillis();
        List<String> result = new ArrayList<>();
        for(String resource : resourcePaths.split("[,，]")){
            try{
                resource = resource.trim();
                if(resource.startsWith("classpath:")){
                    //处理类路径资源
                    result.addAll(loadClasspathResource(resource.replace("classpath:", ""), resourceLoader, resourcePaths));
                }else if(resource.startsWith("http:")){
                    //处理HTTP资源
                    result.addAll(loadHttpResource(resource, resourceLoader));
                }else{
                    //处理非类路径资源
                    result.addAll(loadNoneClasspathResource(resource, resourceLoader, resourcePaths));
                }
            }catch(Exception e){
                LOGGER.error("加载资源失败："+resource, e);
            }
        }
        LOGGER.info("加载资源 "+result.size()+" 行");
        //调用自定义加载逻辑
        resourceLoader.clear();
        resourceLoader.load(result);        
        long cost = System.currentTimeMillis() - start;
        LOGGER.info("完成加载资源，耗时"+cost+" 毫秒");
    }
    /**
     * 加载类路径资源
     * @param resource 资源名称
     * @param resourceLoader 资源自定义加载逻辑
     * @param resourcePaths 资源的所有路径，用于资源监控
     * @return 资源内容
     * @throws IOException 
     */
    private static List<String> loadClasspathResource(String resource, ResourceLoader resourceLoader, String resourcePaths) throws IOException{
        List<String> result = new ArrayList<>();
        LOGGER.info("类路径资源："+resource);
        Enumeration<URL> ps = AutoDetector.class.getClassLoader().getResources(resource);
        while(ps.hasMoreElements()) {
            URL url=ps.nextElement();
            LOGGER.info("类路径资源URL："+url);
            if(url.getFile().contains(".jar!")){
                //加载jar资源
                result.addAll(load("classpath:"+resource));
                continue;
            }
            File file=new File(url.getFile());
            boolean dir = file.isDirectory();
            if(dir){
                //处理目录
                result.addAll(loadAndWatchDir(file.toPath(), resourceLoader, resourcePaths));
            }else{
                //处理文件
                result.addAll(load(file.getAbsolutePath()));
                //监控文件
                watchFile(file, resourceLoader, resourcePaths);
            }            
        }
        return result;
    }
    /**
     * 加载HTTP资源
     * @param resource 资源URL
     * @param resourceLoader 资源自定义加载逻辑
     * @return 资源内容
     */
    private static List<String> loadHttpResource(String resource, ResourceLoader resourceLoader) throws MalformedURLException, IOException {
        List<String> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(resource).openConnection().getInputStream(), "utf-8"))) {
            String line = null;
            while((line = reader.readLine()) != null){
                line = line.trim();
                if("".equals(line) || line.startsWith("#")){
                    continue;
                }
                result.add(line);
            }
        }
        return result;
    }
    /**
     * 加载非类路径资源
     * @param resource 资源路径
     * @param resourceLoader 资源自定义加载逻辑
     * @param resourcePaths 资源的所有路径，用于资源监控
     * @return 资源内容
     * @throws IOException 
     */
    private static List<String> loadNoneClasspathResource(String resource, ResourceLoader resourceLoader, String resourcePaths) throws IOException {
        List<String> result = new ArrayList<>();
        Path path = Paths.get(resource);
        boolean exist = Files.exists(path);
        if(!exist){
            LOGGER.error("资源不存在："+resource);
            return result;
        }
        boolean isDir = Files.isDirectory(path);
        if(isDir){
            //处理目录
            result.addAll(loadAndWatchDir(path, resourceLoader, resourcePaths));
        }else{
            //处理文件
            result.addAll(load(resource));
            //监控文件
            watchFile(path.toFile(), resourceLoader, resourcePaths);
        }
        return result;
    }
    /**
     * 递归加载目录下面的所有资源
     * 并监控目录变化
     * @param path 目录路径
     * @param resourceLoader 资源自定义加载逻辑
     * @param resourcePaths 资源的所有路径，用于资源监控
     * @return 目录所有资源内容
     */
    private static List<String> loadAndWatchDir(Path path, ResourceLoader resourceLoader, String resourcePaths) {
        final List<String> result = new ArrayList<>();
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    result.addAll(load(file.toAbsolutePath().toString()));
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            LOGGER.error("加载资源失败："+path, ex);
        }
        
        if(fileWatchers.contains(path.toString())){
            //之前已经注册过监控服务，此次忽略
            return result;
        }
        fileWatchers.add(path.toString());
        DirectoryWatcher.WatcherCallback watcherCallback = new DirectoryWatcher.WatcherCallback(){

            private long lastExecute = System.currentTimeMillis();
            @Override
            public void execute(WatchEvent.Kind<?> kind, String path) {
                //一秒内发生的多个相同事件认定为一次，防止短时间内多次加载资源
                if(System.currentTimeMillis() - lastExecute > 1000){
                    lastExecute = System.currentTimeMillis();
                    LOGGER.info("事件："+kind.name()+" ,路径："+path);
                    synchronized(AutoDetector.class){
                        DirectoryWatcher dw = watcherCallbacks.get(this);
                        String paths = resources.get(dw);
                        ResourceLoader loader = resourceLoaders.get(dw);
                        LOGGER.info("重新加载数据");
                        loadAndWatch(loader, paths);
                    }
                }
            }

        };
        DirectoryWatcher directoryWatcher = DirectoryWatcher.getDirectoryWatcher(watcherCallback,
                StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
        directoryWatcher.watchDirectoryTree(path);
        
        watcherCallbacks.put(watcherCallback, directoryWatcher);
        resources.put(directoryWatcher, resourcePaths);
        resourceLoaders.put(directoryWatcher, resourceLoader);
        
        return result;
    }
    /**
     * 加载文件资源
     * @param path 文件路径
     * @return 文件内容
     */
    private static List<String> load(String path) {
        List<String> result = new ArrayList<>();
        try{
            InputStream in = null;
            LOGGER.info("加载资源："+path);
            if(path.startsWith("classpath:")){
                in = AutoDetector.class.getClassLoader().getResourceAsStream(path.replace("classpath:", ""));
            }else{
                in = new FileInputStream(path);
            }        
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"))){
                String line;
                while((line = reader.readLine()) != null){
                    line = line.trim();
                    if("".equals(line) || line.startsWith("#")){
                        continue;
                    }
                    result.add(line);
                }
            }
        }catch(Exception e){
            LOGGER.error("加载资源失败："+path, e);
        }
        return result;
    }
    /**
     * 监控文件变化
     * @param file 文件
     */
    private static void watchFile(final File file, ResourceLoader resourceLoader, String resourcePaths) {
        if(fileWatchers.contains(file.toString())){
            //之前已经注册过监控服务，此次忽略
            return;
        }
        fileWatchers.add(file.toString());
        LOGGER.info("监控文件："+file.toString());
        DirectoryWatcher.WatcherCallback watcherCallback = new DirectoryWatcher.WatcherCallback(){
            private long lastExecute = System.currentTimeMillis();
            @Override
            public void execute(WatchEvent.Kind<?> kind, String path) {
                if(System.currentTimeMillis() - lastExecute > 1000){
                    lastExecute = System.currentTimeMillis();
                    if(!path.equals(file.toString())){
                        return;
                    }
                    LOGGER.info("事件："+kind.name()+" ,路径："+path);
                    synchronized(AutoDetector.class){
                        DirectoryWatcher dw = watcherCallbacks.get(this);
                        String paths = resources.get(dw);
                        ResourceLoader loader = resourceLoaders.get(dw);
                        LOGGER.info("重新加载数据");
                        loadAndWatch(loader, paths);
                    }
                }
            }

        };
        DirectoryWatcher fileWatcher = DirectoryWatcher.getDirectoryWatcher(watcherCallback, 
                StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
        fileWatcher.watchDirectory(file.getParent());        
        watcherCallbacks.put(watcherCallback, fileWatcher);
        resources.put(fileWatcher, resourcePaths);
        resourceLoaders.put(fileWatcher, resourceLoader);
    }
    public static void main(String[] args){
        AutoDetector.loadAndWatch(new ResourceLoader(){

            @Override
            public void clear() {
                System.out.println("清空资源");
            }

            @Override
            public void load(List<String> lines) {
                for(String line : lines){
                    System.out.println(line);
                }
            }

            @Override
            public void add(String line) {
                System.out.println("add："+line);
            }

            @Override
            public void remove(String line) {
                System.out.println("remove："+line);
            }
        }, "d:/DIC, d:/DIC2, d:/dic.txt, classpath:dic2.txt,classpath:dic");
    }
}