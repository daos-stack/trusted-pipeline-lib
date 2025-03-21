#!/usr/bin/env groovy
// groovylint-disable DuplicateMapLiteral, DuplicateStringLiteral, VariableName
/* Copyright (C) 2019-2023 Intel Corporation
 * All rights reserved.
 *
 * This file is part of the DAOS Project. It is subject to the license terms
 * in the LICENSE file found in the top-level directory of this distribution
 * and at https://img.shields.io/badge/License-Apache%202.0-blue.svg.
 * No part of the DAOS Project, including this file, may be copied, modified,
 * propagated, or distributed except according to the terms contained in the
 * LICENSE file.
 */

// The @library line needs to be edited in a PR for adding a test.
// Adding a test requires two PRs.  One to add the test.
// That PR should be landed with out deleting the PR branch.
// Then a second PR submitted to comment out the @Library line, and when it
// is landed, both PR branches can be deleted.
//@Library(value="trusted-pipeline-lib@my_pr_branch") _

/* groovylint-disable-next-line CompileStatic */
pipeline {
    agent { label 'lightweight' }
    stages {
        stage('Cancel Previous Builds') {
            when { changeRequest() }
            steps {
                cancelPreviousBuildsTrusted()
            }
        } // stage('Cancel Previous Builds')

        // dont run these in parallel as the env.COMMIT_MESSAGE is shared
        // between the parallel stages and taints one another
        stage('No env.COMMIT_MESSAGE pragma test') {
            steps {
                script {
                    assert(commitPragmaTrusted(pragma: 'Sleep-seconds',
                                               def_val: '1') == '1')
                }
            } // steps
        } //stage('No env.COMMIT_MESSAGE pragma test')
        stage('env.COMMIT_MESSAGE pragma test') {
            steps {
                script {
                    env.COMMIT_MESSAGE = '''A commit message

Set in env.COMMIT_MESSAGE

Sleep-seconds: 2'''
                    assert(commitPragmaTrusted(pragma: 'Sleep-seconds',
                                               def_val: '1') == '2')
                }
            } // steps
        } //stage('env.COMMIT_MESSAGE pragma test')
        stage('env.COMMIT_MESSAGE with double-quote test') {
            steps {
                script {
                    env.COMMIT_MESSAGE = 'A commit message with a "double quote"'
                    // just make sure this doesn't trip an error due to the
                    // double quotes
                    assert(commitPragmaTrusted(pragma: 'Foo-bar',
                                               def_val: '1') == '1')
                }
            } // steps
        } //stage('env.COMMIT_MESSAGE pragma test')
        stage('env.COMMIT_MESSAGE case insensitivity test') {
            steps {
                script {
                    env.COMMIT_MESSAGE = '''A commit message

Set in env.COMMIT_MESSAGE

Sleep-seconds: 2'''
                    // just make sure this doesn't trip an error due to the
                    // double quotes
                    assert(commitPragmaTrusted('sleep-seconds', '1') == '2')
                    // Need to reset this environment variable for next stage
                    env.COMMIT_MESSAGE = ''
                }
            } // steps
        } //stage('env.COMMIT_MESSAGE pragma test')
        stage('daosLatestVersion() tests') {
            steps {
                script {
                    assert(daosLatestVersion('master', 'el8').matches(/2.7\.\d++.*/))
                    assert(daosLatestVersion('release/2.4', 'el8').matches(/2.[34]\.\d++.*/))
                    assert(daosLatestVersion('release/2.6', 'el8').matches(/2.[56]\.\d++.*/))
                }
            }
        }
        stage('DAOS Build and Test') {
            when {
                beforeAgent true
                expression {
                    commitPragmaTrusted('DAOS-build-and-test', 'false') == 'true' &&
                    currentBuild.currentResult == 'SUCCESS' &&
                    !skipStage()
                }
            }
            matrix {
                axes {
                    axis {
                        name 'TEST_BRANCH'
                        values 'master',
                               'release/2.6'
                    }
                }
                when {
                    beforeAgent true
                    expression {
                        // Need to pass the stage name: https://issues.jenkins.io/browse/JENKINS-69394
                        !skipStage([stage_name: 'Test Library',
                                    axes: env.TEST_BRANCH.replaceAll('/', '-')])
                    }
                }
                stages {
                    stage('Test Library') {
                        steps {
                            // Normally we do not want to call pipeline-lib methods
                            // from trusted-pipeline-lib, but this is needed
                            // for properly running the DAOS testing.
                            buildDaosJob(env.TEST_BRANCH, params.BuildPriority)
                        } //steps
                        post {
                            success {
                                script {
                                    setupDownstreamTesting.cleanup('daos-stack/daos', env.TEST_BRANCH)
                                }
                            }
                            always {
                                writeFile file: stageStatusFilename(env.STAGE_NAME,
                                                                    env.TEST_BRANCH.replaceAll('/', '-')),
                                          text: currentBuild.currentResult + '\n'
                                /* groovylint-disable-next-line LineLength */
                                archiveArtifacts artifacts: stageStatusFilename(env.STAGE_NAME,
                                                                                env.TEST_BRANCH.replaceAll('/', '-'))
                            }
                        } // post
                    } // stage('Test Library')
                } // stages
            } // matrix
        } // stage('DAOS Build and Test')
    } // stages
    post {
        success {
            scmNotifyTrusted context: 'JENKINS',
                             status: 'SUCCESS'
        }
        unsuccessful {
            scmNotifyTrusted context: 'JENKINS',
                             status: 'FAILURE'
        }
    } // post
}
