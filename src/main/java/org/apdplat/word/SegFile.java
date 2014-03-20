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
import org.apdplat.word.dictionary.Dictionary;
import org.apdplat.word.dictionary.DictionaryFactory;

/**
 * 将一个文件分词后保存到另一个文件
 * @author 杨尚川
 */
public class SegFile {
    private static final Dictionary DIC = DictionaryFactory.getDictionary();
    
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
        byte[] datas = Files.readAllBytes(Paths.get(input));
        String text = new String(datas,"utf-8");        
        Files.write(Paths.get(output), WordSeg.seg(text), Charset.forName("utf-8"));
    }
}
