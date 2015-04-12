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

import java.util.Objects;

/**
 * 词、词性、词频
 * Word
 * @author 杨尚川
 */
public class Word implements Comparable{
    private String text;
    private PartOfSpeech partOfSpeech = PartOfSpeech.UNKNOWN;
    private int frequency;

    public Word(String text){
        this.text = text;
    }

    public Word(String text, PartOfSpeech partOfSpeech, int frequency) {
        this.text = text;
        this.partOfSpeech = partOfSpeech;
        this.frequency = frequency;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.text);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Word other = (Word) obj;
        return Objects.equals(this.text, other.text);
    }

    @Override
    public String toString(){
        return text+" "+partOfSpeech+" "+frequency;
    }

    @Override
    public int compareTo(Object o) {
        if(this == o){
            return 0;
        }
        if(this.text == null){
            return -1;
        }
        if(o == null){
            return 1;
        }
        if(!(o instanceof Word)){
            return 1;
        }
        String t = ((Word)o).getText();
        if(t == null){
            return 1;
        }
        return this.text.compareTo(t);
    }
}
