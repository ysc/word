/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apdplat.word.segmentation.impl;

import java.util.List;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.Word;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 杨尚川
 */
public class MaximumMatchingTest {
    private Segmentation segmentation = null;
    @Before
    public void setUp() {
        segmentation = new MaximumMatching();
    }
    @Test
    public void testSeg1() {
        String text = "长春市长春节致辞";
        String expResult = "[长春市, 长春, 节, 致辞]";
        List<Word> result = segmentation.seg(text);
        assertEquals(expResult, result.toString());
    }
    @Test
    public void testSeg2() {
        String text = "杨";
        String expResult = "[杨]";
        List<Word> result = segmentation.seg(text);
        assertEquals(expResult, result.toString());
    }
    @Test
    public void testSeg3() {
        String text = "杨尚川好";
        String expResult = "[杨尚川, 好]";
        List<Word> result = segmentation.seg(text);
        assertEquals(expResult, result.toString());
    }
}
