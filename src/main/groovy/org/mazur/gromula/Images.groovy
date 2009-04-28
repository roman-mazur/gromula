package org.mazur.gromula

import javax.swing.Iconimport javax.swing.ImageIcon

/**
 * Images.
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class Images {

  /** Images map. */
  private def imagesMap = [:]
  
  public Icon get(def name) {
    Icon result = imagesMap[name]
    if (result) { return result }
    def resource = Images.class.getResource('/buttons/' + name.toLowerCase() + '.ico')
    result = new ImageIcon(resource)
    imagesMap[name] = result
    return result
  }
  
}
