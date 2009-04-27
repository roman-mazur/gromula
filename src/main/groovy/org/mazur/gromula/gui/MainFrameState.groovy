package org.mazur.gromula.gui

import org.mazur.gromula.Documentimport groovy.swing.SwingBuilderimport org.mazur.gromula.Utilsimport groovy.beans.Bindable
import java.io.Fileimport java.io.FileWriter
/**
 * State of the main frame.
 * 
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class MainFrameState {

  /** Map with documents and text areas. */
  private def documentAreaMap = [:]
  
  /** Active document. */
  Document activeDocument
  
  /** Last index for the pain. */
  private int lastIndex = -1
  
  /**
   * @return active code area
   */
  def getActiveCodeArea() {
    return documentAreaMap[activeDocument]
  }
  
  /**
   * Add the new document.
   */
  void newDocument() {
    ++lastIndex
    def d = new Document(index : lastIndex)
    SwingBuilder.build() {
      documentAreaMap[d] = textArea(text : 'Replace it', font : Utils.createCodeFont())
    }
    activeDocument = d
  }
  
  /**
   * Open a document.
   */
  void openDocument(final File file) {
    ++lastIndex
    def d = new Document(index : lastIndex, name : file.name, sourceFile : file)
    SwingBuilder.build() {
      documentAreaMap[d] = textArea(text : file.text, font : Utils.createCodeFont())
    }
    activeDocument = d
  }
  
  /**
   * 'Save as' a document
   */
  void saveDocument(final File file) {
    activeDocument.sourceFile = file
    activeDocument.name = file.name
    saveDocument()
  }
  
  /**
   * Save a document.
   */
  boolean saveDocument() {
    if (!activeDocument.sourceFile) { return false }
    def w = new FileWriter(activeDocument.sourceFile)
    w << documentAreaMap[activeDocument].text
    w.close()
    return true
  }
}
