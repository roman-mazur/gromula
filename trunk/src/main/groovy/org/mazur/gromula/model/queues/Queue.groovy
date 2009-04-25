package org.mazur.gromula.model.queues

import org.mazur.gromula.model.Request

/**
 * Queue interface.
 * 
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public interface Queue {

  /** Get the request. */
  Request get()
  
  /** Add the request. */
  void add(Request r)
  
}
