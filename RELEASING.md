## Releasing

1. Bump version in `gradle.properties`

2. Update the `CHANGELOG.md`:
   1. Change the `Unreleased` header to the release version.
   2. Add a link URL to ensure the header link works.
   3. Add a new `Unreleased` section to the top.

3. Commit

   ```
   $ git commit -am "Prepare version X.Y.X"
   ```

4. Tag

   ```
   $ git tag -am "Version X.Y.Z" X.Y.Z
   ```

5. Push!

   ```
   $ git push && git push --tags
   ```

6. Workflows will be triggered and release the plugin to the gradle plugins repository.