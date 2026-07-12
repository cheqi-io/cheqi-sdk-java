package com.cheqi.sdk.download;

import java.util.Objects;

/** Client-generated receipt download link credentials. */
public final class DownloadLink {
    private final String downloadId;
    private final String contentKey;
    private final String url;

    public DownloadLink(String downloadId, String contentKey, String url) {
        this.downloadId = Objects.requireNonNull(downloadId, "downloadId");
        this.contentKey = Objects.requireNonNull(contentKey, "contentKey");
        this.url = Objects.requireNonNull(url, "url");
    }

    public String getDownloadId() {
        return downloadId;
    }

    public String getContentKey() {
        return contentKey;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "DownloadLink{downloadId='" + downloadId + "', url='<redacted>'}";
    }
}
