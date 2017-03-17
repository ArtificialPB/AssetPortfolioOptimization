package com.artificial;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A utility that downloads a file from a URL.
 *
 * @author www.codejava.net
 */
public class HttpUtil {
    private static final int BUFFER_SIZE = 4096;

    public static String downloadString(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
        final StringWriter outputStream = new StringWriter();
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            final BufferedReader inputStream = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));

            String line;
            while ((line = inputStream.readLine()) != null) {
                outputStream.write(line + System.lineSeparator());
            }
            inputStream.close();
            //Debug.info("String downloaded from " + fileUrl);
        } else {
            httpConn.disconnect();
            throw new IOException("No file to download. Server replied with HTTP code: " + responseCode);
        }
        httpConn.disconnect();
        outputStream.close();
        return outputStream.toString();
    }

    public static void downloadFile(String fileURL, File file) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(file);

            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
            // Debug.info("File downloaded from " + fileURL);
        } else {
            httpConn.disconnect();
            throw new IOException("No file to download. Server replied with HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
}