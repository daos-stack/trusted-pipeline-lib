// vars/buildCause.groovy

/**
 * Return the reason for the build
 */
Boolean call(Map config = [:]) {

  String buildUser = "Unknown"
  String buildCauses = currentBuild.rawBuild.getCauses()
  echo buildCauses
  echo buildCauses.getShortDescription()
  echo buildCauses.getUserId() 
  echo buildCauses.getUserName() 
  echo buildCauses.getUserUrl() 
  echo buildCauses.hashCode() 
  if (buildCauses.contains("hudson.triggers.TimerTrigger")){
    buildUser = "TimerTrigger"
  } /* jelse {
    wrap([$class: 'BuildUser']) {
      buildUser = "${BUILD_USER}"
    }
  }*/
  echo "Initiated by: ${buildUser}"

  return buildUser == "TimerTrigger"
}
