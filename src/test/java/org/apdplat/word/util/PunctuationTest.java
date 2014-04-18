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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 杨尚川
 */
public class PunctuationTest {
    private static final List<Character> LIST = new ArrayList<>();
    @BeforeClass
    public static void initData() throws IOException{
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/punctuation.txt"), Charset.forName("utf-8"));
        for(String line : lines){
            LIST.add(line.trim().charAt(0));
        }
    }
    @Test
    public void testIs() {
        for(char item : LIST){
            boolean result = Punctuation.is(item);
            assertEquals(true, result);
        }
        assertEquals(false, Punctuation.is('y'));
        assertEquals(false, Punctuation.is('s'));
        assertEquals(false, Punctuation.is('1'));
    }
}