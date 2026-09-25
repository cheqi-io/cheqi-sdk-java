# Releasing the Java SDK

The `v*` tag protection rule allows only its bypass users to create release tags. GitHub Actions has no bypass access, so a merge to `main` does not create a tag. A push of a release tag starts the Maven Central publishing workflow.

1. Merge the release changes to `main` and wait for CI to pass.
2. As a user with tag-rule bypass access, fetch the latest `main` and confirm that `build.gradle` and `pom.xml` contain the intended version.
3. Create an annotated tag on that commit and push it:

   ```bash
   git fetch origin main
   git tag -a vX.Y.Z origin/main -m "Release vX.Y.Z"
   git push origin vX.Y.Z
   ```

4. Check the **Publish cheqi-sdk to Maven Central** workflow run for that tag. It verifies that the commit is on `main`, that the tag matches both project versions, and that the release artifacts build before publishing.

Do not move an existing release tag. If publishing fails after the tag is created, rerun its workflow rather than creating another tag at a different commit.
