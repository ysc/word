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

package org.apdplat.word.segmentation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import org.apdplat.word.util.DirectoryWatcher;
import org.apdplat.word.util.WordConfTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 停用词判定
 * 通过系统属性及配置文件指定停用词词典（stopwords.path）
 * 指定方式一，编程指定（高优先级）：
 *      System.setProperty("stopwords.path", "classpath:stopwords.txt");
 * 指定方式二，Java虚拟机启动参数（中优先级）：
 *      java -Dstopwords.path=classpath:stopwords.txt
 * 指定方式三，配置文件指定（低优先级）：
 *      在类路径下的word.conf中指定配置信息
 *      stopwords.path=classpath:stopwords.txt
 * 如未指定，则默认使用停用词词典文件（类路径下的stopwords.txt）
 * @author 杨尚川
 */
public class StopWord {
    private static final Logger LOGGER = LoggerFactory.getLogger(StopWord.class);
    private static final Set<String> stopwords = new HashSet<>();
    private static final DirectoryWatcher dictionaryDirectoryWatcher = new DirectoryWatcher(new DirectoryWatcher.WatcherCallback(){

                private long lastExecute = System.currentTimeMillis();
                @Override
                public void execute(WatchEvent.Kind<?> kind, String path) {
                    if(System.currentTimeMillis() - lastExecute > 1000){
                        lastExecute = System.currentTimeMillis();
                        LOGGER.info("事件："+kind.name()+" ,路径："+path);
                        synchronized(StopWord.class){
                            LOGGER.info("清空停用词典数据");
                            stopwords.clear();
                            LOGGER.info("重新加载停用词典数据");
                            loadStopWords();
                        }
                    }
                }
            
            }, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
    static{
        loadStopWords();
    }
    public static boolean is(String word){
        return stopwords.contains(word);
    }
    public static void main(String[] args){
        LOGGER.info("停用词：");
        for(String w : stopwords){
            LOGGER.info(w);
        }
    }
    /**
     * 加载停用词典
     */
    public static void loadStopWords(){
        LOGGER.info("开始初始化停用词");
        long start = System.currentTimeMillis();
        String stopwordsPath = System.getProperty("stopwords.path");
        if(stopwordsPath == null){
            stopwordsPath = WordConfTools.get("stopwords.path", "classpath:stopwords.txt");
        }
        LOGGER.info("stopwords.path="+stopwordsPath);
        loadStopWords(stopwordsPath.trim());
        long cost = System.currentTimeMillis() - start;
        LOGGER.info("完成初始化停用词，耗时"+cost+" 毫秒，停用词数目："+stopwords.size());
    }
    /**
     * 加载停用词典
     * @param stopwordsPath 逗号分隔开的多个停用词典文件
     */
    private static void loadStopWords(String dicPaths) {
        String[] dics = dicPaths.split("[,，]");
        for(String dic : dics){
            dic = dic.trim();
            Path path = Paths.get(dic.replace("classpath:", ""));
            boolean exist = Files.exists(path);
            boolean isDir = Files.isDirectory(path);
            if(exist && isDir){
                //处理目录
                loadAndWatchDir(path);
            }else{
                //处理文件
                load(dic);
            }
        }
    }
    private static void loadAndWatchDir(Path path) {
        //自动检测词库变化
        dictionaryDirectoryWatcher.watchDirectoryTree(path);
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    load(file.toAbsolutePath().toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            LOGGER.error("装载停用词典失败："+path, ex);
        }
    }
    private static void load(String path) {
        try{
            InputStream in = null;
            LOGGER.info("加载停用词典："+path);
            if(path.startsWith("classpath:")){
                in = StopWord.class.getClassLoader().getResourceAsStream(path.replace("classpath:", ""));
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
                    stopwords.add(line);
                }
            }
        }catch(Exception e){
            LOGGER.error("装载停用词典失败："+path, e);
        }
    }
}