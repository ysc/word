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

package org.apdplat.word.corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.recognition.Punctuation;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.slf4j.LoggerFactory;

/**
 * 利用人工标注的语料库
 * 对分词算法效果进行评估
 * @author 杨尚川
 */
public class Evaluation {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Evaluation.class);

    public static void main(String[] args) throws Exception{
        String corpusText = "target/evaluation/corpus-text.txt";
        String testText = "target/evaluation/test-text.txt";
        String standardText = "target/evaluation/standard-text.txt";
        String resultText = "target/evaluation/result-text-";
        String perfectResult = "target/evaluation/perfect-result-";
        String wrongResult = "target/evaluation/wrong-result-";
        Path path = Paths.get("target/evaluation");
        if(!Files.exists(path)){
            Files.createDirectory(path);
        }
        //1、抽取文本
        ExtractText.extractFromCorpus(corpusText, " ", false);
        //2、生成测试数据集和标准数据集
        generateDataset(corpusText, testText, standardText);
        Map<String, String> map = new HashMap<>();
        for(SegmentationAlgorithm segmentationAlgorithm : SegmentationAlgorithm.values()){
            //3、对测试数据集进行分词
            WordSegmenter.segWithStopWords(new File(testText), new File(resultText+segmentationAlgorithm.name()+".txt"), segmentationAlgorithm);
            //4、分词效果评估
            String report = evaluation(resultText+segmentationAlgorithm.name()+".txt", standardText, perfectResult+segmentationAlgorithm.name()+".txt", wrongResult+segmentationAlgorithm.name()+".txt");
            map.put(segmentationAlgorithm.name(), report);
        }
        //5、输出测试报告
        LOGGER.info("*********************************************");
        for(String key : map.keySet()){
            LOGGER.info(key+" ：");
            LOGGER.info(map.get(key));
        }
        LOGGER.info("*********************************************");
    }
    /**
     * 生成测试数据集和标准数据集
     * @param file 已分词文本，词之间空格分隔
     * @param test 生成测试数据集文件路径
     * @param standard 生成标准数据集文件路径
     */
    public static void generateDataset(String file, String test, String standard){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
            BufferedWriter testWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(test),"utf-8"));
            BufferedWriter standardWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(standard),"utf-8"))){
            String line;
            while( (line = reader.readLine()) != null ){
                //不把空格当做标点符号
                List<String> list = Punctuation.seg(line, false, ' ');
                for(String item : list){
                    testWriter.write(item.replaceAll(" ", "")+"\n");
                }
                for(String item : list){
                    standardWriter.write(item.trim()+"\n");
                }
            }
        } catch (IOException ex) {
            LOGGER.error("生成测试数据集和标准数据集失败：", ex);
        }
    }
    /**
     * 分词效果评估
     * @param resultText 实际分词结果
     * @param standardText 标准分词结果
     */
    private static String evaluation(String resultText, String standardText, String perfectResult, String wrongResult) {
        long start = System.currentTimeMillis();
        int perfectCount=0;
        int wrongCount=0;
        try(BufferedReader resultReader = new BufferedReader(new InputStreamReader(new FileInputStream(resultText),"utf-8"));
            BufferedReader standardReader = new BufferedReader(new InputStreamReader(new FileInputStream(standardText),"utf-8"));
            BufferedWriter perfectResultWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(perfectResult),"utf-8"));
            BufferedWriter wrongResultWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wrongResult),"utf-8"))){
            String result;
            while( (result = resultReader.readLine()) != null ){
                result = result.trim();
                String standard = standardReader.readLine().trim();
                if(result.equals("")){
                    continue;
                }
                if(result.equals(standard)){
                    //分词结果和标准一模一样
                    perfectResultWriter.write(standard+"\n");
                    perfectCount++;
                }else{
                    //分词结果和标准不一样
                    wrongResultWriter.write("实际分词结果："+result+"\n");
                    wrongResultWriter.write("标准分词结果："+standard+"\n");
                    wrongCount++;
                }
            }
        } catch (IOException ex) {
            LOGGER.error("分词效果评估失败：", ex);
        }
        long cost = System.currentTimeMillis() - start;
        int total = perfectCount+wrongCount;
        LOGGER.info("评估耗时："+cost+" 毫秒");
        String report = "总行数："+total+" ，完美行数："+perfectCount+" ，错误行数："+wrongCount+" ，完美率："+perfectCount/(float)total*100+"% ，错误率："+wrongCount/(float)total*100+"%";
        LOGGER.info(report);
        return report;
    }
}