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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apdplat.word.dictionary.Dictionary;

/**
 * 前缀树的Java实现
 * 用于查找一个指定的字符串是否在字典中
 * @author 杨尚川
 */
public class TrieV2  implements Dictionary{
    private final TrieNode ROOT_NODE = new TrieNode('/');

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
        //去掉首尾空白字符
        item=item.trim();
        int len = item.length();
        if(len < 1){
            return false;
        }
        //从根节点开始查找
        TrieNode node = ROOT_NODE;
        for(int i=0;i<len;i++){
            char character = item.charAt(i);
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
    private static class TrieNode{
        private char character;
        private boolean terminal;
        private TrieNode[] children = new TrieNode[0];
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
            return Arrays.asList(children);            
        }
        public TrieNode getChild(char character) {
            for(TrieNode child : children){
                if(child.getCharacter() == character){
                    return child;
                }
            }
            return null;
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
            children = Arrays.copyOf(children, children.length+1);
            this.children[children.length-1]=child;
        }
    }
    
    public void show(){
        show(ROOT_NODE,"");
    }
    private void show(TrieNode node, String indent){
        if(node.isTerminal()){
            System.out.println(indent+node.getCharacter()+"(T)");
        }else{
            System.out.println(indent+node.getCharacter());
        }        
        for(TrieNode item : node.getChildren()){
            show(item,indent+"\t");
        }
    }
    public static void main(String[] args){
        TrieV2 trie = new TrieV2();
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
        System.out.println(trie.prefix("中"));
        System.out.println(trie.prefix("中华"));
        System.out.println(trie.prefix("杨"));
        System.out.println(trie.prefix("杨尚"));
    }
}
