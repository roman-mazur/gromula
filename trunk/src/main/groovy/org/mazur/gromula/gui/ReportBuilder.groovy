package org.mazur.gromula.gui

import groovy.swing.SwingBuilder
import org.mazur.gromula.model.Reportimport org.mazur.gromula.model.Deviceimport java.awt.BorderLayout as BLimport javax.swing.SwingConstants as SC

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class ReportBuilder extends SwingBuilder{

  private Report report
  
  private def valuesPanel(Device d, String caption, String prop) {
    def list = ['min', 'avg', 'max']
    return panel() {
      borderLayout()
      label(text : caption, constraints : BL.NORTH, border : raisedEtchedBorder(),
            horizontalAlignment : SC.CENTER)
      panel(constraints : BL.CENTER) {
        list.each() { def p ->
          panel() {
            gridLayout(cols : 1, rows : 2)
            label(text : "$p", horizontalAlignment : SC.CENTER)
            label(text : d."$p$prop", horizontalAlignment : SC.CENTER)
          }
        }
      }
    }
  }
  
  private def devicesPanel(def list) {
    if (list.empty) { return }
    int cc = 5
    int rc = list.size().div(cc)
    if (list.size() % cc) { ++rc }
    return panel() {
      gridLayout(rows : rc, cols : cc)
      list.each() { def device ->
        panel(border : raisedEtchedBorder()) {
          borderLayout()
          panel(constraints : BL.NORTH) {
            gridLayout(rows : 3, cols : 1)
            label(text : "Name: ${device.name}")
            label(text : "Count of processed requests: ${device.processedRequestsCount}")
            label(text : "Efficiency: ${device.efficiency}")
          }
          panel(constraints : BL.CENTER) {
            gridLayout(rows : 2, cols : 1)
            this.valuesPanel(device, "Queue size", "QueueSize")
            this.valuesPanel(device, "Wait time", "WaitTime")
          }
        }
      }
    }
  }
  
  def reportFrame = { Map args ->
    report = args['report']
    assert report != null
    return frame(title : "Gromula report ${report.createDate}") {
      borderLayout()
      panel(constraints : BL.NORTH) {
        label(text : "Total count of requests: ${report.totalCountOfRequests}", horizontalAlignment : SC.CENTER)
        label(text : "Total time: ${report.totalTime}", horizontalAlignment : SC.CENTER)
      }
      tabbedPane(constraints : BL.CENTER) {
        panel(title : 'Processors') {
          this.devicesPanel(report.processorsList)
        }
        panel(title : 'Storages') {
          this.devicesPanel(report.storagesList)
        }
      }
    }
  }
  
}
