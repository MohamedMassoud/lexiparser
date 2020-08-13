/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiparser;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Crap
 */
public class Parser {
    
    private ArrayList<ArrayList<String>> parseTable;
    private ArrayList<String> output = new ArrayList<>();
    private ArrayList<String> input = new ArrayList<>();
    private Stack<String> stack = new Stack<>();
    private ArrayList<String> errors = new ArrayList<>();
    private static ArrayList<String> terminals = new ArrayList<>();
    private ArrayList<String> nonTerminals = new ArrayList<>();
    private boolean success = false;
    private ArrayList<String> lmd = new ArrayList<>();
    private JungTest j = new JungTest();
    private int edgeCount = 0;
    private ArrayList<String> check = new ArrayList<>();
    
    public static ArrayList<String> getTerminals() {
        return terminals;
    }

    public Parser(ArrayList<ArrayList<String>> parseTable, ArrayList<String> input) {
        this.parseTable = parseTable;
        this.input = input;
        input.add("$");
        fillTerminals();
        fillNonTerminals();
        generateOutput();
        System.out.println("Output: " + output);
        System.out.println("Errors: " + errors);
        
        viewErrors();
        
        try {
            LMD();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unknown Error, Recovery failed!");
            System.exit(0);
            
        }
        //LMD lmd = new LMD(output, terminals, nonTerminals);

        for (String str : lmd) {
            System.out.println(str);
        }
        viewLMD();
        j.visualize();
        
    }
    
    public void viewErrors() {
        JFrame f = new JFrame("Recovered Errors");        
        TextArea area = new TextArea();        
        area.setBounds(10, 30, 1500, 1000);        
        f.add(area);
        f.setSize(512, 512);        
        f.setLayout(null);        
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        for (String str : errors) {
            
            area.append(str + "\n");
            
        }
    }
    
    public void viewLMD() {
        JFrame f = new JFrame("Left most derivation");        
        TextArea area = new TextArea();        
        area.setBounds(10, 30, 1500, 1000);        
        f.add(area);
        f.setSize(1500, 1000);        
        f.setLayout(null);        
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        for (String str : lmd) {
            
            area.append(str + "\n");
            
        }
    }
    
    public ArrayList<String> getOutput() {
        return output;
    }
    
    public void generateOutput() {
        
        stack.add("$");
        stack.add(parseTable.get(1).get(0));
        while (true) {
            System.out.println("Stack: " + stack + " Input: " + input);
            if (stack.peek().equals("$") && input.get(0).equals("$")) {
                System.out.println("Parsing finished successfully !");
                JOptionPane.showMessageDialog(null, "Parsing finished successfully !");
                
                success = true;
                break;
            } else if ((!stack.peek().equals("$") && input.get(0).equals("$") || stack.peek().equals("$") && !input.get(0).equals("$")) && terminals.contains(stack.peek())) {
                System.out.println("Parsing Failed: Stack.peek= " + stack.peek() + ". Last input: " + input.get(0));
                System.out.println("erred STACK: " + stack);
                JFrame f = new JFrame();
                JOptionPane.showMessageDialog(f, "[Syntax Error]: expected \"" + stack.peek() + "\".");
                JOptionPane.showMessageDialog(f, "Parsing Failed: Stack.peek= " + stack.peek() + ". Last input: " + input.get(0));
                System.exit(0);
            } else {
                if (terminals.contains(stack.peek())) {
                    if (stack.peek().equals(input.get(0))) {
                        stack.pop();
                        input.remove(0);
                    } else {
                        String err = stack.pop();
                        errors.add("Error: [Missing]: \'" + err + "\' , inserted");
                        System.out.println("Error: missing " + err + ", inserted");
                        
                    }
                    
                } else if (nonTerminals.contains(stack.peek())) {
                    
                    String value = getFromTable(stack.peek(), input.get(0));
                    // System.out.println("VAAAAAALUEEEEEEEEEEEEE: " + value);
                    if (value == null) {
                        
                        errors.add("Error: [Illegal]: \'" + stack.peek() + "\' - discard " + input.get(0));
                        
                        input.remove(0);
                        
                    } else if (value.equals("sync")) {
                        
                        errors.add("Error: [Illegal: \'" + stack.peek() + "\'] - Skipped " + input.get(0));
                        input.remove(0);
                        stack.pop();
                        
                    } else {
                        String[] tempTokens = value.replace("\\L", "").replaceFirst("[^=]*=\\s+", "").split(" ");
                        //System.out.println(tempTokens);
                        stack.pop();
                        for (int i = tempTokens.length - 1; i >= 0; i--) {
                            if (!tempTokens[i].equals("")) {
                                stack.add(tempTokens[i]);
                            }
                            
                        }
                        output.add(value);
                    }
                }
                
            }
            
        }
    }
    
    public void fillNonTerminals() {
        for (ArrayList<String> arr : parseTable.subList(1, parseTable.size())) {
            nonTerminals.add(arr.get(0));
        }
    }
    
    public void fillTerminals() {
        /*terminals.add("id");
        terminals.add(";");
        terminals.add("int");
        terminals.add("float");
        terminals.add("if");
        terminals.add("(");
        terminals.add(")");
        terminals.add("{");
        terminals.add("}");
        terminals.add("else");
        terminals.add("=");
        terminals.add("relop");
        terminals.add("addop");
        terminals.add("mulop");
        terminals.add("while");
        terminals.add("+");
        terminals.add("-");
        terminals.add("num");*/
        
        terminals = First_Follow.getTerminals();
    }
    
    private String getFromTable(String nonTerminal, String terminal) {
        //System.out.println("Terminal: " + terminal + " NonTerminal: " + nonTerminal);
        int i, j;
        
        for (i = 0; i < parseTable.size(); i++) {
            if (parseTable.get(i).get(0) != null) {
                if (parseTable.get(i).get(0).equals(nonTerminal)) {
                    break;
                }
            }
        }
        
        for (j = 0; j < parseTable.get(0).size(); j++) {
            if (parseTable.get(0).get(j) != null) {
                if (parseTable.get(0).get(j).equals(terminal)) {
                    break;
                }
            }
            
        }
        
        if (parseTable.size() == i || parseTable.get(0).size() == j) {
            return null;
        }
        return parseTable.get(i).get(j);
    }
    
    public void clean(ArrayList<String> output) {
        for (int i = 0; i < output.size(); i++) {
            String[] temp = output.get(i).split("\\s+");
            String f = String.join(" ", temp);
            output.set(i, f);
        }
        
    }
    
    public void LMD() {
        
        clean(output);
        
        String s1 = output.get(0);
        String[] s3 = s1.split(" ");
        lmd.add(s3[0]);
        String prev;
        j.addVertex(s3[0]);
        prev = s3[0];
        String q = "";
        for (int p = 2; p < s3.length; p++) {
            q = q + s3[p];
        }
        lmd.add(q);
        j.addVertex(q);
        j.addEdge(String.valueOf(edgeCount), prev, q);
        prev = q;
        edgeCount++;
        
        System.out.println(Arrays.toString(s3));
        for (int i = 1; i < output.size(); i++) {
            // if(output.get(i) == "="){
            //s3 = s3.replaceAll(" ","");
            // System.out.println("Output is " + output.get(i));
            String[] s4 = output.get(i).split("=", 2);
            // System.out.println("Non terminal is : " + s4[0]);
            s4[0] = s4[0].replace(" ", "");
            for (int x = 2; x < s3.length; x++) {
                // System.out.println("S3[x] = " + s3[x]);
                // System.out.println("S4[0] = " + s4[0]);
                if (s3[x].contains(s4[0])) {
                    
                    prev = s4[0];
                    //System.out.println("check: " + check + " prev: " + prev);
                    while (check.contains(prev)) {
                        //System.out.println("????????????????????????");
                        prev = prev + " ";
                    }
                    //System.out.println("PREEEEV: " + prev);
                    //  System.out.println("TRUE");
                    //System.out.println("S3   "+ Arrays.toString(s3) );
                    
                    String md = getModified(s3[x], s4[0], s4[1]);
                    //System.out.println("S4[1]: " + s4[1]);
                    String[] haha = s4[1].split(" ");
                    
                    for (String idk : haha) {
                        if (!idk.equals("\\L") && !idk.equals("") && !idk.matches("\\s+")) {
                            //System.out.println("ASDASDASD: " + idk);
                            while (j.getGraph().containsVertex(idk)) {
                                check.add(idk);
                                idk = idk + " ";
                                
                            }
                            j.addVertex(idk);
                            //System.out.println("From: " + "-" + prev + "-" + " To: " + "-" + idk + "-");
                            j.addEdge(String.valueOf(edgeCount), prev, idk);
                            
                            edgeCount++;
                            
                        }
                        
                    }
                    
                    s3[x] = md;
                    //s3[x] = s3[x].replace(s4[0], s4[1]);
                    //  System.out.println("S3   "+ s3[x] );
                    //s3[x] = s3[x].replaceFirst(" ", "");

                    String[] temp = s3[x].split("\\s+");
                    //  System.out.println("Temp : " + Arrays.toString(temp));
                    if (temp.length >= 2) {
                        //    System.out.println("True");
                    }
                    
                    String temp2 = Arrays.toString(temp);
                }
                // char[] s5 = s3[x].toCharArray();
                //for (char output : s5) {
                //System.out.println(output);
                //}
                
                String j = "";
                
                for (int body = 2; body < s3.length; body++) {
                    j = j + s3[body];
                }
                
                j = j.replace("\\L", "").replaceAll("\\s+", " ");
                lmd.add(j);
                //  j = j.replace("\\s+", " ");
            }
        }
    }
    
    public String getModified(String str, String key, String value) {
        
        if (value.equals("\\L")) {
            return str;
        }
        
        String[] arr = str.split("\\s+");
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(key)) {
                arr[i] = value;
            }
        }
        String temp = Arrays.toString(arr).replace("[", "").replace("]", "").trim().replace(",", "");
        //System.out.println("Mod: " + temp);
        return temp;
    }
    
}
