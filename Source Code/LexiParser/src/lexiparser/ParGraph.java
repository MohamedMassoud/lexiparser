/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiparser;

import java.util.ArrayList;

/**
 *
 * @author Crap
 */
public class ParGraph {

    private ArrayList<ArrayList<String>> transitionTable = new ArrayList<>();
    private int defaultSize = 1;

    public ParGraph() {
        ArrayList<String> row0 = new ArrayList<>();
        row0.add(0, " ");
        transitionTable.add(0, row0);
    }

    public void createState(String stateName) {

        ArrayList<String> newState = new ArrayList<>();
        newState.add(stateName);
        for (int i = 0; i < defaultSize - 1; i++) {
            newState.add(" ");
        }
        transitionTable.add(newState);

    }

    public void connect(String stateName1, String stateName2, String input) {
        boolean found = false;
        int i;
        for (i = 1; i < transitionTable.size(); i++) {
            if (stateName1.equals(transitionTable.get(i).get(0))) {
                break;
            }
        }
        int j = 1;
        for (j = 1; j < transitionTable.get(0).size(); j++) {

            if (input.equals(transitionTable.get(0).get(j))) {
                //System.out.println(transitionTable);
                ArrayList<String> arr = transitionTable.get(i);
                if (arr.get(j) == " ") {
                    arr.set(j, stateName2);
                } else {
                    arr.set(j, arr.get(j) + " " + stateName2);
                }

                transitionTable.set(i, arr);
                found = true;
            }

        }

        if (!found) {
            ArrayList<String> arr = transitionTable.get(0);
            arr.add(input);
            defaultSize++;
            for (int x = 1; x < transitionTable.size(); x++) {
                ArrayList<String> temp = transitionTable.get(x);
                temp.add(" ");
                transitionTable.set(x, temp);
            }
            transitionTable.set(0, arr);
            arr = transitionTable.get(i);
            arr.set(j, stateName2);
            transitionTable.set(i, arr);
        }
    }

    public ArrayList<ArrayList<String>> getTransitionTable() {
        return transitionTable;
    }

    public String getNextState(String currentState, String input) {
        int row = 0;
        int column = 0;
        int flag1 = 0;
        int flag2 = 0;

        for (int i = 0; i < transitionTable.size(); i++) {
            if (transitionTable.get(i).get(0).equals(currentState)) {
                row = i;
                flag1 = 1;
                break;
            }
        }

        for (int i = 0; i < transitionTable.get(0).size(); i++) {
            if (transitionTable.get(0).get(i).equals(input)) {
                column = i;
                flag2 = 1;
                break;
            }
        }
        if (flag1 == 1 && flag2 == 1) {
            return transitionTable.get(row).get(column);
        } else {
            return null;
        }
    }

    public void addAllToGraph(ArrayList<ArrayList<String>> x) {
        transitionTable = new ArrayList<>();
        transitionTable.addAll(x);
    }

    public void printMatrix() {
        for (ArrayList<String> arr : transitionTable) {
            System.out.println(arr);
        }
    }

}
