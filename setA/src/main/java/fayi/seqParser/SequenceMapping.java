package fayi.seqParser;

import fayi.tableObject.MappingResult;
import fayi.tableObject.StrInfo;

import java.util.*;

public class SequenceMapping {

    private static final String EMPTY_DIRECTION = "none";
    private static final String UP = "UP";
    private static final String LEFT = "LEFT";
    private static final String DIAGONAL = "DIAGONAL";

    public MappingResult resultMapper(String sequenceA, String sequenceB) {

//        sequenceA = new StringBuilder(sequenceA).reverse().toString();
//        sequenceB = new StringBuilder(sequenceB).reverse().toString();

        int match = 15;
        int mismatch = -5;
        float gap = -5;
        float newGap = -35;

        float[][] score = new float[sequenceB.length() + 1][sequenceA.length() + 1];
        String[][] pointer = new String[sequenceB.length() + 1][sequenceA.length() + 1];

        //初始化二维表
        score[0][0] = 0;
        pointer[0][0] = EMPTY_DIRECTION;

        for (int i = 1; i < sequenceB.length() + 1; i++) {
            score[i][0] = gap * i;
            pointer[i][0] = UP;
            for (int j = 1; j < sequenceA.length() + 1; j++) {
                score[0][j] = gap * j;
                pointer[0][j] = LEFT;
            }
        }

        for (int i = 1; i < sequenceB.length() + 1; i++) {
            for (int j = 1; j < sequenceA.length() + 1; j++) {
                char baseA = sequenceA.charAt(j - 1);
                char baseB = sequenceB.charAt(i - 1);
                float baseScore;
                if (baseA == baseB) {
                    baseScore = score[i - 1][j - 1] + match;
                } else {
                    baseScore = score[i - 1][j - 1] + mismatch;
                }
                float upperScore;
                if("UP".equals(pointer[i-1][j])){
                    upperScore = score[i - 1][j] + gap;
                }else{
                    upperScore = score[i - 1][j] + newGap;
                }
                float leftScore;
                if(LEFT.equals(pointer[i][j-1])) {
                    leftScore = score[i][j - 1] + gap;
                }else {
                    leftScore = score[i][j - 1] + newGap;
                }

                if (baseScore >= upperScore) {
                    if (baseScore >= leftScore) {
                        score[i][j] = baseScore;
                        pointer[i][j] = DIAGONAL;
                    } else {
                        score[i][j] = leftScore;
                        pointer[i][j] = LEFT;
                    }
                } else if (upperScore >= leftScore) {
                    if(upperScore > leftScore){
                        score[i][j] = upperScore;
                        pointer[i][j] = UP;
                    } else {
                        score[i][j] = leftScore;
                        pointer[i][j] = LEFT;
                    }
                } else {
                    score[i][j] = leftScore;
                    pointer[i][j] = LEFT;
                }
            }
        }

//        for (String [] values:pointer) {
//            for (String value : values) {
//                System.out.print(value + "\t");
//            }
//            System.out.println();
//        }
//
//        for (float[] values:score) {
//            for (float value : values) {
//                System.out.print(value + "\t");
//            }
//            System.out.println();
//        }
        int j = sequenceA.length();
        int i = sequenceB.length();
        StringBuilder resultA = new StringBuilder();
        StringBuilder resultB = new StringBuilder();
        boolean flag = false;
        int total_score = 0;
        do {
            switch (pointer[i][j]) {
                case DIAGONAL: {
                    resultA.append(sequenceA.charAt(j - 1));
                    resultB.append(sequenceB.charAt(i - 1));
                    if (sequenceA.charAt(j - 1) == sequenceB.charAt(i - 1)) {
                        total_score += match;
                    } else {
                        total_score += mismatch;
                    }
                    i -= 1;
                    j -= 1;
                    break;
                }
                case LEFT: {
                    resultA.append(sequenceA.charAt(j - 1));
                    resultB.append("-");
                    total_score += gap;
                    j -= 1;
                    break;
                }
                case UP: {
                    resultA.append("-");
                    resultB.append(sequenceB.charAt(i - 1));
                    total_score += gap;
                    i -= 1;
                    break;
                }
                case EMPTY_DIRECTION: {
                    flag = true;
                    break;
                }
            }
        } while (!flag);

        return new MappingResult(resultA.reverse().toString(), resultB.reverse().toString(), total_score);
    }

    public float compareWithZero(float a){

        return Math.max(a,a);
    }


    public MappingResult gatkMapper(String sequenceA, String sequenceB) {

//        sequenceA = new StringBuilder(sequenceA).reverse().toString();
//        sequenceB = new StringBuilder(sequenceB).reverse().toString();
        int mismatch = -35;
        float match = 35;
        int gap = 0;
        float keepMatch = 60;
        float newGap = -60;

        float[][] score = new float[sequenceB.length() + 1][sequenceA.length() + 1];
        String[][] pointer = new String[sequenceB.length() + 1][sequenceA.length() + 1];

        //初始化二维表
        score[0][0] = 0;
        pointer[0][0] = EMPTY_DIRECTION;

        for (int i = 1; i < sequenceB.length() + 1; i++) {
            score[i][0] = gap * i;
            pointer[i][0] = UP;
            for (int j = 1; j < sequenceA.length() + 1; j++) {
                score[0][j] = gap * j;
                pointer[0][j] = LEFT;
            }
        }


        for (int i = 1; i < sequenceB.length() + 1; i++) {
            for (int j = 1; j < sequenceA.length() + 1; j++) {
                char baseA = sequenceA.charAt(j - 1);
                char baseB = sequenceB.charAt(i - 1);
                float baseScore;
                if (baseA == baseB) {
                    baseScore = score[i - 1][j - 1] + (DIAGONAL.equals(pointer[i-1][j-1])? keepMatch : match);
                } else {
                    baseScore = score[i - 1][j - 1] + mismatch;
                }
                float upperScore;
                if(UP.equals(pointer[i-1][j])){
                    upperScore = score[i - 1][j] + gap;
                }else{
                    upperScore = score[i - 1][j] + newGap;
                }
                float leftScore;
                if(LEFT.equals(pointer[i][j-1])) {
                    leftScore = score[i][j - 1] + gap;
                }else {
                    leftScore = score[i][j - 1] + newGap;
                }

                if (baseScore >= upperScore) {
                    if (baseScore >= leftScore) {
                        score[i][j] = compareWithZero(baseScore);
                        pointer[i][j] = DIAGONAL;
                    } else {
                        score[i][j] = compareWithZero(leftScore);
                        pointer[i][j] = LEFT;
                    }
                } else{
                    if (upperScore >= leftScore) {
                        if(upperScore > leftScore) {
                            score[i][j] = compareWithZero(upperScore);
                            pointer[i][j] = UP;
                        }else{
                            score[i][j] = sequenceA.length() >= sequenceB.length()?compareWithZero(leftScore):compareWithZero(upperScore);
                            pointer[i][j] = sequenceA.length() >= sequenceB.length()?LEFT:UP;

                        }
                    } else {
                        score[i][j] = compareWithZero(leftScore);
                        pointer[i][j] = LEFT;
                    }
                }
            }
        }

//        for (String [] values:pointer) {
//            for (String value : values) {
//                System.out.print(value + "\t");
//            }
//            System.out.println();
//        }
//
//        for (float[] values:score) {
//            for (float value : values) {
//                System.out.print(value + "\t");
//            }
//            System.out.println();
//        }
        int j = sequenceA.length();
        int i = sequenceB.length();
        StringBuilder resultA = new StringBuilder();
        StringBuilder resultB = new StringBuilder();
        boolean flag = false;
        int total_score = 0;
        do {
            switch (pointer[i][j]) {
                case DIAGONAL: {
                    resultA.append(sequenceA.charAt(j - 1));
                    resultB.append(sequenceB.charAt(i - 1));
                    if (sequenceA.charAt(j - 1) == sequenceB.charAt(i - 1)) {
                        total_score += match;
                    } else {
                        total_score += mismatch;
                    }
                    i -= 1;
                    j -= 1;
                    break;
                }
                case LEFT: {
                    resultA.append(sequenceA.charAt(j - 1));
                    resultB.append("-");
                    j -= 1;
                    break;
                }
                case UP: {
                    resultA.append("-");
                    resultB.append(sequenceB.charAt(i - 1));
                    i -= 1;
                    break;
                }
                case EMPTY_DIRECTION: {
                    flag = true;
                    break;
                }
            }
        } while (!flag);

        return new MappingResult(resultA.reverse().toString(), resultB.reverse().toString(), score[sequenceB.length()][sequenceA.length()]);
    }

    public MappingResult lessGap(String sequenceA, String sequenceB) {

        sequenceA = new StringBuilder(sequenceA).reverse().toString();
        sequenceB = new StringBuilder(sequenceB).reverse().toString();
        int mismatch = 0;
        float match = 5;
        int gap = -3;
        float newGap = -15;

        float[][] score = new float[sequenceB.length() + 1][sequenceA.length() + 1];
        String[][] pointer = new String[sequenceB.length() + 1][sequenceA.length() + 1];

        //初始化二维表
        score[0][0] = 0;
        pointer[0][0] = EMPTY_DIRECTION;

        for (int i = 1; i < sequenceB.length() + 1; i++) {
            score[i][0] = gap * i;
            pointer[i][0] = UP;
            for (int j = 1; j < sequenceA.length() + 1; j++) {
                score[0][j] = gap * j;
                pointer[0][j] = LEFT;
            }
        }


        for (int i = 1; i < sequenceB.length() + 1; i++) {
            for (int j = 1; j < sequenceA.length() + 1; j++) {
                char baseA = sequenceA.charAt(j - 1);
                char baseB = sequenceB.charAt(i - 1);
                float baseScore;
                if (baseA == baseB) {
                    baseScore = score[i - 1][j - 1] + match;
                } else {
                    baseScore = score[i - 1][j - 1] + mismatch;
                }
                float upperScore;
                if (UP.equals(pointer[i - 1][j])) {
                    upperScore = score[i - 1][j] + gap;
                } else {
                    upperScore = score[i - 1][j] + newGap;
                }
                float leftScore;
                if (LEFT.equals(pointer[i][j - 1])) {
                    leftScore = score[i][j - 1] + gap;
                } else {
                    leftScore = score[i][j - 1] + newGap;
                }

                if (baseScore >= upperScore) {
                    if (baseScore >= leftScore) {
                        score[i][j] = compareWithZero(baseScore);
                        pointer[i][j] = DIAGONAL;
                    } else {
                        score[i][j] = compareWithZero(leftScore);
                        pointer[i][j] = LEFT;
                    }
                } else {
                    if (upperScore >= leftScore) {
                        if (upperScore > leftScore) {
                            score[i][j] = compareWithZero(upperScore);
                            pointer[i][j] = UP;
                        } else {
                            score[i][j] = sequenceA.length() >= sequenceB.length() ? compareWithZero(leftScore) : compareWithZero(upperScore);
                            pointer[i][j] = sequenceA.length() >= sequenceB.length() ? LEFT : UP;

                        }
                    } else {
                        score[i][j] = compareWithZero(leftScore);
                        pointer[i][j] = LEFT;
                    }
                }
            }
        }

//        for (String [] values:pointer) {
//            for (String value : values) {
//                System.out.print(value + "\t");
//            }
//            System.out.println();
//        }
//
//        for (float[] values:score) {
//            for (float value : values) {
//                System.out.print(value + "\t");
//            }
//            System.out.println();
//        }
        int j = sequenceA.length();
        int i = sequenceB.length();
        StringBuilder resultA = new StringBuilder();
        StringBuilder resultB = new StringBuilder();
        boolean flag = false;
        int total_score = 0;
        do {
            switch (pointer[i][j]) {
                case DIAGONAL: {
                    resultA.append(sequenceA.charAt(j - 1));
                    resultB.append(sequenceB.charAt(i - 1));
                    if (sequenceA.charAt(j - 1) == sequenceB.charAt(i - 1)) {
                        total_score += match;
                    } else {
                        total_score += mismatch;
                    }
                    i -= 1;
                    j -= 1;
                    break;
                }
                case LEFT: {
                    resultA.append(sequenceA.charAt(j - 1));
                    resultB.append("-");
                    j -= 1;
                    break;
                }
                case UP: {
                    resultA.append("-");
                    resultB.append(sequenceB.charAt(i - 1));
                    i -= 1;
                    break;
                }
                case EMPTY_DIRECTION: {
                    flag = true;
                    break;
                }
            }
        } while (!flag);

        return new MappingResult(resultA.reverse().toString(), resultB.reverse().toString(), score[sequenceB.length()][sequenceA.length()]);
    }

    public MappingResult unitMapper(String sequenceA, String sequenceB) {

//        sequenceA = new StringBuilder(sequenceA).reverse().toString();
//        sequenceB = new StringBuilder(sequenceB).reverse().toString();

        int gap = -5;
        int mismatch = -5;
        float match = 15;
        float newGap = gap * Math.max(Math.abs(sequenceA.length() - sequenceB.length()), 2);

        float[][] score = new float[sequenceB.length() + 1][sequenceA.length() + 1];
        String[][] pointer = new String[sequenceB.length() + 1][sequenceA.length() + 1];

        //初始化二维表
        score[0][0] = 0;
        pointer[0][0] = EMPTY_DIRECTION;

        for (int i = 1; i < sequenceB.length() + 1; i++) {
            score[i][0] = gap * i;
            pointer[i][0] = UP;
            for (int j = 1; j < sequenceA.length() + 1; j++) {
                score[0][j] = gap * j;
                pointer[0][j] = LEFT;
            }
        }

        for (int i = 1; i < sequenceB.length() + 1; i++) {
            for (int j = 1; j < sequenceA.length() + 1; j++) {
                char baseA = sequenceA.charAt(j - 1);
                char baseB = sequenceB.charAt(i - 1);
                float baseScore;
                if (baseA == baseB) {
                    baseScore = score[i - 1][j - 1] + match;
                } else {
                    baseScore = score[i - 1][j - 1] + mismatch;
                }
                float upperScore;
                if(UP.equals(pointer[i-1][j])){
                    upperScore = score[i - 1][j] + gap;
                }else{
                    upperScore = score[i - 1][j] + newGap;
                }
                float leftScore;
                if(LEFT.equals(pointer[i][j-1])) {
                    leftScore = score[i][j - 1] + gap;
                }else {
                    leftScore = score[i][j - 1] + newGap;
                }

                if (baseScore >= upperScore) {
                    if (baseScore >= leftScore) {
                        score[i][j] = compareWithZero(baseScore);
                        pointer[i][j] = DIAGONAL;
                    } else {
                        score[i][j] = compareWithZero(leftScore);
                        pointer[i][j] = LEFT;
                    }
                } else{
                    if (upperScore >= leftScore) {
                        if(upperScore > leftScore) {
                            score[i][j] = compareWithZero(upperScore);
                            pointer[i][j] = UP;
                        }else{
                            score[i][j] = sequenceA.length() >= sequenceB.length()?compareWithZero(leftScore):compareWithZero(upperScore);
                            pointer[i][j] = sequenceA.length() >= sequenceB.length()?LEFT:UP;

                        }
                    } else {
                        score[i][j] = compareWithZero(leftScore);
                        pointer[i][j] = LEFT;
                    }
                }
            }
        }

//        for (String [] values:pointer) {
//            for (String value : values) {
//                System.out.print(value + "\t");
//            }
//            System.out.println();
//        }
//
//        for (float[] values:score) {
//            for (float value : values) {
//                System.out.print(value + "\t");
//            }
//            System.out.println();
//        }
        int j = sequenceA.length();
        int i = sequenceB.length();
        StringBuilder resultA = new StringBuilder();
        StringBuilder resultB = new StringBuilder();
        boolean flag = false;
        int total_score = 0;
        do {
            switch (pointer[i][j]) {
                case DIAGONAL: {
                    resultA.append(sequenceA.charAt(j - 1));
                    resultB.append(sequenceB.charAt(i - 1));
                    if (sequenceA.charAt(j - 1) != sequenceB.charAt(i - 1)) {
                        total_score += mismatch;
                    }
                    i -= 1;
                    j -= 1;
                    break;
                }
                case LEFT: {
                    resultA.append(sequenceA.charAt(j - 1));
                    resultB.append("-");
                    total_score += gap;
                    j -= 1;
                    break;
                }
                case UP: {
                    resultA.append("-");
                    resultB.append(sequenceB.charAt(i - 1));
                    total_score += gap;
                    i -= 1;
                    break;
                }
                case EMPTY_DIRECTION: {
                    flag = true;
                    break;
                }
            }
        } while (!flag);

        return new MappingResult(resultA.reverse().toString(), resultB.reverse().toString(), total_score);
    }


    public void arrayMapper(StrInfo refStr, StrInfo alleleStr) {
        ArrayList<String> sequenceA = refStr.getNoneCoreseq();
        ArrayList<String> sequenceB = alleleStr.getNoneCoreseq();

        int gap = -1;
        float[][] score = new float[sequenceB.size() + 1][sequenceA.size() + 1];
        String[][] pointer = new String[sequenceB.size() + 1][sequenceA.size() + 1];

        //初始化二维表
        score[0][0] = 0;
        pointer[0][0] = EMPTY_DIRECTION;
        for (int i = 1; i < sequenceB.size() + 1; i++) {
            score[i][0] = gap * i;
            pointer[i][0] = UP;
            for (int j = 1; j < sequenceA.size() + 1; j++) {
                score[0][j] = gap * j;
                pointer[0][j] = LEFT;
            }
        }

        for (int i = 1; i < sequenceB.size() + 1; i++) {
            for (int j = 1; j < sequenceA.size() + 1; j++) {
                String baseA = sequenceA.get(j - 1);
                String baseB = sequenceB.get(i - 1);
                float mappingScore = unitMapper(baseA, baseB).score / (Math.max(baseA.length(),baseB.length())*5f);
                System.out.println(mappingScore);
                float baseScore = score[i - 1][j - 1] + mappingScore;
                float upperScore = score[i - 1][j] + mappingScore;
                float leftScore = score[i][j - 1] + mappingScore;
//                if (pointer[i - 1][j].equals(UP)){
//                    upperScore = score[i - 1][j] + gap_a;
//                }else if(pointer[i][j-1].equals(LEFT)){
//                    leftScore = score[i - 1][j] + gap;
//                }
                if (baseScore >= upperScore) {
                    if (baseScore >= leftScore) {
                        score[i][j] = compareWithZero(baseScore);
                        pointer[i][j] = DIAGONAL;
                    } else {
                        score[i][j] = compareWithZero(leftScore);
                        pointer[i][j] = LEFT;
                    }
                } else{
                    if (upperScore >= leftScore) {
                        if(upperScore > leftScore) {
                            score[i][j] = compareWithZero(upperScore);
                            pointer[i][j] = UP;
                        }else{
                            score[i][j] = baseA.length() >= baseB.length()?compareWithZero(leftScore):compareWithZero(upperScore);
                            pointer[i][j] = baseA.length() >= baseB.length()?LEFT:UP;

                        }
                    } else {
                        score[i][j] = compareWithZero(leftScore);
                        pointer[i][j] = LEFT;
                    }
                }
            }
        }
//
        for (String[] values : pointer) {
            for (String value : values) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }

        for (float[] values : score) {
            for (float value : values) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
        int j = sequenceA.size();
        int i = sequenceB.size();
        ArrayList<String>[] results = new ArrayList[2];
        ArrayList<String> resultA = new ArrayList<>();
        ArrayList<String> resultB = new ArrayList<>();
        results[0] = resultA;
        results[1] = resultB;
        boolean flag = false;
        while (true) {
            switch (pointer[i][j]) {
                case DIAGONAL: {
                    resultA.add(sequenceA.get(j - 1));
                    resultB.add(sequenceB.get(i - 1));
                    i -= 1;
                    j -= 1;
                    break;
                }
                case LEFT: {
                    resultA.add(sequenceA.get(j - 1));
                    resultB.add("-");
                    j -= 1;
                    break;
                }
                case UP: {
                    resultA.add("-");
                    resultB.add(sequenceB.get(i - 1));
                    i -= 1;
                    break;
                }
                case EMPTY_DIRECTION: {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                break;
            }
        }
        Collections.reverse(resultA);
        Collections.reverse(resultB);
        refStr.setNoneCoreseq(resultA);
        alleleStr.setNoneCoreseq(resultB);
    }


    public static void main(String[] args) {
        int a = 10;
        while(--a > 0){
            System.out.println(a);
        }
    }


}
