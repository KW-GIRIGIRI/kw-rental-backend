package com.girigiri.kwrental.common;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

public interface MultiPartFileHandler {

    URL upload(MultipartFile multipartFile) throws IOException;
}
