# Konfig Plugin
[![Integration](https://github.com/sjcqs/gradle-konfig/actions/workflows/integrate.yml/badge.svg)](https://github.com/sjcqs/gradle-konfig/actions/workflows/integrate.yml)

This gradle plugin generates a Kotlin `Configuration` object using yaml files for Android.
It can merge multiple variants yaml files.

This plugin is a rewrite in Kotlin of [sjcqs/gradle-config](https://github.com/sjcqs/gradle-config)

## Usage

### Using `apply`
In the project-level `build.gradle`
```groovy
buildscript {
  repositories {
    gradlePluginPortal()
  }
  dependencies {
    classpath "fr.sjcqs.konfig:konfig:<version>"
  }
}
```
In your module (e.g app) `build.gradle`
```groovy
apply plugin: "fr.sjcqs.konfig"
```

### Using the plugins block
```groovy
plugins {
  id "fr.sjcqs.konfig" version "<version>"
}
```

## Config files

To generate the `Configuration` class, you need to create a directory named `config` in the module (e.g app) directory and add your yaml files inside:
```
config/default.yml
config/default_secret.yml
config/${dimension}.yml
config/${productFlavor}.yml
config/${productFlavor}_secret.yml
config/${buildType}.yml
config/${buildType}_secret.yml
```

Entries are generated using the current build variant. There are merged and the lower one overwrites upper one.
Left-most flavors dimensions overwrite the others ones.

Example:

- build.gradle
```groovy
flavorDimensions "brand", "environment"
productFlavors {
        brand1 {
            dimension "brand"
            // ...
        }
        brand2 {
            dimension "brand"
            // ...
        }
        stubs {
            dimension "environment"
            // ...
        }
        preprod {
            dimension "environment"
            // ...
        }
        prod {
            dimension "environment"
            // ...
        }
    }
```
- `<module>/config/default.yml`
``` yaml
section_1:
  entry_1: "Hello"
  entry_2: "world"
entry_3: 94
section_2:
  entry_1: "John"
  entry_2: "Doe"
entry_4: "Some value"
```
- `<module>/config/brand1.yml`
```yaml
section_1:
  entry_2: "tom"
```
- `<module>/config/brand2.yml`
```yaml
section_2:
  entry_1: "Jane"
```
- `<module>/config/stubs.yml`
```yaml
entry_3: 24
section_1:
  entry_2: "Pete"
```
- `<module>/config/debug.yml`
```yaml
section_2:
  entry_1: "Paul"
  entry_2: "Johson"
```
- `<module>/config/brand1StubsDebug.yml`
```yaml
entry_3: 42
```

On compilation, ```brand1StubsDebug``` build will generate the following ````Configuration```` fields.
variant.

```kotlin
Configuration.section_1.entry_1 // => "Hello" in default.yml
Configuration.section_1.entry_2 // => "Pete" overwritten by brand1.yml
Configuration.entry_3 // => 42 overwritten by brand1StubsDebug.yml
Configuration.section_2.entry_1 // => "Paul" overwritten by debug.yml
Configuration.section_2.entry_2 // => "Johnson" overwritten by debug.yml
Configuration.entry_4 // => "Some value" original value
```

You can suffix files with `_secret` (`<module>/config/debug_secret.yml`) in order to avoid to commit a secret file to remote repository. If you want to use this feature, don't forget to include those files to your `<module>/config/.gitignore`:

```
*_secret.yml
```

## Type

Consider this config file:

```yaml
string_entry: string
int_entry: 1
double_entry: 1.0
date_entry: 2001-11-23 15:03:17
list_entry:
  - 1
  - 2
invalid_list_entry:
  - 1
  - 'test'
object_entry:
  entry_a: 1
  entry_b: 2
list_of_map_entry:
  - a: 'a1'
    b: 'b1'
  - a: 'a2'
    b: 'b2'
```

The [snakeyaml](https://bitbucket.org/asomov/snakeyaml) library this plugin uses provides the following basic type conversions:

```kotlin
Int i = Configuration.int_entry;
String s = Configuration.string_entry;
Double d = Configuration.double_entry;
Date da = Configuration.date_entry;
List<Int> list = Configuration.list_entry;
```

The list with mixed type elements are not supported and throw an exception.

```
Error:Execution failed for task ':app:generateDevDebugConfiguration'.
> Not supported list with mixed type: [class Int, class java.lang.String]
```

The nested map entries are converted a generated class to achieve dot access.

## Known issues

## License
```
Copyright 2018 Satyan Jacquens
Copyright 2017 Polidea
Copyright 2015 Takuya Miyamoto

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
