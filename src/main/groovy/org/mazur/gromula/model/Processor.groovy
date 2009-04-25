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
  
  /** Requests queue. */
  Queue queue
  
}
