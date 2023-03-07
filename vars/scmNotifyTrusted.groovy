// vars/scmNotifyTrusted.groovy
// groovylint-disable DuplicateStringLiteral, VariableName
/**
 * Simplified scmNotifyTrusted command for GitHub/Gitlab
 *
 * Mimics the notifyGithub API for use with either GitHub or GitLab.
 *
 * @param config Map of parameters passed.
 *
 * config['status']      Status text.  Required.
 *                       Should be one of 'PENDING','SUCCESS','FAILURE',
 *                       or 'ERROR'.
 *                       For GitLab, this will be translated to 'pending',
 *                       'success', or 'failed'.
 * config[credentialsId] Jenkins credential ID that should be used for
 *                       notifying the SCM repository.
 *                       Not used for GitLab.
 * config['description'] Description text for notification.
 *                       Human readable text.
 *                       Default is "${env.STAGE_NAME}"
 *                       Do not use a default for a Matrix Stage.
 *                       Not used for GitLab.
 * config['context']     Context for the notification.
 *                       This is used by GitHub/GitLab to track status
 *                       changes.
 *                       Default is "build/${env.STAGE_NAME}".
 *                       Do not use a default for a Matrix Stage.
 * config['targetUrl']   Target URL for notification if present.
 *                       Not used for GitLab.
 */
void call(Map config = [:]) {
    if (!config['status']) {
        error 'scmNotifySystem needs a status parameter value!'
    }

    boolean is_github = false
    boolean is_gitlab = false
    if (env.GIT_URL) {
        // First thing is to determine if this is GitHub or GitLab
        is_github = env.GIT_URL.contains('github.com/')
        is_gitlab = env.GIT_URL.contains('gitlab')
    }
    Map params = [:]

    if (is_github) {
        if (config['credentialsId']) {
            params['credentialsId'] = config['credentialsId']
        } else {
            if (env.SCM_COMMIT_STATUS_ID) {
                params['credentialsId'] = env.SCM_COMMIT_STATUS_ID
            } else {
                steps.println 'Jenkins not configured to notify GitLab'
            }
        }
        params['description'] = config.get('description', env.STAGE_NAME)
        params['context'] = config.get('context', 'build/' + env.STAGE_NAME)
        params['status'] = config['status']
        if (config['targetUrl']) {
            params['targetUrl'] = config['targetUrl']
        }
        steps.githubNotify params
        return
    }
    if (is_gitlab) {
        params['state'] = config['status'].toLowerCase()
        if ((params['state'] == 'failure') || (params['state'] == 'error')) {
            params['state'] = 'failed'
        }
        params['name'] = config.get('context', 'build/' + env.STAGE_NAME)
        steps.updateGitlabCommitStatus params
        return
    }
    println 'Could not detect SCM system!'
}
