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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 比较词典查询算法的性能（比较内存占用）
 *  
 * @author 杨尚川
 */
public class SearchTestForMemory {
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
                count++;
            }
            System.out.println("完成初始化词典，词数目："+count);
        } catch (IOException ex) {
            System.err.println("词典装载失败:"+ex.getMessage());
        }      
    }
    public static void main(String[] args) throws InterruptedException{
        Thread.sleep(1000000000);
    }
}
