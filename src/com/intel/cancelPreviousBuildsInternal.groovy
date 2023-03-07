// src/com/intel/cancelPreviousBuildsInternal.groovy
package com.intel

/**
 * cancelPreviousBuildsInternal.groovy
 *
 * Routine to cancel old builds in progress
 */

// groovylint-disable UnnecessaryGetter
// groovylint-disable-next-line UnusedMethodParameter
void cancelPreviousBuildsInternal(Map config = [:]) {
  /**
   * Cancel old builds method.
   *
   * @param config Map of parameters passed (currently none)
   * @return Nothing
   */

    String jobName = env.JOB_NAME
    int buildNumber = env.BUILD_NUMBER.toInteger()
    /* Get job name */
    // groovylint-disable-next-line NoDef, VariableTypeRequired
    def currentJob = Jenkins.instance.getItemByFullName(jobName)

    /* Iterating over the builds for specific job */
    // groovylint-disable-next-line NoDef, VariableTypeRequired
    for (def build : currentJob.builds) {
      /* If there is a build that is currently running and
         it's not current build */
        if (build.isBuilding() && build.number.toInteger() < buildNumber) {
            print "Stopping currently running build #${build.number}"
            /* Than stopping it */
            build.doStop()
        }
    }
}
