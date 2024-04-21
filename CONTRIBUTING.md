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

If you think there's something missing in Spectrum that is a good candidate to become a feature of the framework, please
[open a feature request](https://github.com/giulong/spectrum/issues/new?assignees=giulong&labels=&projects=&template=feature-request.md&title=%5BRFE%5D+%3CProvide+a+short+title%3E)
by fulfilling the provided template.

# Docs Enhancements

If you found something missing in the [docs](https://giulong.github.io/spectrum/#spectrum),
or you want to contribute with real configuration examples of how you use Spectrum, feel free to open a Pull Request.

> ⚠ Minor changes such as fixing typos or rewriting the current docs will not be accepted.

# Discussions

If you just want to ask something about Spectrum, either to maintainers or to other users, check the
[Discussions section](https://github.com/giulong/spectrum/discussions)
to see if there's something on that topic already. If not, feel free to open a new one.

# Submitting Changes

> ⚠ Purely cosmetic changes, such as just fixing whitespaces or formatting code, will not be accepted.
> Neither refactoring will.

If you'd like to actively contribute to Spectrum, whether it's source code or documentation, take these steps:

1. First of all, be sure to read the [DEV.md](DEV.md) to be able to build Spectrum and its docs locally
2. [Fork the repository](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/fork-a-repo)
3. Checkout a `feature/*`, `bugfix/*`, or `docs/*` branch with a short and meaningful name
4. Make your changes and be sure you have a successful local build (at least the framework-only build)
5. Be sure your code complies to the [Coding Conventions](#coding-conventions)
6. Submit a Pull Request towards `develop`

## Coding Conventions

Though the following conventions might appear pedantic, they aim to keep the codebase clean and maintainable.
Take your time to read the following bullets and to take a look at the already existing code:
Spectrum has quite a small codebase, so understanding the conventions already in place should be an easy task.

In any case, don't be overwhelmed by these general rules, reviews are made for checking everything is fine.
Don't be afraid to ask if you have any doubt or question, or if you just need some help. Let's talk!

### Source Code

Do's:

* be [SOLID](https://en.wikipedia.org/wiki/SOLID)
* Take a look around: your code should follow the conventions already in place
* Write small classes and methods, with reusability and evolvability in mind
* Leverage [convention over configuration](https://en.wikipedia.org/wiki/Convention_over_configuration), providing defaults to reduce the boilerplate code a user would need to explicitly write
* Explicitly mark variables as final. Mutable variables are not accepted, with very few exceptions
* Leverage Java 21 api
* Lines up to 180 chars are ok, with a grain of salt: put methods-chained calls on a new line only if they're many
* Write few meaningful logs at a proper level
* Ensure the checkstyle plugin doesn't produce warnings during the build

Don'ts:

* Shorten variables names
* Declare multiple variables on the same line
* Avoid script-like business logic. We have an object-oriented language here: if/switch/ternary usage must be kept to a minimum
* Avoid catching and re-throwing exceptions
* Avoid creating checked exceptions
* Avoid one-liners. Use variables with meaningful names to clarify
* Don't break lines as if we still have 80-chars terminals

### Unit Tests

Every line of code must be unit-tested. There's no reason not to do so.
If you write conditional logic, be sure you test each branch.
You can leverage the coverage report produced by the build at [docs/jacoco/index.html](docs/jacoco/index.html)
to see what's missing. Keep in mind that coverage is an important way to check missing branches, not just as an empty number.

Rules:

* Each class must:
  * be in the same package of its source counterpart
  * have a name that is made up of the **source** class' name + 'Test', such as `MySourceClass` &rarr; `MySourceClassTest`
* Each method must:
  * be `public void`
  * have a short and clear `@DisplayName`
  * not contain any conditional logic
* Strict mocking is required, as per mockito's default
* Use dummy values as arguments. Suggestion: if you have a method with an argument like `String fileName`, use a variable like `String fileName = "fileName";` in your test
* Avoid generic argument matchers such as `any()` when possible. Knowing what we're passing to methods calls matter, even if they're dummy values

### Integration Tests

Integration tests are not always needed. They are, for example, when implementing a new feature that produce some kind of artifact, such as a report.
In that case, we need an integration test that checks that artifact, and provide a way to avoid regressions.

Generally speaking, you can write integration tests if you think they're useful, but maybe it's **better asking** before wasting time.

Rules:

* Each class must:
  * have a name that ends with 'IT', such as `ExtentReportVerifierIT`
* Each method must:
  * be `public void`
  * have a short and clear `@DisplayName`
  * not contain any conditional logic