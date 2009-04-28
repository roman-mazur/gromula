package org.mazur.gromula

import groovy.lang.Bindingimport org.codehaus.groovy.control.CompilerConfigurationimport java.io.StringWriterimport java.io.PrintWriterimport groovy.lang.Scriptimport org.mazur.gromula.model.Event
import java.util.Randomimport org.mazur.gromula.model.Storageimport org.mazur.gromula.model.Processorimport org.mazur.gromula.model.queues.QueuesFactoryimport org.mazur.gromula.model.Requestimport org.mazur.gromula.model.queues.Queueimport java.lang.IllegalArgumentExceptionimport org.mazur.gromula.InterpreterException

/**
 * Version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
class ProgramInterpreter {

  /** Names of objects to bind. */
  private static final def BIND_LIST = [
    'event', 'processor', 'storage', 'start', 'fire',
    'request',
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
  
  boolean runScript(def scriptCode, def log, def error) {
    Context ctx = new Context()
    CommonClasures clasures = new CommonClasures(ctx)
    Binding binding = new Binding()
    BIND_LIST.each() { binding."$it" = clasures."$it" }
    clasures.doLog = {
      def msg = "T ${ctx.time}: $it"
      log(msg.toString())
    }
    def shell = new GroovyShell(binding, compilerConf)
    try {
      def res = shell.evaluate(scriptCode)
      return true
    } catch (InterpreterException e) {
      error(e.message)
    } catch (Exception e) {
      StringWriter sw = new StringWriter()
      PrintWriter p = new PrintWriter(sw)
      e.printStackTrace(p)
      error("-----------------\nCompilation error:\n${sw}\n-----------------")
    }
    return false
  }
  
  
}

class Context {
  /** Internal time. */
  long time
  /** Context maps */
  def eventsMap = [:], devicesMap = [:]
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
  
  private String processorName(def name) { return "_processor_$name" }
  private String storageName(def name) { return "_storage_$name" }
  
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
    if (ctx.eventsMap[e.name]) {
      throw new InterpreterException("Event with name ${e.name} is already declared.") 
    }
    ctx.eventsMap[e.name] = e
  }
  
  private Queue createQueue(def type) {  
    def qName = type?.toLowerCase()
    if (!qName) { qName = 'fifo' }
    return queuesFactory."$qName"()
  }
  
  /** Declare a processor. */
  def processor = { Map args ->
    def q = createQueue(args['queue'])
    Processor p = new Processor(name : args['name'], queue : q)
    def n = processorName(p.name)
    if (ctx.devicesMap[n]) { 
      throw new InterpreterException("Processor with name ${p.name} is already declared.") 
    }
    ctx.devicesMap[n] = p
    log("$p was initialized")
    return p
  }

  /** Declare a storage. */
  def storage = { Map args ->
    def q = createQueue(args['queue'])
    Storage s = new Storage(name : args['name'], queue : q)
    def n = storageName(s.name)
    if (ctx.devicesMap[n]) { 
      throw new InterpreterException("Storage with name ${s.name} is already declared.") 
    }
    ctx.devicesMap[n] = s
    log("$s was initialized")
    return s
  }
  
  /** Start point. */
  def start = { it() }
  
  def fire = { String eName -> callEvent(ctx.eventsMap[eName]) }
  
  /** Request the device. */
  def request = { String deviceName, int w, int p = 5 ->
    Request r = new Request(priority : p, weight : w)
    def d = devicesMap[deviceName]
    d.request(r)
    return r
  }
  
  /** Get a random number [0;1). */
  def uniform = { return randomGen.nextFloat() }
  
  /** Get a random number with the normal distribution. */
  def normal = { return randomGen.nextGaussian() }
}
