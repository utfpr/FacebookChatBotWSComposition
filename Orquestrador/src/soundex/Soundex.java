/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package soundex;

import java.util.List;

/**
 *
 * @author tchulla
 */
public class Soundex {
    /* 
     * MAPA DE CARACTERES
     * F: A E H I O U W Y B F P V C G J K Q S X Z D T L M N R
     * T: 0 0 0 0 0 0 0 0 1 1 1 1 2 2 2 2 2 2 2 2 3 3 4 5 5 6
     */

    public static final char[] MAP = {
        //A    B    C    D    E    F    G    H    I    J    K    
        '0', '1', '2', '3', '0', '1', '2', '0', '0', '2', '2',
        //L    M    N    O    P    Q    R    S    T    U    V   
        '0', '5', '5', '0', '1', '2', '6', '2', '3', '0', '1',
        //X    Y    W    Z 
        '2', '0', '1', '2'
    };

    public static String soundex(String s) {
        String t = s.toUpperCase();

        StringBuilder res = new StringBuilder();
        char c, prev = '?';

        for (int i = 0; i < t.length() && res.length() < 4 && (c = t.charAt(i)) != ','; i++) {
            if (c >= 'A' && c <= 'Z' && c != prev) {
                prev = c;

                if (i == 0) {
                    char m = MAP[ c - 'A'];
//                    res.append(m == '0' ? '?' : c);
                    res.append(c);
                } else {
                    char m = MAP[ c - 'A'];
                    if (m != '0') {
                        res.append(m);
                    }
                }
            }
        }

        if (res.length() == 0) {
            return "";
        }

        for (int i = res.length(); i < 4; i++) {
            res.append('0');
        }

        return res.toString();
    }

    public static void main(String[] args) {

        String[] names = {
            "Darwin, Ian",
            "Davidson, Greg",
            "Darwent, William",
            "Derwin, Daemon",
            "caqui",
            "caki"
        };

        System.out.println(comparacaoFonetica("ola", "ola "));

    }

    public static boolean comparacaoFonetica(String a, String b) {
        String sa = soundex(a.trim());
        String sb = soundex(b.trim());
        return sa.equals(sb);
    }

    public static int comparacaoFonetica(String a, List<String> args) {
        int ret = 0;

        String[] av = a.split(" ");
        for (String sa : av) {
            for (String sb : args) {
                if (comparacaoFonetica(sa.trim(), sb.trim())) {
                    ret++;
                }
            }
        }

        return ret;
    }
//    public static int comparacaoFonetica (String a, String ... args){
//        int ret = 0;
//        
//        String [] av = a.split(" ");
//        for (String sa : av) {
//            for (String sb : args) {
//                if(comparacaoFonetica(sa, sb)){
//                    ret ++;
//                }
//            }
//        }
//        
//        return ret;
//    }
}
