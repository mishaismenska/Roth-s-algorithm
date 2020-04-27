package com.mishaismenska.minimizationalgorithm;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {


    private static final char emptySet = 'Ø';

    
    private static void print(char[] s){
        for (char c : s) {
            System.out.print(c);
        }
        System.out.println();
    }

    private static String charArrayToString(char[] s){
        StringBuilder res = new StringBuilder("");
        for(char c : s){
            res.append(c);
        }
        return res.toString();
    }

    private static void printArrayList(ArrayList<char[]> a){
        for(char[] s : a){
            print(s);
        }
    }

    private static int cubeType(char[] s, int pc){
        int r = 0;
        for(char c : s){
            if (c == 'x') r++;
        }
        return r;
    }

    private static boolean contains(char[] s, ArrayList<char []> a){
        for(char[] ai : a){
            boolean ok = true;
            for(int i = 0; i < s.length; i++){
                if(s[i] != ai[i]) ok = false;
            }
            if(ok) return true;
        }
        return false;
    }

    private static ArrayList<char[]> combineSets(ArrayList<char[]> a, ArrayList<char[]> b, int pc) {
        ArrayList<char[]> resultingSet = new ArrayList<>();
        resultingSet.addAll(a);
        for(int i = 0; i < b.size(); i++){
            boolean contains = false;
            for(int j = 0; j < a.size(); j++){
                char[] ne = intersect(a.get(j), b.get(i), pc);
                if(Arrays.equals(ne, b.get(i))){
                    contains = true;
                    break;
                }
            }
            if(!contains){
                resultingSet.add(b.get(i));
            }
        }
        return  resultingSet;
    }

    private static ArrayList<char[]> subtract(char[] a, ArrayList<char[]> b, int pc, int i){
        ArrayList<char[]> cr = subtract(a, b.get(i), pc);
        if(i == b.size() - 1) return cr;
        if(cr.size() == 1){
            return subtract(cr.get(0), b, pc, i+1);
        } else{
            ArrayList<char[]> result = new ArrayList<>();
            for(char[] s : cr){
                result.addAll(subtract(s, b, pc, i+1));
            }
            return result;
        }
    }

    private static ArrayList<char[]> subtractSets(ArrayList<char[]> a, ArrayList<char[]> b, int pc){
        if(b.size() == 0){
            return a;
        }
        ArrayList<char []> resultSet = new ArrayList<>();
        //remove all b elements for every element of a
        for(char[] ai : a){
            resultSet.addAll(subtract(ai, b, pc, 0));
        }
        return resultSet;
    }

    public static char[] multiply(char[] c1, char[] c2, int pc){
        char[] result = new char[pc];
        int ysCount = 0;
        int yIndex;
        for(int i = 0; i < pc; i++) {
            if((c1[i] == '0' && c2[i] == '0')
                    ||(c1[i] == 'x' && c2[i] == '0')
                    ||(c1[i] == '0' && c2[i] == 'x')) result[i] = '0';
            else if((c1[i] == '1' && c2[i] == '0')
                    ||(c1[i] == '0' && c2[i] == '1')) {
                if(ysCount > 0){
                    return new char[]{emptySet};
                }
                ysCount++;
                yIndex = i;
                result[i] = 'x';
            }
            else if((c1[i] == '1' && c2[i] == '1')
                    ||(c1[i] == 'x' && c2[i] == '1')
                    ||(c1[i] == '1' && c2[i] == 'x')) result[i] = '1';
            else result[i] = 'x';
        }
        return result;
    }

    public static char[] intersect(char[] c1, char[] c2, int pc){
        char[] result = new char[pc];
        for(int i = 0; i < pc; i++) {
            if ((c1[i] == '0' && c2[i] == '0')
                    || (c1[i] == 'x' && c2[i] == '0')
                    || (c1[i] == '0' && c2[i] == 'x')) result[i] = '0';
            else if((c1[i] == '1' && c2[i] == '0')
                    ||(c1[i] == '0' && c2[i] == '1')) {
                return new char[]{emptySet};
            }
            else if((c1[i] == '1' && c2[i] == '1')
                    ||(c1[i] == 'x' && c2[i] == '1')
                    ||(c1[i] == '1' && c2[i] == 'x')) result[i] = '1';
            else result[i] = 'x';
        }
        return result;
    }

    public static ArrayList<char[]> subtract(char[] c1, char[] c2, int pc){
        ArrayList<char[]> result = new ArrayList<>();
        int zsCount = 0;
        for(int i = 0; i < pc; i++) {
            if((c1[i] == '1' && c2[i] == '0')
                    ||(c1[i] == '0' && c2[i] == '1')) {
                //y
                result.clear();
                result.add(c1.clone());
                return result;
            } else if(c1[i] == 'x' && c2[i] == '0'){
                result.add(c1.clone());
                result.get(result.size() - 1)[i] = '1';
            } else if(c1[i] == 'x' && c2[i] == '1'){
                result.add(c1.clone());
                result.get(result.size() - 1)[i] = '0';
            }
        }
        return result;
    }


    public static ArrayList<char[]> findSimpleImplicants(int pc, ArrayList<char[]> L, ArrayList<char[]> N){
        ArrayList<char[]> c = new ArrayList<>();
        c.addAll(L);
        c.addAll(N);
        ArrayList<char[]> zs = new ArrayList<>();
        ArrayList<ArrayList<char[]>> table = new ArrayList<>();
        ArrayList<char[]> Z = new ArrayList<>();
        ArrayList<char[]> A = new ArrayList<>();
        int ct = 1;
        do{
            Z.clear();
            A.clear();
            table.clear();
            for(int i = 0; i < c.size(); i++){
                table.add(new ArrayList<>());
                for(int j = 0; j < i; j++){
                    table.get(i).add(multiply(c.get(i), c.get(j), pc));
                }
            }

            for(int i = 0; i < table.size(); i++){
                for(int j = 0; j < table.get(i).size(); j++){
                    char[] element = table.get(i).get(j);
                    if(element[0] != emptySet){
                        if(ct == cubeType(element, pc) && !contains(element, A))
                            A.add(table.get(i).get(j));
                    }
                }
            }
            boolean contains;
            for(int i = 0; i < c.size(); i++){
                contains = false;
                for(int j = 0; j < c.size(); j++){
                    //j row i column
                    if(i < table.get(j).size())
                        if(table.get(j).get(i)[0] != emptySet && cubeType(table.get(j).get(i), pc) == ct){
                            contains = true;
                            break;
                        }
                    if(j < table.get(i).size()&& cubeType(table.get(i).get(j), pc) == ct)
                        if(table.get(i).get(j)[0] != emptySet){
                            contains = true;
                            break;
                        }
                }
                if(!contains && !contains(c.get(i), Z)){
                    Z.add(c.get(i));
                }
            }
            ArrayList<char[]> b = subtractSets(c, Z, pc);
            c = combineSets(A, b, pc);
            zs.addAll(Z);
            ct++;
        }while (!A.isEmpty());

        return zs;
    }

    public static void findLExtremals(ArrayList<char[]> L, ArrayList<char[]> Z, int pc){
        ArrayList<char[]> subtrahend = new ArrayList<>();
        //fission, extremal
        ArrayList<Pair<char[], char[]>> fissions = new ArrayList<>();
        for(int i = 0; i < Z.size(); i++){
            subtrahend.clear();
            subtrahend.addAll(Z);
            subtrahend.remove(i);
            ArrayList<char[]> fission = subtract(Z.get(i), subtrahend, pc, 0);
            print(Z.get(i));
            for(char[] s : fission){
                fissions.add(new Pair<>(s, Z.get(i)));
            }
            System.out.println("fission");
            printArrayList(fission);
            System.out.println("------------------------------");
        }

        char[][][] table = new char[fissions.size()][L.size()][];

        for(int i = 0; i < fissions.size(); i++){
            for(int j = 0; j < L.size(); j++){
                table[i][j] = intersect(fissions.get(i).getKey(), L.get(j), pc);
            }
        }

        for(int i = 0; i < fissions.size(); i++){
            for(int j = 0; j < L.size(); j++){
                int k = 0;
                for(; k < table[i][j].length; k++){
                    System.out.print(table[i][j][k]);
                }
                for(;k <= pc; k++){
                    System.out.print(" ");
                }
                System.out.print("\t");
            }
            System.out.println("\n");
        }
        ArrayList<char[]> E = new ArrayList<>();

        if(fissions.isEmpty()) {
            Random r = new Random();
            int index = r.nextInt() % Z.size();
            E.add(Z.get(index));
        }else{
            for(int i = 0; i < fissions.size(); i++){
                boolean empty = true;
                for(int j = 0; j < L.size(); j++){
                    if(table[i][j][0] != emptySet){
                        empty = false;
                        break;
                    }
                }

                if(!empty && !contains(fissions.get(i).getValue(), E)){
                    E.add(fissions.get(i).getValue());
                }
            }
        }
        System.out.println("E");
        printArrayList(E);

        ArrayList<char[]> fissionsLsE = new ArrayList<>();
        for(char[] l : L){
            ArrayList<char[]> fission = subtract(l, E, pc, 0);
            if(!fission.isEmpty()){
                System.out.println("fuck");
                print(l);
                fissionsLsE.addAll(fission);
            }
        }

        ArrayList<char[]> ZWithoutE = new ArrayList<>();
        for(char[] z : Z){
            if(!contains(z, E)){
                ZWithoutE.add(z);
            }
        }

        System.out.println("zwe");
        printArrayList(ZWithoutE);
        char[][][] tableZWEintLsE = new char[ZWithoutE.size()][fissionsLsE.size()][];
        ArrayList<ArrayList<char[]>> tableZWEintLsENotForOutput = new ArrayList<>();
        for(int i = 0; i < ZWithoutE.size(); i++){
            tableZWEintLsENotForOutput.add(new ArrayList<>());
            for(int j = 0; j < fissionsLsE.size(); j++){
                char[] ne = intersect(ZWithoutE.get(i), fissionsLsE.get(j), pc);
                tableZWEintLsE[i][j] = ne;
                if(ne[0] != emptySet) tableZWEintLsENotForOutput.get(i).add(ne);
            }
        }
        System.out.println("table");
        for(int i = 0; i < ZWithoutE.size(); i++){
            for(int j = 0; j < fissionsLsE.size(); j++) {
                int k = 0;
                for(; k < tableZWEintLsE[i][j].length; k++){
                    System.out.print(tableZWEintLsE[i][j][k]);
                }
                for(;k <= pc; k++){
                    System.out.print(" ");
                }
                System.out.print("\t");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        ArrayList<char[]> L = new ArrayList<>();
        ArrayList<char[]> N = new ArrayList<>();
        /*L.add("00000".toCharArray());
        L.add("11001".toCharArray());
        L.add("00010".toCharArray());
        L.add("10010".toCharArray());
        L.add("10110".toCharArray());
        L.add("01101".toCharArray());
        L.add("00100".toCharArray());
        N.add("00011".toCharArray());
        N.add("00110".toCharArray());
        N.add("00111".toCharArray());
        N.add("01111".toCharArray());
        N.add("11101".toCharArray());*/



        L.add("0000".toCharArray());
        L.add("0001".toCharArray());
        L.add("0101".toCharArray());
        L.add("1100".toCharArray());
        L.add("1101".toCharArray());
        L.add("1000".toCharArray());

        /*L.add("00000".toCharArray());
        L.add("00001".toCharArray());
        L.add("00010".toCharArray());
        L.add("00011".toCharArray());
        L.add("00100".toCharArray());
        L.add("00101".toCharArray());
        L.add("01000".toCharArray());
        L.add("01001".toCharArray());
        L.add("01010".toCharArray());
        L.add("01011".toCharArray());
        L.add("01100".toCharArray());
        L.add("01101".toCharArray());
        L.add("10000".toCharArray());
        L.add("10001".toCharArray());
        L.add("10010".toCharArray());
        L.add("10011".toCharArray());
        L.add("10101".toCharArray());
        L.add("11000".toCharArray());
        L.add("11001".toCharArray());
        L.add("11010".toCharArray());
        L.add("11011".toCharArray());
        L.add("11101".toCharArray());
        L.add("00110".toCharArray());
        L.add("00111".toCharArray());
        L.add("01110".toCharArray());
        L.add("01111".toCharArray());
        L.add("10110".toCharArray());
        L.add("10111".toCharArray());
        L.add("11110".toCharArray());
        L.add("11111".toCharArray());*/
/*        L.add("1010".toCharArray());
        L.add("0010".toCharArray());
        L.add("0110".toCharArray());
        L.add("0010".toCharArray());
        L.add("0000".toCharArray());
        L.add("0101".toCharArray());
        L.add("0001".toCharArray());
        L.add("1110".toCharArray());
        L.add("1110".toCharArray());
        L.add("1101".toCharArray());
        L.add("1001".toCharArray());*/
        ArrayList<char[]> zs = findSimpleImplicants(4, L, N);
        findLExtremals(L, zs, 4);
        //System.out.println("ZS");
        //printArrayList(zs);
        //System.out.println(cubeType("00x1x".toCharArray(), 5));
        //print(subtract("1xx".toCharArray(), "000".toCharArray(), 3).get(0));
    }
}
