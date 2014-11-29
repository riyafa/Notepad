package Notepad;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JTextPane;

/**
 *
 * @author riyafa
 */
public class NotepadFunctions {
    private JTextPane textFile;
    private String name;
    public NotepadFunctions(JTextPane textFile, String name){
        this.name=name;
        this.textFile=textFile;
    }
    
    void save(JTextPane textFile) {
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
}
