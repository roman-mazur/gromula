package org.mazur.gromula.model



/**
 * Kind of devices that can storage data according to the request.
 * 
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class Storage extends Device {
  /** Storage amount. */
  int totalAmout
  /** Current size. */
  int currentSize

  protected boolean canProcess(final Request request) {
    return currentSize + request.weight <= totalAmout
  }
  
  protected void process(final Request request) {
    currentSize += request.weight
  }
}
