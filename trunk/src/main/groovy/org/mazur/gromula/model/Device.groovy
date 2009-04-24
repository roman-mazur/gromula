package org.mazur.gromula.model



/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public abstract class Device{

  /** Device name. */
  String name
  
  String toString() { return "[${getClass().simpleName} '$name']" }
  
}
