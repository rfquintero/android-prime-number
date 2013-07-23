Android Starter Project
=======================

### What is this?
This is a base project for an android application using the ionic framework. It includes:
* Base android application
* Robolectric test project
* Integration test project

### Setup
1. Clone this project and copy the android-starter folder to wherever you'd like.

2. Run the rename script with your new project name: `./rename_project new-project-name`
This will:
    * Update project files and inter-project references
    * Update ant.properties and app_name in strings files
    * Create local.properties files with sdk.dir set to your ANDROID_HOME
    * Rename folders from android-starter-* to new-project-name-*
    * Delete itself as a precaution. You shouldn't use the script in any state other than an initial one.

  >Note:
  >If you're wondering why some Eclipse `.project` and `.classpath` files are included, it's because some inter-project dependencies are set (including a test folder link, and directory container lib folders).

3. Grab dependencies
    * Make sure the Directory Container Plugin for eclipse is installed. It is included in this utilities repo.
    * `ant update-all` in the root directory.

4. Import the projects in Eclipse
    * `Import | Android | Existing android code` -> this will pull in new-project-name and new-project-name-test-integration
    * `Import | Existing projects` -> this will pull in new-project-name-test

5. Refactor/Rename packages from com.ionicmobile.android.starter

6. Check that your Manifests were updated properly. If not (usually Eclipse misses this), do so.

        new-project-name/AndroidManifest.xml:3    package="com.ionicmobile.android.starter"

        new-project-name/AndroidManifest.xml:3    package="com.ionicmobile.android.starter.test"
        new-project-name/AndroidManifest.xml:11   android:targetPackage="com.ionicmobile.android.starter" />
	
7. Setup the Robolectric Test Runner
    * Open `Run Configurations`
    * Add a new JUnit Configuration
    * Under the `Test` tab:
    * Select `Run all tests in the selected project` and choose `new-project-name-test`
    * Test Runner: Junit4
    * Next to a Red X that says, "Multiple launchers available" click `Select one` and choose the Eclipse JUnit Launcher (NOT android)
    * Under the `Arguments` tab:
    * For working directory, choose `Other`, `Workspace` and then select `new-project-name`

8. Run your new Run Config and the Android Integration test to make sure everything is setup properly.

  ***If you see: Unable to find path to Android SDK, update the generated local.properties files with the path to your android sdk***

9. git init!

### Testing
Two test projects are included: a Robolectric test project and a standard Android test project. So, why have two?

Robolectric is useful for unit testing your project. Some very clear benefits are:
* You can test Android classes without everything blowing up.
* You don't need the sim to run, and the sim is extremely slow.
* You have easy access to Android resources (R.*)

Occasionally, you will have to test actual Android functionality. For these cases, you can write tests in the integration suite. Again, these will tend to be slower, and it may be more difficult to test certain behaviors without Android throwing errors.

***Structure:***
We have maintained the project structures inherent with each testing suite. For Robolectric, test code lives in the main project folder (as is common with junit projects). Similarly, the `junit` build target must be run within the main project, ivy dependencies share a file, etc. This also mirrors the setup of the SDK project.

For the Integration test, Android forces you to have a completely separate project, with its own manifest, build, and so forth. As such, ivy dependencies and custom targets are also found separately in the integration folder.

***One final tip:***
We recommend separating these test suites even on your CI box. It can be helpful to know whether one suite is passing and the other failing, for example if you made an assumption in the Unit Tests that isn't true in Integration.

### Build Targets
In an effort to unify the android builds both on the SDK and app level, we are using ant/ivy (android already has ant build targets) and created similar targets for both types of projects. Most of the changes and targets should be straightforward, but here's a brief description of each one. The following targets are in addition to those given by android:

* update,update-tests,update-all: These pull all dependencies and put them in the libs folder. Current search order is: local, asynchrony nexus, and then maven central. Note that old libs will be removed, to prevent duplicate libraries with different versions.
* compile, compile-tests, jar: Self explanatory
* junit: Compiles and runs junit tests.
* fetch-test-report (integration only): Create a test report for use by CI.

These targets are defined in the `custom_rules.xml` file in the main project folder, with additional targets in the integration project folder. The `build.xml` is simply the standard android one, and can be regenerated with the `android update` tool. In addition, a common `ivysettings.xml` can be found on the root path, and is automatically imported in the main `custom_rules.xml`. As with a standard android build, specific settings should be defined in `ant.properties`.

Finally, for convenience, the root project has an `update-all` target that updates all specified sub-projects.

### Dependencies
As new dependencies are added, simply update the corresponding ivy.xml file. Note that the main project folder's ivy.xml includes dependencies for compile and for the unit tests. The integration test project has a separate ivy file. Compile/test dependencies can be differentiated as such:

	  <!-- compile depencies -->
      <dependency org="com.google.code.gson" name="gson" rev="2.2.2" />  

      <!-- test dependencies -->
      <dependency org="junit" name="junit" rev="4.10" conf="test->default" />

If an external dependency does not exist in maven central, we advise adding it to the asynchrony nexus as a third party library. Do not check it into source control.

### Notes on Usage
All three projects have been structured to work the same way. Eclipse Android projects automatically add jars from the `libs` folder as android dependencies, as does the Robolectric test project via the Directory Container Plugin. Thus to add a dependency, simply add it to the ivy file and run `ant update` or `ant update-tests`. Note that this will delete the libs folder and redownload to prevent duplicate libraries with different versions.

Developing with the SDK then should be straightforward, as it is simply another dependency. You can tie your project to a particular revision (i.e. '1.2.5'), use a matcher (i.e. '1.0.+'), or always use the latest of a status (i.e. 'latest.integration'). See ivy docs for more information on matchers:

http://ant.apache.org/ivy/history/2.1.0/settings/version-matchers.html

As explained in the SDK docs, the ivy resolvers will check the local repo first, then the asynchrony nexus, and finally maven central. Thus if concurrent dev is needed between the SDK and an application, you can publish the SDK locally and update from there. As an example, let's say the SDK is currently on revision 1.2.5, and I will be working on both it and my app. Rather than having to push changes, build, and publish them to the repo (which might break other devs), I can simply work off my local SDK. Basically, that would involve:

* Change SDK publish locally
* Have SDK publish version 1.2.5.x, and set our dependency to 1.2.5.+
* As you work on the SDK, `ant publish` the SDK, and then `ant update` for the app.
* Once changes are finalized, go through standard merging/SDK publishing, so build box should presumably publish version 1.2.6.
* Switch dependency back to 1.2.+, or whatever it was before, and `ant update` the app.

**\*Note***
If you need to clear your local repo for whatever reason, the location for it is specified in the `ivysettings.xml`. It defaults to `${user.home}/.ivy/local`

See the SDK docs for more information on publishing.

#### An Aside
The reason for updating externally to the IDE (versus using an ivy or maven plugin) was that the Android/Maven Android/Ivy plugins were flaky, and presented issues with running unit tests or integration tests out of Eclipse. Keeping the libs relatively external to the IDE seemed to present the fewest issues, and allowed for uniformity between all projects, both on the SDK and App side.
