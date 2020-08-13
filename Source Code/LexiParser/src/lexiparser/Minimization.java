/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiparser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Ibrahim Moursy
 */
public class Minimization {

    private DFA dfa;
    private LexGraph dfaGraph;
    private ArrayList<ArrayList<String>> dfaTransitionTable = new ArrayList<>();
    private ArrayList<ArrayList<String>> minimizedTable = new ArrayList<>();
    private HashMap<String, String> acceptanceStates = new HashMap();
    private ArrayList<ArrayList<String>> test1 = new ArrayList<>();
    private ArrayList<ArrayList<String>> test2 = new ArrayList<>();
    private ArrayList<String> finalStates = new ArrayList<>();

    public Minimization(DFA dfa, LexGraph graph) {
        this.dfa = dfa;
        dfaTransitionTable = dfa.getTransitionTable();
        acceptanceStates = dfa.getAcceptanceStates();
        dfaGraph = dfa.getGraph();
        finalStates = dfa.getPriorities();
        System.out.println("------------------------------------");
        System.out.println("Minimization");
        System.out.println("Acceptance States: " + acceptanceStates);
        System.out.println("TransitionTable: " + dfaTransitionTable);
        //dfaGraph.printMatrix();
        initialiseList();
        System.out.println("before: " + test1);
        iteration(test1);
        recursion();
        System.out.println("after: " + test2);
        fillTable();
        System.out.println("Minimized: " + minimizedTable);
        System.out.println("Acceptance States before min: " + acceptanceStates);
        removeFromHash();
        System.out.println("Acceptance States after min: " + acceptanceStates);
        graph.addAllToGraph(minimizedTable);
    }

    private void initialiseList() {
        ArrayList<String> nonTransition = new ArrayList<>();
        ArrayList<String> transition = new ArrayList<>();
        ArrayList<ArrayList<String>> allFinal = new ArrayList<>();
        ArrayList<String> temp = new ArrayList<>();
        for (int i = 1; i < dfaTransitionTable.size(); i++) {
            if (!acceptanceStates.containsKey(dfaTransitionTable.get(i).get(0))) {
                //System.out.println("Stats That are no final:" + dfaTransitionTable.get(i).get(0));
                nonTransition.add(dfaTransitionTable.get(i).get(0));
            } else {
                transition.add(dfaTransitionTable.get(i).get(0));
            }
        }
        test1.add(nonTransition);
        for (int i = 0; i < finalStates.size(); i++) {
            temp = new ArrayList<>();
            for (int j = 0; j < transition.size(); j++) {
                if (finalStates.get(i).equals(acceptanceStates.get(transition.get(j)))) {
                    temp.add(transition.get(j));
                }
            }
            allFinal.add(temp);
        }
        test1.addAll(allFinal);
        //System.out.println("test1: " + test1);  
    }

    private void iteration(ArrayList<ArrayList<String>> test1) {

        for (int i = 0; i < test1.size(); i++) {
            ArrayList<String> temp = new ArrayList<>();
            ArrayList<ArrayList<String>> unDone = new ArrayList<>();
            for (int j = 0; j < test1.get(i).size(); j++) {

                if (j == 0) {
                    temp.add(test1.get(i).get(j));
                    unDone.add(temp);
                } else {
                    //System.out.println(test1.get(i).get(j))
                    //System.out.println("tempppppppppppppppppppppp"+ temp);
                    boolean flag = true;
                    //System.out.println("state Compairing: "+test1.get(i).get(j));
                    for (int l = 0; l < unDone.size(); l++) {
                        flag = true;
                        temp = new ArrayList<>();
                        temp = unDone.get(l);
                        // System.out.println("temp in" + temp);
                        // System.out.println("pppppppppppppppppppp "+l+unDone);
                        for (int k = 1; k < dfaTransitionTable.get(0).size(); k++) {
                            String x = dfaGraph.getNextState(test1.get(i).get(j), dfaTransitionTable.get(0).get(k));
                            //System.out.println("x= " + x);
                            String y = dfaGraph.getNextState(unDone.get(l).get(0), dfaTransitionTable.get(0).get(k));
                            // System.out.println("y= " + y);
                            if (!isSameGroup(test1, x, y)) {
                                flag = false;
                                break;
                            }

                        }
                        if (flag) {
                            temp.add(test1.get(i).get(j));
                            unDone.set(l, temp);
                            break;
                        }

                    }
                    if (!flag) {
                        ArrayList<String> temp2 = new ArrayList<>();
                        temp2.add(test1.get(i).get(j));
                        unDone.add(temp2);
                    }

                    //System.out.println("value after end of undone loop: " + unDone);
                }

            }
            test2.addAll(unDone);
            // System.out.println("test22222222222222222222222222" + test2);
        }
        /* System.out.println(test2);
            System.out.println(unDone);
            System.out.println(!isEmpt(unDone));*/

 /*if(!isEmpt(unDone)){
                iteration(unDone);
           }*/
        //System.out.println(test2);
    }

    private void recursion() {
        while (!test1.equals(test2)) {

            test1.clear();
            test1.addAll(test2);
            //System.out.println("test1 in Recursion" + test1);
            test2.clear();
            iteration(test1);
            //System.out.println("test2 in Recursion" + test2);
        }
        // System.out.println(test2);

    }

    private void fillTable() {
        //minimizedTable.addAll(dfaTransitionTable);
        minimizedTable.add(dfaTransitionTable.get(0));
        for (int i = 0; i < dfaTransitionTable.size(); i++) {
            for (int j = 0; j < test2.size(); j++) {
                if (dfaTransitionTable.get(i).get(0).equals(test2.get(j).get(0))) {
                    ArrayList<String> temp = new ArrayList<>();
                    temp = removeEqualStates(dfaTransitionTable.get(i), test2);
                    minimizedTable.add(temp);
                }

            }
        }
    }

    private ArrayList<String> removeEqualStates(ArrayList<String> before, ArrayList<ArrayList<String>> equalStates) {
        ArrayList<String> after = new ArrayList<>();
        // System.out.println(before.get(0));
        for (int i = 0; i < equalStates.size(); i++) {
            for (int j = 1; j < equalStates.get(i).size(); j++) {
                for (int k = 0; k < before.size(); k++) {
                    if (before.get(k).equals(equalStates.get(i).get(j))) {
                        //System.out.println("replace= " + replace);
                        //System.out.println("replaced" + before.get(k) + "by" + equalStates.get(i).get(0) );
                        before.set(k, equalStates.get(i).get(0));
                    }
                }
            }
        }
        after.addAll(before);

        //System.out.println("remove return : " + after);
        return after;
    }

    private void removeFromHash() {
        for (int i = 0; i < test2.size(); i++) {
            for (int j = 1; j < test2.get(i).size(); j++) {
                acceptanceStates.remove(test2.get(i).get(j));
            }
        }

    }

    private boolean isSameGroup(ArrayList<ArrayList<String>> group, String s1, String s2) {
        int index = -1;
        int index2 = -2;
        for (int i = 0; i < group.size(); i++) {
            boolean x = group.get(i).contains(s1);
            boolean y = group.get(i).contains(s2);
            if (x) {
                index = i;
            }
            if (y) {
                index2 = i;
            }

        }
        if (index == index2) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isEmpt(ArrayList<ArrayList<String>> x) {
        for (int i = 0; i < x.size(); i++) {
            if (x.get(i).size() != 0) {
                return false;
            }

        }
        return true;
    }

    public ArrayList<ArrayList<String>> getMinimizedTable() {
        return minimizedTable;
    }

    public HashMap<String, String> getAcceptanceStates() {
        return acceptanceStates;
    }

}
