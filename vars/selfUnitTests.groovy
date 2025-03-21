/* groovylint-disable DuplicateNumberLiteral, ParameterName, VariableName */
// vars/selfUnitTest.groovy

  /**
   * selfUnitTest.groovy
   *
   * Runs trusted-pipeline-lib unit tests
   */

void call() {
    // Run unit tests
    _run_unit_tests()
}

void _run_unit_tests() {
    // Run all the units
    println('Test daosLatestVersion()')
    _test_daosLatestVersion("master", "el8", /2.7\.\d++.*/)
    _test_daosLatestVersion('release/2.4', 'el8', /2.[34]\.\d++.*/)
    _test_daosLatestVersion('release/2.6', 'el8', /2.[56]\.\d++.*/)
}

void _test_daosLatestVersion(String distro, String next_version, String expected) {
    // Verify the value returned by daosLatestVersion() matches expected
    println("  with distro=${distro}, next_version=${next_version}")
    result =daosLatestVersion(distro, next_version)
    println("    result:   ${result}")
    println("    expected: ${expected}")
    assert(result.matches(expected))
}
