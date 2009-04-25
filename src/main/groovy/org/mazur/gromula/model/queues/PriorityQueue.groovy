package org.mazur.gromula.model.queues

import java.util.PriorityQueue as PQ
import java.util.Comparatorimport org.mazur.gromula.model.Request

/**
 * Queue to process requests with prioroties.
 * 
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class PriorityQueue implements Queue {
  /** Initial capacity. */
  private static final int INIT_CAPACITY = 10
  /** Real queue. */
  private PQ<Request> queue = new PQ(INIT_CAPACITY, new RequestsComparator()) 
  
  Request get() { return queue.poll() }
  
  void add(Request r) { queue.add(r) }
  
}

/**
 * Requests comparator.
 */
class RequestsComparator implements Comparator<Request> {
  int compare(Request r1, Request r2) { return r1.priority - r2.priority }
}
