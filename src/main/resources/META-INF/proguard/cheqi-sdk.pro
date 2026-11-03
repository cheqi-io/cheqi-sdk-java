# Jackson reads validation reports through their constructors and accessors.
-keep class com.cheqi.sdk.validation.InvoiceValidationReport { *; }
-keep class com.cheqi.sdk.validation.InvoiceValidationIssue { *; }

# Compiled validator metadata is loaded relative to its generated class.
-keepnames class com.cheqi.sdk.validation.compiled.**
