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

// @Library(value="pipeline-lib@my_pr_branch") _

pipeline {
  agent { label 'lightweight' }
  stages {
    stage('Cancel Previous Builds') {
      when { changeRequest() }
      steps {
        cancelPreviousBuilds()
      }
    } // stage('Cancel Previous Builds')

    stage ('Test') {
      steps {
        sh label: 'Pragma test',
           script: 'sleep ' +
                    commitPragmaTrusted(pragma: 'Sleep-minutes',
                                        def_val: '1')
      } // steps
    } // stage ('Test')
  } //stages
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
