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

package org.apdplat.word.analysis;

import junit.framework.TestCase;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 *
 * 测试 利用NGRAM做热词分析
 * @author 杨尚川
 */
public class HotWordTest extends TestCase {

    public void testGet() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/hot-word-test-text.txt"));
        Map<String, Integer> data = HotWord.get(lines.toString(), 2);
        assertEquals(342, data.get("冷血").intValue());
        assertEquals(128, data.get("无情").intValue());
        data = HotWord.get(lines.toString(), 3);
        assertEquals(498, data.get("楚离陌").intValue());
        assertEquals(168, data.get("安世耿").intValue());
        assertEquals(146, data.get("姬瑶花").intValue());
        assertEquals(92, data.get("神侯府").intValue());
        data = HotWord.get(lines.toString(), 4);
        assertEquals(89, data.get("紫罗公主").intValue());
        assertEquals(58, data.get("四大名捕").intValue());
        assertEquals(22, data.get("四大凶徒").intValue());
    }
}