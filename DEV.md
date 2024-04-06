# Development Readme

This is a multi-module project, since we need a way to run a bunch of e2e tests using Spectrum as a regular
client would. So, we build the framework in a dedicated module, and then we include the built jar as a regular dependency in other modules to run e2e tests.

## Modules

| Module                             | Description                                                                   |
|------------------------------------|-------------------------------------------------------------------------------|
| [spectrum](spectrum)               | Framework                                                                     |
| [it](it)                           | Runs the same e2e suite with all the browsers, no testbook                    |
| [it-grid](it-grid)                 | Runs the same e2e suite as the `it` module, pointing to a local embedded grid |
| [it-testbook](it-testbook)         | Runs a bunch of tests with a testbook                                         |
| [it-appium](it-appium)             | Runs a bunch of tests with Appium                                             |
| [verify-commons](verify-commons)   | Contains common classes used in other verify modules                          |
| [verify-browsers](verify-browsers) | Verifies results of the `it`, `it-testbook`, and `it.grid` module             |
| [verify-appium](verify-appium)     | Verifies results of the `it-appium` module                                    |
| [cleanup](cleanup)                 | Cleans each module after the execution                                        |

In some modules, some tests are meant to fail or skipped for demonstration purposes, for example to check how they are displayed in the html report.
These modules' build will not fail anyway: they will be checked later on by the `verify-*` modules.

## Entrypoint

Spectrum leverages [SpectrumSessionListener](spectrum/src/main/java/io/github/giulong/spectrum/SpectrumSessionListener.java) as its entrypoint,
which is a [LauncherSessionListener](https://junit.org/junit5/docs/current/user-guide/#launcher-api-launcher-session-listeners-custom)
registered via the Service Loader mechanism. This means we provide its fqdn in the
[org.junit.platform.launcher.LauncherSessionListener](spectrum/src/main/resources/org.junit.platform.launcher.LauncherSessionListener)
file, which is copied into the `META-INF/services` folder during the `prepare-package` phase.
It's not placed already there since that would load the framework during its own unit tests, breaking them.

Outside the Maven build of the entire project, so to trigger single tests from the IDE, these conditions need to be satisfied:

* to be able to run the framework's **unit tests**, we need to delete
  the [org.junit.platform.launcher.LauncherSessionListener](spectrum/src/main/resources/org.junit.platform.launcher.LauncherSessionListener)
  from [spectrum/target/classes/META-INF/services](spectrum/target/classes/META-INF/services)
* to be able to run **unit** and **e2e tests** in other modules, we need to have it in their respective `target/classes/META-INF/services` folder

To avoid manual operations, at the end of the full build, the `cleanup` module will execute the corresponding action for each module listed below.

| Module          | Unit tests | E2E tests | Action                           |
|-----------------|------------|-----------|----------------------------------|
| spectrum        | ✅          | ❌         | remove `SpectrumSessionListener` |
| it              | ❌          | ✅         | add `SpectrumSessionListener`    |
| it-grid         | ❌          | ✅         | add `SpectrumSessionListener`    |
| it-testbook     | ❌          | ✅         | add `SpectrumSessionListener`    |
| it-appium       | ❌          | ✅         | add `SpectrumSessionListener`    |
| verify-browsers | ✅          | ✅         | add `SpectrumSessionListener`    |
| verify-appium   | ✅          | ✅         | add `SpectrumSessionListener`    |

# How to build the project

You can leverage the [Maven wrapper](https://maven.apache.org/wrapper/) bundled in this repo.

## Full build

This is how to trigger the full build:

| OS      | Command                                                                     |
|---------|-----------------------------------------------------------------------------|
| unix    | `./mvnw clean install -DskipSign -DbrowsersTests -DappiumTests -fae -ntp`   |
| windows | `mvnw.cmd clean install -DskipSign -DbrowsersTests -DappiumTests -fae -ntp` |

Where:

* `clean` is needed to avoid the `verify-*` modules check outdated reports of previous builds.
* `install` will copy the built framework (jar) in your local maven repo, so that you can use it locally in other projects/modules.
* the `-DskipSign` flag allows to skip signing the artifact with a gpg key. That's needed in GitHub actions to publish
  on [Ossrh](https://s01.oss.sonatype.org/content/repositories/releases/io/github/giulong/spectrum/).
* the `-DbrowsersTests` property is a shorthand to activate all the profiles needed to run tests on all the browsers. It's equivalent to
  running with these active profiles: `-P chrome,firefox,edge`.
* the `-DappiumTests` property is a shorthand to activate all the profiles needed to run tests on Appium. It's equivalent to
  running with these active profiles: `-P uiAutomator2`.
* the `-fae` option is [Maven's](https://maven.apache.org/ref/3.6.3/maven-embedder/cli.html) shorthand for `--fail-at-end`, needed to always run the `cleanup` module.
* the `-ntp` option is [Maven's](https://maven.apache.org/docs/3.6.1/release-notes.html#user-visible-changes) shorthand for `--no-transfer-progress`.

> ⚠️ Appium<br/>
> In order to run Appium tests, you need to install it on your local machine. Be sure to check
> [Appium quickstart](http://appium.io/docs/en/latest/quickstart/).
> You need Appium server and all the drivers for the corresponding technologies.

> ⚠️ Safari<br/>
> [Safari Integration Tests](it-testbook/src/test/java/io/github/giulong/spectrum/it_testbook/tests/SafariCheckboxIT.java)
> are not executed during the Maven build, since they depend on the underlying OS,
> and their conditional execution would affect the `verify-browsers` module.
> If needed, they can be executed programmatically.

## Framework-only build

If you need a fresh local build of just the framework's jar to use it locally, you can run the command below.
With that, just the framework's module is built, without running any test.

| OS      | Command                                                          |
|---------|------------------------------------------------------------------|
| unix    | `./mvnw install -DskipTests -DskipSign -ntp -P framework-only`   |
| windows | `mvnw.cmd install -DskipTests -DskipSign -ntp -P framework-only` |

# Workflow

We use [GitFlow](http://datasift.github.io/gitflow/IntroducingGitFlow.html):

* `feature` branches are forked from `develop`. Once the feature is complete, a pull request must be opened towards `develop`.
* `release` branches must be forked from `develop`. A pull request must be opened towards `main`.
* When a PR on `main` is merged, the `deploy` workflow will start. Once that is successful, a merge back on `develop` is made.

GitHub [workflows](.github/workflows):

| Branch     | Event     | Workflow                               |
|------------|-----------|----------------------------------------|
| develop    | push      | [build](.github/workflows/build.yml)   |
| feature/** | push      | [build](.github/workflows/build.yml)   |
| bugfix/**  | push      | [build](.github/workflows/build.yml)   |
| docs/**    | PR closed | [docs](.github/workflows/docs.yml)     |
| main       | PR closed | [deploy](.github/workflows/deploy.yml) |

# How to build the docs locally

Spectrum's docs is versioned under the [docs](docs) folder. It leverages [Jekyll](https://jekyllrb.com/) and the
[Modernist Theme](https://github.com/pages-themes/modernist).
To be able to run it locally so to make and review your changes before pushing them, follow these steps:

1. Setup your local environment as explained
   in [Testing your GitHub Pages site locally with Jekyll](https://docs.github.com/en/pages/setting-up-a-github-pages-site-with-jekyll/testing-your-github-pages-site-locally-with-jekyll)
2. Run Jekyll under the `docs` folder with `cd docs && bundle exec jekyll serve --config _config.yml,_config_local.yml`
3. Browse the docs at http://127.0.0.1:4000/spectrum/
