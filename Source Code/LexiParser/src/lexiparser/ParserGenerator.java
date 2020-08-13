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
 * @author ahmed
 */
public class ParserGenerator {

    private String cfgPath;
    private String lexemesPath;

    public ParserGenerator(String cfgPath, String lexemesPath) throws IOException {
        this.cfgPath = cfgPath;
        this.lexemesPath = lexemesPath;
        generate();
    }

    public void generate() throws IOException {
        ReadInputFile in = new ReadInputFile();
        in.read(cfgPath);
        in.CheckLL1();
        First_Follow f = new First_Follow(in.getGrammer());
        ParGraph graph = new ParGraph();
        FillTable t = new FillTable(f.getFirst(), f.getFollow(), in.getGrammer(), graph);
        new ParTable(graph.getTransitionTable(), "Parsing Table");

        System.out.println("-----------------------------------");
        System.out.println("Parsing Table " + graph.getTransitionTable());

        //---------------------------------------------------------------------------------
        ArrayList<ArrayList<String>> parsingTable = graph.getTransitionTable();
        LexemesReader lr = new LexemesReader(lexemesPath);
        System.out.println(lr.getInput());
        Parser parser = new Parser(parsingTable, lr.getInput());
    }

}
