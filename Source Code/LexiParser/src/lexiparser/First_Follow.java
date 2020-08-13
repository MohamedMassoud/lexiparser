/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiparser;

import java.awt.TextArea;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.JFrame;

/**
 *
 * @author ahmed
 */
public class First_Follow {

    ArrayList<ArrayList<String>> grammer = new ArrayList<>();
    ArrayList<ArrayList<String>> first = new ArrayList<>();
    ArrayList<ArrayList<String>> follow = new ArrayList<>();
    ArrayList<ArrayList<String>> hemasFirst = new ArrayList<>();
    ArrayList<ArrayList<String>> tokenized = new ArrayList<>();

    ArrayList<String> mytemp = new ArrayList<>();
    ArrayList<String> HemasTemp = new ArrayList<>();

    ArrayList<Character> tokens = new ArrayList<>();
    ArrayList<String> ss = new ArrayList<>();
    ArrayList<String> sstemp = new ArrayList<>();
    private static ArrayList<String> terminals = new ArrayList<>();

    public static ArrayList<String> getTerminals() {
        return terminals;
    }

    public First_Follow(ArrayList<ArrayList<String>> grammer) {
        this.grammer = grammer;
        find();

        ArrayList<ArrayList<String>> fixed = fix(first, follow);
        new ParTable(fixed, "First and Follow");

    }

    private ArrayList<ArrayList<String>> fix(ArrayList<ArrayList<String>> fi, ArrayList<ArrayList<String>> fo) {
        ArrayList<ArrayList<String>> fixed = new ArrayList<>();
        ArrayList<String> row0 = new ArrayList<>();
        row0.add(0, " ");
        row0.add("First");
        row0.add("Follow");
        //System.out.println("oooooooooooooooooooo" + row0);
        fixed.add(row0);

        for (int i = 0; i < fi.size(); i++) {
            ArrayList<String> newState = new ArrayList<>();
            newState.add(fi.get(i).get(0));

            //////////////////////////////////First
            String temp = "";
            for (int j = 1; j < fi.get(i).size(); j++) {
                if (j == 1) {
                    temp = fi.get(i).get(j);
                } else {
                    temp = temp + " , " + fi.get(i).get(j);
                }
            }
            newState.add(temp);

            ///////////////////Follow
            temp = "";
            for (int j = 1; j < fo.get(i).size(); j++) {
                if (j == 1) {
                    temp = fo.get(i).get(j);
                } else {
                    temp = temp + " , " + fo.get(i).get(j);
                }
            }
            newState.add(temp);

            fixed.add(newState);
        }

        //////////////////////////Follow
        return fixed;
    }

    public void find() {
        for (int i = 0; i < grammer.size(); i++) {
            mytemp = new ArrayList<>(); //reseting
            HemasTemp = new ArrayList<>();
            mytemp.add(grammer.get(i).get(0)); // passing non-terminal
            HemasTemp.add(grammer.get(i).get(0));
            for (int j = 1; j < grammer.get(i).size(); j++) {
                tokens = new ArrayList();
                String seg = grammer.get(i).get(j).trim();
                char[] ll = seg.toCharArray();

                //System.out.println(ll[1]);
                if (ll[0] == 39) {

                    for (int kp = 1; kp < ll.length; kp++) {
                        //System.out.println(ll[kp]);
                        if (ll[kp] != 39) {
                            tokens.add(ll[kp]);
                        } else {
                            break;
                        }
                    }

                    StringBuilder sb = new StringBuilder();
                    tokens.forEach((cha) -> {
                        sb.append(cha);
                    });
                    //System.out.println(">"+sb);
                    mytemp.add(sb.toString());
                    HemasTemp.add(sb.toString());

                } else if (ll[0] == 92 && ll[1] == 'L') {
                    mytemp.add("\\L");
                    HemasTemp.add("\\L");

                } else {
                    for (int kp = 0; kp < ll.length; kp++) {
                        //System.out.println(ll[kp]);
                        if (ll[kp] != 32) {
                            tokens.add(ll[kp]);
                        } else {
                            break;
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    tokens.forEach((cha) -> {
                        sb.append(cha);
                    });
                    String token = sb.toString();
                    HemasTemp.add("$" + token + "$");
                    //System.out.println("1>"+token);
                    findFirstOf(token);

                }

            }
            //add mytemp to first here
            first.add(mytemp);
            hemasFirst.add(HemasTemp);
        }

        System.out.println("---------------FIRST---------------");
        for (int i = 0; i < first.size(); i++) {
            System.out.println(first.get(i));
        }

        System.out.println("---------------Hema's FIRST---------------");
        for (int i = 0; i < hemasFirst.size(); i++) {
            System.out.println(hemasFirst.get(i));
        }
        //System.out.println("------------------------------");

        //FOLLOW /////////////////////////////////////////////////////////////////////////////////////
        for (int i = 0; i < grammer.size(); i++) {

            mytemp = new ArrayList<>(); //reseting
            mytemp.add(grammer.get(i).get(0)); // passing non-terminal

            if (i == 0) { // this NOT gonna work ->>>>> GO BACK
                mytemp.add("$");
            }
            //   At this moment I HAVE TO GET ""ALL"" THE FOLLOW( grammer.get(i).get(0) ) "somehow xD"
            findFollowOf(grammer.get(i).get(0));
            follow.add(mytemp);

        }

        System.out.println("---------------FOLLOW---------------");
        for (int i = 0; i < follow.size(); i++) {
            System.out.println(follow.get(i));
        }
        System.out.println("------------------------------");

    }

    private void findFirstOf(String token) {
        int index = 0;
        boolean flag = false;
        for (int i = 0; i < grammer.size(); i++) {
            if (grammer.get(i).get(0) == null ? token == null : grammer.get(i).get(0).equals(token)) {
                index = i;
                break;
            }

        }
        //System.out.println(index + ">" + token);
        for (int j = 1; j < grammer.get(index).size(); j++) {
            tokens = new ArrayList();
            String seg = grammer.get(index).get(j).trim();
            char[] ll = seg.toCharArray();

            //System.out.println(ll[1]);
            if (ll[0] == 39) {

                for (int kp = 1; kp < ll.length; kp++) {
                    //System.out.println(ll[kp]);
                    if (ll[kp] != 39) {
                        tokens.add(ll[kp]);
                    } else {
                        break;
                    }
                }

                StringBuilder sb = new StringBuilder();
                tokens.forEach((cha) -> {
                    sb.append(cha);
                });
                //System.out.println(">"+sb);
                mytemp.add(sb.toString());
                HemasTemp.add(sb.toString());

            } else if (ll[0] == 92 && ll[1] == 'L') {
                mytemp.add("\\L");
                HemasTemp.add("\\L");

            } else {
                for (int kp = 0; kp < ll.length; kp++) {
                    //System.out.println(ll[kp]);
                    if (ll[kp] != 32) {
                        tokens.add(ll[kp]);
                    } else {
                        break;
                    }
                }
                StringBuilder sb = new StringBuilder();
                tokens.forEach((cha) -> {
                    sb.append(cha);
                });
                String tokenlol = sb.toString();
                //System.out.println("2>"+tokenlol);
                findFirstOf(tokenlol);

            }

        }

    }

    private void findFollowOf(String s) {

        String token = s;
        tokenized = new ArrayList();
        for (int i = 0; i < grammer.size(); i++) {
            for (int j = 1; j < grammer.get(i).size(); j++) {
                String prog = grammer.get(i).get(0).trim(); // x -> 
                String seg = grammer.get(i).get(j).trim();

                ss = new ArrayList();
                sstemp = new ArrayList();

                seg = seg.replace("\'", " \' ");
                //System.out.println(seg);

                String[] str = seg.split("\\s");
                ss.addAll(Arrays.asList(str));
                sstemp.add(prog);
                for (int jj = 0; jj < ss.size(); jj++) {
                    if (!ss.get(jj).equals("") && !ss.get(jj).equals("\'")) {

                        sstemp.add(ss.get(jj));
                    }

                }

                /// terminals --------------------------------------------------------------------
                boolean tflag = false;
                for (int m = 1; m < sstemp.size(); m++) {
                    //String tok = sstemp.get(m);
                    //System.out.println(tok);
                    for (int n = 0; n < grammer.size(); n++) {

                        if (grammer.get(n).get(0).equals(sstemp.get(m)) || sstemp.get(m).equals("\\L")) {
                            tflag = true;
                            break;
                        }

                    }

                    if (tflag) {
                        tflag = false;
                    } else {

                        //System.out.println(grammer.get(n).get(0) + "???" + sstemp.get(m));
                        terminals.add(sstemp.get(m));

                    }

                }

                boolean flag = false;
                for (int k = 1; k < sstemp.size(); k++) {

                    if (sstemp.get(k).equals(token)) {
                        flag = true;
                        break;
                    }

                }
                if (flag) {
                    tokenized.add(sstemp);
                }

            }

        }

        for (int i = 0; i < tokenized.size(); i++) {
            //System.out.println(tokenized.get(i));
            for (int j = 1; j < tokenized.get(i).size(); j++) {
                String prod = tokenized.get(i).get(0);
                String next = "";
                if (!tokenized.get(i).get(j).equals(token)) // I DON'T GIVE A FUCK ABOUT TOKENS BEFORE THE REQUIRED TOKEN!
                {
                    continue;
                }

                if ((tokenized.get(i).size() - j) == 1) {
                    next = "null";
                } else {
                    next = tokenized.get(i).get(j + 1);
                }
                //System.out.println(tokenized.get(i).get(j)+"->"+next);

                if (next != "null") {

                    boolean isNon = false;
                    // check Terminal or Non-Terminal
                    for (int kraken = 0; kraken < grammer.size(); kraken++) {

                        if (grammer.get(kraken).get(0).equals(next)) {
                            isNon = true;
                            break;

                        } // END-CHECK? TERMINAL : NON-TERMINAL

                    } // END-LOOP-CHECK? TERMINAL : NON-TERMINAL

                    if (isNon) {
                        // Non-Terminal  
                        //System.out.println(next+"non-terminal");

                        // Check if First(next) contains \L or Not
                        boolean isL = false;
                        for (int kraken = 0; kraken < first.size(); kraken++) {
                            if (first.get(kraken).get(0).equals(next)) {
                                if (first.get(kraken).contains("\\L")) {
                                    isL = true;
                                    //System.out.println(token+"-"+next+"contains \\L"+first.get(kraken));
                                    break;
                                }
                            } // END-CHECK? \L
                        }// END-LOOP-CHECK? \L

                        if (isL) {
                            // FIRST(next) except \L + FOLLOW(prod)
                            for (int kraken = 0; kraken < first.size(); kraken++) {
                                if (first.get(kraken).get(0).equals(next)) {
                                    for (int bunny = 1; bunny < first.get(kraken).size(); bunny++) {
                                        if (first.get(kraken).get(bunny).equals("\\L")) {
                                            continue;
                                        } else {
                                            mytemp.add(first.get(kraken).get(bunny));
                                        }

                                    }
                                    break;
                                } // END-CHECK? ADD next

                            }// END-LOOP-CHECK? next

                            // + follow(prod)
                            if (prod == token) {
                                continue;
                            } else {
                                for (int kraken = 0; kraken < follow.size(); kraken++) {
                                    if (follow.get(kraken).get(0).equals(prod)) {
                                        for (int bunny = 1; bunny < follow.get(kraken).size(); bunny++) {
                                            mytemp.add(follow.get(kraken).get(bunny));
                                            //System.out.println();
                                        }
                                        break;
                                    }
                                }
                            }

                        } else {
                            // First(next)
                            for (int kraken = 0; kraken < first.size(); kraken++) {
                                if (first.get(kraken).get(0).equals(next)) {
                                    for (int bunny = 1; bunny < first.get(kraken).size(); bunny++) {
                                        mytemp.add(first.get(kraken).get(bunny));
                                    }
                                    break;
                                } // END-CHECK? ADD next

                            }// END-LOOP-CHECK? next

                        }

                    } else {
                        // Terminal
                        //System.out.println(next+"terminal");
                        mytemp.add(next);
                        break;
                    }

                } else {

                    // No Next
                    // SO we get follow(prod)
                    if (prod == token) {
                        continue;
                    } else {
                        for (int kraken = 0; kraken < follow.size(); kraken++) {
                            if (follow.get(kraken).get(0).equals(prod)) {
                                //System.out.println("LINE : "+follow.get(kraken).get(0)+"-"+prod);
                                for (int bunny = 1; bunny < follow.get(kraken).size(); bunny++) {
                                    //System.out.println("ELEMENT : "+follow.get(kraken).get(bunny));
                                    mytemp.add(follow.get(kraken).get(bunny));

                                }
                                break;
                            }
                        }
                    }

                }

            }
        }

        ArrayList<String> newList = removeDuplicates(mytemp);
        /*for(int i=0;i<newList.size();i++)
             System.out.println(newList.get(i));
        System.out.println("------------------------"+token);*/
        mytemp = new ArrayList<>();
        mytemp.addAll(newList);
        tokenized = new ArrayList<>();
    }

    // remove duplicates from an ArrayList 
    public static <String> ArrayList<String> removeDuplicates(ArrayList<String> list) {

        // Create a new LinkedHashSet 
        Set<String> set = new LinkedHashSet<>();

        // Add the elements to set 
        set.addAll(list);

        // Clear the list 
        list.clear();

        // add the elements of set 
        // with no duplicates to the list 
        list.addAll(set);

        // return the list 
        return list;
    }

    public ArrayList<ArrayList<String>> getFirst() {
        return hemasFirst;
    }

    public ArrayList<ArrayList<String>> getFollow() {
        return follow;
    }
}
