# Development Readme

This is a multi-module project. The reason behind this structure is that we need a way to run a bunch of e2e tests using the framework as a regular
client would. So, we build the framework in its own module, and then we can include it as a regular dependency in other modules, dedicated to run e2e tests.

These are the modules, in the order they're built:

```
spectrum-aggregate (parent pom)
|─ spectrum (framework)
|─ it (run the same e2e suite with all the browsers, no testbook)
|─ it-testbook (run a bunch of tests with a testbook)
|─ it-verifier (verifying results of the it modules)
└─ cleanup
```

In both the `it` and `it-testbook` modules, some tests are meant to fail for demonstration purposes. The module's build will not fail.
They will be checked later on by the `it-verifier` module.

Spectrum leverages `SpectrumSessionListener`, a [LauncherSessionListener](https://junit.org/junit5/docs/current/user-guide/#launcher-api-launcher-session-listeners-custom)
registered via the Service Loader mechanism, to fully load itself.
The [org.junit.platform.launcher.LauncherSessionListener](spectrum/src/main/resources/org.junit.platform.launcher.LauncherSessionListener)
file is copied into the `META-INF/services` folder during the `prepare-package` phase.
It's not placed already there since that would load the framework during its own unit tests, breaking them.

Outside the full maven build of the entire project, so to trigger single tests from the IDE, some conditions need to be satisfied.
In general, to be able to run unit tests, we need to delete that file from [spectrum/target/classes/META-INF/services](spectrum/target/classes/META-INF/services),
while we need to have it in modules that run e2e tests or both unit and e2e.
To avoid manual operations, at the end of the full build, the `cleanup` module will execute the corresponding action for each module listed below.

| Module      | Unit tests | E2E tests | Action                           |
|-------------|------------|-----------|----------------------------------|
| spectrum    | ✅          | ❌         | remove `SpectrumSessionListener` |
| it          | ❌          | ✅         | add `SpectrumSessionListener`    |
| it-testbook | ❌          | ✅         | add `SpectrumSessionListener`    |
| it-verifier | ✅          | ✅         | add `SpectrumSessionListener`    |

## How to build the project

`mvn clean install -DallTests -DskipSign -fae`

* `clean` is needed to delete old reports and avoid the `it-verifier` module checks outdated ones.
* `install` will copy the built framework (jar) in your local maven repo, so that you can use it locally in other projects.
* the `allTests` property is a shorthand to activate all the profiles needed to run tests on all the browsers. It's equivalent to
  run: `mvn clean install -P chrome,firefox,edge -fae`.
* the `skipSign` skips signing the artifact with a gpg key.
* the `-fae` option is [Maven's](https://maven.apache.org/ref/3.6.3/maven-embedder/cli.html) shorthand for `--fail-at-end`, needed to always run the `cleanup` module.

## Workflow

We use [GitFlow](http://datasift.github.io/gitflow/IntroducingGitFlow.html):

* `feature` branches are forked from `develop`. Once the feature is complete, a pull request must be opened towards `develop`.
* `release` branches must be forked from `develop`. A pull request must be opened towards `main`.
* When a PR on `main` is merged, the `deploy` workflow will start. Once that is successful, the marge back on `develop` must be made.

GitHub workflows:

| Branch     | Event     | Workflow                               |
|------------|-----------|----------------------------------------|
| develop    | push      | [build](.github/workflows/build.yml)   |
| feature/** | push      | [build](.github/workflows/build.yml)   |
| main       | pr closed | [deploy](.github/workflows/deploy.yml) |
