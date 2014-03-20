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

package org.apdplat.word.dictionary;

import org.apdplat.word.dictionary.impl.TrieV3;

/**
 * 词典工厂
 * @author 杨尚川
 */
public final class DictionaryFactory {
    private DictionaryFactory(){}
    public static final Dictionary getDictionary(){
        return DictionaryHolder.DIC;
    }
    private static final class DictionaryHolder{
        private static final Dictionary DIC = new TrieV3();
    }
}
