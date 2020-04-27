package com.mishaismenska.minimizationalgorithm;

import javafx.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

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

    private static XSSFSheet sheet;
    private static int sheetJ;
    private static ArrayList<char[]> subtract(char[] a, ArrayList<char[]> b, int pc, int i){
        Cell cell = null;
        if(sheet != null){
            int sheeti;
            if(i >= sheetJ -1) sheeti = i+2;
            else sheeti = i+1;
            if(sheet.getRow(sheeti) == null) sheet.createRow(sheeti);
            Row row = sheet.getRow(sheeti);
            if(row.getCell(sheetJ) == null) row.createCell(sheetJ);
            cell = row.getCell(sheetJ);
        }
        ArrayList<char[]> cr = subtract(a, b.get(i), pc);
        if (cell != null) {
            for(char[] s : cr) {
                cell.setCellValue(cell.getStringCellValue() + "\n" + charArrayToString(s));
            }
        }

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
        XSSFWorkbook workbook = new XSSFWorkbook();
        ArrayList<char[]> c = new ArrayList<>();
        c.addAll(L);
        c.addAll(N);
        ArrayList<char[]> zs = new ArrayList<>();
        ArrayList<ArrayList<char[]>> table = new ArrayList<>();
        ArrayList<char[]> Z = new ArrayList<>();
        ArrayList<char[]> A = new ArrayList<>();
        int ct = 1;
        do{
            XSSFSheet tableSheet = workbook.createSheet("C table " + ct);
            Z.clear();
            A.clear();
            table.clear();
            for(int i = 0; i < c.size(); i++){
                table.add(new ArrayList<>());
                for(int j = 0; j < i; j++){
                    table.get(i).add(multiply(c.get(i), c.get(j), pc));
                }
            }
            Row row = tableSheet.createRow(c.size() + 1);
            row.createCell(0).setCellValue("A");
            for(int i = 0; i < table.size(); i++){
                for(int j = 0; j < table.get(i).size(); j++){
                    char[] element = table.get(i).get(j);
                    if(element[0] != emptySet){
                        if(ct == cubeType(element, pc)){
                            if(!contains(element, A)) A.add(table.get(i).get(j));
                            if(row.getCell(j+1) == null) row.createCell(j+1);
                            row.getCell(j+1).setCellValue(row.getCell(j+1).getStringCellValue() + "\n" + charArrayToString(element));
                        }
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
            int rowCount = 0;
            row = tableSheet.createRow(rowCount);
            row.createCell(0).setCellValue("");
            for(int i = 1; i <= c.size(); i++){
                row.createCell(i).setCellValue(charArrayToString(c.get(i - 1)));
            }

            for(int i = 0; i < c.size(); i++){
                row = tableSheet.createRow(i+1);
                row.createCell(0).setCellValue(charArrayToString(c.get(i)));
                for(int j = 0; j < table.get(i).size(); j++){
                    row.createCell(j+1).setCellValue(charArrayToString(table.get(i).get(j)));
                }
                row.createCell(table.get(i).size() + 1).setCellValue("-");
            }
            ArrayList<char[]> b = subtractSets(c, Z, pc);
            c = combineSets(A, b, pc);
            zs.addAll(Z);
            ct++;
        }while (!A.isEmpty());
        try (FileOutputStream outputStream = new FileOutputStream("Ctables.xlsx")) {
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zs;
    }

    private static int indexOf(char[] s, ArrayList<char[]> a){
        for(int i = 0; i < a.size(); i++){
            boolean ok = true;
            for(int j = 0; j < s.length; j++){
                if(a.get(i)[j] != s[j]){
                    ok = false;
                    break;
                }
            }
            if(ok) return i;
        }
        return -1;
    }
    public static ArrayList<char[]> findLExtremals(ArrayList<char[]> L, ArrayList<char[]> Z, int pc) throws Exception {
        ArrayList<char[]> subtrahend = new ArrayList<>();
        XSSFWorkbook xw = new XSSFWorkbook();
        sheet = xw.createSheet("Z subtract Z - Zi");
        //fission, extremal
        ArrayList<Pair<char[], char[]>> fissions = new ArrayList<>();
        for(int i = 0; i < Z.size(); i++){
            sheetJ = i+1;
            subtrahend.clear();
            subtrahend.addAll(Z);
            subtrahend.remove(i);
            ArrayList<char[]> fission = subtract(Z.get(i), subtrahend, pc, 0);
            for(char[] s : fission){
                fissions.add(new Pair<>(s, Z.get(i)));
            }
        }
        Row row = sheet.createRow(0);
        for(int i = 0; i < Z.size(); i++){
            row.createCell(i+1).setCellValue(charArrayToString(Z.get(i)));
        }
        for(int i = 0; i < Z.size(); i++){
            row = sheet.getRow(i+1);
            row.createCell(0).setCellValue(charArrayToString(Z.get(i)));
        }

        sheet = null;
        char[][][] table = new char[fissions.size()][L.size()][];

        for(int i = 0; i < fissions.size(); i++){
            for(int j = 0; j < L.size(); j++){
                table[i][j] = intersect(fissions.get(i).getKey(), L.get(j), pc);
            }
        }

        XSSFSheet oneMoreTable = xw.createSheet("IntersectTable");
        row = oneMoreTable.createRow(0);
        for(int i = 0; i < L.size(); i++){
            row.createCell(i+1).setCellValue(charArrayToString(L.get(i)));
        }
        for(int i = 0; i < fissions.size(); i++){
            row = oneMoreTable.createRow(i+1);
            row.createCell(0).setCellValue(charArrayToString(fissions.get(i).getKey()));
            for(int j = 0; j < L.size(); j++){
                row.createCell(j+1).setCellValue(charArrayToString(table[i][j]));
            }
        }
        ArrayList<char[]> E = new ArrayList<>();

        if(fissions.isEmpty()) {
            Random r = new Random();
            int index = Math.abs(r.nextInt()) % Z.size();
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

        ArrayList<char[]> fissionsLsE = new ArrayList<>();
        sheet = xw.createSheet("L#E");
        for(char[] l : L){
            ArrayList<char[]> fission = subtract(l, E, pc, 0);
            if(!fission.isEmpty()){
                fissionsLsE.addAll(fission);
            }
        }

        ArrayList<char[]> ZWithoutE = new ArrayList<>();
        for(char[] z : Z){
            if(!contains(z, E)){
                ZWithoutE.add(z);
            }
        }

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
        XSSFSheet oneMoreSheet = xw.createSheet("Z' intersect L1");
        row = oneMoreSheet.createRow(0);
        for(int i = 0; i < fissionsLsE.size(); i++){
            row.createCell(i+1).setCellValue(charArrayToString(fissionsLsE.get(i)));
        }
        for(int i = 0; i < ZWithoutE.size(); i++){
            row = oneMoreSheet.createRow(i+1);
            row.createCell(0).setCellValue(charArrayToString(ZWithoutE.get(i)));
            for(int j = 0; j < fissionsLsE.size(); j++){
                row.createCell(j+1).setCellValue(charArrayToString(tableZWEintLsE[i][j]));
            }
        }
        try (FileOutputStream outputStream = new FileOutputStream("Ztables.xlsx")) {
            xw.write(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return E;
    }

    public static void main(String[] args) throws Exception {
        ArrayList<char[]> L = new ArrayList<>();
        ArrayList<char[]> N = new ArrayList<>();
        L.add("001001".toCharArray());
        L.add("001010".toCharArray());
        L.add("001011".toCharArray());
        L.add("001101".toCharArray());
        L.add("010100".toCharArray());
        L.add("011001".toCharArray());
        L.add("011010".toCharArray());
        L.add("011011".toCharArray());
        L.add("011101".toCharArray());
        L.add("100100".toCharArray());
        L.add("101100".toCharArray());
        L.add("110100".toCharArray());
        L.add("111100".toCharArray());
        N.add("000110".toCharArray());
        N.add("000111".toCharArray());
        N.add("001110".toCharArray());
        N.add("001111".toCharArray());
        N.add("010110".toCharArray());
        N.add("010111".toCharArray());
        N.add("011110".toCharArray());
        N.add("011111".toCharArray());
        N.add("100000".toCharArray());
        N.add("100001".toCharArray());
        N.add("100010".toCharArray());
        N.add("100011".toCharArray());
        N.add("100101".toCharArray());
        N.add("100110".toCharArray());
        N.add("100111".toCharArray());
        N.add("101000".toCharArray());
        N.add("101001".toCharArray());
        N.add("101010".toCharArray());
        N.add("101011".toCharArray());
        N.add("101101".toCharArray());
        N.add("101110".toCharArray());
        N.add("101111".toCharArray());
        N.add("110000".toCharArray());
        N.add("110001".toCharArray());
        N.add("110010".toCharArray());
        N.add("110011".toCharArray());
        N.add("110101".toCharArray());
        N.add("110110".toCharArray());
        N.add("110111".toCharArray());
        N.add("111000".toCharArray());
        N.add("111001".toCharArray());
        N.add("111010".toCharArray());
        N.add("111011".toCharArray());
        N.add("111101".toCharArray());
        N.add("111110".toCharArray());
        N.add("111111".toCharArray());

        ArrayList<char[]> zs = findSimpleImplicants(6, L, N);
        ArrayList<char[]> r = findLExtremals(L, zs, 6);
        System.out.println();
        System.out.println();
        printArrayList(zs);
        System.out.println();
        System.out.println();
        printArrayList(r);
    }
}
