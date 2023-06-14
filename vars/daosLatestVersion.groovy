/* groovylint-disable DuplicateNumberLiteral, ParameterName, VariableName */
// vars/daosLatestVersion.groovy

  /**
   * daosLatestVersion step method
   *
   * @param version search ceiling
   * @param repository type
   */
// groovylint-disable-next-line UnusedMethodParameter
String call(String next_version='1000', String repo_type='stable') {
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
                case ~/^(release|weekly).*(\/|-)(\d+)\.(\d+)/:
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

    String v = null
    try {
        v = sh(label: 'Get RPM packages version',
               script: 'dnf --refresh repoquery --repofrompath=daos,' + env.ARTIFACTORY_URL +
                       '/artifactory/daos-stack-daos-el-8-x86_64-stable-local/' +
                     ''' --repoid daos --qf %{version}-%{release} --whatprovides 'daos-tests < ''' +
                                  _next_version + '''' |
                              rpmdev-sort | tail -1''',
               returnStdout: true).trim()
    /* groovylint-disable-next-line CatchException */
    } catch (Exception e) {
        sh(label: 'Get debug info',
           script: 'hostname; pwd; df -h /var/cache; lsb_release -a || true')
        println('Error getting latest daos version.')
        throw e
    }

    return v[0..<v.lastIndexOf('.')]
}
