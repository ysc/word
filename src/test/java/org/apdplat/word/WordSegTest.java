/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.apdplat.word;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 杨尚川
 */
public class WordSegTest {
    @Test
    public void testSeg() {
        String text = "长春市长春节致辞";
        String expResult = "[长春市, 长春, 节, 致辞]";
        List<String> result = WordSeg.seg(text);
        assertEquals(expResult, result.toString());
    }
    @Test
    public void testSegReverse() {
        String text = "长春市长春节致辞";
        String expResult = "[长春, 市长, 春节, 致辞]";
        List<String> result = WordSeg.segReverse(text);
        assertEquals(expResult, result.toString());
    }    
}
