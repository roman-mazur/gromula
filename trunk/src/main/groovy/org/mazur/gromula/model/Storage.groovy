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

  int processedAmount = 0
  
  protected boolean canProcess(final Request request) {
    return currentSize + request.weight <= totalAmout
  }
  
  protected void process(final Request request) {
    ++processedRequestsCount
    if (request.weight > 0) { processedAmount += request.weight }
    currentSize += request.weight
    assert currentSize >= 0
    Request nextR = queue.getFirst(currentSize)
    if (nextR) { process(nextR) }
  }
  
  void release(final Request request) {
    currentSize -= request.weight
    assert currentSize >= 0
  }
  
  public Double getEfficiency() {
    return processedAmount / totalAmout
  }
}
