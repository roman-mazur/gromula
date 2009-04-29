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
public abstract class Queue {

  /** Length. */
  private Integer minLength, maxLength, avgLength = 0
  /** Count of operations. */
  private int countOfOperations = 0
  /** Wait times. */
  private int minWaitTime, maxWaitTime, sumWaitTime
  
  /** Get time clasure. */
  def getTime
  
  boolean blockLengthAnalyze = false
  
  private void analyzeLength() {
    if (blockLengthAnalyze) { return }
    int s = size()
    if (minLength == null || minLength > s) { minLength = s }
    if (maxLength == null || maxLength < s) { maxLength = s }
    avgLength += s
    ++countOfOperations
  }
  
  private void analyzeWaitTime(final Request r) {
    int w = getTime() - r.createTime
    if (minWaitTime == null || minWaitTime > w) { minWaitTime = w }
    if (maxWaitTime == null || maxWaitTime < w) { maxWaitTime = w }
    sumWaitTime += w
  }
  
  Double getAvgLength() { return countOfOperations ? avgLength / countOfOperations : null }
  Integer getMinLength() { return minLength }
  Integer getMaxLength() { return maxLength }
  Integer getMinWaitTime() { return minWaitTime }
  Integer getMaxWaitTime() { return maxWaitTime }
  Integer getSumWaitTime() { return sumWaitTime }
  
  /** Get the request. */
  public Request get() {
    Request r = getRequest()
    analyzeLength()
    if (r) { analyzeWaitTime(r) } 
    return r
  }
  
  /** Add the request. */
  public void add(Request r) {
    addRequest(r)
    analyzeLength()
  }
  
  /** Get first request having the weight less than the defined one. */
  public Request getFirst(int marginWeight) {
    Request r = getFirstRequest(marginWeight)
    analyzeLength()
    if (r) { analyzeWaitTime(r) } 
    return r
  }
  
  /** Get the queue size. */
  public abstract int size()

  /** Get the request. */
  protected abstract Request getRequest()
  
  /** Add the request. */
  protected abstract void addRequest(Request r)
  
  /** Get first request having the weight less than the defined one. */
  protected abstract Request getFirstRequest(int marginWeight)
  
}
