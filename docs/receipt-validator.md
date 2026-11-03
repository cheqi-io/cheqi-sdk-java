# Bundled Rust payload validation

The SDK carries the exact validator WASM and manifest in
`src/main/resources/com/cheqi/sdk/validation`. The module is compiled to JVM
bytecode during both Maven and Gradle builds using Chicory 1.7.5. Generated
classes and WASM metadata are packaged in the SDK JAR. Only Chicory's pure-Java
runtime is a consumer dependency; its compiler and ASM stay in the build tools.
The loader verifies the bundled binary checksum and ABI before use. Each call
owns independent memory, and failures stop issuance.

The Java `InvoiceValidationReport` and `InvoiceValidationIssue` classes bind the
existing Rust report contract. Invalid input is attached to the existing
`CheqiSDKException.getValidationReport()`. The SDK contains no copies of the EN
rules or amount calculations. The single validated JSON snapshot is encrypted
unchanged for each device and reused by fallback; BigDecimal values are encoded
as exact decimal strings.

## Bundle provenance

This bundle uses the successful four-language CI artifact from the merged
[Cheqi PR #712](https://github.com/cheqi-io/cheqi/pull/712),
[run 41487248795](https://github.com/cheqi-io/cheqi/actions/runs/41487248795).
The committed manifest records the precise source commit, ABI and SHA-256.
It is pinned to main commit `fb96508512b4d42eb72078632dd4238c174bbe75`,
rather than a published validator release.

The Java models were regenerated from `https://api.cheqi.io/v3/api-docs.yaml`
using `make fetch-spec ENV=prod` and `make generate`. The models include payment
status, split-payment and adjustment-total fields, plus the required payment
object and VAT breakdown constraints. Bean Validation annotations are emitted
by the generator; generated classes are not edited manually. The production
spec guard remains enabled. This work publishes nothing to Maven Central and
changes no release tags.

## Android

Enable core library desugaring in the consuming Android app. Chicory uses
Java collection APIs that are unavailable on older Android versions without
desugaring. Cheqi Android enables `com.android.tools:desugar_jdk_libs:2.1.5`
and supports API 28 and newer. The validator loader reads resources using
stream operations available on those versions. The JAR includes consumer R8
rules for the validation reports and compiled module resource names.

The module is compiled when building the SDK; Android does not need a runtime
compiler, Rust toolchain, JNI wrapper or validator download. Validation applies
to new issuance; historical received receipt data is not revalidated through
this issuer preflight.

## Updating the bundle

Download a specific tested validator release/CI bundle, then run:

```sh
python3 scripts/update-receipt-validator.py /path/to/verified-bundle
mvn test
./gradlew clean build --no-daemon
./scripts/run-validation-demo.sh
```

The importer rejects dirty builds, unsupported ABIs and checksum mismatches.
Review and commit the binary and manifest together. The normal SDK release
then ships this pinned bundle; customer machines never fetch the validator or
rebuild Rust. Once validator releases are published, use a specific versioned
release asset as the import source.
