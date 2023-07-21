# Development Readme

This is a multi-module project. The reason behind this structure is that we need a way to run a bunch of e2e tests using the framework as a regular
client would. So, we build the framework in its own module, and then we can include it as a regular dependencies in other modules.

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
registered via the Service Loader mechanism. The [org.junit.platform.launcher.LauncherSessionListener](spectrum/src/main/resources/org.junit.platform.launcher.LauncherSessionListener)
file is copied into the `META-INF/services` folder during the `prepare-package` phase.
It's not placed already there since that would load the framework during its own unit tests, breaking them.

In general, to be able to run Spectrum's unit tests, we need to delete that file from [spectrum/target/classes/META-INF/services](spectrum/target/classes/META-INF/services).
The `cleanup` module takes care of this, but in case it's needed, you need to delete it manually.

## How to build the project

`mvn clean install -D allTests -fae`

* the `allTests` property is a shorthand to activate all the profiles needed to run tests on all the browsers. It's equivalent to run: `mvn clean install -P chrome,firefox,edge -fae`.
* the `-fae` option is [Maven's](https://maven.apache.org/ref/3.6.3/maven-embedder/cli.html) shorthand for `--fail-at-end`, needed to always run the `cleanup` module.

## Workflow

We use [GitFlow](http://datasift.github.io/gitflow/IntroducingGitFlow.html)
