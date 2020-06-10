# trusted-pipeline-lib

Global pipeline library used for common routines

This library is for these main purposes.

* Common Code that is used for multiple pipeline projects including
  the unit testing of the methods in a pipeline library.

* Routines that need groovy methods that are not white listed by Jenkins.

## Usage

This library or a fork of it that implements the routines in it needs to be
installed on the Jenkins system as a global pipeline library.

The global variables also known to Jenkins as "steps" in this library have the
suffix 'Trusted' on them as both an attempt to prevent name space colisions
with other pipeline libraries, and also to indicate that this library
contained the global symbol.

### Global Variables or Steps Provided

#### cancelPreviousBuildsTrusted

This is step to allow a Jenkins job to cancel a build job that has been
superceeded by a later commit.

It takes no parameters and has no return value.

#### commitPragmaTrusted

This is a step to look at the GIT commit message for special instructions
known as pragmas have been set.

~~~groovy
  def skip_stage(String stage) {
    return commitPragmaTrusted(pragma: 'Skip-' + stage) == 'true'
  }

  TEST_RPMS = commitPragmaTrusted(pragma: 'RPM-test', def_val: 'false')
~~~

These pragmas can be used control what Jenkins steps or CI tests are run.

A pragma consist of a name value pair.  The name portition starts at the
beginning of a line, has no spaces in it, and ends with a colon character.

The value string is preceeded by a least one space after the colon, and
should also not have any white space in it.

The return value is the string from the pragma, or an empty string if the
pragma is not set and no default value is provided.

This step takes a map of with these member names:

##### def_val

A string for the default value to be used if the pragma is not present.
This parameter is optional.  The default is an empty string.

##### pragma

A string for the pragma name.  No spaces in the name.  The trailing colon
is not specified.

#### scmNotifyTrusted

This is a simplified wrapper for the gitHubNotify to also allow supporting
alternate SCM systems such as GitLab.

This step has no return value.

This step takes a map with these member names:

##### context

This a string used by the SCM system to track status changes.  Each stage
that reports status needs a different string.

Default is "build/${env.STAGE_NAME}".

The default value can not be used for a Matrix stage.

##### credentialsID

This is a Jenkins Credential ID that needs to be provided for GitHub
status updates.

If one is not provided, this routine will lookup the environment variable
SCM_COMMIT_STATUS_ID that will need to be set in Jenkins and use it.

If no Credential ID is provided as a parameter, or can be looked up in an
environment variable, then a message will be logged that the jenkins system
is not configured to notify the scm.

##### Description

This is a human readable text that can be displayed by the SCM system.
Not used for GitLab.

Default is "${env.STAGE_NAME}"

The default value should probably not be used for a Matrix stage.

##### status

Required parameter.  Should be one of 'PENDING', 'SUCCESS', 'FAILURE' or
'ERROR'.

This will be translated for GitLab.

##### targetUrl

Optional parameter to add a target URL in the notification that will be
provided as a link.

Not used for GitLab.
