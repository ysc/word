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

package org.apdplat.word;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.StopWord;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.segmentation.WordSegmentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 中文分词基础入口（启用停用词过滤）
 * @author 杨尚川
 */
public class WordSeg {
    private static final Logger LOGGER = LoggerFactory.getLogger(WordSeg.class);    
    private static final Segmentation segmentation = new WordSegmentation();
    /**
     * 对文本进行分词但不移除停用词
     * @param text 文本
     * @return 分词结果
     */
    public static List<Word> segWithStopWords(String text){
        return segmentation.seg(text);
    }
    /**
     * 对文本进行分词并移除停用词
     * @param text 文本
     * @return 分词结果
     */
    public static List<Word> seg(String text){
        List<Word> words = segmentation.seg(text);
        Iterator<Word> iter = words.iterator();
        while(iter.hasNext()){
            Word word = iter.next();
            if(StopWord.is(word.getText())){
                //去除停用词
                LOGGER.debug("去除停用词："+word.getText());
                iter.remove();
            }
        }
        return words;
    }
    /**
     * 对文件进行分词
     * @param input 输入文件
     * @param output 输出文件
     * @throws Exception 
     */
    public static void seg(File input, File output) throws Exception{
        LOGGER.info("开始对文件进行分词："+input.toString());
        float max=(float)Runtime.getRuntime().maxMemory()/1000000;
        float total=(float)Runtime.getRuntime().totalMemory()/1000000;
        float free=(float)Runtime.getRuntime().freeMemory()/1000000;
        String pre="执行之前剩余内存:"+max+"-"+total+"+"+free+"="+(max-total+free);
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input),"utf-8"));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output),"utf-8"))){
            long size = Files.size(input.toPath());
            LOGGER.info("size:"+size);
            LOGGER.info("文件大小："+(float)size/1024/1024+" MB");
            int textLength=0;
            int progress=0;
            long start = System.currentTimeMillis();
            String line = null;
            while((line = reader.readLine()) != null){
                textLength += line.length();
                List<Word> words = seg(line);
                for(Word word : words){
                    writer.write(word.getText()+" ");
                }
                writer.write("\n");
                progress += line.length();
                if( progress > 500000){
                    progress = 0;
                    LOGGER.info("分词进度："+(int)((float)textLength*2/size*100)+"%");
                }
            }
            long cost = System.currentTimeMillis() - start;
            float rate = textLength/cost;
            LOGGER.info("字符数目："+textLength);
            LOGGER.info("分词耗时："+cost+" 毫秒");
            LOGGER.info("分词速度："+rate+" 字符/毫秒");
        }
        max=(float)Runtime.getRuntime().maxMemory()/1000000;
        total=(float)Runtime.getRuntime().totalMemory()/1000000;
        free=(float)Runtime.getRuntime().freeMemory()/1000000;
        String post="执行之后剩余内存:"+max+"-"+total+"+"+free+"="+(max-total+free);
        LOGGER.info(pre);
        LOGGER.info(post);
        LOGGER.info("将文件 "+input.toString()+" 的分词结果保存到文件 "+output);
    }
    private static void demo(){
        long start = System.currentTimeMillis();
        List<String> sentences = new ArrayList<>();
        sentences.add("杨尚川是APDPlat应用级产品开发平台的作者");
        sentences.add("他说的确实在理");
        sentences.add("提高人民生活水平");
        sentences.add("他俩儿谈恋爱是从头年元月开始的");
        sentences.add("王府饭店的设施和服务是一流的");
        sentences.add("和服务于三日后裁制完毕，并呈送将军府中");
        sentences.add("研究生命的起源");
        sentences.add("他明天起身去北京");
        sentences.add("在这些企业中国有企业有十个");
        sentences.add("他站起身来");
        sentences.add("他们是来查金泰撞人那件事的");
        sentences.add("行侠仗义的查金泰远近闻名");
        sentences.add("长春市长春节致辞");
        sentences.add("他从马上摔下来了,你马上下来一下");
        sentences.add("乒乓球拍卖完了");
        sentences.add("咬死猎人的狗");
        sentences.add("地面积了厚厚的雪");
        sentences.add("这几块地面积还真不小");
        sentences.add("大学生活象白纸");
        sentences.add("结合成分子式");
        sentences.add("有意见分歧");
        sentences.add("发展中国家兔的计划");
        sentences.add("明天他将来北京");
        sentences.add("税收制度将来会更完善");
        sentences.add("依靠群众才能做好工作");
        sentences.add("现在是施展才能的好机会");
        sentences.add("把手举起来");
        sentences.add("茶杯的把手断了");
        sentences.add("以新的姿态出现在世界东方");
        sentences.add("使节约粮食进一步形成风气");
        sentences.add("反映了一个人的精神面貌");
        sentences.add("美国加州大学的科学家发现");
        sentences.add("我好不挺好");
        sentences.add("木有"); 
        sentences.add("下雨天留客天天留我不留");
        sentences.add("叔叔亲了我妈妈也亲了我");
        sentences.add("白马非马");
        sentences.add("学生会写文章");
        sentences.add("张掖市民陈军");
        sentences.add("张掖市明乐县");
        sentences.add("中华人民共和国万岁万岁万万岁");
        sentences.add("word是一个中文分词项目，作者是杨尚川，杨尚川的英文名叫ysc");
        int i=1;
        for(String sentence : sentences){
            List<Word> words = seg(sentence);
            LOGGER.info((i++)+"、切分句子: "+sentence);
            LOGGER.info("    切分结果："+words);
        }
        long cost = System.currentTimeMillis() - start;
        LOGGER.info("耗时: "+cost+" 毫秒");
    }
    public static void processCommand(String... args) throws Exception{
        if(args == null || args.length < 1){
            LOGGER.info("命令不正确");
            return;
        }
        switch(args[0].trim().charAt(0)){
            case 'd':
                demo();
                break;
            case 't':
                if(args.length != 2){
                    showUsage();
                }else{
                    List<Word> words = seg(args[1]);
                    LOGGER.info("切分句子："+args[1]);
                    LOGGER.info("切分结果："+words.toString());
                }
                break;
            case 'f':
                if(args.length != 3){
                    showUsage();
                }else{
                    seg(new File(args[1]), new File(args[2]));
                }
                break;
            default:
                List<Word> words = seg(args[0]);
                LOGGER.info("切分句子："+args[0]);
                LOGGER.info("切分结果："+words.toString());
                break;
        }
    }
    private static void run(String encoding) throws UnsupportedEncodingException, IOException, Exception {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, encoding))){
            String line = null;
            while((line = reader.readLine()) != null){
                if("exit".equals(line)){
                    System.exit(0);
                    LOGGER.info("退出");
                    return;
                }
                if(line.trim().equals("")){
                    continue;
                }
                processCommand(line.split(" "));
                showUsage();
            }
        }
    }
    private static void showUsage(){
        LOGGER.info("");
        LOGGER.info("********************************************");
        LOGGER.info("用法: command [text] [input] [output]");
        LOGGER.info("命令command的可选值为：demo、text、file");
        LOGGER.info("命令可使用缩写d t f，如不指定命令，则默认为text命令，对输入的文本分词");
        LOGGER.info("demo");
        LOGGER.info("text 杨尚川是APDPlat应用级产品开发平台的作者");
        LOGGER.info("file d:/text.txt d:/word.txt");
        LOGGER.info("exit");
        LOGGER.info("********************************************");
        LOGGER.info("输入命令后回车确认：");
    }
    public static void main(String[] args) throws Exception{
        String encoding = "utf-8";
        if(args.length == 1){
            encoding = args[0];
        }
        showUsage();
        run(encoding);
    }
}