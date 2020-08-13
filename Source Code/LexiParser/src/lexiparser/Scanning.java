/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiparser;

import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author ahmed
 */
public class Scanning {

    Charset encoding = Charset.defaultCharset();
    private String filename;
    private String output;
    private Minimization min;
    private DFA dfa;
    private LexGraph Graph;
    private ArrayList<ArrayList<String>> TransitionTable = new ArrayList<>();
    private ArrayList<ArrayList<String>> tokens = new ArrayList<>();
    private HashMap<String, String> acceptanceStates = new HashMap();
    String startState;
    String deadState;
    String nextState;

    public Scanning(Minimization min, LexGraph graph, DFA dfa, String filename) throws IOException {
        this.filename = filename;
        output = filename.substring(0, filename.length() - 4) + "output.txt";
        this.min = min;
        this.Graph = graph;
        this.dfa = dfa;
        TransitionTable = min.getMinimizedTable();
        acceptanceStates = min.getAcceptanceStates();
        startState = TransitionTable.get(1).get(0);
        deadState = TransitionTable.get(TransitionTable.size() - 1).get(0);
        File file = new File(filename);
        handleFile(file, encoding);
        showTokens();
        writeOutputFile();
        addToTextArea();
    }

    private void handleFile(File file, Charset encoding) throws IOException {
        InputStream in = new FileInputStream(file);
        Reader reader = new InputStreamReader(in, encoding);
        Reader buffer = new BufferedReader(reader);
        handleCharacters(buffer);
    }

    private void handleCharacters(Reader reader) throws IOException {
        int r;
        String finalState = null;
        ArrayList<Character> c = new ArrayList<>();
        String lexeme = "";
        String token;
        String state = startState;
        String input;
        String temp;
        int i;
        while ((r = reader.read()) != -1) {
            char ch = (char) r;
            c.add(ch);
        }

        i = 0;
        int start = 0, end = 0;
        while (i < c.size()) {
            // System.out.println("i: " + i + " cSize: "+ c.size());
            if (lexeme.equals("")) {
                start = i;
            }
            input = Character.toString(c.get(i));
            System.out.println("input :" + input);
            if (input.equals("\t") || input.equals("\n") || input.equals("")) {
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                if (lexeme.equals("")) {
                    i++;
                    continue;
                } else {
                    ArrayList<String> t = new ArrayList<>();
                    token = acceptanceStates.get(finalState);
                    //int j = end - start;
                    //lexeme = lexeme.substring(0,j);
                    t.add(lexeme);
                    t.add(token);
                    //System.out.println(lexeme+":"+token);
                    tokens.add(t);
                    state = startState;
                    lexeme = "";
                    i = end + 1;
                }
                i++;
                continue;
            }
            nextState = Graph.getNextState(state, input);
            if (nextState == null) {
                temp = dfa.matches(input);
                nextState = Graph.getNextState(state, temp);
            }
            //System.out.println("state: "+nextState);
            if (nextState != null) {
                if (!nextState.equals(deadState) && !input.equals(" ")) {
                    lexeme += input;
                    System.out.println("l: " + lexeme);
                    state = nextState;
                    if (acceptanceStates.containsKey(state)) {
                        finalState = state;
                        //System.out.println("accept: "+finalState);
                        end = i;
                    }
                    i++;
                } else {
                    if (lexeme.equals("")) {
                        i++;
                    } else {
                        ArrayList<String> t = new ArrayList<>();
                        token = acceptanceStates.get(finalState);
                        //int j = end - start;
                        //lexeme = lexeme.substring(0,j);
                        t.add(lexeme);
                        t.add(token);
                        //System.out.println(lexeme+":"+token);
                        tokens.add(t);
                        state = startState;
                        lexeme = "";
                        i = end + 1;
                    }
                }
            } else {
                if (!Character.isWhitespace(c.get(i))) {
                    JOptionPane.showMessageDialog(null, "Invalid Expression: " + c.get(i));
                    System.out.println("Invalid Expression:" + c.get(i));
                    System.exit(0);
                }
                i++;
            }

        }
        if (!lexeme.isEmpty()) {
            ArrayList<String> t = new ArrayList<>();
            token = acceptanceStates.get(finalState);
            //int j = end - start;
            //lexeme = lexeme.substring(0,j);
            t.add(lexeme);
            t.add(token);
            //System.out.println(lexeme+":"+token);
            tokens.add(t);
            lexeme = "";
        }
    }

    private void writeOutputFile() throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        for (int i = 0; i < tokens.size(); i++) {
            bw.write(tokens.get(i).get(1));
            bw.write("\n");
        }
        bw.flush();
        bw.close();
    }

    private void showTokens() {
        System.out.println("Tokens:");
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(tokens.get(i).get(0) + " : " + tokens.get(i).get(1));
        }
    }

    /*
    private String isCharacter(char c){
        if(c >= 'a' && c <= 'z')
            return "a-z";
        else if(c >= 'A' && c <= 'Z')
            return "A-Z";
        else if(c >= '0' && c <= '9')
            return "0-9";
        return null;
    }
     */

    public void addToTextArea() {

        JFrame f = new JFrame("Lexemes output file");
        TextArea area = new TextArea();
        area.setBounds(10, 30, 300, 300);
        f.add(area);
        f.setSize(400, 400);
        f.setLayout(null);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(output)));
            String str;
            while ((str = in.readLine()) != null) {
                area.append(str + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }

    }
}
