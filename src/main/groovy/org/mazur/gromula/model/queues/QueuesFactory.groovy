package org.mazur.gromula.model.queues



/**
 * Queues factory.
 * 
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class QueuesFactory {

  /** FIFO queue. */
  def fifo = { new FIFOQueue(getTime : it) }
  
  /** Priority queue. */
  def priority = { new PriorityQueue(getTime : it) }
  
}
