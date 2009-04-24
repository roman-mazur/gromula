package org.mazur.gromula

import groovy.lang.Bindingimport org.codehaus.groovy.control.CompilerConfigurationimport java.io.StringWriterimport java.io.PrintWriterimport groovy.lang.Scriptimport org.mazur.gromula.model.Event
import java.util.Randomimport org.mazur.gromula.model.Storageimport org.mazur.gromula.model.Processor
/**
 * Version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
class ProgramInterpreter {

  /** Names of objects to bind. */
  private static final def BIND_LIST = [
    'event', 'processor', 'storage', 'start', 'fire', 'random', 'randomGaus',
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
  private Context ctx
  private Random randomGen
  
  CommonClasures(def ctx) { this.ctx = ctx }
  
  private void callEvent(Event e) {
    println "Calling $e"
    ctx.time++
    e.action()
  }
  
  def log = { doLog(it) }
  
  def event = { Map args, def action = {} ->
    Event e = new Event()
    e.name = args['name']
    e.action = action
    ctx.eventsMap[e.name] = e
  }
  
  def processor = { Map args ->
    Processor p = new Processor(name : args['name'])
    ctx.processorsMap[p.name] = p
    log("$p was initialized")
  }

  def storage = { Map args ->
    Storage s = new Storage(name : args['name'])
    ctx.storagesMap[s.name] = s
    log("$s was initialized")
  }
  
  def start = { it() }
  
  def fire = { String eName -> callEvent(ctx.eventsMap[eName]) }
  
  def random = { return randomGen.nextFloat() }
  
  def randomGaus = { return randomGen.nextGaussian() }
}
