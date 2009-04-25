package org.mazur.gromula.model.queues

import java.util.LinkedListimport org.mazur.gromula.model.Request

/**
 * FIFO queue.
 * 
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class FIFOQueue implements Queue {

  /** Real queue. */
  private LinkedList<Request> queue = new LinkedList<Request>()
  
  Request get() { queue.poll() }
  
  void add(Request r) { queue.addLast(r) }
  
}
