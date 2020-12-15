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

import java.util.regex.Pattern

def call(Map config = [:]) {

    def def_value = ''
    if (config['def_val']) {
        def_value = config['def_val']
    }
    msg = getFinalCommitComment()
    try {
        def (_,val) = (msg =~ /(?mi)^${config['pragma']}:\s*(.+)$/)[0]
        return val
    } catch (java.lang.IndexOutOfBoundsException e) {
        return def_value
    }

}
