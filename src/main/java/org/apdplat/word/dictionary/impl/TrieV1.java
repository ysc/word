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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.apdplat.word.dictionary.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 前缀树的Java实现
 * 用于查找一个指定的字符串是否在字典中
 * @author 杨尚川
 */
public class TrieV1  implements Dictionary{
    private static final Logger LOGGER = LoggerFactory.getLogger(TrieV1.class);
    private final TrieNode ROOT_NODE = new TrieNode('/');
    private int maxLength;

    public List<String> prefix(String prefix){
        List<String> result = new ArrayList<>();
        //去掉首尾空白字符
        prefix=prefix.trim();
        int len = prefix.length();    
        if(len < 1){
            return result;
        }          
        //从根节点开始查找
        TrieNode node = ROOT_NODE;
        for(int i=0;i<len;i++){
            char character = prefix.charAt(i);
            TrieNode child = node.getChild(character);
            if(child == null){
                //未找到匹配节点
                return result;
            }else{
                //找到节点，继续往下找
                node = child;
            }
        }
        for(TrieNode item : node.getChildren()){            
            result.add(prefix+item.getCharacter());
        }
        return result;
    }
    
    @Override
    public boolean contains(String item){
        return contains(item, 0, item.length());
    }
    @Override
    public boolean contains(String item, int start, int length){
        if(start < 0 || length < 1){
            return false;
        }
        if(item == null || item.length() < length){
            return false;
        }
        //从根节点开始查找
        TrieNode node = ROOT_NODE;
        for(int i=0;i<length;i++){
            char character = item.charAt(i+start);
            TrieNode child = node.getChild(character);
            if(child == null){
                //未找到匹配节点
                return false;
            }else{
                //找到节点，继续往下找
                node = child;
            }
        }
        if(node.isTerminal()){
            return true;
        }
        return false;
    }
    @Override
    public void addAll(List<String> items){
        for(String item : items){
            add(item);
        }
    }
    @Override
    public void add(String item){
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
        //从根节点开始添加
        TrieNode node = ROOT_NODE;
        for(int i=0;i<len;i++){
            char character = item.charAt(i);
            TrieNode child = node.getChildIfNotExistThenCreate(character);
            //改变顶级节点
            node = child;
        }
        //设置终结字符，表示从根节点遍历到此是一个合法的词
        node.setTerminal(true);
    }
    
    @Override
    public int getMaxLength() {
        return maxLength;
    }
    private static class TrieNode{
        private char character;
        private boolean terminal;
        private final Map<Character,TrieNode> children = new HashMap<>();        
        public TrieNode(char character){
            this.character = character;
        }
        public boolean isTerminal() {
            return terminal;
        }
        public void setTerminal(boolean terminal) {
            this.terminal = terminal;
        }        
        public char getCharacter() {
            return character;
        }
        public void setCharacter(char character) {
            this.character = character;
        }
        public Collection<TrieNode> getChildren() {
            return this.children.values();
        }
        public TrieNode getChild(char character) {
            return this.children.get(character);
        }        
        public TrieNode getChildIfNotExistThenCreate(char character) {
            TrieNode child = getChild(character);
            if(child == null){
                child = new TrieNode(character);
                addChild(child);
            }
            return child;
        }
        public void addChild(TrieNode child) {
            this.children.put(child.getCharacter(), child);
        }
        public void removeChild(TrieNode child) {
            this.children.remove(child.getCharacter());
        }        
    }
    
    public void show(){
        show(ROOT_NODE,"");
    }
    private void show(TrieNode node, String indent){
        if(node.isTerminal()){
            LOGGER.info(indent+node.getCharacter()+"(T)");
        }else{
            LOGGER.info(indent+node.getCharacter());
        }        
        for(TrieNode item : node.getChildren()){
            show(item,indent+"\t");
        }
    }
    public static void main(String[] args){
        TrieV1 trie = new TrieV1();
        trie.add("APDPlat");
        trie.add("APP");
        trie.add("APD");
        trie.add("杨尚川");
        trie.add("杨尚昆");
        trie.add("杨尚喜");
        trie.add("中华人民共和国");
        trie.add("中华人民打太极");
        trie.add("中华");
        trie.add("中心思想");
        trie.add("杨家将");        
        trie.show();
        LOGGER.info(trie.prefix("中").toString());
        LOGGER.info(trie.prefix("中华").toString());
        LOGGER.info(trie.prefix("杨").toString());
        LOGGER.info(trie.prefix("杨尚").toString());
    }
}
