/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class TrieTest {
    private Trie trie = null;
    @Before
    public void setUp() {
        trie = new Trie();
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
