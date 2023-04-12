package test;

public class SWAlgorism {
        public static final int MATCH_COST = 1;     //匹配
        public static final int INSERT_COST = 5;     //错位
        public static final int DELETE_COST = 3;    //缺失
        public static void cal(String s1,String s2){

            int x=s1.length(), y = s2.length();
            int[][] opt = new int[x+1][y+1];
            for(int i=0;i<x;i++) opt[i][y]=DELETE_COST*(x-i);
            for(int j=0;j<y;j++) opt[x][j]=DELETE_COST*(y-j);
            opt[x][y]=0;
            int minxy = Math.min(x,y);
            for(int k=1;k<=minxy;k++){
                for(int i=x-1;i>=0;i--){
                    opt[i][y-k] = getMin(opt, i, y-k, s1, s2);
                }
                for(int j=y-1;j>=0;j--){
                    opt[x-k][j] = getMin(opt, x-k,j, s1, s2);
                }
            }
            for(int k=x-minxy;k>=0;k--){
                opt[k][0] = getMin(opt, k, 0,s1, s2);
            }
            for(int k=y-minxy;k>=0;k--){
                opt[0][k] = getMin(opt, 0, k, s1, s2);
            }
            System.out.println(opt[0][0]);
            //还原配对序列
            int i=0, j=0;String a1="", a2="";
            while(i<x&&j<y) {
                if(opt[i][j] == ((s1.charAt(i) == s2.charAt(j)) ? MATCH_COST : INSERT_COST) + opt[i + 1][j + 1]){
                    a1 += s1.charAt(i);
                    a2 += s2.charAt(j);
                    i = i + 1;
                    j = j + 1;
                }
                else if(opt[i][j]==opt[i+1][j]+DELETE_COST){
                    a1+=s1.charAt(i);
                    a2+='-';
                    i=i+1;
                }else if(opt[i][j]==opt[i][j+1]+DELETE_COST){
                    a1+='-';
                    a2+=s2.charAt(j);
                    j=j+1;
                }
            }
            int lenDiff = a1.length()-a2.length();
            for(int k=0;k<-lenDiff;k++) a1 += '-';
            for(int k=0;k<lenDiff;k++) a2 += '-';
            System.out.println(a1);
            System.out.println(a2);
        }

        public static int getMin(int[][] opt, int x, int y, String s1, String s2){
            return Math.min(opt[x][y+1]+2,
                    Math.min(opt[x+1][y+1]+((s1.charAt(x)==s2.charAt(y))?MATCH_COST:INSERT_COST),
                            opt[x+1][y]+DELETE_COST));
        }
}
