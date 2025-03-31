package com.shitcode.demo1.component;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

@Component
public class MediaDetector {
    public static String detect(InputStream stream)
            throws IOException {
        Tika tika = new Tika();
        String mediaType = tika.detect(stream);
        return mediaType;
    }
}
