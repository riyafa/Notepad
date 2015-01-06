/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Notepad;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

/**
 *
 * @author A.H.F. Riyafa
 */
 public abstract class BasicAction extends AbstractAction{
    JTextComponent comp;
    
    public BasicAction(String text, Icon icon) {
      super(text, icon);
      putValue(Action.SHORT_DESCRIPTION, text);
    }
    public void setTextComponent(JTextComponent comp){
      this.comp = comp;
    }
    @Override
    public abstract void actionPerformed(ActionEvent e);
  }
  class CutAction extends BasicAction {
    public CutAction(String text, Icon icon) {
      super(text, icon);
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl X"));
    }
    @Override
    public void actionPerformed(ActionEvent e){
      comp.cut();
    }
    @Override
    public boolean isEnabled(){
      return comp != null && comp.isEditable() && comp.getSelectedText() != null;
    }
  }
   class CopyAction extends BasicAction{
    public CopyAction(String text, Icon icon){
      super(text,icon);
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl C"));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      comp.copy();
    }
    @Override
    public boolean isEnabled() {
      return comp != null && comp.getSelectedText() != null;
    }
  }
   class PasteAction extends BasicAction{
    public PasteAction(String text, Icon icon){
      super(text,icon);
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl V"));
    }
    public void actionPerformed(ActionEvent e) {
      comp.paste();
    }
    @Override
    public boolean isEnabled() {
      Transferable content = Toolkit.getDefaultToolkit().getSystemClipboard()
.getContents(null);
      return comp != null && comp.isEnabled() && comp.isEditable() 
            && content.isDataFlavorSupported(DataFlavor.stringFlavor);
    }
  }

   class SelectAllAction extends BasicAction{
    public SelectAllAction(String text, Icon icon){
      super(text,icon);
      putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl A"));
    }
    @Override
    public void actionPerformed(ActionEvent e){
      comp.selectAll();
    }
    @Override
    public boolean isEnabled() {
      return comp != null && comp.isEnabled() && comp.getText().length() > 0 
          && (comp.getSelectedText() == null || 
            comp.getSelectedText().length() < comp.getText().length());
    }
  }
