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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author 杨尚川
 */
public class GenericTrieTest {
    private final GenericTrie<Integer> genericTrie = new GenericTrie<>();
    @Before
    public void setUp() {
        genericTrie.put("杨尚川", 100);
        genericTrie.put("杨尚喜", 99);
        genericTrie.put("杨尚丽", 98);
        genericTrie.put("中华人民共和国", 1);
    }
    @After
    public void tearDown() {
        genericTrie.clear();
    }
    @Test
    public void testClear() {
        assertEquals(100, genericTrie.get("杨尚川").intValue());
        assertEquals(1, genericTrie.get("中华人民共和国").intValue());
        genericTrie.clear();
        assertEquals(null, genericTrie.get("杨尚川"));
        assertEquals(null, genericTrie.get("中华人民共和国"));
    }
    @Test
    public void testGet() {
        assertEquals(100, genericTrie.get("杨尚川").intValue());
        assertEquals(99, genericTrie.get("杨尚喜").intValue());
        assertEquals(98, genericTrie.get("杨尚丽").intValue());
        assertEquals(1, genericTrie.get("中华人民共和国").intValue());
        assertEquals(null, genericTrie.get("杨"));
        assertEquals(null, genericTrie.get("杨尚"));
    }
    @Test
    public void testBigram(){
        try {
            GenericTrie<Integer> genericTrie = new GenericTrie<>();
            Map<String, Integer> map = new HashMap<>();
            List<String> lines = Files.readAllLines(Paths.get("src/test/resources/bigram.txt"));
            lines.forEach(line -> {
                String[] attrs = line.split("\\s+");
                if(attrs!=null && attrs.length==2){
                    map.put(attrs[0], Integer.parseInt(attrs[1]));
                    genericTrie.put(attrs[0], map.get(attrs[0]));
                }
            });
            map.keySet().forEach(key->assertEquals(map.get(key).intValue(), genericTrie.get(key).intValue()));
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
    @Test
    public void testTrigram(){
        try {
            GenericTrie<Integer> genericTrie = new GenericTrie<>();
            Map<String, Integer> map = new HashMap<>();
            List<String> lines = Files.readAllLines(Paths.get("src/test/resources/trigram.txt"));
            lines.forEach(line -> {
                String[] attrs = line.split("\\s+");
                if(attrs!=null && attrs.length==2){
                    map.put(attrs[0], Integer.parseInt(attrs[1]));
                    genericTrie.put(attrs[0], map.get(attrs[0]));
                }
            });
            map.keySet().forEach(key->assertEquals(map.get(key).intValue(), genericTrie.get(key).intValue()));
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
}