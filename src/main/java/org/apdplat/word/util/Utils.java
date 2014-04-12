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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 工具类
 * @author 杨尚川
 */
public class Utils {
    //至少出现两次中文字符，且以中文字符开头和结束
    private static final Pattern PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5]{2,}$");
    /**
     * 至少出现两次中文字符，且以中文字符开头和结束
     * @param word
     * @return 
     */
    public static boolean isChineseCharAndLengthAtLeastTwo(String word){
        if(PATTERN.matcher(word).find()){
            return true;
        }
        return false;
    }
    /**
     * 根据MAP的VALUE进行排序
     * @param <K> key
     * @param <V> value
     * @param map map
     * @return 根据MAP的VALUE由大到小的排序结果列表
     */
    public static <K, V extends Number> List<Map.Entry<K, V>> getSortedMapByValue(Map<K, V> map) {        
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());  
        Collections.sort(list, new Comparator<Map.Entry<K,V>>() {    
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {    
                if(o1.getValue() instanceof Integer){
                    return o2.getValue().intValue() - o1.getValue().intValue();
                }
                if(o1.getValue() instanceof Long){
                    return (int)(o2.getValue().longValue() - o1.getValue().longValue());
                }
                if(o1.getValue() instanceof Float){
                    float f1 = o1.getValue().floatValue();
                    float f2 = o2.getValue().floatValue();
                    if(f1 < f2){
                        return 1;
                    }
                    if(f1 == f2){
                        return 0;
                    }
                    return -1;
                }
                if(o1.getValue() instanceof Double){
                    double f1 = o1.getValue().doubleValue();
                    double f2 = o2.getValue().doubleValue();
                    if(f1 < f2){
                        return 1;
                    }
                    if(f1 == f2){
                        return 0;
                    }
                    return -1;
                }
                return 0;
            }    
        });     
        return list;  
    }
}
