package org.mazur.gromula

import groovy.lang.Bindingimport org.codehaus.groovy.control.CompilerConfigurationimport java.io.StringWriterimport java.io.PrintWriterimport groovy.lang.Scriptimport org.mazur.gromula.model.Event
import java.util.Randomimport org.mazur.gromula.model.Storageimport org.mazur.gromula.model.Processorimport org.mazur.gromula.model.queues.QueuesFactoryimport org.mazur.gromula.model.Requestimport org.mazur.gromula.model.queues.Queueimport java.lang.IllegalArgumentExceptionimport org.mazur.gromula.InterpreterException
import java.util.BitSetimport org.mazur.gromula.model.Deviceimport org.mazur.gromula.model.Reportimport org.mazur.gromula.model.Report
/**
 * Version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
class ProgramInterpreter {

  /** Names of objects to bind. */
  private static final def BIND_LIST = [
    'start', 'setMaxTime',
    'event', 'processor', 'storage',
    'requestProcessor', 'requestStorage', 'cancel', 'schedule',
    'uniform', 'normal', 'expntl', 'hyperexp',
    'queueSize', 'time',
    'log'
  ]
  
  /** Compiler configuration. */
  private CompilerConfiguration compilerConf
  
  /** Last report. */
  Report lastReport
  
  ProgramInterpreter() {
    compilerConf = new CompilerConfiguration()
    compilerConf.debug = true
    compilerConf.recompileGroovySource = true
  }
  
  private void formReport(Context ctx) {
    lastReport = new Report(processorsList : [], storagesList : [])
    ctx.devicesMap.each() {
      def d = it.value
      d.prepareForReport(ctx.time)
      if (d instanceof Processor) { lastReport.processorsList += d }
      if (d instanceof Storage) { lastReport.storagesList += d }
    }
    lastReport.totalCountOfRequests = ctx.totalCountOfRequests
    lastReport.totalTime = ctx.time
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
      formReport(ctx)
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
  int totalCountOfRequests
  /** Internal time. */
  private int time = 0, maxTime = -1, lastTime = 0
  /** Context maps */
  def eventsMap = [:], devicesMap = [:]
  /** Time line. */
  private def timeLine = [:]
  private BitSet closeTimes = new BitSet()
  
  /** Schedule the time. */
  void schedule(def e, int delay) {
    int time = this.time + delay
    if (!timeLine[time]) { timeLine[time] = new LinkedList() }
    timeLine[time] += e
    closeTimes.set(time)
  }
  
  /** Main method. */
  void work(def log) {
    while (time < maxTime || maxTime < 0) {
      time = closeTimes.nextSetBit(time)
      if (time < 0) { break }
      def currentEvents = new ArrayList(timeLine[time])
      currentEvents.eachWithIndex() { def e, int index ->
        log("Event ${index + 1}: ${e.name}")
        e.action()
      }
      lastTime = time
      ++time
    }
    time = lastTime
  }
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
    return queuesFactory."$qName"(time)
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
    Storage s = new Storage(name : args['name'], queue : q, totalAmout : args['size'])
    def n = storageName(s.name)
    if (ctx.devicesMap[n]) { 
      throw new InterpreterException("Storage with name ${s.name} is already declared.") 
    }
    ctx.devicesMap[n] = s
    log("$s was initialized")
    return s
  }
  
  /** Set the maximum time. */
  def setMaxTime = { int t -> ctx.maxTime = t }
  
  /** Start point. */
  def start = { 
    it()
    ctx.work(log)
  }
  
  private void requestP(final Processor d, final Request r) {
    boolean s = d.request(r)
    if (s) {
      Event releaseEvent = new Event(name : 'releaseProcessorEvent')
      releaseEvent.action = { 
        d.release(r)
        Request nextR = d.queue.get()
        if (nextR) { requestP(d, nextR) }
      }
      ctx.schedule(releaseEvent, r.weight)
    }
  }
  
  /** Request the processor. */
  def requestProcessor = { String deviceName, int w, int p = 5 ->
    if (w < 1) { throw new InterpreterException("Bad weight value: $w") }
    ++ctx.totalCountOfRequests
    Request r = new Request(priority : p, weight : w, createTime : ctx.time)
    Processor d = ctx.devicesMap[processorName(deviceName)]
    requestP(d, r)
    return r
  }

  /** Request the storage. */
  def requestStorage = { String deviceName, int w, int p = 5 ->
    if (w < 1) { throw new InterpreterException("Bad weight value: $w") }
    ++ctx.totalCountOfRequests
    Request r = new Request(priority : p, weight : w, createTime : ctx.time)
    Storage d = ctx.devicesMap[storageName(deviceName)]
    d.request(r)
    return r
  }
  
  /** Cancel the event. */
  def cancel = { String eName -> ctx.eventsMap -= eName }

  /** Schedule the event. */
  def schedule = { String eName, int delay ->
    if (delay < 1) { throw new InterpreterException("Bad delay value: $delay") }
    def e = ctx.eventsMap[eName]
    ctx.schedule(e, delay)
  }
  
  /** Get a random number [0;1). */
  def uniform = { return randomGen.nextFloat() }
  
  /** Get a random number with the normal distribution. */
  def normal = { return randomGen.nextGaussian() }
  
  /** Get a random number with the exponential distribution. */
  def expntl = {
    float x = uniform()
    return (float)((-1 / it) * (Math.log(1 - x)))
  }
  
  /** Get a random number with the hyperexp distribution. */
  def hyperexp = { g, lambda ->
    double fi = 0.5 - Math.sqrt(0.25 - 1 / (2 * g + 2))
    float x = uniform()
    double a = x < fi ? 2 * fi * lambda : 2 * (1 - fi) * lambda
    return -Math.log(x) / a
  }
  
  def queueSize = { String deviceName ->
    def d = ctx.devicesMap[processorName(deviceName)]
    if (!d) { d = ctx.devicesMap[storageName(deviceName)] }
    return d.queue.size()
  }
  
  def time = { return ctx.time }
}
