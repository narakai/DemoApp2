package com.clem.ipocadonation1.asynctask;

import android.support.annotation.NonNull;
import android.util.Log;

import com.clem.ipocadonation1.core.export.ExportWriter;
import com.clem.ipocadonation1.core.preferences.UserPreferences;
import com.clem.ipocadonation1.core.storage.DBReader;
import com.clem.ipocadonation1.core.util.LangUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import rx.Observable;

/**
 * Writes an OPML file into the export directory in the background.
 */
public class ExportWorker {

    private static final String EXPORT_DIR = "export/";
    private static final String TAG = "ExportWorker";
    private static final String DEFAULT_OUTPUT_NAME = "antennapod-feeds";

    private ExportWriter exportWriter;
    private File output;

    public ExportWorker(ExportWriter exportWriter) {
        this(exportWriter, new File(UserPreferences.getDataFolder(EXPORT_DIR),
                DEFAULT_OUTPUT_NAME + "." + exportWriter.fileExtension()));
    }

    public ExportWorker(ExportWriter exportWriter, @NonNull File output) {
        this.exportWriter = exportWriter;
        this.output = output;
    }

    public Observable<File> exportObservable() {
        if (output.exists()) {
            Log.w(TAG, "Overwriting previously exported file.");
            output.delete();
        }
        return Observable.create(subscriber -> {
            OutputStreamWriter writer = null;
            try {
                writer = new OutputStreamWriter(new FileOutputStream(output), LangUtils.UTF_8);
                exportWriter.writeDocument(DBReader.getFeedList(), writer);
                subscriber.onNext(output);
            } catch (IOException e) {
                subscriber.onError(e);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                }
                subscriber.onCompleted();
            }
        });
    }

}
