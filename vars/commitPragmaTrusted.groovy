// vars/commitPragmaTrusted.groovy

/**
 * Method to get a commit pragma value
 *
 *
 * @param config Map of parameters passed.
 *
 * config['pragma']     Pragma to get the value of
 * config['def_val']    Value to return if not found
 */
def call(Map config = [:]) {

    def def_value = ''
    if (config['def_val']) {
        def_value = config['def_val']
    }

    String commit_message=""
    /* TODO: Replace all of this with currentBuild->changeSets
     *       https://build.hpdd.intel.com/pipeline-syntax/globals#currentBuild
     *       Something along the lines of:
     *       def changeLogSets = currentBuild.changeSets
     *       for (int i = 0; i < changeLogSets.size(); i++) {
     *           def entries = changeLogSets[i].items
     *           for (int j = 0; j < entries.length; j++) {
     *               def entry = entries[j]
     *               commit_message= $entry.msg
     *           }
     *       }
     */
    if (env.COMMIT_MESSAGE && env.COMMIT_MESSAGE != "") {
        commit_message = env.COMMIT_MESSAGE
    } else {
        commit_message = sh(script: 'git show -s --format=%B',
                            returnStdout: true).trim()
    }
    return sh(script: 'b=$(echo "' + commit_message.replaceAll('"', '\\\\"') +
                    '''" | sed -ne 's/^''' + config['pragma'] +
                    ''': *\\(.*\\)/\\1/p')
                       if [ -n "$b" ]; then
                           echo "$b"
                       else
                           echo "''' + def_value + '''"
                       fi''',
              returnStdout: true).trim()

}