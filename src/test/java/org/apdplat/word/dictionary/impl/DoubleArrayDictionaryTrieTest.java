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

package org.apdplat.word.dictionary.impl;

import junit.framework.TestCase;
import org.apdplat.word.dictionary.Dictionary;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 双数组前缀树单元测试
 *
 * @author 杨尚川
 */
public class DoubleArrayDictionaryTrieTest extends TestCase {
    @Test
    public void testAdd() {
        Dictionary dictionary = new DoubleArrayDictionaryTrie();
        try{
            dictionary.add("APDPlat");
            fail();
        }catch (Exception e){
            assertEquals("not yet support, please use addAll method!", e.getMessage());
        }
    }
    @Test
    public void testRemove() {
        Dictionary dictionary = new DoubleArrayDictionaryTrie();
        try{
            dictionary.remove("APDPlat");
            fail();
        }catch (Exception e){
            assertEquals("not yet support menthod!", e.getMessage());
        }
    }
    @Test
    public void testRemoveAll() {
        Dictionary dictionary = new DoubleArrayDictionaryTrie();
        try{
            dictionary.removeAll(Arrays.asList("APDPlat", "杨尚川"));
            fail();
        }catch (Exception e){
            assertEquals("not yet support menthod!", e.getMessage());
        }
    }
    @Test
    public void testAddAll() {
        Dictionary dictionary = new DoubleArrayDictionaryTrie();

        List<String> words = Arrays.asList("杨尚川", "章子怡", "刘亦菲", "刘", "刘诗诗", "巩俐", "中国", "主演");
        //构造词典
        dictionary.addAll(words);

        assertEquals(3, dictionary.getMaxLength());
        assertEquals(true, dictionary.contains("杨尚川"));
        assertEquals(true, dictionary.contains("章子怡"));
        assertEquals(true, dictionary.contains("刘"));
        assertEquals(true, dictionary.contains("刘亦菲"));
        assertEquals(true, dictionary.contains("刘诗诗"));
        assertEquals(true, dictionary.contains("巩俐"));
        assertEquals(true, dictionary.contains("中国的巩俐是红高粱的主演", 3, 2));
        assertEquals(true, dictionary.contains("中国的巩俐是红高粱的主演", 0, 2));
        assertEquals(true, dictionary.contains("中国的巩俐是红高粱的主演", 10, 2));
        assertEquals(false, dictionary.contains("复仇者联盟2"));
        assertEquals(false, dictionary.contains("白掌"));
        assertEquals(false, dictionary.contains("红掌"));

        try{
            dictionary.addAll(Arrays.asList("天空", "热爱", "白天"));
            fail();
        }catch (Exception e){
            assertEquals("addAll method can just be used once after clear method!", e.getMessage());
        }
        dictionary.clear();
        dictionary.addAll(Arrays.asList("天空", "热爱", "白天"));

        assertEquals(2, dictionary.getMaxLength());
        assertEquals(false, dictionary.contains("杨尚川"));
        assertEquals(false, dictionary.contains("章子怡"));
        assertEquals(false, dictionary.contains("刘"));
        assertEquals(false, dictionary.contains("刘亦菲"));
        assertEquals(false, dictionary.contains("刘诗诗"));
        assertEquals(false, dictionary.contains("巩俐"));
        assertEquals(false, dictionary.contains("中国的巩俐是红高粱的主演", 3, 2));
        assertEquals(false, dictionary.contains("中国的巩俐是红高粱的主演", 0, 2));
        assertEquals(false, dictionary.contains("中国的巩俐是红高粱的主演", 10, 2));
        assertEquals(false, dictionary.contains("复仇者联盟2"));
        assertEquals(false, dictionary.contains("白掌"));
        assertEquals(false, dictionary.contains("红掌"));
        assertEquals(true, dictionary.contains("天空"));
        assertEquals(true, dictionary.contains("热爱"));
        assertEquals(true, dictionary.contains("白天"));
    }

    @Test
    public void testWhole() {
        Dictionary dictionary = new DoubleArrayDictionaryTrie();

        List<String> words = Arrays.asList("杨尚川", "章子怡", "刘亦菲", "刘", "刘诗诗", "巩俐", "中国", "主演");
        //构造词典
        dictionary.addAll(words);

        assertEquals(3, dictionary.getMaxLength());
        assertEquals(false, dictionary.contains("杨"));
        assertEquals(false, dictionary.contains("杨尚"));
        assertEquals(true, dictionary.contains("杨尚川"));
        assertEquals(true, dictionary.contains("章子怡"));
        assertEquals(true, dictionary.contains("刘"));
        assertEquals(true, dictionary.contains("刘亦菲"));
        assertEquals(true, dictionary.contains("刘诗诗"));
        assertEquals(true, dictionary.contains("巩俐"));
        assertEquals(true, dictionary.contains("中国的巩俐是红高粱的主演", 3, 2));
        assertEquals(true, dictionary.contains("中国的巩俐是红高粱的主演", 0, 2));
        assertEquals(true, dictionary.contains("中国的巩俐是红高粱的主演", 10, 2));
        assertEquals(false, dictionary.contains("复仇者联盟2"));
        assertEquals(false, dictionary.contains("白掌"));
        assertEquals(false, dictionary.contains("红掌"));

        dictionary.clear();

        assertEquals(0, dictionary.getMaxLength());
        assertEquals(false, dictionary.contains("杨尚川"));
        assertEquals(false, dictionary.contains("章子怡"));
        assertEquals(false, dictionary.contains("刘"));
        assertEquals(false, dictionary.contains("刘亦菲"));
        assertEquals(false, dictionary.contains("刘诗诗"));
        assertEquals(false, dictionary.contains("巩俐"));
        assertEquals(false, dictionary.contains("中国的巩俐是红高粱的主演", 3, 2));
        assertEquals(false, dictionary.contains("中国的巩俐是红高粱的主演", 0, 2));
        assertEquals(false, dictionary.contains("中国的巩俐是红高粱的主演", 10, 2));
        assertEquals(false, dictionary.contains("复仇者联盟2"));
        assertEquals(false, dictionary.contains("白掌"));
        assertEquals(false, dictionary.contains("红掌"));

        List<String> data = new ArrayList<>();
        data.add("白掌");
        data.add("红掌");
        data.add("复仇者联盟2");
        data.addAll(words);
        dictionary.addAll(data);

        assertEquals(6, dictionary.getMaxLength());
        assertEquals(true, dictionary.contains("杨尚川"));
        assertEquals(true, dictionary.contains("章子怡"));
        assertEquals(true, dictionary.contains("刘"));
        assertEquals(true, dictionary.contains("刘亦菲"));
        assertEquals(true, dictionary.contains("刘诗诗"));
        assertEquals(true, dictionary.contains("巩俐"));
        assertEquals(true, dictionary.contains("中国的巩俐是红高粱的主演", 3, 2));
        assertEquals(true, dictionary.contains("中国的巩俐是红高粱的主演", 0, 2));
        assertEquals(true, dictionary.contains("中国的巩俐是红高粱的主演", 10, 2));
        assertEquals(true, dictionary.contains("复仇者联盟2"));
        assertEquals(true, dictionary.contains("白掌"));
        assertEquals(true, dictionary.contains("红掌"));
        assertEquals(false, dictionary.contains("金钱树"));
    }
    @Test
    public void testWhole2(){
        try {
            AtomicInteger h = new AtomicInteger();
            AtomicInteger e = new AtomicInteger();
            List<String> words = Files.readAllLines(Paths.get("src/test/resources/dic.txt"));
            Dictionary dictionary = new DoubleArrayDictionaryTrie();
            dictionary.addAll(words);
            words.forEach(word -> {
                for (int j = 0; j < word.length(); j++) {
                    String sw = word.substring(0, j + 1);
                    for (int k = 0; k < sw.length(); k++) {
                        if (dictionary.contains(sw, k, sw.length() - k)) {
                            h.incrementAndGet();
                        } else {
                            e.incrementAndGet();
                        }
                    }
                }
            });
            assertEquals(3010699, e.get());
            assertEquals(1383728, h.get());
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }
}