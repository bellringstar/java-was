package codesquad.webserver.parser;

import codesquad.webserver.httprequest.HttpRequest;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipartParser {
    private static final Logger logger = LoggerFactory.getLogger(MultipartParser.class);
    private static final byte[] CRLF = "\r\n".getBytes();
    private static final byte[] DOUBLE_CRLF = "\r\n\r\n".getBytes();
    private static final String CHARSET = "UTF-8";

    public static void parse(BufferedInputStream in, HttpRequest request) throws IOException {
        String boundary = extractBoundary(request.getHeaders().get("Content-Type"));
        byte[] boundaryBytes = boundary.getBytes(CHARSET);
        byte[] closingBoundaryBytes = (boundary + "--").getBytes(CHARSET);

        Map<String, List<String>> multipartFields = new HashMap<>();
        Map<String, List<HttpRequest.FileItem>> multipartFiles = new HashMap<>();

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        boolean inHeader = false;
        String currentName = null;
        String currentFilename = null;
        ByteArrayOutputStream currentContent = new ByteArrayOutputStream();
        Map<String, String> currentHeaders = new HashMap<>();

        int b;
        while ((b = in.read()) != -1) {
            buffer.write(b);

            if (endsWith(buffer, closingBoundaryBytes)) {
                if (currentName != null) {
                    savePart(currentName, currentFilename, currentContent, currentHeaders, multipartFields,
                            multipartFiles);
                }
                break;
            }

            if (endsWith(buffer, boundaryBytes)) {
                if (currentName != null) {
                    savePart(currentName, currentFilename, currentContent, currentHeaders, multipartFields,
                            multipartFiles);
                }
                buffer.reset();
                inHeader = true;
                currentName = null;
                currentFilename = null;
                currentContent.reset();
                currentHeaders.clear();
                continue;
            }

            if (inHeader && endsWith(buffer, DOUBLE_CRLF)) {
                String header = buffer.toString();
                parseHeaders(header, currentHeaders);
                currentName = extractAttribute(currentHeaders.get("Content-Disposition"), "name");
                currentFilename = extractAttribute(currentHeaders.get("Content-Disposition"), "filename");
                logger.debug("Found part: name={}, filename={}", currentName, currentFilename);
                buffer.reset();
                inHeader = false;
                continue;
            }

            if (!inHeader) {
                currentContent.write(b);
            }
        }

        request.setMultipartFields(multipartFields);
        request.setMultipartFiles(multipartFiles);
        logger.debug("Finished parsing multipart data. Fields: {}, Files: {}", multipartFields.keySet(),
                multipartFiles.keySet());
    }

    private static boolean endsWith(ByteArrayOutputStream buffer, byte[] suffix) {
        byte[] bufferArray = buffer.toByteArray();
        if (bufferArray.length < suffix.length) {
            return false;
        }
        for (int i = 0; i < suffix.length; i++) {
            if (bufferArray[bufferArray.length - suffix.length + i] != suffix[i]) {
                return false;
            }
        }
        return true;
    }

    private static void savePart(String name, String filename, ByteArrayOutputStream content,
                                 Map<String, String> headers, Map<String, List<String>> fields,
                                 Map<String, List<HttpRequest.FileItem>> files) throws UnsupportedEncodingException {
        if (name == null) {
            logger.warn("Attempting to save part with null name, skipping");
            return;
        }

        byte[] contentBytes = content.toByteArray();
        int contentLength = contentBytes.length;
        if (contentLength > CRLF.length) {
            contentLength -= CRLF.length;
        }

        if (filename != null) {
            files.computeIfAbsent(name, k -> new ArrayList<>()).add(
                    new HttpRequest.FileItem(filename, java.util.Arrays.copyOf(contentBytes, contentLength))
            );
            logger.debug("Saved file: name={}, filename={}, size={} bytes", name, filename, contentLength);
        } else {
            String value = new String(contentBytes, 0, contentLength, CHARSET).trim();
            fields.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
            logger.debug("Saved field: name={}, value={}", name, value);
        }
    }

    private static String extractBoundary(List<String> contentTypes) {
        if (contentTypes == null || contentTypes.isEmpty()) {
            throw new IllegalArgumentException("Content-Type header not found");
        }
        String contentType = contentTypes.get(0);
        String[] parts = contentType.split(";");
        for (String part : parts) {
            if (part.trim().startsWith("boundary=")) {
                return part.split("=", 2)[1].trim().replaceAll("^\"|\"$", "");
            }
        }
        throw new IllegalArgumentException("Boundary not found in Content-Type");
    }

    private static void parseHeaders(String headerContent, Map<String, String> headers) {
        String[] lines = headerContent.split("\r\n");
        for (String line : lines) {
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }
    }

    private static String extractAttribute(String header, String attribute) {
        if (header == null) {
            return null;
        }
        int start = header.indexOf(attribute + "=\"");
        if (start != -1) {
            int end = header.indexOf("\"", start + attribute.length() + 2);
            if (end != -1) {
                return header.substring(start + attribute.length() + 2, end);
            }
        }
        return null;
    }
}