package org.mazur.gromula.model.queues

import java.util.LinkedListimport org.mazur.gromula.model.Requestimport java.util.ListIterator

/**
 * FIFO queue.
 * 
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class FIFOQueue extends Queue {

  /** Real queue. */
  private LinkedList<Request> queue = new LinkedList<Request>()
  
  protected Request getRequest() { queue.poll() }
  
  protected void addRequest(Request r) { queue.addLast(r) }
  
  protected Request getFirstRequest(final int margin) {
    Iterator<Request> iterator = queue.iterator()
    Request result = null
    while (iterator.hasNext()) {
      Request r = iterator.next()
      if (r.weight < margin) {
        result = r
        iterator.remove()
        break
      }
    }
    return result
  }
  
  int size() { return queue.size() }
}
