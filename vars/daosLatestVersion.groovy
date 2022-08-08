/* groovylint-disable DuplicateNumberLiteral, ParameterName, VariableName */
// vars/daosLatestVersion.groovy

  /**
   * daosLatestVersion step method
   *
   * @param version search ceiling
   * @param repository type
   */

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
                case 'release/2.2':
                case 'weekly-testing-2.2':
                    _next_version = 2.3
                    break
                case 'release/2.0':
                case 'weekly-testing-2.0':
                    _next_version = 2.1
                    break
                default:
                    error("Don't know what the latest version is for ${next_version}")
            }
        }
    }

    String v = null
    try {
        v = sh(label: 'Get RPM packages version',
               script: '$(command -v dnf) repoquery --repofrompath=daos,' + env.ARTIFACTORY_URL +
                       '/artifactory/daos-stack-daos-el-8-x86_64-stable-local/' +
                     ''' --repoid daos --qf %{version}-%{release} --whatprovides 'daos-tests(x86-64) < ''' +
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
