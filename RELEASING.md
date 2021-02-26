## Releasing
1. Prepare the release
   ```
   $ ./scripts/release.main.kts --action=prepare-release --version=X.Y.Z
   ```

2. Push!

   ```
   $ git push && git push --tags
   ```

3. Workflows will be triggered and release the plugin to the gradle plugins repository.
