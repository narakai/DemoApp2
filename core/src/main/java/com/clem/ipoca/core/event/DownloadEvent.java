package com.clem.ipoca.core.event;

import com.clem.ipoca.core.service.download.Downloader;

import java.util.ArrayList;
import java.util.List;

public class DownloadEvent {

    public final DownloaderUpdate update;

    private DownloadEvent(DownloaderUpdate downloader) {
        this.update = downloader;
    }

    public static DownloadEvent refresh(List<Downloader> list) {
        list = new ArrayList<>(list);
        DownloaderUpdate update = new DownloaderUpdate(list);
        return new DownloadEvent(update);
    }

    @Override
    public String toString() {
        return "DownloadEvent{" +
                "update=" + update +
                '}';
    }
}
