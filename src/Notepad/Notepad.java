/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Notepad;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import javax.swing.text.View;

/**
 *
 * @author A.H.F. Riyafa
 */
public class Notepad extends javax.swing.JFrame {

    /**
     * Creates new form Notepad
     */
    private String name="Untitled";
    private FileFilter filter;
    private JFileChooser fileChooser;
    private File selectedFile;
    private Scanner sc;
    private JFileChooser saveAsFileChooser;
    private boolean changes=false;
    private static final boolean CANCEL=true;
    private static final boolean OK=false;
    private PrinterJob pj;
    protected PrintView m_printView;
    protected DefaultStyledDocument m_doc;
    private StyleContext m_context;
    private Printable printable;
    private UndoManager manager;
    private boolean unredo=true;
    private BasicAction cutAction, pasteAction,copyAction;
    private Font font;
    private JFontChooser chooser;
    private CaretListener caretListener;
    private Find find;
    private FindReplace findReplace;
    
    public Notepad() {
        initComponents();
        setLocationRelativeTo(null);
        cutAction=new CutAction("Cut", null);
        cutAction.setTextComponent(textFile);
        font=new Font("Calibri",Font.PLAIN , 11);
        chooser=new JFontChooser();
        textFile.setFont(font);
        m_context = new StyleContext();
        m_doc = new DefaultStyledDocument(m_context);
        textFile.setDocument(m_doc);
        manager = new UndoManager();
        textFile.getDocument().addUndoableEditListener(manager);
        find=new Find(this,false,textFile);
        this.findReplace = new FindReplace(this, false,find);
        fileChooser = new JFileChooser(){
        @Override
        public void approveSelection(){
            File f = getSelectedFile();
            if(!f.exists() ){                   
                JOptionPane.showMessageDialog(this, f.getName()+"\nFile not found."
                        + "\nCheck the filename and try again.","Open", JOptionPane.WARNING_MESSAGE);                   
            }else
                super.approveSelection();
        }           
    };
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        setTitle(name+"-Notepad");
        filter =new FileNameExtensionFilter("TextDocuments(*.txt)" ,"txt");
        fileChooser.setFileFilter(filter);
        setIconImage(new ImageIcon(Notepad.class.getResource("icon.png")).getImage());
        saveAsFileChooser = new JFileChooser(){
        @Override
        public void approveSelection(){
            File f = getSelectedFile();
            if(f.exists() ){                   
                int result=JOptionPane.showConfirmDialog(this, f.getName()+"already exists."
                        + "\nDo you want to replace it?","Confirm Save As",
                        JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                if (result==JOptionPane.YES_OPTION) {
                    super.approveSelection();
                }
            }else
                super.approveSelection();
        }           
    };
      textFile.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                changes=true;
                menuUndo.setEnabled(true);
               // System.out.println(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changes=true;
                menuUndo.setEnabled(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changes=true;
                menuUndo.setEnabled(true);
            }
      });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleClosing();
            }
        });
         pj = PrinterJob.getPrinterJob();
         
         printable=new Printer();
         pj.setPrintable(printable);      
         caretListener=(CaretEvent e) -> {
            JTextArea editArea = (JTextArea)e.getSource();
            
            // Lets start with some default values for the line and column.
            int linenum = 1;
            int columnnum = 1;
            
            // We create a try catch to catch any exceptions. We will simply ignore such an error for our demonstration.
            try {
                // First we find the position of the caret. This is the number of where the caret is in relation to the start of the JTextArea
                // in the upper left corner. We use this position to find offset values (eg what line we are on for the given position as well as
                // what position that line starts on.
                int caretpos = editArea.getCaretPosition();
                linenum = editArea.getLineOfOffset(caretpos);
                
                // We subtract the offset of where our line starts from the overall caret position.
                // So lets say that we are on line 5 and that line starts at caret position 100, if our caret position is currently 106
                // we know that we must be on column 6 of line 5.
                columnnum = caretpos - editArea.getLineStartOffset(linenum)+1;
                
                // We have to add one here because line numbers start at 0 for getLineOfOffset and we want it to start at 1 for display.
                linenum += 1;
            }
            catch(BadLocationException ex) { }
            
            // Once we know the position of the line and the column
            labellncol.setText("Ln "+linenum+", Col "+columnnum);
            
            };
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        textFile = new javax.swing.JTextArea();
        statusBar = new javax.swing.JToolBar();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        labellncol = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuNew = new javax.swing.JMenuItem();
        menuOpen = new javax.swing.JMenuItem();
        saveMenu = new javax.swing.JMenuItem();
        menuSaveAs = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuPageSetup = new javax.swing.JMenuItem();
        menuPrint = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        menuExit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        menuUndo = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        cutAction=new CutAction("Cut", null);
        cutAction.setTextComponent(textFile);
        menuCut = new javax.swing.JMenuItem(cutAction);
        copyAction=new CopyAction("Copy", null);
        copyAction.setTextComponent(textFile);
        menuCopy = new javax.swing.JMenuItem(copyAction);
        pasteAction=new PasteAction("Paste", null);
        pasteAction.setTextComponent(textFile);
        menuPaste = new javax.swing.JMenuItem(pasteAction);
        menuDelete = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        menuFind = new javax.swing.JMenuItem();
        menuFindNext = new javax.swing.JMenuItem();
        menuReplace = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        menuSelect = new javax.swing.JMenuItem();
        menuTimeDate = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        menuWordWrap = new javax.swing.JCheckBoxMenuItem();
        menuFont = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        menuStatusBar = new javax.swing.JCheckBoxMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(964, 524));

        textFile.setColumns(20);
        textFile.setRows(5);
        scrollPane.setViewportView(textFile);

        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);

        statusBar.add( Box.createHorizontalGlue() );
        statusBar.setRollover(true);
        statusBar.setVisible(false);
        statusBar.add(jSeparator7);

        labellncol.setText("Ln 1, Col 1");
        statusBar.add(labellncol);

        getContentPane().add(statusBar, java.awt.BorderLayout.PAGE_END);

        jMenu1.setText("File");

        menuNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuNew.setText("New");
        menuNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewActionPerformed(evt);
            }
        });
        jMenu1.add(menuNew);

        menuOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuOpen.setText("Open");
        menuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenActionPerformed(evt);
            }
        });
        jMenu1.add(menuOpen);

        saveMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenu.setText("Save");
        saveMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuActionPerformed(evt);
            }
        });
        jMenu1.add(saveMenu);

        menuSaveAs.setText("Save As...");
        menuSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSaveAsActionPerformed(evt);
            }
        });
        jMenu1.add(menuSaveAs);
        jMenu1.add(jSeparator2);

        menuPageSetup.setText("Page Setup..");
        menuPageSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPageSetupActionPerformed(evt);
            }
        });
        jMenu1.add(menuPageSetup);

        menuPrint.setText("Print...");
        menuPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPrintActionPerformed(evt);
            }
        });
        jMenu1.add(menuPrint);
        jMenu1.add(jSeparator3);

        menuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        menuExit.setText("Exit");
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        jMenu1.add(menuExit);

        jMenuBar1.add(jMenu1);

        menuEdit.setText("Edit");
        menuEdit.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                menuEditMenuSelected(evt);
            }
        });

        menuUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        menuUndo.setText("Undo");
        menuUndo.setEnabled(false);
        menuUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuUndoActionPerformed(evt);
            }
        });
        menuEdit.add(menuUndo);
        menuEdit.add(jSeparator1);
        menuEdit.add(menuCut);
        menuEdit.add(menuCopy);
        menuEdit.add(menuPaste);

        menuDelete.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuDelete.setText("Delete");
        menuDelete.setEnabled(false);
        menuDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDeleteActionPerformed(evt);
            }
        });
        menuEdit.add(menuDelete);
        menuEdit.add(jSeparator4);

        menuFind.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        menuFind.setText("Find...");
        menuFind.setEnabled(false);
        menuFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFindActionPerformed(evt);
            }
        });
        menuEdit.add(menuFind);

        menuFindNext.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        menuFindNext.setText("Find Next");
        menuFindNext.setEnabled(false);
        menuFindNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFindNextActionPerformed(evt);
            }
        });
        menuEdit.add(menuFindNext);

        menuReplace.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        menuReplace.setText("Replace...");
        menuReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuReplaceActionPerformed(evt);
            }
        });
        menuEdit.add(menuReplace);

        jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem11.setText("Go To...");
        jMenuItem11.setEnabled(false);
        menuEdit.add(jMenuItem11);
        menuEdit.add(jSeparator5);

        menuSelect.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        menuSelect.setMnemonic('c');
        menuSelect.setText("Select All");
        menuSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSelectActionPerformed(evt);
            }
        });
        menuEdit.add(menuSelect);

        menuTimeDate.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        menuTimeDate.setText("Time/Date");
        menuTimeDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuTimeDateActionPerformed(evt);
            }
        });
        menuEdit.add(menuTimeDate);

        jMenuBar1.add(menuEdit);

        jMenu3.setText("Format");

        menuWordWrap.setText("Word Wrap");
        menuWordWrap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuWordWrapActionPerformed(evt);
            }
        });
        jMenu3.add(menuWordWrap);

        menuFont.setText("Font...");
        menuFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuFontActionPerformed(evt);
            }
        });
        jMenu3.add(menuFont);

        jMenuBar1.add(jMenu3);

        jMenu4.setText("View");

        menuStatusBar.setText("Status Bar");
        menuStatusBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuStatusBarActionPerformed(evt);
            }
        });
        jMenu4.add(menuStatusBar);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewActionPerformed
        saveDialog();        
    }//GEN-LAST:event_menuNewActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
       textFile.replaceSelection("");
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void menuTimeDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuTimeDateActionPerformed
        textFile.insert(new SimpleDateFormat("HH:mm yyyy-MM-dd").format(new Date()), textFile.getCaretPosition());
    }//GEN-LAST:event_menuTimeDateActionPerformed

    private void saveMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuActionPerformed
       save();         
    }//GEN-LAST:event_saveMenuActionPerformed

    private void menuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSaveAsActionPerformed
        // TODO add your handling code here:.
        saveAs();
    }//GEN-LAST:event_menuSaveAsActionPerformed

    private void menuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenActionPerformed
        if (changes) {
            saveDialog();
        }
        int status=fileChooser.showOpenDialog(null);
       if (status == JFileChooser.APPROVE_OPTION) {
           selectedFile=fileChooser.getSelectedFile();
           name=selectedFile.getName();
           setTitle(name+"-Notepad");
            try {
                String savetxt="";
                sc=new Scanner(new BufferedInputStream(new FileInputStream(selectedFile)));
                while(sc.hasNextLine()){
                    savetxt+=sc.nextLine()+"\n";
                }
                textFile.setText(savetxt);
                textFile.setCaretPosition(0);
                changes=false;
            } catch (FileNotFoundException ex) {            
            }       
       }
    }//GEN-LAST:event_menuOpenActionPerformed

    private void menuPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPrintActionPerformed
        if (pj.printDialog()) {
            try {pj.print();}
            catch (PrinterException ex) {
               //JOptionPane.showMessageDialog(this, ex, "Error", JOptionPane.ERROR_MESSAGE);
               JOptionPane.showMessageDialog(this, "The operation was cancelled by the user",
                       "NotePad", JOptionPane.WARNING_MESSAGE);
             }
        }
    }//GEN-LAST:event_menuPrintActionPerformed

    private void menuPageSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPageSetupActionPerformed
        // TODO add your handling code here:
        pj.setPrintable(printable,pj.pageDialog(pj.defaultPage()));
    }//GEN-LAST:event_menuPageSetupActionPerformed

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuExitActionPerformed
        handleClosing();
    }//GEN-LAST:event_menuExitActionPerformed

    private void menuUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuUndoActionPerformed
        if (unredo) {
             if (manager.canUndo()) {
                manager.undo();
                unredo=false;
            }
        }else{
            if(manager.canRedo()){
                manager.redo();
                unredo=true;
            }
        }
        
        
    }//GEN-LAST:event_menuUndoActionPerformed

    private void menuSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSelectActionPerformed
       textFile.selectAll();
    }//GEN-LAST:event_menuSelectActionPerformed

    private void menuFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFindActionPerformed
        find.setVisible(true);
    }//GEN-LAST:event_menuFindActionPerformed

    private void menuDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDeleteActionPerformed
        textFile.replaceSelection("");
    }//GEN-LAST:event_menuDeleteActionPerformed

    private void menuEditMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_menuEditMenuSelected
        if (cutAction.isEnabled()) {
            menuCut.setEnabled(true);
        }else{
            menuCut.setEnabled(false);
        }
        if (copyAction.isEnabled()) {
            menuCopy.setEnabled(true);
        }else{
            menuCopy.setEnabled(false);
        }
        if (pasteAction.isEnabled()) {
            menuPaste.setEnabled(true);
        }else{
            menuPaste.setEnabled(false);
        }
        if (textFile.getSelectedText() != null &&
            textFile.getSelectedText().length() <= textFile.getText().length()) {
            menuSelect.setEnabled(false);
        } else {
            menuSelect.setEnabled(true);
        }
        if (textFile.getSelectedText()!=null) {
            menuDelete.setEnabled(true);            
        } else {
            menuDelete.setEnabled(false);            
        }
        if (textFile.getText().length()!=0) {
            menuFind.setEnabled(true);            
            menuFindNext.setEnabled(true);
        } else {
            menuFind.setEnabled(false);
            menuFindNext.setEnabled(false);
        }
    }//GEN-LAST:event_menuEditMenuSelected

    private void menuWordWrapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuWordWrapActionPerformed
        if (menuWordWrap.isSelected()) {
            textFile.setLineWrap(true);
            textFile.setWrapStyleWord(true);
        }else{
            textFile.setLineWrap(false);
            textFile.setWrapStyleWord(false);
        }
    }//GEN-LAST:event_menuWordWrapActionPerformed

    private void menuFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFontActionPerformed
        chooser.setSelectedFont(font);
        chooser.showDialog(this);
       font=chooser.getSelectedFont();
       textFile.setFont(font);
    }//GEN-LAST:event_menuFontActionPerformed

    private void menuStatusBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuStatusBarActionPerformed
        if (menuStatusBar.isSelected()) {
            statusBar.setVisible(true);
            // Add a caretListener to the editor. This is an anonymous class because it is inline and has no specific name.
            textFile.addCaretListener(caretListener);
            try {
                labellncol.setText("Ln "+(textFile.getLineOfOffset(textFile.getCaretPosition())+1)+", Col "+(textFile.getCaretPosition() - textFile.getLineStartOffset(textFile.getLineOfOffset(textFile.getCaretPosition()))+1));
            } catch (BadLocationException ex) {
            }
        }else{
            statusBar.setVisible(false);
            textFile.removeCaretListener(caretListener);
        }
    }//GEN-LAST:event_menuStatusBarActionPerformed

    private void menuFindNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuFindNextActionPerformed
        find.findNextMenu();
    }//GEN-LAST:event_menuFindNextActionPerformed

    private void menuReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuReplaceActionPerformed
       findReplace.setVisible(true);
    }//GEN-LAST:event_menuReplaceActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            /* for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
            javax.swing.UIManager.setLookAndFeel(info.getClassName());
            break;
            }*/
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException ex) {
            Logger.getLogger(Notepad.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Notepad.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Notepad.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Notepad.class.getName()).log(Level.SEVERE, null, ex);
        }
         
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Notepad().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JLabel labellncol;
    private javax.swing.JMenuItem menuCopy;
    private javax.swing.JMenuItem menuCut;
    private javax.swing.JMenuItem menuDelete;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenuItem menuFind;
    private javax.swing.JMenuItem menuFindNext;
    private javax.swing.JMenuItem menuFont;
    private javax.swing.JMenuItem menuNew;
    private javax.swing.JMenuItem menuOpen;
    private javax.swing.JMenuItem menuPageSetup;
    private javax.swing.JMenuItem menuPaste;
    private javax.swing.JMenuItem menuPrint;
    private javax.swing.JMenuItem menuReplace;
    private javax.swing.JMenuItem menuSaveAs;
    private javax.swing.JMenuItem menuSelect;
    private javax.swing.JCheckBoxMenuItem menuStatusBar;
    private javax.swing.JMenuItem menuTimeDate;
    private javax.swing.JMenuItem menuUndo;
    private javax.swing.JCheckBoxMenuItem menuWordWrap;
    private javax.swing.JMenuItem saveMenu;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JToolBar statusBar;
    private javax.swing.JTextArea textFile;
    // End of variables declaration//GEN-END:variables

    private boolean saveDialog() {
        if (changes) {
            String msg="<html><body><p style='color: blue; font-size:16pt'>Do you want to save changes to "
                    +name+"?</p></body></html>";
            int option=JOptionPane.showOptionDialog(this, msg,
                    "Notepad", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
                    null, new String[]{"Save","Don't Save","Cancel"}, "Save");
            if (option==0) {
                return save();
            }else if(option==2){
               return CANCEL;
            }
             //if option is Don't save
                textFile.setText("");
                name="Untitled";
                setTitle(name+"-Notepad");
                changes=false;
        }else{//if no changes
            textFile.setText("");
            name="Untitled";
            setTitle(name+"-Notepad");
        }
        return OK;
    }

    private boolean save() {
        if("Untitled".equals(name)){
           return saveAs();
       }else{           
           try( BufferedWriter bw=new BufferedWriter(new FileWriter(selectedFile))) {              
               textFile.write(bw);
               changes=false;               
           } catch (IOException ex) {
               JOptionPane.showMessageDialog(this, ex, "IOError", JOptionPane.ERROR_MESSAGE);
           }
        }
        return OK;
    }

    private boolean saveAs() {
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("TextDocuments(*.txt)" ,"txt");
        saveAsFileChooser.setDialogTitle("Save As");
        saveAsFileChooser.setApproveButtonText("Save");
      saveAsFileChooser.setFileFilter(extensionFilter);
      int actionDialog = saveAsFileChooser.showOpenDialog(this);
      if (actionDialog != JFileChooser.APPROVE_OPTION) {
         return CANCEL;
      }

      // !! File fileName = new File(SaveAs.getSelectedFile() + ".txt");
      selectedFile = saveAsFileChooser.getSelectedFile();
      name=selectedFile.getName();
      if (!selectedFile.getName().endsWith(".txt")) {
         selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
      }
      return save();     
    }
    private void handleClosing(){
        if (saveDialog()!=CANCEL) {
            dispose();
        }
    }
    class Printer implements Printable {

            @Override
            public int print(Graphics pg, PageFormat pageFormat, int pageIndex) throws PrinterException {
                pg.translate((int)pageFormat.getImageableX(),  
        (int)pageFormat.getImageableY());  
        int wPage = (int)pageFormat.getImageableWidth();  
        int hPage = (int)pageFormat.getImageableHeight();  
        pg.setClip(0, 0, wPage, hPage);  
          
        // Only do this once per print  
        if (m_printView == null) {  
            BasicTextUI btui = (BasicTextUI)textFile.getUI();  
            View root = btui.getRootView( textFile );  
            m_printView = new PrintView(  
            m_doc.getDefaultRootElement(),  
            root, wPage, hPage);  
        }  
          
        boolean bContinue = m_printView.paintPage(pg,  
        hPage, pageIndex);  
        System.gc();  
          
        if (bContinue)  
            return PAGE_EXISTS;  
        else {  
            m_printView = null;  
            return NO_SUCH_PAGE;  
        } 
            }
        }
}

