package com.cheqi.sdk.receipt;

import com.cheqi.sdk.models.generated.ClientReceiptDownloadRequest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * SDK-only pairing of a download URL (including its secret fragment key) with
 * the generated encrypted upload request. Persist before showing the QR.
 */
public final class PreparedReceiptDownload {
    private final String downloadUrl;
    private final ClientReceiptDownloadRequest uploadRequest;

    @JsonCreator
    public PreparedReceiptDownload(
            @JsonProperty("downloadUrl") String downloadUrl,
            @JsonProperty("uploadRequest") ClientReceiptDownloadRequest uploadRequest
    ) {
        this.downloadUrl = Objects.requireNonNull(downloadUrl, "downloadUrl");
        Objects.requireNonNull(uploadRequest, "uploadRequest");
        Objects.requireNonNull(uploadRequest.getDownloadId(), "downloadId");
        Objects.requireNonNull(uploadRequest.getCiphertext(), "ciphertext");
        this.uploadRequest = copy(uploadRequest);
    }

    public PreparedReceiptDownload(String downloadUrl, String downloadId,
                                   String ciphertext, String templateHash) {
        this(downloadUrl, new ClientReceiptDownloadRequest().downloadId(downloadId)
                .ciphertext(ciphertext).templateHash(templateHash));
    }

    public String getDownloadUrl() { return downloadUrl; }

    @JsonIgnore
    public String getDownloadId() { return uploadRequest.getDownloadId(); }
    @JsonIgnore
    public String getCiphertext() { return uploadRequest.getCiphertext(); }
    @JsonIgnore
    public String getTemplateHash() { return uploadRequest.getTemplateHash(); }

    /** Returns the generated API request, without exposing mutable persisted state. */
    @JsonProperty("uploadRequest")
    public ClientReceiptDownloadRequest getUploadRequest() { return copy(uploadRequest); }

    private static ClientReceiptDownloadRequest copy(ClientReceiptDownloadRequest request) {
        return new ClientReceiptDownloadRequest().downloadId(request.getDownloadId())
                .ciphertext(request.getCiphertext()).templateHash(request.getTemplateHash());
    }

    @Override
    public String toString() { return "PreparedReceiptDownload{url='<redacted>', ciphertext='<redacted>'}"; }
}
