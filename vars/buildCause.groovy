// vars/buildCause.groovy

/**
 * Return the reason for the build
 */
def call(Map config = [:]) {

  String buildUser = "Unknown"
  String buildCauses = currentBuild.rawBuild.getCauses()
  echo buildCauses
  if (buildCauses.contains("hudson.triggers.TimerTrigger")){
    buildUser = "TimerTrigger"
  } else {
    wrap([$class: 'BuildUser']) {
      buildUser = "${BUILD_USER}"
    }
  }
  echo "Initiated by: ${buildUser}"

}
