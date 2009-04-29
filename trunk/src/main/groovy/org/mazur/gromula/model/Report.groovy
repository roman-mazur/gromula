package org.mazur.gromula.model



/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class Report {

  /** Create date. */
  Date createDate = new Date()
  
  /** Lists of devices to report. */
  def processorsList, storagesList
  
  /** Total count of requests. */
  int totalCountOfRequests
  
  /** Total time. */
  int totalTime
}
