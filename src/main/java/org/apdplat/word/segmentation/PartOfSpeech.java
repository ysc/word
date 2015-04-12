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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 词性
 * 词性不是固定不变的，所以不能选枚举
 * @author 杨尚川
 */
public class PartOfSpeech {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartOfSpeech.class);
    private String pos;
    private String des;
    public PartOfSpeech(String pos, String des){
        this.pos = pos.toLowerCase();
        this.des = des;
    }
    private static class PartOfSpeechMap{
        private static final Map<String, PartOfSpeech> BUILDIN_POS = getBuildInPos();
        private static Map<String, PartOfSpeech> getBuildInPos(){
            Map<String, PartOfSpeech> bip = new HashMap<>();
            try {
                for (Field field : PartOfSpeech.class.getFields()) {
                    PartOfSpeech partOfSpeech = (PartOfSpeech)field.get(PartOfSpeech.class);
                    bip.put(partOfSpeech.getPos(), partOfSpeech);
                }
            }catch (Exception e){
                LOGGER.error("词性初始化失败", e);
            }
            return bip;
        }
    }
    public static PartOfSpeech valueOf(String pos){
        PartOfSpeech partOfSpeech = PartOfSpeechMap.BUILDIN_POS.get(pos.toLowerCase());
        if(partOfSpeech==null){
            partOfSpeech = UNKNOWN;
        }
        return partOfSpeech;
    }
    public static boolean isBuildIn(String pos){
        return PartOfSpeechMap.BUILDIN_POS.get(pos.toLowerCase()) != null;
    }
    //1. 名词
    public static final PartOfSpeech N = new PartOfSpeech("n", "名词");
    public static final PartOfSpeech NR = new PartOfSpeech("nr", "人名");
    public static final PartOfSpeech NS = new PartOfSpeech("ns", "地名");
    public static final PartOfSpeech NT = new PartOfSpeech("nt", "团体机构名");
    public static final PartOfSpeech NZ = new PartOfSpeech("nz", "其它专名");
    //2. 动词
    public static final PartOfSpeech V = new PartOfSpeech("v", "动词");
    public static final PartOfSpeech VD = new PartOfSpeech("vd", "副动词");
    public static final PartOfSpeech VN = new PartOfSpeech("vn", "名动词");
    public static final PartOfSpeech VI = new PartOfSpeech("vi", "不及物动词");
    //3. 形容词
    public static final PartOfSpeech A = new PartOfSpeech("a", "形容词");
    public static final PartOfSpeech AD = new PartOfSpeech("ad", "副形容词");
    public static final PartOfSpeech AN = new PartOfSpeech("an", "名形容词");
    //4. 数词
    public static final PartOfSpeech M = new PartOfSpeech("m", "数词");
    public static final PartOfSpeech MQ = new PartOfSpeech("mq", "数量词");
    //5. 量词
    public static final PartOfSpeech Q = new PartOfSpeech("q", "量词");
    //6. 代词
    public static final PartOfSpeech R = new PartOfSpeech("r", "代词");
    public static final PartOfSpeech RR = new PartOfSpeech("rr", "人称代词");
    public static final PartOfSpeech RZ = new PartOfSpeech("rz", "指示代词");
    //7. 副词
    public static final PartOfSpeech D = new PartOfSpeech("d", "副词");
    //8. 介词
    public static final PartOfSpeech P = new PartOfSpeech("p", "介词");
    //9. 连词
    public static final PartOfSpeech C = new PartOfSpeech("c", "连词");
    //10. 助词
    public static final PartOfSpeech U = new PartOfSpeech("u", "助词");
    //11. 拟声词
    public static final PartOfSpeech O = new PartOfSpeech("o", "拟声词");
    //12. 叹词
    public static final PartOfSpeech E = new PartOfSpeech("e", "叹词");
    //13. 时间词
    public static final PartOfSpeech T = new PartOfSpeech("t", "时间词");
    //14. 处所词
    public static final PartOfSpeech S = new PartOfSpeech("s", "处所词");
    //15. 方位词
    public static final PartOfSpeech F = new PartOfSpeech("f", "方位词");
    //16. 区别词
    public static final PartOfSpeech B = new PartOfSpeech("b", "区别词");
    //17. 语气词
    public static final PartOfSpeech Y = new PartOfSpeech("y", "语气词");
    //18. 状态词
    public static final PartOfSpeech Z = new PartOfSpeech("z", "状态词");
    //词组
    public static final PartOfSpeech L = new PartOfSpeech("l", "词组");
    //未知词性
    public static final PartOfSpeech UNKNOWN = new PartOfSpeech("unknown", "未知");

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public static void main(String[] args) {
        System.out.println(PartOfSpeech.isBuildIn("n"));
        System.out.println(PartOfSpeech.isBuildIn("ns"));
        System.out.println(PartOfSpeech.isBuildIn("nn"));
        System.out.println(PartOfSpeech.N.getPos()+" "+PartOfSpeech.N.getDes());
        System.out.println(PartOfSpeech.M.getPos() + " " + PartOfSpeech.M.getDes());
        PartOfSpeech N_ANIMAL = new PartOfSpeech("n_animal", "动物");
        System.out.println(N_ANIMAL.getPos() + " " + N_ANIMAL.getDes());
    }
}
