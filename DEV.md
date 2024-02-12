# Development Readme

This is a multi-module project. The reason behind this structure is that we need a way to run a bunch of e2e tests using the framework as a regular
client would. So, we build the framework in its own module, and then we can include it as a regular dependency in other modules dedicated to run e2e tests.

These are the modules, in the order they're built:

```
spectrum-aggregate (parent pom)
|─ spectrum (framework)
|─ it (run the same e2e suite with all the browsers, no testbook)
|─ it-grid (run the same e2e suite as the it modules, pointing to a local embedded grid)
|─ it-testbook (run a bunch of tests with a testbook)
|─ it-appium (run a bunch of tests with appium)
|─ verify-browsers (verifying results of the browsers it modules)
|─ verify-appium (verifying results of the appium it modules)
└─ cleanup
```

In all the modules, some tests are meant to fail for demonstration purposes.
The module's build will not fail: they will be checked later on by the `verify-*` modules.

Spectrum leverages [SpectrumSessionListener](spectrum/src/main/java/io/github/giulong/spectrum/SpectrumSessionListener.java),
a [LauncherSessionListener](https://junit.org/junit5/docs/current/user-guide/#launcher-api-launcher-session-listeners-custom)
registered via the Service Loader mechanism, to fully load itself.
The [org.junit.platform.launcher.LauncherSessionListener](spectrum/src/main/resources/org.junit.platform.launcher.LauncherSessionListener)
file is copied into the `META-INF/services` folder during the `prepare-package` phase.
It's not placed already there since that would load the framework during its own unit tests, breaking them.

Outside the full maven build of the entire project, so to trigger single tests from the IDE, some conditions need to be satisfied.
In general, to be able to run unit tests, we need to delete
the [org.junit.platform.launcher.LauncherSessionListener](spectrum/src/main/resources/org.junit.platform.launcher.LauncherSessionListener)
from [spectrum/target/classes/META-INF/services](spectrum/target/classes/META-INF/services),
while we need to have it in modules that run e2e tests or both unit and e2e.
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

## How to build the project

You can leverage the maven wrapper bundled in this repo. This is how to trigger the complete build:

`./mvnw clean install -DskipSign -Dmaven.plugin.validation=BRIEF -DbrowsersTests -DappiumTests -fae -ntp`

Where:

* `clean` is needed to avoid the `verify-*` modules check outdated reports of previous builds.
* `install` will copy the built framework (jar) in your local maven repo, so that you can use it locally in other projects/modules.
* the `-DskipSign` skips signing the artifact with a gpg key. That's needed in GitHub actions to publish
  on [Ossrh](https://s01.oss.sonatype.org/content/repositories/releases/io/github/giulong/spectrum/).
* check [Maven's docs](https://maven.apache.org/guides/plugins/validation/index.html) to understand `-Dmaven.plugin.validation=BRIEF`
* the `-DbrowsersTests` property is a shorthand to activate all the profiles needed to run tests on all the browsers. It's equivalent to
  running with these active profiles: `-P chrome,firefox,edge`.
* the `-DappiumTests` property is a shorthand to activate all the profiles needed to run tests on all the browsers. It's equivalent to
  running with these active profiles: `-P uiAutomator2`.
* the `-fae` option is [Maven's](https://maven.apache.org/ref/3.6.3/maven-embedder/cli.html) shorthand for `--fail-at-end`, needed to always run the `cleanup` module.
* the `-ntp` option is [Maven's](https://maven.apache.org/docs/3.6.1/release-notes.html#user-visible-changes) shorthand for `--no-transfer-progress`.

If you need a fresh local build of just the framework's jar to use it locally, you can run this:

`./mvnw install -DskipTests -DskipSign -Dmaven.plugin.validation=BRIEF -ntp -P framework-only`

> ⚠️ Appium<br/>
> In order to run Appium tests, you need to install it on your local machine. Be sure to check 
> [Appium quickstart](http://appium.io/docs/en/latest/quickstart/).
> You need Appium server and all the drivers for the corresponding technologies.

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
| docs/**    | push      | [docs](.github/workflows/docs.yml)     |
| main       | pr closed | [deploy](.github/workflows/deploy.yml) |

## How to build the docs locally

Follow the instructions
on [Testing your GitHub Pages site locally with Jekyll](https://docs.github.com/en/pages/setting-up-a-github-pages-site-with-jekyll/testing-your-github-pages-site-locally-with-jekyll).

Once everything is setup and working locally, run Jekyll under the `docs` folder with:

`cd docs && bundle exec jekyll serve --config _config.yml,_config_local.yml`

Then, browse the docs at http://127.0.0.1:4000/spectrum/
