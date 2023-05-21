# Development version

* Migrate Apache HttpClient from version 4 to 5 ([documentation](https://hc.apache.org/httpcomponents-client-5.2.x/migration-guide/index.html)).

# Version 0.7 - 2023-03-13

* Fix OKAPI requests with small coordinate values near zero which have been serialized in scientific notation before.
* Remove own heap management.
* Update some libraries.
* Ignore pre-releases in library update checks.
* Test on OpenJDK >= 16 as well.
* Fix CI build due to outdated GitHub action.

# Version 0.6.1 - 2021-05-10

* Remember the bounds of the log copy dialog during the active session.
* Fix OKAPI requests with small coordinate values near zero which have been serialized in scientific notation before.
* Fix some NullPointerExceptions.
* Build the actual releases with OpenJDK 15.
* Update some libraries.

# Version 0.6.0 - 2020-10-14

* More deterministic handling of empty searches by using a fixed age of 6 months.
* Exclude own caches when searching for duplicates.
* Add log password field if the geocache requires one.
* Add missing Javadoc comments.
* Switch from Travis CI to GitHub Actions.
* Some internal refactoring for inner classes and enumerations.

# Version 0.5.3 - 2020-08-05

* Consider the date for event caches during comparison.

# Version 0.5.2 - 2020-07-08

* Make the duplicate search more reliable when running it multiple times without closing the application in between.
* Fix bug when trying to filter empty cache lists.
* Enable tests requiring existing logs or write operations again.
* Move OKAPI URL building to external class to increase readability.

# Version 0.5.1 - 2020-06-19

* Avoid blocking the application on cache list deserialization when updating from a version older than 0.4.1.

# Version 0.5.0 - 2020-06-18

* Require a configured GC username for the log synchronization.
* Limit "Sync with OC" results to caches which actually have a found log by the configured GC user.
* Warn on closing modified lists.
* Some internal refactoring regarding cache details and log entry panels.

# Version 0.4.1 - 2020-06-09

* Provide a link to the log after submission.
* Use look and feel of the system.
* Cache names just differing in trailing or leading whitespaces are now considered equal.
* Add basic HTTP proxy support using terminal arguments.
* Lots of internal refactoring.

# Version 0.4.0 - 2020-05-22

* Add fallback methods for opening an URL in the web browser.
* View logs as plaintext instead of HTML. This should finally provide correct line breaks in the text.
* Allow sending modified logs.
* Add support for Travis CI for automated `master` builds.
* Fix log type for webcam caches.
* BREAKING: Due to changed OKAPI keys, the application has to be authorized again.

# Version 0.2.48 - 2020-05-18

* Perform some refactoring.
* Add a vector version of the logo.
* Disable shadow list posting.
* Fix the colors on the map.
* Send the original log type.
* Check the log posting response and show errors to the user.
* Add support for the Java 14 `jpackage` tool.

# Version 0.2.47 - 2020-05-14

* Start working on parsing the log success status.
* Fix wrong difference highlighting for the cache type and container.
* Update ScribeJava.
* Add support for Java 14.
* Re-enable update checks.
* Show the about dialog.
* Add workaround for Java 8. This is due to some APIs being deprecated since Java 10 and some API being new there. As long as the current Java version does not remove the old API, a compatibility layer is used for providing the correct method call.

# Version 0.2.46 - 2020-05-13

* Disable the shadow list handler as the API endpoint seems to be broken. See https://github.com/FriedrichFroebel/cmanager/issues/5.

# Version 0.2.45 - 2020-04-24

* Use the latest Gradle and library versions.
* Sync with the original repository.
* Fix existing deprecation messages.

# Version 0.2.44.1 - 2017-07-01

* First version provided by *FriedrichFröbel*.
* Fix "Project Ape" error.
* Disable update check.
* Add Gradle wrapper again.
* Clean up files.
* Use the default Gradle directory structure.

# Version 0.2.44 - 2017-05-04

* Add Gradle versions plugin.
* Update libraries.

# Version 0.2.43 - 2017-05-02

* Perform some refactoring.
* Store information about HTTP responses without code 200.
* Handle OKAPI error responses for the username and cache getter.
* Switch OKAPI responses to JSON.
* Split OKAPI result caching and cache lookup.
* Add interface for OKAPI PIN entry.
* Only enable authorized "Find on OC" with valid OKAPI token.
* Add application logo by *Der Windling*.
* Add some network tests.

# Version 0.2.42 - 2017-03-29

* Check memory settings before trying to determine the JAR path to avoid unnecessary error messages.

# Version 0.2.41 - 2017-03-26

* Improve the error message about unknown JAR path for memory settings.
* Perform some refactoring.

# Version 0.2.40 - 2017-03-22

* Perform lots of refactoring by using sub-packages.
* Replace sphere distance calculation by haversine calculation for better precision.
* Do not block the GUI during the update checks.
* Improve coordinate parser.
* Add support for case-insensitive GC code comparison.
* Use better message for OKAPI PIN lookup.
* Add "Retract Listing" as valid log type.
* Add some tests.

# Version 0.2.39 - 2017-01-21

* Drop Gradle helpers.
* Update libraries.

# Version 0.2.38 - 2016-09-17

* Update libraries.
* Fix the application not quitting properly after selecting "Exit" from the menu.
* BREAKING: Disallow starting with Java 7 since some libraries are built with Java 8.

# Version 0.2.35 - 2016-04-07

* Fix setting username with uninitialized heap size setting.

# Version 0.2.34 - 2016-03-19

* Add update check.

# Version 0.2.33 - 2016-03-11

* Only use one instance of the shadow list.
* Move OKAPI search results cache.
* Use the timestamp for cache matching.
* Add Gradle support.
* Upgrade ScribeJava.
* Improve XML parser.

# Version 0.2.32 - 2016-03-11

* Fetch whole cache details on shadow list match.
* Post logged caches to shadow list.

# Version 0.2.31 - 2016-03-11

* Upgrade ScribeJava.
* Use OC shadow list.
* Do not match dummy caches at `0.0/0.0`.

# Version 0.2.30 - 2016-03-10

* Make access to listing store thread-safe.

# Version 0.2.29 - 2016-03-01

* Enable map movement.
* Remember which logs have already been copied to OC.

# Version 0.2.28 - 2016-02-29

* Parallelize the duplicate check.
* Use predefined values for difficulty and terrain filtering.
* Add a persistent OSM map cache.
* Do not reset the OKAPI cache for listings for each duplicate search.

# Version 0.2.27 - 2016-02-26

* Add table column for the log date.

# Version 0.2z - 2016-02-25

* Only enable undo menu if undo actions are available.
* Allow restarting the program to update the used heap size.
* Add OC username to settings.

# Version 0.2y - 2016-02-24

* Display a message if an error occurred with a 32 bit VM.
* Some cleanup.

# Version 0.2x - 2016-02-24

* Upgrade ScribeJava and joda-time.
* Generalize serialized settings functions.
* Remember and re-open opened cache lists.

# Version 0.2w - 2016-02-21

* Move filters to main menu.
* Allow setting cache coordinates as location using a right click.
* Display number of selected caches.
* Switch to HTTPS.
* Add undo support.
* Some cleanup.

# Version 0.2v - 2016-02-19

* Use official JMapViewer releases instead of bundling its source code.
* Allow inverting the table selection.

# Version 0.2u - 2016-02-14

* Fix exception if there is no value associated to the location list.

# Version 0.2t - 2016-02-12

* Add support for caching filter results.
* Fix distance filter.
* Fix caches not being copyable.

# Version 0.2s - 2016-02-11

* Make cache list table sortable.
* Add partial support for named geo locations.
* Generalize serialization methods.
* Add support for location persistence.
* Make locations accessible using the GUI.
* Add distance support to the cache list.
* Retrieve home location using OKAPI.

# Version 0.2r - 2016-02-08

* Unify found log definition.
* Add cache name filter.
* Ignore caches ignored on OC.
* Use multi-threaded filtering.

# Version 0.2q - 2016-01-28

* Stop saving cache listings to the hard-disk.

# Version 0.2p - 2016-01-27

* Allow adaption of heap size.

# Version 0.2o - 2016-01-26

* Bugfix for multi-threading.
* Allow reduced GPX files.

# Version 0.2n - 2016-01-19

* Add version to user agent.

# Version 0.2m - 2016-01-18

* First public version.
