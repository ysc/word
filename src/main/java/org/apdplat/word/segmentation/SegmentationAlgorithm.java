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

package org.apdplat.word.segmentation;

/**
 * 中文分词算法
 * Chinese word segmentation algorithm
 * @author 杨尚川
 */
public enum SegmentationAlgorithm {
    /**
     * 正向最大匹配算法
     */
    MaximumMatching,
    /**
     * 逆向最大匹配算法
     */
    ReverseMaximumMatching,
    /**
     * 正向最小匹配算法
     */
    MinimumMatching,
    /**
     * 逆向最小匹配算法
     */
    ReverseMinimumMatching,
    /**
     * 双向最大匹配算法
     */
    BidirectionalMaximumMatching,
    /**
     * 双向最小匹配算法
     */
    BidirectionalMinimumMatching,
    /**
     * 双向最大最小匹配算法
     */
    BidirectionalMaximumMinimumMatching
}
