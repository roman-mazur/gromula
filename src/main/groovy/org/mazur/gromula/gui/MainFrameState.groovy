package org.mazur.gromula.gui

import org.mazur.gromula.Documentimport groovy.swing.SwingBuilderimport org.mazur.gromula.Utilsimport groovy.beans.Bindable

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
  
  /** Log. */
  @Bindable
  String log
  
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
    def d = new Document()
    SwingBuilder.build() {
      documentAreaMap[d] = textArea(text : 'Replace it', font : Utils.createCodeFont())
    }
    activeDocument = d
  }
  
}
