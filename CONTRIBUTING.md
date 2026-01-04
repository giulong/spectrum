# How To Contribute

Contributions to Spectrum are welcome! There are different ways you can contribute to it:

* [Bug Report](#bug-report)
* [Feature Request](#feature-request)
* [Docs Enhancements](#docs-enhancements)
* [Discussions](#discussions)
* [Submitting Changes](#submitting-changes)

# Bug Report

Found a bug? Take these steps:
1. Ensure there is no [issue already opened](https://github.com/giulong/spectrum/issues) regarding the same bug.
2. Submit a [new issue](https://github.com/giulong/spectrum/issues/new?assignees=giulong&labels=&projects=&template=bug_report.md&title=%5BBUG%5D+%3CProvide+a+short+title%3E)
   by fulfilling the provided template.

# Feature Request

If you think there's something missing in Spectrum that is a good candidate to become a new feature, please
[open a feature request](https://github.com/giulong/spectrum/issues/new?assignees=giulong&labels=&projects=&template=feature-request.md&title=%5BRFE%5D+%3CProvide+a+short+title%3E)
by fulfilling the provided template.

# Docs Enhancements

If you found something missing in the [docs](https://giulong.github.io/spectrum/#spectrum),
or you want to contribute with real configuration examples of how you use Spectrum, feel free to open a Pull Request.

> ⚠️ Minor changes such as fixing typos will be rejected.

# Discussions

If you just want to ask something about Spectrum, either to maintainers or other users, check the
[Discussions section](https://github.com/giulong/spectrum/discussions)
to see if there's something on that topic already. If not, feel free to open a new one.

# Submitting Changes

Take these steps:

1. [Fork the repository](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/fork-a-repo)
3. Read the [Local Development section](#local-development) below to be able to work locally
4. Make your changes and be sure to have a successful local build (at least the _framework-only_ build)
5. Be sure your code complies to the [Coding Conventions](#coding-conventions)
6. Submit a Pull Request towards `develop`

## Coding Conventions

The following conventions aim to keep the codebase clean and maintainable.

### Source Code

Do's:

* be [SOLID](https://en.wikipedia.org/wiki/SOLID)
* Take a look around: your code should follow the conventions already in place
* Write small classes and methods, with reusability and maintainability in mind
* Leverage [convention over configuration](https://en.wikipedia.org/wiki/Convention_over_configuration), providing defaults to reduce the boilerplate code a user would need to explicitly write
* Explicitly mark variables as `final`. Mutable variables are not accepted, with very few exceptions
* Leverage Java 21 api
* Lines up to 180 chars are ok, with a grain of salt: put methods-chained calls on a new line only if they're many
* Write few meaningful logs at a proper level
* Ensure the checkstyle plugin doesn't produce warnings during the build

Don'ts:

* Shorten variables names
* Declare multiple variables on the same line
* Script-like business logic. We have an object-oriented language here: if/switch/ternary usage must be kept to a minimum
* Catching and re-throwing exceptions
* Creating checked exceptions
* One-liners. Use variables with meaningful names to clarify
* Break lines as if we still have 80-chars terminals

### Unit Tests

Every line of code and conditional branch must be unit-tested.
You can leverage the coverage report produced by the build at [docs/jacoco/index.html](docs/jacoco/index.html)
to see what's missing. Keep in mind that coverage _per se_ is just an empty number, but it's an important way to check missing branches.

Rules:

* Each test class must:
  * be package-private
  * have a name that is made up of the **source** class' name + 'Test', such as `MySourceClass` &rarr; `MySourceClassTest`
* Each test method must:
  * be package-private
  * have a short and clear `@DisplayName`
  * contain no conditional logic
* Strict mocking is required, as per Mockito's default, with few exceptions allowed
* Use dummy values as arguments. Suggestion: if you have a method with an argument like `String fileName`, use a variable with name and value matching, 
like `String fileName = "fileName";`
* Avoid generic argument matchers such as `any()` when possible. Knowing what we're passing to methods calls matter, even if they're dummy values

### Integration Tests

Integration tests are not always needed. They are, for example, when implementing a new feature that produces some kind of artifact, such as a report.
In that case, we need an integration test that checks that artifact, and provide a way to avoid regressions.

Generally speaking, you can write integration tests if you think they're useful, but maybe it's **better asking** before wasting your time.

Rules:

* Each test class must:
  * have a name that ends with 'IT', such as `ExtentReportVerifierIT`
* Each test method must:
  * be package-private
  * have a short and clear `@DisplayName`
  * contain no conditional logic

# Local Development

This is a multimodule project, since we must run e2e tests of the newly built Spectrum version as a regular
client would: we build the framework in a dedicated module,
and then we include the built jar as a regular dependency in other modules to run e2e tests.

## Modules

| Module                                               | Description                                                                                                                       |
|------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| [it](it)                                             | Runs tests with all the browsers, no testbook                                                                                     |
| [it-appium](it-appium)                               | Runs tests with Appium                                                                                                            |
| [it-bidi](it-bidi)                                   | Runs tests with the `webSocketUrl` capability                                                                                     |
| [it-grid](it-grid)                                   | Runs tests pointing to a local embedded grid                                                                                      |
| [it-macos](it-macos)                                 | Runs tests specific to macOS (Safari)                                                                                             |
| [it-testbook](it-testbook)                           | Runs tests with a testbook                                                                                                        |
| [it-visual-regression](it-visual-regression)         | Runs tests with the VRT capability enabled, failing fast                                                                          |
| [it-visual-regression-fae](it-visual-regression-fae) | Runs tests with the VRT capability enabled, failing at the end                                                                    |
| [it-windows](it-windows)                             | Runs tests on Windows (useful for GH actions to reduce flakiness)                                                                 |
| [spectrum](spectrum)                                 | Framework                                                                                                                         |
| [verify-appium](verify-appium)                       | Verifies results of the `it-appium` module                                                                                        |
| [verify-browsers](verify-browsers)                   | Verifies results of the `it`, `it-testbook`, `it-grid`, `it-bidi`, `it-visual-regression`, and `it-visual-regression-fae` modules |
| [verify-commons](verify-commons)                     | Contains common classes used in other verify modules                                                                              |
| [verify-macos](verify-macos)                         | Verifies results of the `it-macos` module                                                                                         |
| [verify-windows](verify-windows)                     | Verifies results of the `it-windows` module                                                                                       |
| [cleanup](cleanup)                                   | Cleans each module after the execution. See [below](#purpose-of-the-cleanup-module)                                               |

Some tests are meant to fail or skipped for demonstration purposes, for example to check how they are displayed in the html report.
These modules' build will not fail anyway: they will be checked later on by the `verify-*` modules.

## Entrypoint

Spectrum leverages [SpectrumSessionListener](spectrum/src/main/java/io/github/giulong/spectrum/SpectrumSessionListener.java) as its entrypoint,
which is a [LauncherSessionListener](https://docs.junit.org/current/user-guide/#launcher-api-launcher-session-listeners-custom)
registered via the Service Loader mechanism. This means we provide its fqdn in the
[org.junit.platform.launcher.LauncherSessionListener](spectrum/src/main/resources/org.junit.platform.launcher.LauncherSessionListener)
file, which is copied into the `META-INF/services` folder during the `prepare-package` phase.
It's not placed already there since that would load the framework during its own unit tests, breaking them.

## Purpose of the Cleanup module

Outside the Maven build of the entire project, so to trigger single tests from the IDE, these conditions need to be satisfied:

* to be able to run the framework's **unit tests**, we need to delete
  the [org.junit.platform.launcher.LauncherSessionListener](spectrum/src/main/resources/org.junit.platform.launcher.LauncherSessionListener)
  from [spectrum/target/classes/META-INF/services](spectrum/target/classes/META-INF/services)
* to be able to run **unit** and **e2e tests** in other modules, we need to have it in their respective `target/classes/META-INF/services` folder

To avoid manual operations, at the end of the full build, the `cleanup` module will execute the corresponding action for each module.

## How to build the project

You can leverage the [Maven wrapper](https://maven.apache.org/wrapper/) bundled in this repo.
Below you can see how to build the entire project or just few submodules.

> ⚠️ **Run configurations**<br/>
> `IntelliJ IDEA` run configurations are versioned in the [.run](.run) folder, so to be imported automatically in `IDEA`.

### Full build

This is how to trigger the full build:

| OS      | Command                                                                                                       |
|---------|---------------------------------------------------------------------------------------------------------------|
| unix    | `./mvnw -T 1C clean spotless:apply install -DskipSign -DbrowsersTests -DappiumTests -DmacosTests -fae -ntp`   |
| windows | `mvnw.cmd -T 1C clean spotless:apply install -DskipSign -DbrowsersTests -DappiumTests -DmacosTests -fae -ntp` |

Where:

* `-T 1C` means we're building with 1 thread per core.
* `clean` is needed to avoid the `verify-*` modules check outdated reports of previous builds.
* `spotless:apply` lints and fixes the source code formatting according to [spotless.xml](spotless.xml).
* `install` will copy the built framework (jar) in your local maven repo, so that you can use it locally in other projects/modules.
* `-DskipSign` avoid signing the artifact with a gpg key. That's needed in GitHub actions to publish
  on [Maven Central](https://central.sonatype.com/artifact/io.github.giulong/spectrum).
* `-DbrowsersTests` activates the profiles needed to run tests on all browsers. It's equivalent to `-P chrome,firefox,edge`.
* `-DappiumTests` activates the profiles needed to run tests on Appium. It's equivalent to `-P uiAutomator2`.
* `-DmacosTests` activates the profiles needed to run tests specific to Safari. It's equivalent to `-P safari`.
* `-fae` is [Maven's](https://maven.apache.org/ref/3.6.3/maven-embedder/cli.html) shorthand for `--fail-at-end`, needed to always run the `cleanup` module.
* `-ntp` is [Maven's](https://maven.apache.org/docs/3.6.1/release-notes.html#user-visible-changes) shorthand for `--no-transfer-progress`.

> ⚠️ **Appium**<br/>
> In order to run Appium tests, you need to install it on your local machine. Be sure to check
> [Appium quickstart](http://appium.io/docs/en/latest/quickstart/).
> You need Appium server and all the drivers for the corresponding technologies.

> ⚠️ **Safari**<br/>
> [Safari Integration Tests](it-macos/src/test/java/io/github/giulong/spectrum/it_macos/tests/SafariCheckboxIT.java)
> are executed only on macOS.

### Framework-only build

To build just the framework's jar locally, you can run the commands below.

| OS      | Command                                                                         | Unit Tests |
|---------|---------------------------------------------------------------------------------|------------|
| unix    | `./mvnw spotless:apply install -DskipSign -ntp -P framework-only`               | ✅          |
| unix    | `./mvnw spotless:apply install -DskipTests -DskipSign -ntp -P framework-only`   | ❌          |
| windows | `mvnw.cmd spotless:apply install -DskipSign -ntp -P framework-only`             | ✅          |
| windows | `mvnw.cmd spotless:apply install -DskipTests -DskipSign -ntp -P framework-only` | ❌          |

### Linting

Both the [Checkstyle plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/) and
[Spotless](https://github.com/diffplug/spotless) run on every build. If you'd like to just lint the code, you can run:

| OS      | Command                                                                                                                              |
|---------|--------------------------------------------------------------------------------------------------------------------------------------|
| unix    | `./mvnw checkstyle:checkstyle spotless:apply -Dcheckstyle.config.location=checkstyle.xml -DskipSign -DskipTests -ntp -pl spectrum`   |
| windows | `mvnw.cmd checkstyle:checkstyle spotless:apply -Dcheckstyle.config.location=checkstyle.xml -DskipSign -DskipTests -ntp -pl spectrum` |

### Maven Profiles

These are the available profiles you can find in [pom.xml](pom.xml):

| Profile        | Description                                                             |
|----------------|-------------------------------------------------------------------------|
| all            | runs the full build. Active by default                                  |
| framework-only | builds the framework, skipping unit tests                               |
| browsers       | runs tests in the `it`, `it-testbook`, `it-grid`, and `it-bidi` modules |
| macos          | runs tests in the `it-macos` module                                     |
| windows        | runs tests in the `it-windows` module                                   |
| appium         | runs tests in the `it-appium` module                                    |

You can leverage them to run specific groups of submodules together with the related `verify-*` and `cleanup` modules as well.
Additionally, submodules have their own profiles to limit the execution to specific drivers.
Check their own pom files for details.

## Workflow

We use [GitFlow](http://datasift.github.io/gitflow/IntroducingGitFlow.html):

* `feature` branches are created from `develop`. Once the feature is complete, a pull request must be opened towards `develop`.
* `release` branches must be created from `develop`. A pull request must be opened towards `main`.
* When a PR on `main` is merged, the `deploy` workflow will start. Once that is successful, a merge back on `develop` is made.

GitHub [workflows](.github/workflows):

| Branch     | Push                                 | PR opened                            | PR reopened                          | PR edited                            | PR closed                              |
|------------|--------------------------------------|--------------------------------------|--------------------------------------|--------------------------------------|----------------------------------------|
| develop    | [build](.github/workflows/build.yml) | [build](.github/workflows/build.yml) | [build](.github/workflows/build.yml) | [build](.github/workflows/build.yml) |                                        |
| feature/** | [build](.github/workflows/build.yml) |                                      |                                      |                                      |                                        |
| bugfix/**  | [build](.github/workflows/build.yml) |                                      |                                      |                                      |                                        |
| docs/**    |                                      |                                      |                                      |                                      | [docs](.github/workflows/docs.yml)     |
| main       |                                      |                                      |                                      |                                      | [deploy](.github/workflows/deploy.yml) |

## How to build the docs locally

Spectrum's docs is versioned under the [docs](docs) folder. It leverages [Jekyll](https://jekyllrb.com/) and the
[Modernist Theme](https://github.com/pages-themes/modernist).
To run it locally:

1. Setup your local environment as explained
   in [Testing your GitHub Pages site locally with Jekyll](https://docs.github.com/en/pages/setting-up-a-github-pages-site-with-jekyll/testing-your-github-pages-site-locally-with-jekyll).
2. Run Jekyll under the `docs` folder with `cd docs && bundle install && bundle exec jekyll serve --config _config.yml,_config_local.yml --open-url http://127.0.0.1:4000/spectrum/`.

> 💡 **Tip**<br/>
> You can leverage the [docs](.run/docs.run.xml) run configuration, which is automatically loaded in IntelliJ

