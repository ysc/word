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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 词典监控服务 - 自动检测词库变化
 * @author 杨尚川
 */
public class DictionaryWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryWatcher.class);
    
    private WatchService watchService;
    private final Map<WatchKey, Path> directories = new HashMap<>();
    private Thread thread = null;
    
    /**
     * 开始监控目录（启动一个新的线程）
     * @param path 目录
     * @param watcherCallback 回调
     */
    public void startWatch(final String path, final WatcherCallback watcherCallback) {
        startWatch(Paths.get(path), watcherCallback);
    }
    /**
     * 开始监控目录（启动一个新的线程）
     * @param path 目录
     * @param watcherCallback 回调
     */
    public void startWatch(final Path path, final WatcherCallback watcherCallback) {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            registerTree(path);
        } catch (IOException ex) {
            LOGGER.error("监控目录失败：" + path.toAbsolutePath(), ex);
            return;
        }
        if(thread == null){
            thread = new Thread(new Runnable(){

                @Override
                public void run() {
                    watch(watcherCallback);
                }

            });
            thread.start();
        }
    }
    /**
     * 停止目录监控
     */
    public void stopWatch(){
        if(thread != null){
            thread.interrupt();
        }
        if(watchService != null){
            try {
                watchService.close();
            } catch (IOException ex) {
                LOGGER.error("停止监控服务出错", ex);
            }
        }
        directories.clear();
    }
    private void watch(WatcherCallback watcherCallback){
        try {
            while (true) {
                final WatchKey key = watchService.take();
                if(key == null){
                    continue;
                }
                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    final WatchEvent.Kind<?> kind = watchEvent.kind();
                    //忽略无效事件
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                    //path是相对路径（相对于监控目录）
                    final Path contextPath = watchEventPath.context();
                    LOGGER.info("contextPath:"+contextPath);
                    //获取监控目录
                    final Path directoryPath = directories.get(key);
                    LOGGER.info("directoryPath:"+directoryPath);
                    //得到绝对路径
                    final Path absolutePath = directoryPath.resolve(contextPath);
                    LOGGER.info("absolutePath:"+absolutePath);
                    LOGGER.info("kind:"+kind);
                    //判断事件类别
                    switch (kind.name()) {
                        case "ENTRY_CREATE":
                            if (Files.isDirectory(absolutePath, LinkOption.NOFOLLOW_LINKS)) {
                                LOGGER.info("新增目录：" + absolutePath);
                                //为新增的目录及其所有子目录注册监控事件
                                registerTree(absolutePath);
                            }else{
                                LOGGER.info("新增文件：" + absolutePath);
                            }
                            break;
                        case "ENTRY_DELETE":
                            LOGGER.info("删除：" + absolutePath);
                            break;
                        case "ENTRY_MODIFY":
                            LOGGER.info("修改：" + absolutePath);
                            break;
                    }
                    //业务逻辑
                    watcherCallback.execute(absolutePath.toAbsolutePath().toString());
                }
                boolean valid = key.reset();
                if (!valid) {
                    if(directories.get(key) != null){
                        LOGGER.info("停止监控目录："+directories.get(key));
                        directories.remove(key);
                    }
                    if (directories.isEmpty()) {
                        LOGGER.error("退出监控");
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            LOGGER.error("监控目录出错", ex);
        } catch (InterruptedException ex) {
            LOGGER.info("监控目录线程退出");
        } finally{
            try {
                watchService.close();
                LOGGER.info("关闭监控目录服务");
            } catch (IOException ex) {
                LOGGER.error("关闭监控目录服务出错", ex);
            }
        }
    }
    /**
     * 为指定目录及其所有子目录注册监控事件
     * @param start 目录
     * @throws IOException 
     */
    private void registerTree(Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                registerPath(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    /**
     * 为指定目录注册监控事件
     * @param path
     * @throws IOException 
     */
    private void registerPath(Path path) throws IOException {
        LOGGER.info("监控目录:" + path);
        WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
        directories.put(key, path);
    }
    public static void main(String[] args) {
        DictionaryWatcher dictionaryWatcher = new DictionaryWatcher();
        dictionaryWatcher.startWatch("d:/DIC", new WatcherCallback(){
            private long lastExecute = System.currentTimeMillis();
            @Override
            public void execute(String path) {
                if(System.currentTimeMillis() - lastExecute > 1000){                  
                    lastExecute = System.currentTimeMillis();
                    //刷新词典
                    System.out.println("回调："+path);
                }
            }
        });
    }
    public static interface WatcherCallback{
        public void execute(String path);
    }
}