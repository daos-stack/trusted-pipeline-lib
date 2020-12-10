// vars/buildCause.groovy

/**
 * Return the reason for the build
 */
Boolean call(Map config = [:]) {

  String buildUser = "Unknown"
  println "CAUSE ${currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause).properties}"

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

  return buildUser == "TimerTrigger"
}
