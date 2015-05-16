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

package org.apdplat.word.util;

import java.io.File;
import java.util.regex.Pattern;

/**
 * 工具类
 * @author 杨尚川
 */
public class Utils {
    //至少出现一次中文字符，且以中文字符开头和结束
    private static final Pattern PATTERN_ONE = Pattern.compile("^[\\u4e00-\\u9fa5]+$");
    //至少出现两次中文字符，且以中文字符开头和结束
    private static final Pattern PATTERN_TWO = Pattern.compile("^[\\u4e00-\\u9fa5]{2,}$");
    /**
     * 至少出现一次中文字符，且以中文字符开头和结束
     * @param word
     * @return 
     */
    public static boolean isChineseCharAndLengthAtLeastOne(String word){
        if(PATTERN_ONE.matcher(word).find()){
            return true;
        }
        return false;
    }
    /**
     * 至少出现两次中文字符，且以中文字符开头和结束
     * @param word
     * @return 
     */
    public static boolean isChineseCharAndLengthAtLeastTwo(String word){
        if(PATTERN_TWO.matcher(word).find()){
            return true;
        }
        return false;
    }
    /**
     * 删除目录
     * @param dir 目录
     * @return 是否成功
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (File child : children) {
                boolean success = deleteDir(child);
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
