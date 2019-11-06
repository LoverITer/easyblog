package org.easyblog.config;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class BASE64DecodeMultipartFile implements MultipartFile {

    private final byte[]  imageContent;
    private final String header;

    public BASE64DecodeMultipartFile(byte[] imageContent, String header) {
        this.imageContent = imageContent;
        this.header = header.split("；")[0];
    }

    @Override
    public String getName() {
        return System.currentTimeMillis() + Math.random() + "." + header.split("/")[1];
    }

    @Override
    public String getOriginalFilename() {
        return System.currentTimeMillis() + (int)Math.random() * 10000 + "." + header.split("/")[1];
    }

    @Override
    public String getContentType() {
        return header.split(":")[1];
    }

    @Override
    public boolean isEmpty() {
        return imageContent == null || imageContent.length == 0;
    }

    @Override
    public long getSize() {
        return imageContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return imageContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(imageContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(imageContent);
    }
}
