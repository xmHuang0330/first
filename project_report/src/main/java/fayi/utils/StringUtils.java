package fayi.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    static final HashMap<String, String> comp = new HashMap<>();

    static {
        comp.put("A", "T");
        comp.put("T", "A");
        comp.put("C", "G");
        comp.put("G", "C");
    }

    public static boolean commonPoly(String a, String b, int maxNab) {
        String leftPoly = "";
        String rightPoly = "";
        int[] ints = StringUtils.oneGenDiff(a, b);
        if (ints[0] == -1) {
            return false;
        }
        String leftString = a.substring(0, ints[2]);
        Pattern leftPattern = Pattern.compile("([A]+|[T]+)$");
        Matcher leftMatcher = leftPattern.matcher(leftString);
        if (leftMatcher.find()) {
            leftPoly = leftMatcher.group();
        }
        if (ints[2] + 1 < a.length()) {
            String rightString = a.substring(ints[2] + 1);
            Pattern rightPattern = Pattern.compile("^([A]+|[T]+)");
            Matcher rightMatcher = rightPattern.matcher(rightString);
            if (rightMatcher.find()) {
                rightPoly = rightMatcher.group();
            }
        }
        return (rightPoly + leftPoly).matches("^([A]{" + maxNab + ",}|[T]{" + maxNab + ",})$");
    }

    public static int[] oneByteMisMatch(String source, String current) {

        for (int i = 0; i < source.length(); i++) {
            int mismatch = 0;
            if (current.length() > source.length() - i) {
                return new int[]{-1, -1, -1};
            }
            int length = 0;
            int pos = -1;
            for (int j = 0; j < current.length(); j++) {
//                System.out.println(source.charAt(i)+" | "+current.charAt(j));
//                System.out.println(source.charAt(i) == current.charAt(j));
                if (source.charAt(i + j) != current.charAt(j)) {
                    pos = j;
                    if (pos < current.length() - 1) {
                        if ((current.length() - j) > source.length() - (i + j) - 1 && source.charAt(i + j) == current.charAt(j + 1)) {
                            int count = 1;
                            while (count < (current.length() - j - 1) && source.charAt(i + j + count) == current.charAt(j + 1 + count)) {
                                count++;
                                length += 1;
                            }
                            if (count == current.length() - j) {
                                return new int[]{i, length, pos};
                            }
                        } else if (source.charAt(i + j + 1) == current.charAt(j)) {
                            int count = 1;
                            while (count < (current.length() - j - 1) && source.charAt(i + j + 1 + count) == current.charAt(j + count)) {
                                count++;
                                length += 1;
                            }
                            if (count == current.length() - j) {
                                return new int[]{i, length, pos};
                            }
                        }
                    }
                    mismatch++;
                }
                if (mismatch > 1) {
                    break;
                }
                length++;
            }
            if (mismatch <= 1) {
                return new int[]{i, length, pos};
            }
        }
        return new int[]{-1, -1, -1};
    }

    public static String reverseComp(String seq) {
        StringBuilder result = new StringBuilder();
        for (int i = seq.length() - 1; i >= 0; i--) {
            result.append(comp.get(seq.charAt(i) + ""));
        }
        return result.toString();
    }

    public static void main(String[] args) {

        int[] ints = oneGenDiff("AAAGAAAAGAAAAGAAAAGAAAAGAA", "AAAGAAAAGAAAAGAAAAGAACAGAA");
        System.out.println(Arrays.toString(ints));

//        System.out.println(reverseComp("ATCG"));
    }

    public static int[] oneGenDiff(String strInfo, String next) {
        if (strInfo.length() != next.length()) return new int[]{-1, -1, -1};
        return oneByteMisMatch(strInfo, next);
    }
}
