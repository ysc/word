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

package org.apdplat.word.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apdplat.word.dictionary.impl.TrieV4;
import org.apdplat.word.recognition.PersonName;
import org.apdplat.word.util.DirectoryWatcher;
import org.apdplat.word.util.WordConfTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 词典工厂
 * 通过系统属性及配置文件指定词典实现类（dic.class）和词典文件（dic.path）
 * 指定方式一，编程指定（高优先级）：
 *      System.setProperty("dic.class", "org.apdplat.word.dictionary.impl.TrieV4");
 *      System.setProperty("dic.path", "classpath:dic.txt");
 * 指定方式二，Java虚拟机启动参数（中优先级）：
 *      java -Ddic.class=org.apdplat.word.dictionary.impl.TrieV4 -Ddic.path=classpath:dic.txt
 * 指定方式三，配置文件指定（低优先级）：
 *      在类路径下的word.conf中指定配置信息
 *      dic.class=org.apdplat.word.dictionary.impl.TrieV4
 *      dic.path=classpath:dic.txt
 * 如未指定，则默认使用词典实现类（org.apdplat.word.dictionary.impl.TrieV4）和词典文件（类路径下的dic.txt）
 * @author 杨尚川
 */
public final class DictionaryFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryFactory.class);
    private DictionaryFactory(){}
    public static final Dictionary getDictionary(){
        return DictionaryHolder.DIC;
    }
    private static final class DictionaryHolder{
        private static final Dictionary DIC = constructDictionary();
        private static final Set<String> fileWatchers = new HashSet<>();
        private static final DirectoryWatcher dictionaryDirectoryWatcher = DirectoryWatcher.getDirectoryWatcher(new DirectoryWatcher.WatcherCallback(){

                private long lastExecute = System.currentTimeMillis();
                @Override
                public void execute(WatchEvent.Kind<?> kind, String path) {
                    if(System.currentTimeMillis() - lastExecute > 1000){
                        lastExecute = System.currentTimeMillis();
                        LOGGER.info("事件："+kind.name()+" ,路径："+path);
                        synchronized(DictionaryHolder.class){
                            LOGGER.info("清空词典数据");
                            DIC.clear();
                            LOGGER.info("重新加载词典数据");
                            initDic();
                        }
                    }
                }
            
            }, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
        
        static{
            initDic();
        }
        private static Dictionary constructDictionary(){  
            try{
                //选择词典实现，可以通过参数选择不同的实现
                String dicClass = System.getProperty("dic.class");
                if(dicClass == null){
                    dicClass = WordConfTools.get("dic.class", "org.apdplat.word.dictionary.impl.TrieV4");
                }
                LOGGER.info("dic.class="+dicClass);
                return (Dictionary)Class.forName(dicClass.trim()).newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
                System.err.println("词典装载失败:"+ex.getMessage());
                throw new RuntimeException(ex);
            }
        }
        private static void initDic(){
            LOGGER.info("开始初始化词典");
            long start = System.currentTimeMillis();
            //选择词典
            String dicPath = System.getProperty("dic.path");
            if(dicPath == null){
                dicPath = WordConfTools.get("dic.path", "classpath:dic.txt");
            }
            LOGGER.info("将标点符号加入词典");            
            dicPath += ","+WordConfTools.get("punctuation.path", "classpath:punctuation.txt");
            LOGGER.info("dic.path="+dicPath);
            int count=0;
            for(String surname : PersonName.getSurnames()){
                if(surname.length() == 2){
                    count++;
                    DIC.add(surname);
                }
            }
            LOGGER.info("将 "+count+" 个复姓加入词典");
            loadDic(dicPath.trim());
            if(DIC instanceof TrieV4){
                TrieV4 trieV4 = (TrieV4)DIC;
                trieV4.showConflict();
            }
            long cost = System.currentTimeMillis() - start;
            LOGGER.info("完成初始化词典，耗时"+cost+" 毫秒");
        }
        private static void loadDic(String trim) {
            //统计词长分布
            Map<Integer,Integer> map = new TreeMap<>();
            String[] dics = trim.split("[,，]");
            for(String dic : dics){
                try{
                    dic = dic.trim();
                    if(dic.startsWith("classpath:")){
                        //处理类路径资源
                        loadClasspathDic(dic.replace("classpath:", ""), map);
                    }else{
                        //处理非类路径资源
                        loadNoneClasspathDic(dic, map);
                    }
                }catch(Exception e){
                    LOGGER.error("装载词典失败："+dic, e);
                }
            }
            showStatistics(map);
        }
        private static void loadClasspathDic(String dic, final Map<Integer, Integer> map) throws IOException{
            Enumeration<URL> ps = Thread.currentThread().getContextClassLoader().getResources(dic);
            while(ps.hasMoreElements()) {
                URL url=ps.nextElement();
                if(url.getFile().contains(".jar!")){
                    //加载jar资源
                    load("classpath:"+dic, map);
                    continue;
                }
                File file=new File(url.getFile());
                boolean dir = file.isDirectory();
                if(dir){
                    //处理目录
                    loadAndWatchDir(file.toPath(), map);
                }else{
                    //处理文件
                    load(file.getAbsolutePath(), map);
                    //监控文件
                    watchFile(file);
                }            
            }
        }
        private static void loadNoneClasspathDic(String dic, final Map<Integer, Integer> map) throws IOException {
            Path path = Paths.get(dic);
            boolean exist = Files.exists(path);
            if(!exist){
                LOGGER.error("词典不存在："+dic);
                return;
            }
            boolean isDir = Files.isDirectory(path);
            if(isDir){
                //处理目录
                loadAndWatchDir(path, map);
            }else{
                //处理文件
                load(dic, map);
                //监控文件
                watchFile(path.toFile());
            }
        }
        private static void loadAndWatchDir(Path path, final Map<Integer, Integer> map) throws IOException {
            //自动检测词库变化
            dictionaryDirectoryWatcher.watchDirectoryTree(path);
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    load(file.toAbsolutePath().toString(), map);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        private static void load(String dic, Map<Integer,Integer> map) throws FileNotFoundException, UnsupportedEncodingException, IOException {
            LOGGER.info("加载词典："+dic);
            InputStream in = null;
            if(dic.startsWith("classpath:")){
                in = DictionaryFactory.class.getClassLoader().getResourceAsStream(dic.replace("classpath:", ""));
            }else{
                in = new FileInputStream(dic);
            }
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in,"utf-8"))){
                String line;
                while((line = reader.readLine()) != null){
                    line = line.trim();
                    if("".equals(line) || line.startsWith("#")){
                        continue;
                    }
                    //加入词典
                    DIC.add(line);
                    //统计不同长度的词的数目
                    int len = line.length();
                    Integer value = map.get(len);
                    if(value==null){
                        value=1;
                    }else{
                        value++;
                    }
                    map.put(len, value);
                }
            }
        }
        private static void showStatistics(Map<Integer, Integer> map) {            
            //统计词数
            int wordCount=0;
            //统计平均词长
            int totalLength=0;
            for(int len : map.keySet()){
                totalLength += len * map.get(len);
                wordCount += map.get(len);
            }
            LOGGER.info("词数目："+wordCount+"，词典最大词长："+DIC.getMaxLength());
            for(int len : map.keySet()){
                if(len<10){
                    LOGGER.info("词长  "+len+" 的词数为："+map.get(len));
                }else{
                    LOGGER.info("词长 "+len+" 的词数为："+map.get(len));
                }
            }
            LOGGER.info("词典平均词长："+(float)totalLength/wordCount);
        }

        private static void watchFile(final File file) {
            LOGGER.info("监控文件："+file.toString());
            if(fileWatchers.contains(file.toString())){
                //之前已经注册过监控服务，此次忽略
                return;
            }
            fileWatchers.add(file.toString());
            DirectoryWatcher dictionaryFileWatcher = DirectoryWatcher.getDirectoryWatcher(new DirectoryWatcher.WatcherCallback(){

                private long lastExecute = System.currentTimeMillis();
                @Override
                public void execute(WatchEvent.Kind<?> kind, String path) {
                    if(System.currentTimeMillis() - lastExecute > 1000){
                        lastExecute = System.currentTimeMillis();
                        if(!path.equals(file.toString())){
                            return;
                        }
                        LOGGER.info("事件："+kind.name()+" ,路径："+path);
                        synchronized(DictionaryHolder.class){
                            LOGGER.info("清空词典数据");
                            DIC.clear();
                            LOGGER.info("重新加载词典数据");
                            initDic();
                        }
                    }
                }
            
            }, StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);
            dictionaryFileWatcher.watchDirectory(file.getParent());
        }
    }
}
