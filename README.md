<div align="center">

[![Build](https://github.com/giulong/spectrum/actions/workflows/build.yml/badge.svg?branch=develop)](https://github.com/giulong/spectrum/actions?query=branch%3Adevelop)
[![CodeQL](https://github.com/giulong/spectrum/actions/workflows/codeql.yml/badge.svg)](https://github.com/giulong/spectrum/actions/workflows/codeql.yml)
[![coverage](https://giulong.github.io/spectrum/badges/jacoco.svg)](https://giulong.github.io/spectrum/jacoco/)
[![javadoc](https://javadoc.io/badge2/io.github.giulong/spectrum/javadoc.svg)](https://javadoc.io/doc/io.github.giulong/spectrum)
[![badge-jdk](https://img.shields.io/badge/jdk-21-blue.svg)](https://jdk.java.net/21/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.giulong/spectrum.svg)](https://search.maven.org/search?q=g:io.github.giulong%20a:spectrum)

<br />
<img src="src/main/resources/images/spectrum-logo.png" alt="Spectrum logo">

<br/>
<br/>
<div>
<a href="https://giulong.github.io/spectrum/">Full Docs</a>
·
<a href="https://javadoc.io/doc/io.github.giulong/spectrum/latest/index.html">Javadoc</a>
·
<a href="https://github.com/giulong/spectrum/issues/new?assignees=giulong&labels=&projects=&template=bug_report.md&title=%5BBUG%5D+%3CProvide+a+short+title%3E">Report Bug</a>
·
<a href="https://github.com/giulong/spectrum/issues/new?assignees=giulong&labels=&projects=&template=feature_request.md&title=%5BRFE%5D+%3CProvide+a+short+title%3E">Request Feature</a>

</div>
</div>
<br/>

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about">About</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#contacts">Contacts</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

# About

Spectrum is a **e2e test automation framework** that leverages **JUnit 5** and **Selenium 4** to simplify tests development providing these features automatically:

* **Driver** instantiation
* **Html report** generation
* **Execution video** generation
* **Coverage report** generation by reading a **testbook**
* **Mail/Slack notifications** with reports as attachments

Spectrum manages all the boilerplate code to get these features and many more, allowing you to focus on test logic.

* **Auto-waits** before interacting with elements, to **highly reduce flakiness**
* It is fully configurable via a **declarative yaml file**
* It provides **out-of-the-box defaults** to let you run tests with no additional configuration
* supports **browsers automation** via Selenium
* supports **mobile and desktop applications automation** via Appium

# Getting Started

> ⚠️ Spectrum requires [Java 21](https://jdk.java.net/archive/) or newer.

The easiest way is to take the three steps listed below, as shown in this video:

https://github.com/giulong/spectrum/assets/27963644/fecee8f5-f6dc-4b47-81a3-514e66b3907d

1. Generate a new project leveraging the [Spectrum Archetype](https://mvnrepository.com/artifact/io.github.giulong/spectrum-archetype):

   ```text
   mvn archetype:generate -DarchetypeGroupId=io.github.giulong -DarchetypeArtifactId=spectrum-archetype
   ```

2. Run the `LoginFormIT` demo test.
3. Check the report generated in `target/spectrum/reports/spectrum-report-<TIMESTAMP>/spectrum-report-<TIMESTAMP>.html`.

> ⚠️ Tests run on Chrome by default. You can change this running with:<br/>
> `-Dspectrum.driver=firefox`, `-Dspectrum.driver=edge` or `-Dspectrum.driver=safari`

# Usage

If you'd rather add Spectrum manually to an existing project, you just need to add its dependency.

## Maven

```xml
<dependency>
    <groupId>io.github.giulong</groupId>
    <artifactId>spectrum</artifactId>
    <version>1.22.2</version>
    <scope>test</scope>
</dependency>
```

## Gradle

```gradle
dependencies {
  implementation group: 'io.github.giulong', name: 'spectrum', version: '1.22.2'
}
```

Here's a quick overview of the project created by the archetype, along with the generated report:<br/><br/>
<img style="width: 49%; vertical-align: top;" src="src/main/resources/images/login-form-it.png" alt="login-form"/>
&nbsp;<img style="width: 49%; vertical-align: top;" src="src/main/resources/images/login-form-it-extent-report.png" alt="report"/>

And here's the generated video:

https://github.com/giulong/spectrum/assets/27963644/df6b801e-91ca-415b-b510-a45b7392de20

Additionally, you can configure Spectrum to automatically produce summary and coverage reports like these:<br/><br/>
<img style="width: 49%; vertical-align: top;" src="src/main/resources/images/html-summary.png" alt="summary"/>
&nbsp;<img style="width: 49%; vertical-align: top;" src="src/main/resources/images/html-testbook.png" alt="html testbook"/>

For all the available reports, how to customise them, and many more, please refer to the [full documentation](https://giulong.github.io/spectrum/).

# Contributing

Contributions to Spectrum are welcome! If you're interested, please check out the [CONTRIBUTING.md](CONTRIBUTING.md).

If you're using Spectrum, please consider giving it a GitHub Star ⭐

# Contacts

| Creator         | GitHub                                | Linkedin                                                       | Email                                                       |
|-----------------|---------------------------------------|----------------------------------------------------------------|-------------------------------------------------------------|
| Giulio Longfils | [giulong](https://github.com/giulong) | [Giulio Longfils](https://www.linkedin.com/in/giuliolongfils/) | [giuliolongfils@gmail.com](mailto:giuliolongfils@gmail.com) |

# Acknowledgments

Spectrum leverages these projects you should definitely check out!

* [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
* [Selenium](https://www.selenium.dev/)
* [Appium](http://appium.io/docs/en/latest/)
* [Lombok](https://projectlombok.org/)
* [Extent Reports](https://www.extentreports.com/)
* [FreeMarker](https://freemarker.apache.org/)
* [Simple Java Mail](https://www.simplejavamail.org/)
* [JCodec](http://www.jcodec.org/)
* [VicTools JsonSchema Generator](https://victools.github.io/jsonschema-generator/#introduction)
* [Jekyll](https://jekyllrb.com/)
* [Modernist Theme](https://github.com/pages-themes/modernist)
* [Datafaker](https://github.com/datafaker-net/datafaker)
