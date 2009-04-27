package org.mazur.gromula.model

import org.mazur.gromula.model.queues.Queue

/**
 * Abstract device.
 * 
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public abstract class Device{

  /** Device name. */
  String name
  
  /** Requests queue. */
  Queue queue
  
  String toString() { return "[${getClass().simpleName} '$name']" }
  
  /**
   * @param request request instance
   * @return true if the device can process the request at the current time
   */
  protected abstract boolean canProcess(final Request request)
  
  /**
   * Process the request.
   * @param request request instance
   */
  protected abstract void process(final Request request)
  
  /**
   * Request the device.
   * @param request request instance
   */
  public void request(final Request request) {
    if (canProcess(request)) {
      process(request)
    } else {
      queue.add(request)
    }
  }
}
