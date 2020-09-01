// vars/setStageNotBuilt.groovy

import static hudson.model.Result.NOT_BUILT

// Hack needed because when{} processing in Jenkins appears to be broken

call (Map config = [:]) {
    currentBuild.setResult('NOT_BUILT')
}
