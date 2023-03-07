/* groovylint-disable ParameterName, VariableName */
// vars/commitPragmaTrusted.groovy

/**
 * Method to get a commit pragma value
 *
 * @param config Map of parameters passed.
 *
 * config['pragma']     Pragma to get the value of
 * config['def_val']    Value to return if not found
 */
String call(Map config = [:]) {
        // convert the map for compat
    return commitPragmaTrusted(config['pragma'], config['def_val'])
}

String call(String name, String def_val = null) {
/**
 * @param name       Pragma to get the value of
 * @param def_val    Value to return if not found
 */

    String def_value = ''
    if (def_val) {
        def_value = def_val
    }

    String commit_message = ''
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
    if (env.COMMIT_MESSAGE && env.COMMIT_MESSAGE != '') {
        commit_message = env.COMMIT_MESSAGE
    } else {
        String cmd = 'if [ -n "$GIT_CHECKOUT_DIR" ] && ' +
                     '''[ -d "$GIT_CHECKOUT_DIR" ]; then
                          cd "$GIT_CHECKOUT_DIR"
                        fi
                        git show -s --format=%B\n'''
        commit_message = sh(label: 'Lookup commit message',
                            script: cmd,
                            returnStdout: true).trim()
    }
    return sh(label: 'Sanitize commmit message',
              script: 'b=$(echo "' + commit_message.replaceAll('"', '\\\\"') +
                    '''" | sed -ne 's/^''' + name.replaceAll('/', '\\\\/') +
                    ''': *\\(.*\\)/\\1/Ip')
                       if [ -n "$b" ]; then
                           echo "$b"
                       else
                           echo "''' + def_value + '''"
                       fi''',
              returnStdout: true).trim()
}
