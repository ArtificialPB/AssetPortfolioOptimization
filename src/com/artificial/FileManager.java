package com.artificial;

import java.io.File;

public class FileManager {
    public static final String TEMP_FOLDER = System.getProperty("java.io.tmpdir");

    public static File getFile(final String folderName, final String fileName) {
        final File folder = new File(TEMP_FOLDER, folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return new File(folder, fileName);
    }

    public static File getFile(final String fileName) {
        return getFile("PortfolioOptimization", fileName);
    }

}
