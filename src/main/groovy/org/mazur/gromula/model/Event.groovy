package org.mazur.gromula.model



/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class Event{

  /** Event name. */
  String name
  /** Action to perform. */
  def action
  
  String toString() { return "[Event '$name']" }
  
}
