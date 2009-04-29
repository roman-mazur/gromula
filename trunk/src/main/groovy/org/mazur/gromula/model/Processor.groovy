package org.mazur.gromula.model

import org.mazur.gromula.model.queues.Queue

/**
 * Kind of devices that can process requests.
 * 
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class Processor extends Device{

  /** Device state. */
  boolean busy
  
  private Request currentRequest
  
  private double efficiency
  private int workTime
  
  protected boolean canProcess(final Request request) {
    return !busy
  }
  
  protected void process(final Request request) {
    ++processedRequestsCount
    busy = true
    currentRequest = request
  }
  
  public void release(final Request request) {
    workTime += request.weight
    busy = false
  }
  
  public void prepareForReport(int totalTime) {
    super.prepareForReport(totalTime)
    efficiency = workTime / totalTime
  }
  
  public Double getEfficiency() {
    return efficiency
  }
}
