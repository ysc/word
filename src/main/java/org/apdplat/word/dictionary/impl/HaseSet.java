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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apdplat.word.dictionary.Dictionary;

/**
 *
 * @author 杨尚川
 */
public class HaseSet implements Dictionary{
    private Set<String> set = new HashSet<>();
    private int maxLength;
    @Override
    public int getMaxLength() {
        return maxLength;
    }
    @Override
    public boolean contains(String item, int start, int length) {
        return set.contains(item.substring(start, start+length));
    }
    @Override
    public boolean contains(String item) {
        return set.contains(item);
    }
    @Override
    public void addAll(List<String> items) {
        for(String item : items){
            add(item);
        }
    }
    @Override
    public void add(String item) {
        //去掉首尾空白字符
        item=item.trim();
        int len = item.length();
        if(len < 1){
            //长度小于1则忽略
            return;
        }
        if(len>maxLength){
            maxLength=len;
        }
        set.add(item);
    }
}