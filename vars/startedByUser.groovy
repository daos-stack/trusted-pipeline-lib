// vars/startedByTimer.groovy

/**
 * Return True if the build was caused by a User (I.e. Build Now)
 * 
 * This needs to be here rather than in pipeline-lib or Jenkinsfile due to
 * Scripts not permitted to use method net.sf.json.JSON isEmpty.
 */
Boolean call(Map config = [:]) {

  return !currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause').isEmpty()

}
