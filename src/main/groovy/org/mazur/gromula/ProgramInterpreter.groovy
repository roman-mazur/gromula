package org.mazur.gromula

import groovy.lang.Bindingimport org.codehaus.groovy.control.CompilerConfigurationimport java.io.StringWriterimport java.io.PrintWriterimport groovy.lang.Scriptimport org.mazur.gromula.model.Event
import java.util.Randomimport org.mazur.gromula.model.Storageimport org.mazur.gromula.model.Processorimport org.mazur.gromula.model.queues.QueuesFactory
/**
 * Version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
class ProgramInterpreter {

  /** Names of objects to bind. */
  private static final def BIND_LIST = [
    'event', 'processor', 'storage', 'start', 'fire', 
    'uniform', 'normal',
    'log'
  ]
  
  /** Compiler configuration. */
  private CompilerConfiguration compilerConf
  
  ProgramInterpreter() {
    compilerConf = new CompilerConfiguration()
    compilerConf.debug = true
    compilerConf.recompileGroovySource = true
  }
  
  void runScript(def scriptCode, def log) {
    Context ctx = new Context()
    CommonClasures clasures = new CommonClasures(ctx)
    Binding binding = new Binding()
    BIND_LIST.each() { binding."$it" = clasures."$it" }
    clasures.doLog = {
      def msg = "T ${ctx.time}: $it"
      log(msg.toString())
    }
    def shell = new GroovyShell(binding, compilerConf)
    def res = shell.evaluate(scriptCode)
  }
  
  
}

class Context {
  /** Internal time. */
  long time
  /** Context maps */
  def eventsMap = [:], processorsMap = [:], storagesMap = [:]
}

class CommonClasures {
  private def doLog
  /** Model context. */
  private Context ctx
  /** Random generator, */
  private Random randomGen = new Random()
  /** Queues factory. */
  private QueuesFactory queuesFactory = new QueuesFactory()
  
  /** Constrcutor with the context. */
  CommonClasures(def ctx) { this.ctx = ctx }
  
  /** Call event. */
  private void callEvent(Event e) {
    println "Calling $e"
    ctx.time++
    e.action()
  }
  
  /** Logging for a user. */
  def log = { doLog(it) }
  
  /** Declare an event. */
  def event = { Map args, def action = {} ->
    Event e = new Event()
    e.name = args['name']
    e.action = action
    ctx.eventsMap[e.name] = e
  }
  
  /** Declare a processor. */
  def processor = { Map args ->
    Processor p = new Processor(name : args['name'])
    def qName = args['queue']?.toLowerCase()
    if (!qName) { qName = 'fifo' }
    p.queue = queuesFactory."$qName"()
    ctx.processorsMap[p.name] = p
    log("$p was initialized")
  }

  /** Declare a storage. */
  def storage = { Map args ->
    Storage s = new Storage(name : args['name'])
    ctx.storagesMap[s.name] = s
    log("$s was initialized")
  }
  
  /** Start point. */
  def start = { it() }
  
  def fire = { String eName -> callEvent(ctx.eventsMap[eName]) }
  
  /** Get random number [0;1). */
  def uniform = { return randomGen.nextFloat() }
  
  /** Get random number with the Gaus distribution. */
  def normal = { return randomGen.nextGaussian() }
}
