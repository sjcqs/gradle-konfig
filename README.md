# Konfig Plugin

This gradle plugin generates a Kotlin `Settings` object using yaml files for Android.
It can merge multiple variants yaml files.

## Usage

In project level `build.gradle`
```groovy
buildscript {
  repositories {
    gradlePluginPortal()
  }
  dependencies {
    classpath "fr.sjcqs.konfig:konfig:1.0.1"
  }
}
```

## TODO

## License
```
Copyright 2018 sjcqs
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
