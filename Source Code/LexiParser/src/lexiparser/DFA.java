/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author Crap
 */
public class DFA {

    private ArrayList<ArrayList<String>> nfaTransitionTable = new ArrayList<>();
    private LexGraph graph;
    private LexGraph nfaGraph;
    private NFA nfa;
    private int stateNum = 0;
    private HashMap<String, String> stateTranslationTable = new HashMap();
    private ArrayList<ArrayList<String>> arrayOfTotalNextStates = new ArrayList<>();
    private ArrayList<String> noTransitions = new ArrayList<>();
    private ArrayList<String> noTransitionsInput = new ArrayList<>();
    private ArrayList<String> rangeOperands = new ArrayList<>();
    private HashMap<String, String> nfaAcceptanceStates = new HashMap();
    private HashMap<String, String> acceptanceStates = new HashMap();
    private ArrayList<String> priorities = new ArrayList<>();

    public DFA(NFA nfa, LexGraph graph) {
        this.nfa = nfa;
        nfaTransitionTable = nfa.getTransitionTable();
        this.graph = graph;
        nfaGraph = nfa.getGraph();
        nfaAcceptanceStates = nfa.getAcceptanceStates();
        priorities = nfa.getPriorities();
        System.out.println("ASDASDASDASD: " + priorities);
        findRanges();
        generateDFA();
        addNullState();
        System.out.println("Hash Table: " + stateTranslationTable);
        System.out.println("DFA acceptanceStates: " + acceptanceStates);
    }

    public void generateDFA() {
        /* for(int i=1; i<nfaTransitionTable.size();i++){
            
        }*/
        ArrayList<String> list = new ArrayList<String>();
        stateNum++;
        String stateName = String.valueOf(stateNum);
        graph.createState(stateName);
        E_Closure(list, "1");
        Collections.sort(list);
        stateTranslationTable.put(String.join(",", list), stateName);

        arrayOfTotalNextStates.add(list);
        //System.out.println("First state: "+ list);
        //ArrayList<String> nextStates = getTotalNextStates(list, "o");
        boolean foundRange = false;
        for (int i = 0; i < arrayOfTotalNextStates.size(); i++) {

            for (int j = 1; j < nfaTransitionTable.get(0).size(); j++) {
                ArrayList<String> totalNextStates = new ArrayList<>();
                if (nfaTransitionTable.get(0).get(j).equals("\\L")) {
                    continue;
                }
                String str = matches(nfaTransitionTable.get(0).get(j));

                boolean alreadyExists = false;
                // System.out.println("AAAAAAAAAA: " + arrayOfTotalNextStates);

                totalNextStates = getTotalNextStates(arrayOfTotalNextStates.get(i), nfaTransitionTable.get(0).get(j));

                //System.out.println("Current States: " + arrayOfTotalNextStates.get(i) +" Input: "  + nfaTransitionTable.get(0).get(j) + " Values: " + totalNextStates);
                if (str != null) {
                    ArrayList<String> temp = getTotalNextStates(arrayOfTotalNextStates.get(i), str);
                    totalNextStates.addAll(temp);
                }

                if (totalNextStates.isEmpty()) {
                    ArrayList<String> arrlst = arrayOfTotalNextStates.get(i);
                    Collections.sort(arrlst);
                    noTransitions.add(stateTranslationTable.get(String.join(",", arrlst)));

                    noTransitionsInput.add(nfaTransitionTable.get(0).get(j));
                } else {
                    for (ArrayList<String> arr : arrayOfTotalNextStates) {
                        if (isDuplicate(arr, totalNextStates)) {
                            alreadyExists = true;

                        }
                    }
                    if (alreadyExists) {

                        String targetState = stateTranslationTable.get(String.join(",", totalNextStates));
                        graph.connect(stateTranslationTable.get(String.join(",", arrayOfTotalNextStates.get(i))), targetState, nfaTransitionTable.get(0).get(j));
                        alreadyExists = false;
                    } else {

                        stateNum++;
                        stateName = String.valueOf(stateNum);
                        graph.createState(stateName);
                        fillAcceptanceStates(stateName, totalNextStates);
                        //System.out.println(acceptanceStates);

                        graph.connect(stateTranslationTable.get(String.join(",", arrayOfTotalNextStates.get(i))), stateName, nfaTransitionTable.get(0).get(j));

                        Collections.sort(totalNextStates);
                        stateTranslationTable.put(String.join(",", totalNextStates), stateName);
                        arrayOfTotalNextStates.add(totalNextStates);
                    }

                }
            }
        }

        /*for(int i=2; i<nfaTransitionTable.size(); i++){
           E_Closure(list,nfaTransitionTable.get(i).get(0));
       }*/
        //E_Closure(list, "68");
        //System.out.println("DFA: " + list);
    }

    public void E_Closure(ArrayList<String> eClosureTransitions, String state) {
        if (!eClosureTransitions.contains(state)) {
            eClosureTransitions.add(state);
        }
        for (int i = 0; i < nfaTransitionTable.size(); i++) {
            if (nfaTransitionTable.get(i).get(0).equals(state)) {
                //System.out.println("state: " + state );
                for (int j = 0; j < nfaTransitionTable.get(i).size(); j++) {
                    if ((!nfaTransitionTable.get(i).get(j).equals(" ")) && nfaTransitionTable.get(0).get(j).equals("\\L")) {
                        //System.out.println("Found: " + nfaTransitionTable.get(0).get(j));
                        String[] inputs = nfaTransitionTable.get(i).get(j).split(",");
                        for (String nextState : inputs) {
                            E_Closure(eClosureTransitions, nextState);
                        }
                    }
                }
            }
        }
    }

    public boolean isDuplicate(ArrayList<String> list1, ArrayList<String> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }

        Collections.sort(list1);
        Collections.sort(list2);

        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }

        return true;
    }

    public ArrayList<String> getTotalNextStates(ArrayList<String> currentStates, String input) {
        //System.out.println("INPUTS " + inputs);
        // System.out.println("BAAAAAAAAAAAAAA: " + currentStates);
        ArrayList<String> totalNextStates = new ArrayList<>();

        //System.out.println("Input: " + inputs.get(i));
        for (int j = 0; j < currentStates.size(); j++) {
            if (!nfaGraph.getNextState(currentStates.get(j), input).equals(" ")) {
                //System.out.println("State: " + currentStates.get(j) + " Input: " + input + " Next State: " + nfaGraph.getNextState(currentStates.get(j), input));
                String[] states = nfaGraph.getNextState(currentStates.get(j), input).split(",");
                //System.out.println("STATES: " + nfaGraph.getNextState(currentStates.get(j), inputs.get(i)));
                for (int k = 0; k < states.length; k++) {
                    E_Closure(totalNextStates, states[k]);
                }
            }
        }

        return totalNextStates;
    }

    public void addNullState() {

        System.out.println(noTransitions);
        System.out.println(noTransitionsInput);
        stateNum++;
        String stateName = String.valueOf(stateNum);
        graph.createState(stateName);

        for (int i = 0; i < noTransitions.size(); i++) {
            graph.connect(noTransitions.get(i), stateName, noTransitionsInput.get(i));
        }

        for (int i = 1; i < nfaTransitionTable.get(0).size(); i++) {
            if (nfaTransitionTable.get(0).get(i).equals("\\L")) {
                continue;
            }
            graph.connect(stateName, stateName, nfaTransitionTable.get(0).get(i));
        }
    }

    public void findRanges() {

        for (int i = 1; i < nfaTransitionTable.get(0).size(); i++) {
            if (nfaTransitionTable.get(0).get(i).matches("[a-zA-Z0-9]-[a-zA-Z0-9]")) {
                rangeOperands.add(nfaTransitionTable.get(0).get(i));
            }
        }

    }

    public String matches(String input) {
        // System.out.println("asdasd"+rangeOperands);
        for (int i = 0; i < rangeOperands.size(); i++) {
            String[] arr = rangeOperands.get(i).split("-");
            if (rangeOperands.get(i).equals(input)) {
                //System.out.println("found: "+ input);
            } else if (arr[0].compareTo(input) <= 0 && arr[1].compareTo(input) >= 0) {
                // System.out.println("Input: " + input + " : " + rangeOperands.get(i));
                return rangeOperands.get(i);
            }
        }

        return null;
    }

    public void fillAcceptanceStates(String stateName, ArrayList<String> states) {

        boolean isAcceptance = false;
        ArrayList<String> arr = new ArrayList<String>();
        ////ystem.out.println("States: " + states);
        //System.out.println("nfaAcceptanceStates: " + nfaAcceptanceStates);
        for (String str : states) {
            for (String str2 : nfaAcceptanceStates.keySet()) {
                if (str.equals(str2)) {
                    isAcceptance = true;
                    arr.add(nfaAcceptanceStates.get(str));
                }

            }
        }
        System.out.println("arr: " + arr);

        int priValue = Integer.MAX_VALUE;
        String priority = null;
        if (isAcceptance) {
            for (int i = 0; i < priorities.size(); i++) {
                for (int j = 0; j < arr.size(); j++) {

                    if (priorities.get(i).equals(arr.get(j))) {

                        if (i < priValue) {
                            priValue = i;
                            priority = priorities.get(i);
                            System.out.println("HEYYY: " + priority);
                        }
                    }
                }
            }
            if (priority != null) {
                //System.out.println("Priority: " + priority);
                acceptanceStates.put(stateName, priority);
            }

        }

    }

    public HashMap<String, String> getAcceptanceStates() {
        return acceptanceStates;
    }

    public ArrayList<ArrayList<String>> getTransitionTable() {
        return graph.getTransitionTable();
    }

    public LexGraph getGraph() {
        return graph;
    }

    public ArrayList<String> getPriorities() {
        return priorities;
    }

}
