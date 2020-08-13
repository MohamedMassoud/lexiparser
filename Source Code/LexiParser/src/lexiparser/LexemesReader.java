/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Crap
 */
public class LexemesReader {

    private ArrayList<String> input = new ArrayList<>();

    public LexemesReader(String fileName) {
        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                input.add(line);
            }
            fileReader.close();
            //System.out.println("Contents of file:");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getInput() {
        return input;
    }

}
