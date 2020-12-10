// vars/buildCause.groovy

/**
 * Return the reason for the build
 */
Boolean call(Map config = [:]) {

  String buildUser = "Unknown"
  if (${currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause)) {
    println "CAUSE ${currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause).properties}"
  } else {
    println "currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause) is null"
  }
  /* [userName:Brian J. Murrell,
      userIdOrUnknown:bmurrell,
      userId:bmurrell,
      class:class hudson.model.Cause$UserIdCause,
      userUrl:user/bmurrell,
      shortDescription:Started by user Brian J. Murrell]
  */
  def causes = currentBuild.getBuildCauses()

  // Get a specific Cause type (in this case the user who kicked off the build),
  // if present.
  def specificCause = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')
  println "specificCause: " + specificCause

  println currentBuild.getBuildCauses().shortDescription[0]


  /*
  def buildCauses = currentBuild.rawBuild.getCauses()
  //echo buildCauses
  echo buildCauses.getShortDescription()
  echo buildCauses.getUserId() 
  echo buildCauses.getUserName() 
  echo buildCauses.getUserUrl() 
  echo buildCauses.hashCode() 
  if (buildCauses.contains("hudson.triggers.TimerTrigger")){
    buildUser = "TimerTrigger"
  }*/ /* else {
    wrap([$class: 'BuildUser']) {
      buildUser = "${BUILD_USER}"
    }
  }*/
  echo "Initiated by: ${buildUser}"

  return !currentBuild.getBuildCauses('hudson.triggers.TimerTrigger$TimerTriggerCause').isEmpty()
}
