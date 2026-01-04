<div align="center">

[![Build](https://github.com/giulong/spectrum/actions/workflows/build.yml/badge.svg?branch=develop)](https://github.com/giulong/spectrum/actions?query=branch%3Adevelop)
[![CodeQL](https://github.com/giulong/spectrum/actions/workflows/codeql.yml/badge.svg)](https://github.com/giulong/spectrum/actions/workflows/codeql.yml)
[![coverage](https://giulong.github.io/spectrum/badges/jacoco.svg)](https://giulong.github.io/spectrum/jacoco/)
[![javadoc](https://javadoc.io/badge2/io.github.giulong/spectrum/javadoc.svg)](https://javadoc.io/doc/io.github.giulong/spectrum)
[![badge-jdk](https://img.shields.io/badge/jdk-21-blue.svg)](https://jdk.java.net/archive/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.giulong/spectrum.svg)](https://central.sonatype.com/artifact/io.github.giulong/spectrum)
[![GitHub commit activity (branch)](https://img.shields.io/github/commit-activity/y/giulong/spectrum/develop)](https://github.com/giulong/spectrum/commits/develop/)
[![GitHub Discussions](https://img.shields.io/github/discussions/giulong/spectrum)](https://github.com/giulong/spectrum/discussions)

<br />
<img src="docs/assets/images/spectrum-logo.png" alt="Spectrum logo">

<br/>
<br/>
<div>
<a href="https://giulong.github.io/spectrum/">Full Docs</a>
·
<a href="https://github.com/giulong/spectrum/issues/new?template=bug_report.md">Report Bug</a>
·
<a href="https://github.com/giulong/spectrum/issues/new?template=feature-request.md">Request Feature</a>

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
      <li><a href="#acknowledgments">Acknowledgments</a></li>
   </ol>
</details>

# About

Spectrum is an **e2e test automation framework** that leverages **JUnit 6** and **Selenium 4** to provide these features automatically:

* [Driver management](https://giulong.github.io/spectrum/#drivers-and-environments) with
  [auto-waiting](https://giulong.github.io/spectrum/#auto-waiting), to **highly reduce flakiness**.
* [Generation of a html report](https://giulong.github.io/spectrum/#automatically-generated-reports) with the
  [execution video](https://giulong.github.io/spectrum/#automatic-execution-video-generation),
  [coverage report](https://giulong.github.io/spectrum/#testbook---coverage) and more. All **fully customisable**.
* [Visual Regression Testing](https://giulong.github.io/spectrum/#visual-regression-testing),
  comparing visual snapshots of the AUT to identify regressions.
* [Mail/Slack notifications](https://giulong.github.io/spectrum/#event-sourcing---notifications) with reports as attachments.
* **No custom API**: it enriches plain Selenium tests transparently.
* Configurable via a **declarative yaml file**, with
  [defaults](https://github.com/giulong/spectrum/blob/develop/spectrum/src/main/resources/yaml/configuration.default.yaml)
  to run with no additional configuration.
* Supports **browsers automation** via Selenium and **mobile/desktop automation** via Appium.
* Supports [WebDriver BiDi](https://giulong.github.io/spectrum/#webdriver-bidi) protocol.

Spectrum manages all the boilerplate code, allowing you to **focus on test logic**:
write a JUnit test using the vanilla Selenium API, and **Spectrum will enrich your suite transparently**.

# Getting Started

> ⚠️ Spectrum requires [Java 21](https://jdk.java.net/archive/) or newer.

The easiest way is to take the three steps listed below, as shown in this video:

https://github.com/giulong/spectrum/assets/27963644/fecee8f5-f6dc-4b47-81a3-514e66b3907d

1. Generate a new project leveraging the [Spectrum Archetype](https://mvnrepository.com/artifact/io.github.giulong/spectrum-archetype):

   ```text
   mvn archetype:generate -DarchetypeGroupId=io.github.giulong -DarchetypeArtifactId=spectrum-archetype
   ```

2. Run the `LoginFormIT` demo test.
3. Check the generated report, which opens automatically in your browser.

> ⚠️ Tests run on Chrome by default. As shown in the video above, you can change this with:<br/>
> `-Dspectrum.driver=firefox`, `-Dspectrum.driver=edge` or `-Dspectrum.driver=safari`

Here's an overview of the project created by the archetype, along with the generated report and video:<br/><br/>
<img style="width: 49%; vertical-align: top;" src="docs/assets/images/login-form-it.png" alt="login-form"/>
&nbsp;<img style="width: 49%; vertical-align: top;" src="docs/assets/images/login-form-it-extent-report.png" alt="report"/>

https://github.com/giulong/spectrum/assets/27963644/df6b801e-91ca-415b-b510-a45b7392de20

You can also configure Spectrum to produce additional reports, such as [summary](https://giulong.github.io/spectrum/#execution-summary)
and [coverage](https://giulong.github.io/spectrum/#testbook---coverage):<br/><br/>
<img style="width: 49%; vertical-align: top;" src="docs/assets/images/readme-html-summary.png" alt="summary"/>
&nbsp;<img style="width: 49%; vertical-align: top;" src="docs/assets/images/readme-html-testbook.png" alt="html testbook"/>

If you like Spectrum, please consider giving it a GitHub Star ⭐

# Usage

Starting without the archetype is as simple as following these steps:

1. Add the Spectrum dependency to your project, you can find the snippet for every build tool [here](https://central.sonatype.com/artifact/io.github.giulong/spectrum).

   ```xml
   <dependency>
       <groupId>io.github.giulong</groupId>
       <artifactId>spectrum</artifactId>
       <version>1.28.0</version>
       <scope>test</scope>
   </dependency>
   ```

2. Create the `HelloWorldIT` test class extending `SpectrumTest`:

   ```java
   import io.github.giulong.spectrum.SpectrumTest;
   import org.junit.jupiter.api.Test;
   
   public class HelloWorldIT extends SpectrumTest<Void> {
   
       @Test
       public void dummyTest() {
           driver.get(configuration.getApplication().getBaseUrl());
       }
   }
   ```

3. create a basic `src/test/resources/configuration.yaml`:

   ```yaml
   application:
     baseUrl: https://the-internet.herokuapp.com/ # Change it with your app's landing page
   
   # video of the execution attached to the html report
   # (will be empty since the test is doing nothing)
   video:
     frames:
       - autoBefore
   
   # the html report will open automatically in your browser after the execution
   extent:
     openAtEnd: true
   ```
   
4. Run the test!

# Contributing

Contributions to Spectrum are welcome! Please check out the [CONTRIBUTING.md](CONTRIBUTING.md)
and the [open issues](https://github.com/giulong/spectrum/issues).

# Acknowledgments

<details>
   <summary>Spectrum leverages these projects you should definitely check out!</summary>
   <ul>
      <li><a href="https://docs.junit.org/current/user-guide/">JUnit 6</a></li>
      <li><a href="https://www.selenium.dev/">Selenium</a></li>
      <li><a href="http://appium.io/docs/en/latest/">Appium</a></li>
      <li><a href="https://projectlombok.org/">Lombok</a></li>
      <li><a href="https://www.extentreports.com/">Extent Reports</a></li>
      <li><a href="https://freemarker.apache.org/">FreeMarker</a></li>
      <li><a href="https://www.simplejavamail.org/">Simple Java Mail</a></li>
      <li><a href="http://www.jcodec.org/">JCodec</a></li>
      <li><a href="https://victools.github.io/jsonschema-generator/#introduction">VicTools JsonSchema Generator</a></li>
      <li><a href="https://jekyllrb.com/">Jekyll</a></li>
      <li><a href="https://github.com/pages-themes/modernist">Modernist Theme</a></li>
      <li><a href="https://github.com/datafaker-net/datafaker">Datafaker</a></li>
   </ul>
</details>
