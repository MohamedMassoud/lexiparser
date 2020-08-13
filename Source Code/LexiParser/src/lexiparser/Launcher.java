/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiparser;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Crap
 */
public class Launcher {

    public Launcher(String grammarPath, String codePath, String cfgPath) throws IOException {

        Formulator formulator = new Formulator(grammarPath);
        ArrayList<ArrayList<String>> transitionTable = formulator.getTransitionTable();
        InfixToPostfix itp = new InfixToPostfix(transitionTable);
        LexGraph graph = new LexGraph();
        NFA nfa = new NFA(itp.getPostfix(), itp.getFinalStates(), formulator.getKeywords(), formulator.getpunc(), graph);

        //graph.printMatrix();
        // new Table(graph.getTransitionTable(), "NFA");
        LexGraph graph2 = new LexGraph();
        DFA dfa = new DFA(nfa, graph2);

        System.out.println(graph2.getTransitionTable());
        LexGraph graph3 = new LexGraph();
        Minimization min = new Minimization(dfa, graph3);
        new LexTable(graph.getTransitionTable(), "NFA");
        new LexTable(graph2.getTransitionTable(), "DFA");
        new LexTable(graph3.getTransitionTable(), "Minimized DFA");
        //System.out.println(transitionTable);
        Scanning s = new Scanning(min, graph3, dfa, codePath);

        new ParserGenerator(cfgPath, codePath.replace(".txt", "") + "output.txt");
    }
}
