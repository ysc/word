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
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author 杨尚川
 */
public class DoubleArrayGenericTrieTest {
    private final DoubleArrayGenericTrie doubleArrayGenericTrie = new DoubleArrayGenericTrie();
    @Before
    public void setUp() {
        Map<String, Integer> map = new HashMap<>();
        map.put("杨尚川", 100);
        map.put("杨尚喜", 99);
        map.put("杨尚丽", 98);
        map.put("中华人民共和国", 1);
        doubleArrayGenericTrie.putAll(map);
    }
    @After
    public void tearDown() {
        doubleArrayGenericTrie.clear();
    }
    @Test
    public void testClear() {
        assertEquals(100, doubleArrayGenericTrie.get("杨尚川"));
        assertEquals(1, doubleArrayGenericTrie.get("中华人民共和国"));
        doubleArrayGenericTrie.clear();
        assertEquals(Integer.MIN_VALUE, doubleArrayGenericTrie.get("杨尚川"));
        assertEquals(Integer.MIN_VALUE, doubleArrayGenericTrie.get("中华人民共和国"));
    }
    @Test
    public void testGet() {
        assertEquals(100, doubleArrayGenericTrie.get("杨尚川"));
        assertEquals(99, doubleArrayGenericTrie.get("杨尚喜"));
        assertEquals(98, doubleArrayGenericTrie.get("杨尚丽"));
        assertEquals(1, doubleArrayGenericTrie.get("中华人民共和国"));
        assertEquals(Integer.MIN_VALUE, doubleArrayGenericTrie.get("杨"));
        assertEquals(Integer.MIN_VALUE, doubleArrayGenericTrie.get("杨尚"));
    }
    @Test
    public void testBigram(){
        try(Stream<String> lines = Files.lines(Paths.get("src/main/resources/bigram.txt")).limit(1000)) {
            Map<String, Integer> map = new HashMap<>();
            lines.forEach(line -> {
                String[] attrs = line.split("\\s+");
                if(attrs!=null && attrs.length==2){
                    map.put(attrs[0], Integer.parseInt(attrs[1]));
                }
            });
            DoubleArrayGenericTrie doubleArrayGenericTrie = new DoubleArrayGenericTrie(WordConfTools.getInt("bigram.double.array.trie.size", 10000));
            doubleArrayGenericTrie.putAll(map);
            map.keySet().forEach(key->assertEquals(map.get(key).intValue(), doubleArrayGenericTrie.get(key)));
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
    @Test
    public void testTrigram(){
        try(Stream<String> lines = Files.lines(Paths.get("src/main/resources/trigram.txt")).limit(1000)) {
            Map<String, Integer> map = new HashMap<>();
            lines.forEach(line -> {
                String[] attrs = line.split("\\s+");
                if(attrs!=null && attrs.length==2){
                    map.put(attrs[0], Integer.parseInt(attrs[1]));
                }
            });
            DoubleArrayGenericTrie doubleArrayGenericTrie = new DoubleArrayGenericTrie(WordConfTools.getInt("trigram.double.array.trie.size", 10000));
            doubleArrayGenericTrie.putAll(map);
            map.keySet().forEach(key->assertEquals(map.get(key).intValue(), doubleArrayGenericTrie.get(key)));
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
}