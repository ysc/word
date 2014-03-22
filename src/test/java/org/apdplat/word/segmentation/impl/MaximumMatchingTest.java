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

package org.apdplat.word.segmentation.impl;

import java.util.ArrayList;
import java.util.List;
import org.apdplat.word.segmentation.Segmentation;
import org.apdplat.word.segmentation.Word;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author 杨尚川
 */
public class MaximumMatchingTest {
    @Test
    public void testSeg() {
        Segmentation segmentation = new MaximumMatching();
        List<String> text = new ArrayList<>();
        text.add("长春市长春节致辞");
        text.add("杨");
        text.add("杨尚川好");
        
        List<String> expResult = new ArrayList<>();
        expResult.add("[长春市, 长春, 节, 致辞]");
        expResult.add("[杨]");
        expResult.add("[杨尚川, 好]");
        
        for(int i=0; i<text.size(); i++){
            List<Word> result = segmentation.seg(text.get(i));
            assertEquals(expResult.get(i).toString(), result.toString());
        }
    }
}
