####

<img src="src/main/resources/spectrum-logo.png" alt="Spectrum logo">

[![Build](https://github.com/giulong/spectrum/actions/workflows/build.yml/badge.svg)](https://github.com/giulong/spectrum/actions?branch=main)
![coverage](https://github.com/giulong/spectrum/blob/actions/badges/.github/badges/jacoco.svg)
![branches coverage](https://github.com/giulong/spectrum/blob/actions/badges/.github/badges/branches.svg)

[![badge-jdk](https://img.shields.io/badge/jdk-17-blue.svg)](https://www.oracle.com/java/technologies/javase-downloads.html)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Spectrum is a Java/[Selenium 4](https://www.selenium.dev/) framework that aims to simplify the writing of E2E tests suites by:
* managing the WebDriver automatically
* generating html execution reports automatically
* mapping testbook execution to check coverage of expected tests cases
* providing out-of-the-box defaults to let you immediately run tests with no additional configuration needed
* providing a human-readable declarative configuration via yaml files

Spectrum leverages [JUnit 5](https://junit.org/junit5/docs/current/user-guide/) extension model to initialise and inject all the needed objects 
directly in your test classes, so that you can focus just on writing tests to navigate through your web application and run the needed assertions.

TODO: TABLE OF CONTENT

---
### Quick example

Let's see a quick example to immediately run a first test. All you need to do is:
1. add the Spectrum dependency to your project TODO maven link
2. create a **JUnit 5** test and make it extend the `SpectrumTest` class:
    ```Java
    public class HelloWorldIT extends SpectrumTest<Data> {
   
        @Test
        public void dummyTest() {
            webDriver.get(configuration.getApplication().getBaseUrl());
        }
   }
    ```

You can immediately run this test. After the execution, you will find a html report generated in the `target/spectrum/reports` folder.

TODO: examples

---
## Configuration

Spectrum is fully configurable and comes with default values which you can find in the [configuration.default.yaml](src/main/resources/yaml/configuration.default.yaml).

To provide different values, you can create the `src/test/resources/configuration.yaml` file in your project.
Furthermore, you can provide how many env-specific configurations in the same folder, by naming them 
`configuration-<ENV>.yaml`, where `<ENV>` is a placeholder that you need to replace with the actual environment name.

To let Spectrum pick the right environment-related configuration, you must run with the `-Denv` flag.

> **_Example:_**
> When running tests with `-Denv=test`, Spectrum will merge these three files in this order of precedence:
> 1. configuration-test.yaml
> 2. configuration.yaml 
> 3. configuration.default.yaml [Spectrum internal defaults]

Values in the most specific configuration file will take precedence over the others.

> 💡<br/>
> There's no need to repeat everything: configuration files are merged, so it's better to keep values that are common to all the environments in the base configuration.yaml,
> while providing `<ENV>`-specific ones in the `configuration-<ENV>.yaml`

> 💡<br/>
> If you need different configurations for the same environment, instead of manually changing values in the configuration*.yaml, you should
> provide different files and choose the right one with the `-Denv` flag. <br/>
> For example, if you need to run from your local machine targeting sometimes a remote grid, and sometimes running in local, you should have these two, where you change just the target runtime:
> * configuration-local-local.yaml
> * configuration-local-grid.yaml

### Vars node
The `vars` node is a special one in the `configuration.yaml`: you can use it to define common vars once and refer to them in several nodes. `vars` is a map, so you can define how many keys you need.

```yaml
vars:
  commonKey: some-value

node:
  property: ${commonKey} # Will be replaced with `some-value`

anotherNode:
  subNode:
    key: ${commonKey} # Will be replaced with `some-value`
```

### Values interpolation

Each non-object value in the configuration can be interpolated by placing a dollar-string like this:

```yaml
object:
  key: ${key:-defaultValue}
```

Where the `:-` is the separator between the name of the key to search for and the default value in case the key is not found. The default value is optional: you can just have `${key}`

Spectrum will replace the dollar-string with the first value found in this list:
1. key named `key` in `vars` node in the `configuration.yaml`
    ```yaml
    vars:
      key: value 
   ```
2. system property named `key`
3. `defaultValue` (if provided)

If the provided key can't be found, a warning will be raised. Both key name and default value can contain dots like in `${some.key:-default.value}`

TODO: Configuration nodes

## Reports



---

## Contributors:

- Giulio Longfils: [![Linkedin](https://i.stack.imgur.com/gVE0j.png) LinkedIn](https://www.linkedin.com/in/giuliolongfils/)
