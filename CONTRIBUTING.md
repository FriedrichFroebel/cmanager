# Contributing

## Reporting Problems

Problems can be reported in either English or German, where the former is recommended on GitHub. Please provide the following details within the report after having verified that there is not an existing report for it already:

* The used version of the application and where you got it from.
* The used operating system.
* The used Java version.
* The steps needed to reproduce this problem. If possible (and required for the problem) add a (minimal) GPX file (alternatively a pair of cache codes) to make it easier to investigate (you might remove personal information from the GPX file if you like).
* The error you get or the problem you have.

## Requesting Features

Feel free to request features after having verified that there is not an existing issue for it already, but please note that I do not have direct plans on implementing completely new features at the moment. You may send a pull request if you have implemented an interesting feature.

## Pull Requests

Feel free to open a pull request to fix a problem yourself or to contribute a new feature. See the list of issues for possible changes. Please indicate your work on an issue by commenting on it.

Please try to keep pull requests as small as possible - one new feature or fix set per pull request is preferred. This makes it easier to review and discuss your contribution. All code should work with Java versions starting at Java 8, but should be usable with later versions as well. Nevertheless you need to use at least Java 11 to successfully compile the application as some newer APIs are included.

Further instructions on how to build and run the application are included in the README.

### Code Style

The code should satisfy the following requirements:

* Everything should be written in English.
* The code style is based on the one of the Android Open Source project. The most important rules:

  * Use UTF-8 encoding.
  * Indent using 4 spaces.
  * Names should be in camelCase, `static final` variables should use UPPER_CASE, class names should be PascalCase.
  * Make as much variables `final` as possible.
  * Try to avoid abbreviated names for better readability.

  You may use the `googleJavaFormatter` (`goJF`) task to let the corresponding Gradle plugin do most of the work for you.

* Document your code.
* Provide dedicated tests if it makes sense.
* Try to avoid adding additional dependencies.

### Additional Notes

* Although Java 10 is sufficient for building the application, the code style plugin requires Java 11 at the moment.
* If you want to run all tests, you have to provide an [OKAPI key for the German test server](https://test.opencaching.de/okapi/signup.html) as well. Please note that some tests (from `OkapiAuthedTest` and `OkapiRealInteractionTest`) might fail by default as they assume that the `cmanager` user is being used for them. You might want to adapt these values to your needs, but please avoid pushing these changes within your pull request.
