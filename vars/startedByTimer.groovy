// vars/startedByTimer.groovy

/**
 * Return the reason for the build
 * 
 * This needs to be here rather than in pipeline-lib or Jenkinsfile due to
 * Scripts not permitted to use method net.sf.json.JSON isEmpty.
 */
Boolean call(Map config = [:]) {

  return !currentBuild.getBuildCauses('hudson.triggers.TimerTrigger$TimerTriggerCause').isEmpty()

}
