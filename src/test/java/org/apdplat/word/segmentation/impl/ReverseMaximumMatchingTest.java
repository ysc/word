/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apdplat.word.segmentation.impl;

import java.util.List;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.Word;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author 杨尚川
 */
public class ReverseMaximumMatchingTest {    
    private Segmentation segmentation = null;
    @Before
    public void setUp() {
        segmentation = new ReverseMaximumMatching();
    }    
    @Test
    public void testSegReverse() {
        String text = "长春市长春节致辞";
        String expResult = "[长春, 市长, 春节, 致辞]";
        List<Word> result = segmentation.seg(text);
        assertEquals(expResult, result.toString());
    }
    @Test
    public void testSegReverse2() {
        String text = "好";
        String expResult = "[好]";
        List<Word> result = segmentation.seg(text);
        assertEquals(expResult, result.toString());
    }
    @Test
    public void testSegReverse3() {
        String text = "杨尚川好";
        String expResult = "[杨尚川, 好]";
        List<Word> result = segmentation.seg(text);
        assertEquals(expResult, result.toString());
    }
}
