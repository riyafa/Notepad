package Notepad;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JTextArea;

/**
 *
 * @author riyafa
 */
public class NotepadFunctions {
    private JTextArea textFile;
    private String name;
    private Scanner sc;
    private File selectedFile;
    public NotepadFunctions(JTextArea textFile, String name){
        this.name=name;
        this.textFile=textFile;
    }
    
    void save(JTextArea textFile) {
        this.textFile=textFile;
        try {
            PrintWriter pw=new PrintWriter(new BufferedWriter(new FileWriter(name+".txt")));
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void open(File file){    
        selectedFile=file;
        try {
            String savetxt="";
            sc=new Scanner(new BufferedInputStream(new FileInputStream(file)));
            while(sc.hasNextLine()){
                savetxt+=sc.nextLine()+"\n";
            }
            textFile.setText(savetxt);
            textFile.setCaretPosition(0);
        } catch (FileNotFoundException ex) {            
        }        
    }
    public void saveAs(){
        
    }
}
