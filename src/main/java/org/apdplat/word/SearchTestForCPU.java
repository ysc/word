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

import org.apdplat.word.dictionary.impl.TrieV3;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 比较词典查询算法的性能（比较运算速度）
 * 
#分别运行10次测试，然后取平均值
LinkedList     10000次查询       cost time:48812 ms
ArrayList      10000次查询       cost time:40219 ms
HashSet        10000次查询       cost time:8 ms
HashSet        1000000次查询     cost time:258 ms
HashSet        100000000次查询   cost time:28575 ms
Trie           10000次查询       cost time:15 ms
Trie           1000000次查询     cost time:1024 ms
Trie           100000000次查询   cost time:104635 ms 
TrieV1         10000次查询       cost time:16 ms
TrieV1         1000000次查询     cost time:780 ms
TrieV1         100000000次查询   cost time:90949 ms
TrieV2         10000次查询       cost time:50 ms
TrieV2         1000000次查询     cost time:4361 ms
TrieV2         100000000次查询   cost time:483398 ms
* 
 * @author 杨尚川
 */
public class SearchTestForCPU {
    //为了生成随机查询的词列表
    private static final List<String> DIC_FOR_TEST = new ArrayList<>();
    //分别使用以下3中DIC实现来测试性能    
    //private static final List<String> DIC = new LinkedList<>();
    //private static final List<String> DIC = new ArrayList<>();
    //private static final Set<String> DIC = new HashSet<>();
    //private static final Trie DIC = new Trie();
    //private static final TrieV1 DIC = new TrieV1();
    //private static final TrieV2 DIC = new TrieV2();
    private static final TrieV3 DIC = new TrieV3();
    static{
        try {
            System.out.println("开始初始化词典");
            int count=0;
            List<String> lines = Files.readAllLines(Paths.get("D:/dic.txt"), Charset.forName("utf-8"));
            for(String line : lines){
                DIC.add(line);
                DIC_FOR_TEST.add(line);
                count++;
            }
            System.out.println("完成初始化词典，词数目："+count);
        } catch (IOException ex) {
            System.err.println("词典装载失败:"+ex.getMessage());
        }      
    }
    public static void main(String[] args){
        //选取随机值
        List<String> words = new ArrayList<>();
        for(int i=0;i<10000;i++){
            words.add(DIC_FOR_TEST.get(new Random(System.nanoTime()+i).nextInt(427452)));
        }
        long start = System.currentTimeMillis();
        for(String word : words){
            DIC.contains(word);
        }
        long cost = System.currentTimeMillis()-start;
        System.out.println("cost time:"+cost+" ms");
    }
}
