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

package org.apdplat.word.recognition;

/**
 * 分词特殊情况识别工具
 * 如英文单词、数字、时间等
 * @author 杨尚川
 */
public class RecognitionTool {
    /**
     * 识别文本（英文单词、数字、时间等）
     * @param text 识别文本
     * @return 是否识别
     */
    public static boolean recog(final String text){
        return recog(text, 0, text.length());
    }
    /**
     * 识别文本（英文单词、数字、时间等）
     * @param text 识别文本
     * @param start 待识别文本开始索引
     * @param len 识别长度
     * @return 是否识别
     */
    public static boolean recog(final String text, final int start, final int len){
        return isEnglish(text, start, len) 
                || isNumber(text, start, len);
    }
    /**
     * 英文单词识别
     * @param text 识别文本
     * @param start 待识别文本开始索引
     * @param len 识别长度
     * @return 是否识别
     */
    private static boolean isEnglish(final String text, final int start, final int len){
        for(int i=start; i<start+len; i++){
            char c = text.charAt(i);
            if(c > 'z'){
                return false;
            }
            if(c < 'A'){
                return false;
            }
            if(c > 'Z' && c < 'a'){
                return false;
            }
        }
        //指定的字符串已经识别为英文串
        //下面要判断英文串是否完整
        if(start>0){
            //判断前一个字符，如果为英文字符则识别失败
            char c = text.charAt(start-1);
            if( (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ){
                return false;
            }
        }
        if(start+len < text.length()){
            //判断后一个字符，如果为英文字符则识别失败
            char c = text.charAt(start+len);
            if( (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ){
                return false;
            }
        }
        return true;
    }
    /**
     * 数字识别
     * @param text 识别文本
     * @param start 待识别文本开始索引
     * @param len 识别长度
     * @return 是否识别
     */
    private static boolean isNumber(final String text, final int start, final int len){
        for(int i=start; i<start+len; i++){
            char c = text.charAt(i);
            if(c > '9'){
                return false;
            }
            if(c < '0'){
                return false;
            }
        }
        //指定的字符串已经识别为数字串
        //下面要判断数字串是否完整
        if(start>0){
            //判断前一个字符，如果为数字字符则识别失败
            char c = text.charAt(start-1);
            if(c >= '0' && c <= '9'){
                return false;
            }
        }
        if(start+len < text.length()){
            //判断后一个字符，如果为数字字符则识别失败
            char c = text.charAt(start+len);
            if(c >= '0' && c <= '9'){
                return false;
            }
        }
        return true;
    }
}
