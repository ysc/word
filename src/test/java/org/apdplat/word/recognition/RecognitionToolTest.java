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

package org.apdplat.word.recognition;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author 杨尚川
 */
public class RecognitionToolTest {
    private static final List<String> LIST = new ArrayList<>();
    private static final List<String> QUANTIFIER = new ArrayList<>();
    @BeforeClass
    public static void initData() throws IOException{
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/chinese-number-test.txt"), Charset.forName("utf-8"));
        for(String line : lines){
            LIST.add(line);
        }
        List<String> lines2 = Files.readAllLines(Paths.get("src/test/resources/quantifier-test.txt"), Charset.forName("utf-8"));
        for(String line : lines2){
            QUANTIFIER.add(line);
        }
    }
    @Test
    public void testIsEnglish() {
        List<String> text = new ArrayList<>();
        text.add("APDPlat");
        text.add("2word");
        text.add("word2");
        text.add("2word3");
        text.add("word");
        text.add("love");        
        String singleStr = "a b c d e f g h i j k l m n o p q r s t u v w x y z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
        String[] single = singleStr.split(" ");
        assertEquals(52, single.length);
        for(String s : single){
            text.add(s);
        }        
        List<Boolean> expect = new ArrayList<>();
        expect.add(true);
        expect.add(false);
        expect.add(false);
        expect.add(false);
        expect.add(true);
        expect.add(true);
        for(int i=0; i<single.length; i++){
            expect.add(true);
        }        
        for(int i=0; i<text.size(); i++){
            String str = text.get(i);
            boolean result = RecognitionTool.isEnglish(str, 0, str.length());
            assertEquals(str, expect.get(i), result);
        }
    }
    @Test
    public void testIsNumber() {
        List<String> text = new ArrayList<>();
        text.add("250");
        text.add("n250h");
        text.add("2h");
        text.add("23h");
        text.add("88996661");
        text.add("1997");        
        String singleStr = "0 1 2 3 4 5 6 7 8 9 ０ １ ２ ３ ４ ５ ６ ７ ８ ９";
        String[] single = singleStr.split("\\s+");
        assertEquals(20, single.length);
        for(String s : single){
            text.add(s);
        }        
        List<Boolean> expect = new ArrayList<>();
        expect.add(true);
        expect.add(false);
        expect.add(false);
        expect.add(false);
        expect.add(true);
        expect.add(true);
        for(int i=0; i<single.length; i++){
            expect.add(true);
        }        
        for(int i=0; i<text.size(); i++){
            String str = text.get(i);
            boolean result = RecognitionTool.isNumber(str, 0, str.length());
            assertEquals(str, expect.get(i), result);
        }
    }
    @Test
    public void testIsChineseNumber() {
        List<String> text = new ArrayList<>();
        text.add("二百五");
        text.add("你二百五呀");
        text.add("三两");
        text.add("5三");
        text.add("一千零一");
        text.add("一九九七");        
        String singleStr = "一 二 三 四 五 六 七 八 九 十 百 千 万 亿 零 壹 贰 叁 肆 伍 陆 柒 捌 玖 拾 佰 仟 〇";
        String[] single = singleStr.split(" ");
        assertEquals(28, single.length);
        for(String s : single){
            text.add(s);
        }     
        for(String item : LIST){
            text.add(item);
        }
        List<Boolean> expect = new ArrayList<>();
        expect.add(true);
        expect.add(false);
        expect.add(false);
        expect.add(false);
        expect.add(true);
        expect.add(true);
        for(int i=0; i<single.length; i++){
            expect.add(true);
        }        
        for(int i=0; i<LIST.size(); i++){
            expect.add(true);
        }
        for(int i=0; i<text.size(); i++){
            String str = text.get(i);
            boolean result = RecognitionTool.isChineseNumber(str, 0, str.length());
            assertEquals(str, expect.get(i), result);
        }
    }    
    @Test
    public void testIsQuantifier() {
        for(String str : QUANTIFIER){
            boolean result = RecognitionTool.isQuantifier(str, 0, str.length());
            assertEquals(str, true, result);
        }
    }
}
