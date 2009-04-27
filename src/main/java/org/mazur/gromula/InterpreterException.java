package org.mazur.gromula;

/**
 * Interpreter exception.
 * 
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class InterpreterException extends RuntimeException {

  /** serialVersionUID. */
  private static final long serialVersionUID = -5444530566901636757L;

  /**
   * @param msg message
   */
  public InterpreterException(final String msg) {
    super(msg);
  }
  
}
