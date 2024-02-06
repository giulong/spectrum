<div align="center">

[![Build](https://github.com/giulong/spectrum/actions/workflows/build.yml/badge.svg?branch=develop)](https://github.com/giulong/spectrum/actions?query=branch%3Adevelop)
[![javadoc](https://javadoc.io/badge2/io.github.giulong/spectrum/javadoc.svg)](https://javadoc.io/doc/io.github.giulong/spectrum)
[![coverage](https://giulong.github.io/spectrum/badges/jacoco.svg)](https://giulong.github.io/spectrum/jacoco/)
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
<a href="https://giulong.github.io/spectrum/jacoco/">Coverage</a>
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
    <li><a href="#contacts">Contacts</a></li>
    <li><a href="#local-development">Local Development</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

## About

Spectrum is a **JUnit 5** and **Selenium 4** framework that aims to simplify e2e tests development providing these features automatically:

* **Webdriver** instantiation
* **Html report** generation
* **Execution video** generation
* **Coverage report** generation by reading a **testbook**
* **Mail/Slack notifications** with reports as attachments

Spectrum manages all the boilerplate code to get these features and many more, allowing you to focus on test logic.
* It is fully configurable via a **declarative yaml file**
* It provides **out-of-the-box defaults** to let you run tests with no additional configuration

## Getting Started

All you need to do is take the three steps listed below, as shown in this video:

https://github.com/giulong/spectrum/assets/27963644/f9339a81-ae55-453a-a013-7ad893738c08

> ⚠️ JDK<br/>
> Since Spectrum is compiled with a jdk 21, you need a [jdk 21+](https://jdk.java.net/21/) to be able to run your tests.

1. Generate a new project leveraging the [Spectrum Archetype](https://mvnrepository.com/artifact/io.github.giulong/spectrum-archetype) via your IDE or by running this in a terminal and providing values when prompted:

   `mvn archetype:generate -DarchetypeGroupId=io.github.giulong -DarchetypeArtifactId=spectrum-archetype -DarchetypeVersion=LATEST`
2. Run the `src/test/java/<PACKAGE NAME>/tests/LoginFormIT` demo test injected by the archetype. You will see an instance of Chrome starting.
3. Once the execution is done, you will find the `target/spectrum/reports/spectrum-report-<TIMESTAMP>.html` html report with the execution video attached.

Here's a quick overview of the project created by the archetype, along with the report generated after the first execution:

<img style="width: 49%;" src="src/main/resources/images/login-form-it.jpg" alt="login-form"/>&nbsp;&nbsp;&nbsp;<img style="width: 49%;" src="src/main/resources/images/login-form-it-extent-report.jpg" alt="report"/>

And here's the generated video:

https://github.com/giulong/spectrum/assets/27963644/df6b801e-91ca-415b-b510-a45b7392de20

Additionally, you can configure Spectrum to automatically produce a html summary and coverage report like these:

<img style="width: 49%;" src="src/main/resources/images/html-summary.png" alt="summary"/>&nbsp;&nbsp;&nbsp;<img style="width: 49%;" src="src/main/resources/images/html-testbook.png" alt="html testbook"/>

For more details, please refer to the [full documentation](https://giulong.github.io/spectrum/).

## Contacts

| Creator         | GitHub ![github logo](src/main/resources/images/github-mark.png) | Linkedin ![LinkedIn](https://i.stack.imgur.com/gVE0j.png)      | Email ![gmail logo](src/main/resources/images/gmail-icon.png) |
|-----------------|------------------------------------------------------------------|----------------------------------------------------------------|---------------------------------------------------------------|
| Giulio Longfils | [giulong](https://github.com/giulong)                            | [Giulio Longfils](https://www.linkedin.com/in/giuliolongfils/) | [giuliolongfils@gmail.com](mailto:giuliolongfils@gmail.com)   |

If you're using Spectrum, please consider giving it a GitHub Star ⭐. It would be really appreciated 🙏

## Local development

If you're interested in building Spectrum locally, check out the [DEV readme](DEV.md)

## Acknowledgments

Spectrum leverages these projects you should definitely check out!

* [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
* [Selenium](https://www.selenium.dev/)
* [Lombok](https://projectlombok.org/)
* [Extent Reports](https://www.extentreports.com/)
* [FreeMarker](https://freemarker.apache.org/)
* [Simple Java Mail](https://www.simplejavamail.org/)
* [JCodec](http://www.jcodec.org/)
* [VicTools JsonSchema Generator](https://victools.github.io/jsonschema-generator/#introduction)
* [Modernist Theme](https://github.com/pages-themes/modernist)
* [Echo Maven Plugin](https://github.com/Ekryd/echo-maven-plugin)
