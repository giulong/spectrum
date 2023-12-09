<div align="center">

[![Build](https://github.com/giulong/spectrum/actions/workflows/build.yml/badge.svg?branch=develop)](https://github.com/giulong/spectrum/actions?query=branch%3Adevelop)
[![coverage](https://github.com/giulong/spectrum/blob/actions/badges/.github/badges/jacoco.svg)](https://giulong.github.io/spectrum/jacoco/)
[![branches coverage](https://github.com/giulong/spectrum/blob/actions/badges/.github/badges/branches.svg)](https://giulong.github.io/spectrum/jacoco/)
[![badge-jdk](https://img.shields.io/badge/jdk-17-blue.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.giulong/spectrum.svg)](https://search.maven.org/search?q=g:io.github.giulong%20a:spectrum)

<br />
<img src="src/main/resources/images/spectrum-logo.png" alt="Spectrum logo">

<br/>
<div>
<a href="https://giulong.github.io/spectrum/">Full Docs</a>
·
<a href="https://giulong.github.io/spectrum/apidocs/">Javadoc</a>
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
    <li><a href="#license">License</a></li>
    <li><a href="#contacts">Contacts</a></li>
    <li><a href="#local-development">Local Development</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

## About

Spectrum is a **JUnit 5** and **Selenium 4** framework that aims to simplify the writing of e2e tests providing these features:

* automatic **log and html report** generation
* automatic **execution video** generation
* automatic **coverage report** generation by reading a **testbook**
* automatic **mail/slack notifications** with reports as attachments
* fully configurable providing human-readable and **declarative yaml files**
* **out-of-the-box defaults** to let you run tests with no additional configuration

## Getting Started

All you need to do is take the three steps listed below, as shown in this video:

https://github.com/giulong/spectrum/assets/27963644/f9339a81-ae55-453a-a013-7ad893738c08

> ⚠️ JDK<br/>
> Since Spectrum is compiled with a jdk 17, you need a [jdk 17+](https://jdk.java.net/java-se-ri/17) to be able to run your tests.

1. Generate a new project leveraging the [Spectrum Archetype](https://mvnrepository.com/artifact/io.github.giulong/spectrum-archetype) via your IDE or by running this (replacing `<GROUP ID>`, `<ARTIFACT ID>`, `<VERSION>`, and `<DESTINATION>` with actual values):

    `mvn archetype:generate -DarchetypeGroupId=io.github.giulong -DarchetypeArtifactId=spectrum-archetype -DarchetypeVersion=LATEST -DinteractiveMode=false -DgroupId=<GROUP ID> -DartifactId=<ARTIFACT ID> -Dversion=<VERSION> -DoutputDirectory=<DESTINATION>`
2. Run the `src/test/java/<PACKAGE NAME>/tests/LoginFormIT` demo test injected by the archetype. You will see an instance of Chrome starting.
3. Once the execution is done, you will find the `target/spectrum/reports/spectrum-report-<TIMESTAMP>.html` html report with the execution video attached.

Here's a quick overview of the project created by the archetype, along with the report and video generated after the first execution:

![LoginFormIT](src/main/resources/images/login-form-it.jpg)
![Extent Report](src/main/resources/images/login-form-it-extent-report.jpg)

https://github.com/giulong/spectrum/assets/27963644/187e237c-7db1-494b-8a3b-5839565ae0b0

For more details, please refer to the [full documentation](https://giulong.github.io/spectrum/)

## License

Distributed under the [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0) License. See [LICENSE](LICENSE) for more information.

## Contacts

[Giulio Longfils ![LinkedIn](https://i.stack.imgur.com/gVE0j.png)](https://www.linkedin.com/in/giuliolongfils/) | [giuliolongfils@gmail.com](mailto:giuliolongfils@gmail.com)

If you're using Spectrum, please consider giving it a GitHub Star ⭐. It would be really appreciated 🙏

## Local development

If you're interested in building this repo locally, check out the [DEV readme](DEV.md).

## Acknowledgments

Spectrum leverages these libraries/frameworks you should definitely check out!

* [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
* [Selenium](https://www.selenium.dev/)
* [Lombok](https://projectlombok.org/)
* [Extent Reports](https://www.extentreports.com/)
* [FreeMarker](https://freemarker.apache.org/)
* [Simple Java Mail](https://www.simplejavamail.org/)
* [JCodec](http://www.jcodec.org/)
* [VicTools JsonSchema Generator](https://victools.github.io/jsonschema-generator/#introduction)
