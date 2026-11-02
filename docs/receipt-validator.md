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

## Preview provenance

This preview uses the successful four-language CI artifact from
[Cheqi PR #712](https://github.com/cheqi-io/cheqi/pull/712),
[run 41459984708](https://github.com/cheqi-io/cheqi/actions/runs/41459984708).
The committed manifest records the precise source commit, ABI and SHA-256.
It is a pinned preview rather than a published validator release.

The Java models were regenerated from `https://test.api.cheqi.io/v3/api-docs.yaml`
to include the new payment status, split-payment and adjustment-total fields.
The production-spec CI guard remains enabled. Production API promotion and the
normal schema sync are prerequisites for a releasable SDK; this work publishes
nothing to Maven Central and changes no release tags.

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
