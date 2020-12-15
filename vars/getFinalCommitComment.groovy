// vars/getFinalCommitComment.groovy

/**
 * Return the Comment of the last git commit
 *
 * This needs to be here rather than in pipeline-lib or Jenkinsfile due to
 * Scripts not permitted to use method hudson.plugins.git.GitChangeSet getComment
 */
String call(Map config = [:]) {

    if (true || config['debug']){
        println("checking currentBuild.changeSets")

        String comment = "Not found"
        if (currentBuild.changeSets != null) {
            println("There are " + currentBuild.changeSets.size() + " changeSets")
            for (changeSetList in currentBuild.changeSets) {
                println("----- changeSetList -----")
                println("There are " + changeSetList.size() + " changeSetLists")
                for (changeSet in changeSetList) {
                    println("----- changeSet -----")
                    println(changeSet.comment)
                    comment = changeSet.comment
                }
            }
        } else {
            println("currentBuild.changeSets was null")
        }
    }

    def changeSetList = currentBuild.changeSets[currentBuild.changeSets.size() - 1]
    def changeSet = changeSetList[changeSetList.size() - 1]

    return changeSet.comment

}
