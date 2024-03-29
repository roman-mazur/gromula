package org.mazur.gromula.model

/**
 * Request (token).
 * 
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class Request {

  /** Request priority. */
  int priority = 5
  
  /** Request weight. */
  int weight

  /** Create time. */
  int createTime
  
  String toString() { return "Request[priority : $priority, weight : $weight, createTime: $createTime]" }
}
