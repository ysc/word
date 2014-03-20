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

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 将一个文件分词后保存到另一个文件
 * @author 杨尚川
 */
public class SegFile {    
    public static void main(String[] args) throws Exception{
        String input = "input.txt";
        String output = "output.txt";
        if(args.length == 2){
            input = args[0];
            output = args[1];
        }
        long start = System.currentTimeMillis();
        segFile(input, output);
        long cost = System.currentTimeMillis()-start;
        System.out.println("cost time:"+cost+" ms");
    }
    public static void segFile(String input, String output) throws Exception{
        byte[] bytes = Files.readAllBytes(Paths.get(input));
        int byteLength = bytes.length;
        String text = new String(bytes,"utf-8");   
        int textLength = text.length();
        bytes=null;
        System.out.println("对字节数为："+byteLength+"，字符数为："+textLength+" 的文本进行分词");
        long start = System.currentTimeMillis();
        List<String> result = WordSeg.seg(text);
        long cost = System.currentTimeMillis() - start;
        float rateForByte = byteLength/cost;
        float rateForCharacter = textLength/cost;
        System.out.println("分词耗时："+cost+" 毫秒");
        System.out.println("分词速度："+rateForByte+"字节/毫秒");
        System.out.println("分词速度："+rateForCharacter+"字符/毫秒");
        Files.write(Paths.get(output), result, Charset.forName("utf-8"));
    }
}
