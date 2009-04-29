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
public abstract class Device {

  /** Count of the processed requests. */
  int processedRequestsCount = 0
  private int totalRequests = 0
  
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
  public boolean request(final Request request) {
    ++totalRequests
    if (canProcess(request)) {
      process(request)
      return true
    } else {
      queue.add(request)
      return false
    }
  }
  
  /**
   * Release the device.
   * @param request request instance
   */
  public abstract void release(final Request request)
  
  public void prepareForReport(int totalTime) {
    queue.blockLengthAnalyze = true
    while (queue.get());
  }
  
  public Integer getMinQueueSize() { return queue.minLength }
  public Integer getMaxQueueSize() { return queue.maxLength }
  public Double getAvgQueueSize() { return queue.avgLength }
  public Integer getMinWaitTime() { return queue.minWaitTime }
  public Integer getMaxWaitTime() { return queue.maxWaitTime }
  public Double getAvgWaitTime() { return totalRequests ? queue.sumWaitTime / totalRequests : null }
  public abstract Double getEfficiency()
}
