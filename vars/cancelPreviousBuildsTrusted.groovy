// vars/cancelPreviousBuildsInternal.groovy

import com.intel.cancelPreviousBuildsInternal

void call(Map config = [:]) {
    /*
     * Cancel previous builds of the current job
     *
     * @param config Map of parameters passed (none currently)
     * @return Nothing
     */
    // groovylint-disable UnnecessaryPackageReference
    // groovylint-disable-next-line NoDef, VariableTypeRequired
    def c = new com.intel.cancelPreviousBuildsInternal()
    c.cancelPreviousBuildsInternal(config)
}
