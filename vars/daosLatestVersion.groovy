/* groovylint-disable DuplicateNumberLiteral, ParameterName, VariableName */
// vars/daosLatestVersion.groovy

  /**
   * daosLatestVersion step method
   *
   * @param version search ceiling
   * @param repository type
   */
// groovylint-disable-next-line UnusedMethodParameter

import java.util.regex.Matcher

String distro2repo(String distro) {
    switch (distro) {
        case 'centos7':
            return 'el-7'
        case ~/centos8.*/:
        case ~/el8.*/:
            return 'el-8'
        case ~/el9.*/:
            return 'el-9'
        case 'leap15':
            return 'sl-15'
        default:
            error("Don't know how to map distro \"${distro}\" to a repository name")
            return false
    }
}

String getLatestVersion(String distro, BigDecimal next_version, String type='stable') {
    String v = null
    String repo = 'daos-stack-daos-' + distro2repo(distro) + '-x86_64-' + type + '-local/'
    String artifactory_url = env.ARTIFACTORY_URL
    /* For backwards support, add the 'artifactory' path if it is missing from the env */
    if (!artifactory_url.endsWith('/artifactory')) {
        artifactory_url = "${artifactory_url}/artifactory"
    }
    try {
        v = sh(label: 'Get RPM packages version for: ' + repo + ' with version < ' + next_version.toString(),
               script: '$(command -v dnf) --refresh repoquery --repofrompath=daos,' + artifactory_url + '/' +
                       repo + ''' --repoid daos --qf %{version}-%{release} --whatprovides 'daos < ''' +
                       next_version + '''' | rpmdev-sort | tail -1''',
               returnStdout: true).trim()
    /* groovylint-disable-next-line CatchException */
    } catch (Exception e) {
        sh(label: 'Get debug info',
           script: 'hostname; pwd; df -h /var/cache; cat /etc/os-release')
        println('Error getting latest daos version from the ' + repo + ' repository.')
        throw e
    }

    if (!v) {
        return ''
    }
    return v.replace(rpmDistValue(distro), '')
}

/* groovylint-disable-next-line UnusedMethodParameter */
String call(String next_version='1000', String distro=null) {
    String _distro = distro ?: parseStageInfo()['target']

    BigDecimal _next_version
    if (next_version == null) {
        _next_version = 1000
    } else {
        try {
            // Test if it's already a number
            _next_version = new BigDecimal(next_version)
        } catch (NumberFormatException e) {
            // Must be a branch name, convert to a number
            switch (next_version) {
                case 'master':
                case 'weekly-testing':
                    _next_version = 1000
                    break
                case ~/^(release|weekly).*(\/|-)(\d+)\.(\d+).*/:
                    major = Matcher.lastMatcher.group(3) as Integer
                    minor = Matcher.lastMatcher.group(4) as Integer
                    minor++
                    _next_version = new BigDecimal("${major}.${minor}")
                    break
                default:
                    error("Don't know what the latest version is for ${next_version}")
            }
        }
    }
    String v = getLatestVersion(_distro, _next_version)
    if (v == '') {
        v = getLatestVersion(_distro, _next_version, 'archive')
    }
    return v
}
