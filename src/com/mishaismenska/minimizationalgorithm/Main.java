package com.mishaismenska.minimizationalgorithm;
import java.lang.reflect.Array;
import java.util.*;

public class Main {


    private static final char emptySet = 'Ø';

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

        if(i == b.size() - 1){
            return cr;
        }

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

    private static ArrayList<char[]> findSimpleImplicants(ArrayList<char[]> L, ArrayList<char[]> N, int pc){
        ArrayList<char[]> C = new ArrayList<>();
        C.addAll(L);
        C.addAll(N);
        ArrayList<char[]> Z = new ArrayList<>();
        ArrayList<char[]> A = new ArrayList<>();
        ArrayList<char[]> Zi = new ArrayList<>();
        do{
            A.clear();
            Zi.clear();
            boolean[] used = new boolean[C.size()];
            for(int i = 0; i < C.size(); i++){
                for(int j = 0; j< i; j++){
                    char[] newCube = multiply(C.get(i), C.get(j), pc);
                    if(cubeType(newCube, pc) > Math.max(cubeType(C.get(i), pc), cubeType(C.get(j), pc))){
                        used[i] = true;
                        used[j] = true;
                        if(!contains(newCube, A))
                            A.add(newCube);
                    }
                }
            }
            for(int i = 0; i < C.size(); i++){
                if(!used[i]) Zi.add(C.get(i));
            }
            Z.addAll(Zi);
            ArrayList<char[]> b = subtractSets(C, Zi, pc);
            C = combineSets(A, b, pc);
        }while(!A.isEmpty());
        return Z;
    }

    public static ArrayList<char[]> findLExtremals(ArrayList<char[]> L, ArrayList<char[]> Z, int pc){
        ArrayList<char[]> E = new ArrayList<>();
        ArrayList<char[]> subtrahend = new ArrayList<>();
        for(int i = 0; i < L.size(); i++){
            subtrahend.clear();
            subtrahend.addAll(Z);
            subtrahend.remove(i);
            ArrayList<char[]> fissionsI = subtract(Z.get(i), subtrahend, pc, 0);
            loop: for(int j = 0; j < fissionsI.size(); j++){
                for(int k = 0; k < L.size(); k++){
                    if(intersect(fissionsI.get(j), L.get(k), pc)[0] != emptySet){
                        E.add(Z.get(i));
                        break loop;
                    }
                }
            }
        }
        return E;
    }

    public static ArrayList<ArrayList<Integer>> getCombinations(ArrayList<Integer> elements, int K, int start){
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        if(K == 1){
            for(int i = start; i < elements.size(); i++){
                result.add(new ArrayList<>());
                result.get(result.size() - 1).add(elements.get(i));
            }
        } else {
            for(int i = start; i <= elements.size() - K; i++){
                ArrayList<ArrayList<Integer>> smallerResult = getCombinations(elements, K - 1, i + 1);
                for(int j = 0; j < smallerResult.size(); j++){
                    result.add(new ArrayList<>());
                    result.get(result.size() - 1).add(elements.get(i));
                    result.get(result.size() - 1).addAll(smallerResult.get(j));
                }
            }
        }
        return result;
    }

    private static boolean contains(boolean[] a, boolean v){
        for(int i = 0; i < a.length; i++){
            if(a[i] == v) return true;
        }
        return false;
    }

    public static ArrayList<ArrayList<char[]>> coverAllFunction(ArrayList<char[]> E, ArrayList<char[]> L, ArrayList<char[]> Z, int pc){
        ArrayList<ArrayList<char[]>> result = new ArrayList<>();
        if(E.size() == 0){
            E.add(Z.get(0));
        }
        ArrayList<char[]> cubesToCover = new ArrayList<>();
        for (int i = 0; i < L.size(); i++){
            cubesToCover.addAll(subtract(L.get(i), E, pc, 0));
        }
        if (cubesToCover.isEmpty()){
            result.add(E);
        }else{
            for(int i = 0; i < Z.size(); i++){
                if(contains(Z.get(i), E)){
                    Z.remove(i);
                    i--;
                }
            }
            ArrayList<ArrayList<Integer>> whichCubesToCoverContains = new ArrayList<>();
            for (int i = 0; i < Z.size(); i++){
                whichCubesToCoverContains.add(new ArrayList<>());
                for(int j = 0; j < cubesToCover.size(); j++){
                    char[] intersected = intersect(Z.get(i), cubesToCover.get(j), pc);
                    if(intersected[0] != emptySet){
                        whichCubesToCoverContains.get(i).add(j);
                    }
                }
            }

            ArrayList<Integer> indexes = new ArrayList<>();
            for(int i = 0; i < Z.size(); i++){
                indexes.add(i);
            }
            boolean[] b = new boolean[cubesToCover.size()];;
            boolean found = false;
            for(int i = 1; i <= Z.size(); i++){
                ArrayList<ArrayList<Integer>> combinations = getCombinations(indexes, i, 0);
                for(int j = 0; j < combinations.size(); j++){
                    Arrays.fill(b, false);
                    for(int k : combinations.get(j)){
                        for(int l : whichCubesToCoverContains.get(k)){
                            b[l] = true;
                        }
                    }
                    if(!contains(b, false)){
                        found = true;
                        result.add(new ArrayList<>());
                        result.get(result.size() - 1).addAll(E);
                        for (int k : combinations.get(j)){
                            result.get(result.size() - 1).add(Z.get(k));
                        }
                    }

                }
                if(found) break;
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        ArrayList<char[]> L = new ArrayList<>();
        ArrayList<char[]> N = new ArrayList<>();
        L.add("00000".toCharArray());
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
        N.add("11101".toCharArray());

/*        L.add("0000".toCharArray());
        L.add("0001".toCharArray());
        L.add("0101".toCharArray());
        L.add("1100".toCharArray());
        L.add("1101".toCharArray());
        L.add("1000".toCharArray());*/
        ArrayList<char[]> Z = findSimpleImplicants(L, N, L.get(0).length);
        ArrayList<char[]> E = findLExtremals(L, Z, L.get(0).length);
        ArrayList<ArrayList<char[]>> result = coverAllFunction(E, L, Z, L.get(0).length);
        System.out.println("Result: ");
        for(ArrayList<char[]>r : result){
            System.out.print("{");
            for(int i = 0; i < r.size(); i++){
                for(char c : r.get(i)) {
                    System.out.print(c);
                }
                if(i != r.size() - 1) System.out.print(", ");
            }
            System.out.println("}");
        }

    }
}
