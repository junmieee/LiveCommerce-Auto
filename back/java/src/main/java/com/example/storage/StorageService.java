package com.example.storage;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  StoredObject store(MultipartFile file, String keyPrefix) throws IOException;

  StoredObject store(InputStream in, String originalFilename, String contentType, String keyPrefix)
      throws IOException;

  String getPublicUrl(String key);

  void delete(String key) throws IOException;
}
