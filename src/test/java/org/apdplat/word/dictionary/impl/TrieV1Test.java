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

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 杨尚川
 */
public class TrieV1Test {
    private TrieV1 trie = null;
    @Before
    public void setUp() {
        trie = new TrieV1();
        trie.add("APDPlat");
        trie.add("APP");
        trie.add("APD");
        trie.add("杨尚川");
        trie.add("杨尚昆");
        trie.add("杨尚喜");
        trie.add("中华人民共和国");
        trie.add("中华");
        trie.add("中心思想");
        trie.add("杨家将");
    }
    
    @Test
    public void testPrefix() {
        String prefix = "中";
        List<String> result = trie.prefix(prefix);
        assertTrue(result.contains("中心"));
        assertTrue(result.contains("中华"));
        
        prefix = "中华";
        result = trie.prefix(prefix);
        assertTrue(result.contains("中华人"));
        
        prefix = "杨";
        result = trie.prefix(prefix);
        assertTrue(result.contains("杨家"));
        assertTrue(result.contains("杨尚"));
        
        prefix = "杨尚";
        result = trie.prefix(prefix);
        assertTrue(result.contains("杨尚昆"));
        assertTrue(result.contains("杨尚喜"));
        assertTrue(result.contains("杨尚川"));
    }
    
    @Test
    public void testContains() {
        String item = "杨家将";
        boolean expResult = true;
        boolean result = trie.contains(item);
        assertEquals(expResult, result);
        
        item = "杨尚川";
        expResult = true;
        result = trie.contains(item);
        assertEquals(expResult, result);
        
        item = "中华人民共和国";
        expResult = true;
        result = trie.contains(item);
        assertEquals(expResult, result);
        
        item = "APDPlat";
        expResult = true;
        result = trie.contains(item);
        assertEquals(expResult, result);
        
        item = "APP";
        expResult = true;
        result = trie.contains(item);
        assertEquals(expResult, result);
        
        item = "APD";
        expResult = true;
        result = trie.contains(item);
        assertEquals(expResult, result);
        
        item = "杨尚";
        expResult = false;
        result = trie.contains(item);
        assertEquals(expResult, result);
        
        item = "杨";
        expResult = false;
        result = trie.contains(item);
        assertEquals(expResult, result);
        
        item = "APDP";
        expResult = false;
        result = trie.contains(item);
        assertEquals(expResult, result);
        
        item = "A";
        expResult = false;
        result = trie.contains(item);
        assertEquals(expResult, result);
        
        item = "中华人民";
        expResult = false;
        result = trie.contains(item);
        assertEquals(expResult, result);
    }    
}
