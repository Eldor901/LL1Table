package com.company;

import LL1.Grammer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.*;


public class Main {

    private static boolean isNonTerminal(String str)
    {
        if (str.equals(""))
        {
            return false;
        }
        if (str.charAt(0) == '<' && (str.charAt(str.length() - 1) == '>' || str.charAt(str.length() - 2) == '>'))
            return true;
        return  false;
    }

    private  static void saveAnalyzedTerminals(ArrayList<Grammer> saveTable, HashMap<String, String> grammar, String analyzeTerminals)
    {

            String terminals[] = analyzeTerminals.split(" ");


            for (int i = 0; i < terminals.length; i++)
            {
                if (!terminals[i].equals("")) {
                    Grammer gr = new Grammer();
                    if (isNonTerminal(terminals[i])) {
                        gr.NonTerminal = terminals[i];
                        String termnls = grammar.get(terminals[i]);
                        if (termnls != null) {
                            gr.Terminal = termnls;
                        } else {
                            gr.Terminal = "Error";
                        }
                    } else {
                        String temnls = terminals[i];
                        gr.NonTerminal = terminals[i];
                        gr.Terminal = terminals[i];

                        if (terminals.length != i+1) {
                            if (terminals[i + 1].isEmpty()) {
                                gr.dirNum = 0;
                            } else {
                                if (!isNonTerminal(terminals[i]))
                                    gr.dirNum = saveTable.size() + 2;
                            }
                        }else
                        {

                            if (!isNonTerminal(terminals[i]))
                            gr.dirNum = 0;

                        }


                    }

                    if (i == terminals.length - 1)
                    {
                        gr.isEndInLine = -1;
                        System.out.println(terminals[i]);
                    }
                    saveTable.add(gr);
                }
            }

    }


    public static void setShift(Grammer grammer, ArrayList<Grammer> saveTable)
    {
        /*if(grammer.isSetDir)
        {
            Integer numSybolLink = grammer.dirNum - 1;

            String ch = saveTable.get(numSybolLink).NonTerminal.replaceAll("\\s","");
            while(isNonTerminal(ch))
            {
                if(saveTable.get(numSybolLink).isSetDir)
                {
                    break;
                }else {
                    numSybolLink++;
                    ch = saveTable.get(numSybolLink).NonTerminal.replaceAll("\\s", "");
                }
            }


            if (!isNonTerminal(ch) && !ch.equals("@"))
            {
                saveTable.get(numSybolLink).Shift = 1;
            }
        }
         */

        if(!grammer.isSetDir)
        {
            String ch = grammer.NonTerminal.replaceAll("\\s","");
            if(!isNonTerminal(ch) && !ch.equals("@") && !ch.equals("#"))
            {
                grammer.Shift = 1;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        File text = new File("./src/"+args[0]);
        FileWriter fr = new FileWriter(text, true);
        fr.write("/ - /\n");
        fr.close();

        Scanner scnr = new Scanner(text);

        HashMap<String, String> grammar = new HashMap<>();
        String NonTerminal = "";
        String dirs = "";

        while(scnr.hasNextLine()){
            String line = scnr.nextLine();
            String keyValue[] = line.split("-");
            keyValue[0] = keyValue[0].trim();
            if(keyValue.length == 2)
            {
                String dir[] = keyValue[1].split("/");


                if (!NonTerminal.equals(""))
                {

                    if (!NonTerminal.equals(keyValue[0])) {
                        grammar.put(NonTerminal, dirs);
                        dirs = "";
                    }
                }

                if (dir.length >= 2)
                {
                    dirs +=  dir[1];
                }


            }

            NonTerminal = keyValue[0];
        }


        Scanner scnr2 = new Scanner(text);
        ArrayList<Grammer> saveTable = new ArrayList<>();

        String NonTerminalch = "";
        String analyzeAlldirs  = "";
        while(scnr2.hasNextLine()) {
            String line = scnr2.nextLine();
            String value[] = line.split("-");
            value[0] = value[0].trim();
            if(value.length == 2)
            {
                String dir[] = value[1].split("/");

                System.out.println(dir[0]);


                if (!NonTerminalch.equals(""))
                {
                    if (!NonTerminalch.equals(value[0])) {
                        saveAnalyzedTerminals(saveTable, grammar, analyzeAlldirs);
                        analyzeAlldirs = "";
                    }
                }


                if (dir.length >= 2) {
                    Grammer gr = new Grammer();
                    gr.NonTerminal = value[0];
                    gr.Terminal = dir[1];
                    gr.isSetDir = true;
                    saveTable.add(gr);
                }



                if (dir.length >= 2)
                {
                    analyzeAlldirs += dir[0];
                }

            }

            NonTerminalch = value[0];
        }


        //nonterminals with links dirs (nonterminals true)
        for(int i = 0; i < saveTable.size(); i++)
        {
            if (saveTable.get(i).isSetDir)
            {
                String chNon = saveTable.get(i).NonTerminal.replaceAll("\\s","");
                String ch = saveTable.get(i).Terminal.replaceAll("\\s","");
                for(int j = i + 1; j < saveTable.size(); j++)
                {
                    String ch3TrNon = saveTable.get(j).NonTerminal.replaceAll("\\s","");
                    String ch3Tr = saveTable.get(j).Terminal.replaceAll("\\s","");
                        if (ch.equals(ch3Tr)) {
                                saveTable.get(i).dirNum = j + 1;
                                break;
                        }
                }
            }
        }



        // nonTerminal without link dirs (nonterminals false)
        for(int i = 0; i < saveTable.size(); i++)
        {
            if (!saveTable.get(i).isSetDir)
            {
                String nonTermi = saveTable.get(i).NonTerminal.replaceAll("\\s","");
                String temi = saveTable.get(i).Terminal.replaceAll("\\s","");
                if (isNonTerminal(nonTermi))
                {
                    for(int j = 0; j < saveTable.size(); j++)
                    {
                        if(saveTable.get(j).isSetDir)
                        {
                            String nonTermj = saveTable.get(j).NonTerminal.replaceAll("\\s","");
                            String temj = saveTable.get(j).Terminal.replaceAll("\\s","");

                            if(nonTermi.equals(nonTermj))
                            {
                                saveTable.get(i).dirNum = j+1;
                                break;
                            }
                        }
                    }
                }
            }
        }

        //find  dirs for empty
        for(int i = 0; i < saveTable.size(); i++)
        {
            String empty = saveTable.get(i).Terminal.replaceAll("\\s","");
            if(empty.equals("@"))
            {
                for (int j = i; j >= 0 ; j--)
                {
                    /*
                    if (saveTable.get(j).dirNum == -1)
                    {
                        saveTable.get(i).Terminal = saveTable.get(j).Terminal;
                        saveTable.get(j).dirNum = i+1;
                        break;
                    }
                     */
                    if (saveTable.get(j).isSetDir)
                    {
                        if (j != 0)
                        {
                           if (!saveTable.get(j-1).isSetDir)
                           {
                               saveTable.get(i).Terminal = saveTable.get(j).Terminal;
                               saveTable.get(j).dirNum = i+1;
                               break;
                           }
                        }
                    }

                }
            }
        }

        //drawTable
        for(int i = 0; i < saveTable.size(); i++)
        {
            setShift(saveTable.get(i), saveTable);
            //Handle stack
            if(!saveTable.get(i).isSetDir) {
                if (isNonTerminal(saveTable.get(i).NonTerminal))
                {
                    if (i + 1!= saveTable.size()) {
                        if ((!saveTable.get(i + 1).isSetDir) && isNonTerminal(saveTable.get(i).NonTerminal)) {
                            boolean was = false;
                                for (int k = i + 1; k < i + 1 + 4; k++) {
                                    if (k < saveTable.size()) {
                                        if (isNonTerminal(saveTable.get(k).NonTerminal)) {

                                            if (saveTable.get(k).isSetDir) {
                                                break;
                                            }

                                            if (saveTable.get(i).NonTerminal.equals(saveTable.get(k).NonTerminal)) {
                                                was = true;
                                            }
                                        }
                                    }
                                }

                            if(!was) {
                                saveTable.get(i).stack = 1;
                                was = false;
                            }
                        }
                    }
                }
            }

            if(saveTable.get(i).Terminal.replaceAll("\\s","").equals("#"))
            {
                saveTable.get(i).EndState = 1;
            }
        }

        //Handle Error
        for(int i = 0; i < saveTable.size(); i++) {

            if (saveTable.get(i).isSetDir) {
                if (i + 1 != saveTable.size()) {
                    if (saveTable.get(i + 1).isSetDir) {
                        saveTable.get(i).Error = -1;
                        while (!saveTable.get(i).isSetDir)
                        {
                            i++;
                        }
                    }
                }
            }
        }


        for(int i = 0; i< saveTable.size(); i++)
        {
            int num = i +1;
            System.out.println(num +  "  " + saveTable.get(i).NonTerminal +"   " + saveTable.get(i).Terminal + "   " + saveTable.get(i).dirNum);
        }

        System.out.println("№" +"                  "+  "DirsSet" +"             " + "Sift"  + "           " + "DirNum"  + "              " + "Stack" + "             " +"Error" + "         " +"EndState");
        for(int i = 0; i< saveTable.size(); i++)
        {
           int num = i +1;
           System.out.println(num +  " " + saveTable.get(i).isEndInLine  + "             " +" " +saveTable.get(i).isSetDir + " " +  saveTable.get(i).Terminal + "               " + saveTable.get(i).Shift+ "                " + saveTable.get(i).dirNum
           + "             " + saveTable.get(i).stack + "                "  + saveTable.get(i).Error  + "                "+ saveTable.get(i).EndState);
        }


        FileWriter  output = new FileWriter("./src/"+args[1]);
        BufferedWriter outputwrite = new BufferedWriter(output);
        outputwrite.flush();
        outputwrite.write("№" +" "+  "DirsSet" +" " + "Sift"  + " " + "DirNum"  + " " + "Stack" + " " +"Error" + " " +"EndState"+ "\n");
        for(int i = 0; i< saveTable.size(); i++)
        {
            int num = i +1;
            String str = saveTable.get(i).Terminal;
            str = str.trim();
            str = str.replace(" ", ",");

            outputwrite.write(num + " " +  str + " " + saveTable.get(i).Shift
                    + " " +  saveTable.get(i).dirNum + " " + saveTable.get(i).stack + " " + saveTable.get(i).Error + " " + saveTable.get(i).EndState + '\n');
        }

        outputwrite.close();


        RandomAccessFile efile = new RandomAccessFile(text, "rw");
        long length = efile.length();
        length = length - 6;
        efile.setLength(length);
        efile.close();

    }

}
