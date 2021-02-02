#!/usr/bin/env groovy
/* Copyright (C) 2019-2020 Intel Corporation
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

pipeline {
  agent { label 'lightweight' }
  stages {
    stage('Cancel Previous Builds') {
      when { changeRequest() }
      steps {
        cancelPreviousBuilds()
      }
    } // stage('Cancel Previous Builds')

    // dont run these in parallel as the env.COMMIT_MESSAGE is shared between
    // the parallel stages and taints one another
    stage ('No env.COMMIT_MESSAGE pragma test') {
      steps {
        script {
          assert(commitPragmaTrusted(pragma: 'Sleep-seconds',
                                     def_val: '1') == "1")
        }
      } // steps
    } //stage ('No env.COMMIT_MESSAGE pragma test')
    stage ('env.COMMIT_MESSAGE pragma test') {
      steps {
        script {
          env.COMMIT_MESSAGE = '''A commit message

Set in env.COMMIT_MESSAGE

Sleep-seconds: 2'''
          assert(commitPragmaTrusted(pragma: 'Sleep-seconds',
                                     def_val: '1') == "2")
        }
      } // steps
    } //stage ('env.COMMIT_MESSAGE pragma test')
    stage ('env.COMMIT_MESSAGE with double-quote test') {
      steps {
        script {
          env.COMMIT_MESSAGE = 'A commit message with a "double quote"'
          // just make sure this doesn't trip an error due to the double
          // quotes
          println(commitPragmaTrusted(pragma: 'Foo-bar:',
                                     def_val: '1') == "1")
        }
      } // steps
    } //stage ('env.COMMIT_MESSAGE pragma test')
  } // stage ('Test')
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
