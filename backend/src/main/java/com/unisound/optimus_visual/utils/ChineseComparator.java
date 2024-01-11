package com.unisound.optimus_visual.utils;


import java.util.Comparator;

public class ChineseComparator<T> implements Comparator<T> {

    private static boolean isDigit(char ch) {
        return ch >= 48 && ch <= 57;
    }

    /**
     * Length of string is passed in for improved efficiency (only need to calculate it once)
     **/
    private static String getChunk(String s, int slength, int marker) {
        StringBuilder chunk = new StringBuilder();
        int index = marker;
        char c = s.charAt(index);
        chunk.append(c);
        index++;
        if (isDigit(c)) {
            while (index < slength) {
                c = s.charAt(index);
                if (!isDigit(c)) {
                    break;
                }
                chunk.append(c);
                index++;
            }
        } else {
            while (index < slength) {
                c = s.charAt(index);
                if (isDigit(c)) {
                    break;
                }
                chunk.append(c);
                index++;
            }
        }
        return chunk.toString();
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (!(o1 instanceof String) || !(o2 instanceof String)) {
            return 0;
        }
        String s1 = (String) o1;
        String s2 = (String) o2;

        return compareString(s1,s2);
    }

    public static int compareString(String s1,String s2){
        if(s1 == null){
            return 1;
        }
        if(s2 == null){
            return -1;
        }
        int thisMarker = 0;
        int thatMarker = 0;
        int s1Length = s1.length();
        int s2Length = s2.length();

        while (thisMarker < s1Length && thatMarker < s2Length) {
            String thisChunk = getChunk(s1, s1Length, thisMarker);
            thisMarker += thisChunk.length();

            String thatChunk = getChunk(s2, s2Length, thatMarker);
            thatMarker += thatChunk.length();

            //如果两个字符块都是数字，则按数字排序
            int result;
            if (isDigit(thisChunk.charAt(0)) && isDigit(thatChunk.charAt(0))) {
                result = compareDigit(thisChunk,thatChunk);
                if (result != 0) {
                    return result;
                }
            } else {
                result = thisChunk.compareTo(thatChunk);
            }

            if (result != 0) {
                return result;
            }
        }

        return s1Length - s2Length;
    }

    /**
     * 按数字比较
     */
    private static int compareDigit(String thisChunk,String thatChunk){
        int thisChunkLength = thisChunk.length();
        int result = thisChunkLength - thatChunk.length();
        if (result == 0) {
            //诸位比较
            for (int i = 0; i < thisChunkLength; i++) {
                result = thisChunk.charAt(i) - thatChunk.charAt(i);
                if (result != 0) {
                    return result;
                }
            }
        }
        return result;
    }

}
