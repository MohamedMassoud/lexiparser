/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiparser;

import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Ibrahim Moursy
 */
public class FillTable {

    private ArrayList<ArrayList<String>> first = new ArrayList<>();
    private ArrayList<ArrayList<String>> follow = new ArrayList<>();
    private ArrayList<ArrayList<String>> grammer = new ArrayList<>();
    private ParGraph graph = new ParGraph();
    private String nonTerminal = "";
    private String terminal = "";

    public FillTable(ArrayList<ArrayList<String>> first, ArrayList<ArrayList<String>> follow, ArrayList<ArrayList<String>> grammer, ParGraph graph) {
        this.first = first;
        this.follow = follow;
        this.graph = graph;
        this.grammer = grammer;
        System.out.println("------------FillTable--------------");
        fillFirst();
        fillFollow();
        // System.out.println(grammer);
    }

    public void fillFirst() {
        for (int i = 0; i < first.size(); i++) {
            graph.createState(first.get(i).get(0));
            int flag = 0;
            for (int j = 1; j < first.get(i).size(); j++) {
                if (first.get(i).get(j).equals("\\L")) {
                    continue;
                } else if (isTerminal(first.get(i).get(0), first.get(i).get(j)) && flag == 0) {
                    graph.connect(first.get(i).get(0), first.get(i).get(0), first.get(i).get(j));
                    graph.connect(first.get(i).get(0), "=", first.get(i).get(j));
                    //System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz" + terminal);
                    graph.connect(first.get(i).get(0), terminal, first.get(i).get(j));
                } else if (isTerminal(first.get(i).get(0), first.get(i).get(j)) && flag == 1) {
                    graph.connect(first.get(i).get(0), first.get(i).get(0), first.get(i).get(j));
                    graph.connect(first.get(i).get(0), "=", first.get(i).get(j));
                    graph.connect(first.get(i).get(0), nonTerminal, first.get(i).get(j));
                } else {
                    flag = 1;
                }
            }
        }
    }

    public boolean isTerminal(String g, String s) {
        String empty = "";
        String newStr = "";
        if (s.charAt(0) == '$' && s.charAt(s.length() - 1) == '$') {
            newStr = s.replace("$", empty);
            //System.out.println("new String = " + newStr);
            for (int i = 0; i < grammer.size(); i++) {
                if (g.equals(grammer.get(i).get(0))) {
                    for (int j = 1; j < grammer.get(i).size(); j++) {
                        if (grammer.get(i).get(j).contains(newStr)) {
                            //System.out.println("nonTerminal = " + grammer.get(i).get(j));
                            nonTerminal = grammer.get(i).get(j).replace("'", " ");
                            System.out.println("nonTerminal = " + nonTerminal);
                            return false;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < grammer.size(); i++) {
            if (g.equals(grammer.get(i).get(0))) {
                for (int j = 1; j < grammer.get(i).size(); j++) {
                    if (grammer.get(i).get(j).contains(s)) {
                        //System.out.println("Terminal = " + grammer.get(i).get(j));
                        terminal = grammer.get(i).get(j).replace("'", " ");
                        System.out.println("Terminal = " + terminal);
                    }
                }
            }
        }
        return true;
    }

    public void fillFollow() {
        for (int i = 0; i < follow.size(); i++) {
            for (int j = 1; j < follow.get(i).size(); j++) {
                String get = graph.getNextState(first.get(i).get(0), follow.get(i).get(j));
                if (get == " " || get == null) {
                    if (checkEpsolin(first.get(i))) {
                        graph.connect(follow.get(i).get(0), first.get(i).get(0), follow.get(i).get(j));
                        graph.connect(follow.get(i).get(0), "=", follow.get(i).get(j));
                        graph.connect(first.get(i).get(0), "\\L", follow.get(i).get(j));
                    } else {
                        graph.connect(follow.get(i).get(0), "sync", follow.get(i).get(j));
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "[Ambiguity Error]: at \'" + get + "\'");

                    System.out.println("Ambiguity Error");
                    continue;
                }
            }
        }
    }

    public boolean checkEpsolin(ArrayList<String> a) {
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).equals("\\L")) {
                return true;
            }
        }
        return false;
    }

}
