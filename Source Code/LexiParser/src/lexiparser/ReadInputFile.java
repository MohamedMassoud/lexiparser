/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiparser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;

/**
 *
 * @author ahmed
 */
public class ReadInputFile {

    ArrayList<String> input = new ArrayList<>();
    ArrayList<String> rule = new ArrayList<>();
    ArrayList<ArrayList<String>> grammer = new ArrayList<>();
    ArrayList<ArrayList<String>> grammer1 = new ArrayList<>();
    ArrayList<ArrayList<String>> LL1grammer = new ArrayList<>();
    String line, Replace, rest = "";
    int k = 0;

    public ArrayList<ArrayList<String>> getGrammer() {
        return LL1grammer;
    }

    public void read(String filename) throws FileNotFoundException, IOException {
        ArrayList<String> temp = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        line = br.readLine();

        while (line != null) {
            input.add(line);
            System.out.println(line);
            line = br.readLine();
        }
        for (int i = 0; i < input.size(); i++) {
            line = input.get(i);
            if (line.contains("#")) {
                rule.clear();
                String c[];
                Replace = line.replace("#", "");
                if (line.contains("'='")) {
                    Replace = Replace.replace("'='", "z");
                    c = Replace.split("=");
                    c[1] = c[1].replace("z", "'='");
                } else {
                    c = Replace.split("=");
                }

                rule.add(c[0].replace(" ", ""));
                if (c[1].contains("|")) {
                    Replace = c[1].replace("|", "%");
                    for (String r : Replace.split("%")) {
                        rule.add(r);
                    }
                } else {
                    rule.add(c[1]);
                }
                //System.out.println(rule);
                temp = (ArrayList<String>) rule.clone();
                grammer.add(temp);
                k++;
            } else if (line.contains("|")) {
                Replace = line.replace("|", "");
                rule.add(Replace.replace(" ", ""));
                //System.out.println(rule);
                temp = (ArrayList<String>) rule.clone();
                grammer.set(k - 1, temp);
            }
        }
    }

    public void CheckLL1() {
        int flag = 0;
        int k;
        boolean LL1 = true;
        boolean LR = false, LF = false;
        boolean ok = true;
        boolean ILR = CheckIndirectLeftRecursion();
        System.out.println("Indirect LR:" + ILR);
        for (int i = 0; i < grammer.size(); i++) {
            LR = CheckLeftRecursion(grammer.get(i));
            LF = CheckLeftFactoring(grammer.get(i));
            if (LR) {
                System.out.println("Rule #" + i + " has LR");
                FixLR(grammer.get(i));
                LL1 = false;
            } else if (LF) {
                System.out.println("Rule #" + i + " has LF");
                FixLF(grammer.get(i));
                LL1 = false;
            } else {
                LL1grammer.add(grammer.get(i));
            }
        }
        if (!LL1 || ILR) {
            JOptionPane.showMessageDialog(null, "The Input Grammer is not LL1");
            ok = false;
        } else {
            JOptionPane.showMessageDialog(null, "The Input Grammer is LL1");
        }

        System.out.println("------------------------------");
        for (int i = 0; i < LL1grammer.size(); i++) {
            System.out.println(LL1grammer.get(i));
        }
        if (!ok) {
            JOptionPane.showMessageDialog(null, "The Input Grammer has been changed to LL1");
        }
    }

    private boolean CheckLeftRecursion(ArrayList<String> rule) {
        String rule_name = rule.get(0);
        //System.out.println("Rule name:"+rule_name);
        boolean LR = false;
        String first[] = getFirst(rule);
        int k = 0;
        while (k < first.length) {
            if (first[k].equals(rule_name)) {
                LR = true;
                System.out.println("Left Recursion Detected");
                break;
            }
            k++;
        }
        /*
       for(int i = 1; i < rule.size(); i++){
           String prod[] = rule.get(i).split("\\s+|'");
           int k = 0;
           while(k < prod.length){
               //First Element found
                if(!prod[k].equals("\\s+") && !prod[k].equals("")){
                    //System.out.println("Prod:"+prod[k]);
                    if(prod[k].equals(rule_name)){
                        LR = true;
                        System.out.println("Left Recursion Detected");
                    }
                    break; 
                }
                k++;
           }
       }
         */
        return LR;
    }

    private boolean CheckIndirectLeftRecursion() {
        boolean LR = false;
        for (int i = 0; i < grammer.size(); i++) {
            boolean cond1 = false;
            boolean cond2 = false;
            String first1[] = getFirst(grammer.get(i));
            String name1 = grammer.get(i).get(0);
            for (int j = i + 1; j < grammer.size(); j++) {
                String first2[] = getFirst(grammer.get(j));
                String name2 = grammer.get(j).get(0);
                int k = 0;
                while (k < first1.length) {
                    //System.out.println("X1:"+first1[k]+" X2:"+name2);
                    if (first1[k].equals(name2)) {
                        cond1 = true;
                        break;
                    }
                    k++;
                }
                k = 0;
                while (k < first2.length) {
                    //System.out.println("Y1:"+first2[k]+" Y2:"+name1);
                    if (first2[k].equals(name1)) {
                        cond2 = true;
                        break;
                    }
                    k++;
                }
                if (cond1 && cond2) {
                    System.out.println("Indirect Left Recursion Detected");
                    LR = true;
                    change(name1, i, j);
                    break;
                }
            }

        }
        return LR;
    }

    private void change(String name1, int pos1, int pos2) {
        int i = 1;
        ArrayList<String> temp = new ArrayList<>();
        temp.add(grammer.get(pos2).get(0));
        while (i < grammer.get(pos2).size()) {
            if (grammer.get(pos2).get(i).contains(name1)) {
                int j = 1;
                while (j < grammer.get(pos1).size()) {
                    String Replace = grammer.get(pos2).get(i).replace(name1, grammer.get(pos1).get(j));
                    temp.add(Replace);
                    j++;
                }
            } else {
                temp.add(grammer.get(pos2).get(i));
            }
            i++;
        }
        grammer.set(pos2, temp);
    }

    private boolean CheckLeftFactoring(ArrayList<String> rule) {
        boolean LF = false;
        int x = 0;
        String first[] = getFirst(rule);
        for (int i = 0; i < first.length; i++) {
            //System.out.println("First:"+Arrays.toString(first));
            for (int j = i + 1; j < first.length; j++) {
                //System.out.println("X:"+first[i]+" Y:"+first[j]);
                if (first[i].equals(first[j])) {
                    LF = true;
                    System.out.println("Left Factoring Detected");
                    break;
                }
            }
        }
        return LF;
    }

    private String[] getFirst(ArrayList<String> rule) {
        String first[] = new String[rule.size() - 1];
        int x = 0;
        for (int i = 1; i < rule.size(); i++) {
            String prod[] = rule.get(i).split("\\s+|'");
            int k = 0;
            while (k < prod.length) {
                if (!prod[k].equals("\\s+") && !prod[k].equals("")) {
                    first[x] = prod[k];
                    x++;
                    break;
                }
                k++;
            }
        }
        return first;
    }

    private void FixLR(ArrayList<String> rule) {
        //System.out.println(rule);
        ArrayList<String> rule1 = new ArrayList<>();
        ArrayList<String> rule2 = new ArrayList<>();
        String[] prod1 = new String[rule.size()];
        String[] prod2 = new String[rule.size()];
        rule1.add(rule.get(0));
        rule2.add(rule.get(0) + "`");
        int i = 1;
        int j = 0;
        int k = 0;
        while (i < rule.size()) {
            if (!rule.get(i).contains(rule.get(0))) {
                prod1[j] = rule.get(i);
                j++;
            } else {
                prod2[k] = rule.get(i);
                k++;
            }
            i++;
        }
        j = 0;
        while (prod1[j] != null) {
            rule1.add(prod1[j] + " " + rule2.get(0));
            j++;
        }
        k = 0;
        while (prod2[k] != null) {
            String newProd = prod2[k].replace(rule1.get(0), "");
            rule2.add(newProd + " " + rule2.get(0));
            k++;
        }
        String E = "\\L";
        rule2.add(E);
        System.out.println(rule1);
        System.out.println(rule2);
        LL1grammer.add(rule1);
        LL1grammer.add(rule2);
    }

    private void FixLF(ArrayList<String> rule) {
        System.out.println(rule);
        ArrayList<String> rule1 = new ArrayList<>();
        ArrayList<ArrayList<String>> rules = new ArrayList<>();
        String[] first = getFirst(rule);
        String[] prod1 = new String[rule.size()];
        int k = 0;
        for (int i = 0; i < first.length; i++) {
            for (int j = i + 1; j < first.length; j++) {
                if (first[i].equals(first[j])) {
                    prod1[k] = first[i];
                    System.out.println("p:" + prod1[k]);
                    k++;
                }
            }
        }
        rule1.add(rule.get(0));
        LL1grammer.add(rule1);
        LL1grammer.add(rule);
        int i = 0;
        while (prod1[i] != null) {
            String name = prod1[i] + "~";
            rule1.add(prod1[i] + " " + name);
            ArrayList<String> temp = new ArrayList<>();
            temp.add(name);
            for (int j = 1; j < rule.size(); j++) {
                if (rule.get(j).contains(prod1[i])) {
                    String Replace = rule.get(j).replaceFirst(prod1[i], "");
                    if (Replace.matches("\\s+") || Replace.equals("") || Replace.equals("\t")) {
                        temp.add("\\L");
                    } else {
                        temp.add(Replace);
                    }
                } else {
                    temp.add(rule.get(i));
                }
            }
            System.out.println(rule1);
            System.out.println(temp);
            LL1grammer.set(LL1grammer.size() - 1, temp);
            i++;
        }
    }
}
