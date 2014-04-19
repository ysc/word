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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 杨尚川
 */
public class GramTrieTest {
    private final GramTrie trie = new GramTrie();
    @Before
    public void setUp() {
        trie.put("杨尚川", 100);
        trie.put("杨尚喜", 99);
        trie.put("杨尚丽", 98);
        trie.put("中华人民共和国", 1);
    }
    @After
    public void tearDown() {
        trie.clear();
    }
    @Test
    public void testClear() {
        assertEquals(100, trie.get("杨尚川"), 0);
        assertEquals(1, trie.get("中华人民共和国"), 0);
        trie.clear();
        assertEquals(0, trie.get("杨尚川"), 0);
        assertEquals(0, trie.get("中华人民共和国"), 0);
    }
    @Test
    public void testGet() {
        assertEquals(100, trie.get("杨尚川"), 0);
        assertEquals(99, trie.get("杨尚喜"), 0);
        assertEquals(98, trie.get("杨尚丽"), 0);
        assertEquals(1, trie.get("中华人民共和国"), 0);
        assertEquals(0, trie.get("杨"), 0);
        assertEquals(0, trie.get("杨尚"), 0);
    }
}