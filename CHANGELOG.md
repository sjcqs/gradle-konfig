# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
[Unreleased]: https://github.com/sjcqs/gradle-konfig/compare/1.0.0...HEAD

## [1.1.1] - 2022-06-06
[1.1.1]: https://github.com/sjcqs/gradle-konfig/releases/tag/1.1.1
### Fixed
- Fixed provider nullable issue

## [1.1.0] - 2022-06-06
[1.1.0]: https://github.com/sjcqs/gradle-konfig/releases/tag/1.1.0
### Added
- Support the namespace attribute from `android` extension to set the Configuration package name.

## [1.0.7] - 2022-05-13
[1.0.7]: https://github.com/sjcqs/gradle-konfig/releases/tag/1.0.7
### Fixed
- Set a JVM language version

## [1.0.5] - 2022-05-06
[1.0.5]: https://github.com/sjcqs/gradle-konfig/releases/tag/1.0.5
### Changed
- Bump dependencies (Kotlin, Gradle, JUnit)
### Added
- Support Gradle configuration cache
- Support Gradle build cache

## [1.0.4]
[1.0.4]: https://github.com/sjcqs/gradle-konfig/compare/1.0.0...HEAD
### Changed
- Read package name from the manifest instead of build config task
### Added
- Weekly test with the latest Android Gradle Plugin version
- Workflow to manually test an Android Gradle Plugin version

## [1.0.3] - 2021-02-26
[1.0.3]: https://github.com/sjcqs/gradle-konfig/releases/tag/1.0.3
### Added
- Continuous integration workflow
- Release instructions
### Changed
- Make TaskConfiguration an interface
- Settings class was renamed Configuration
### Fixed
- Fix outdated tests

## [1.0.2] - 2021-02-17
[1.0.2]: https://github.com/sjcqs/gradle-konfig/releases/tag/1.0.2
### Fixed
- Settings class was written inside the wrong directory

## [1.0.0] - 2021-02-12
[1.0.0]: https://github.com/sjcqs/gradle-konfig/releases/tag/1.0.0
### Added
- Initial release