package com.example.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(value = "storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

  private final Path rootDir;
  private final String publicBase;

  public LocalStorageService(
      @Value("${storage.local-dir:/data/uploads}") String localDir,
      @Value("${storage.public-base:http://localhost:8081/media}") String publicBase)
      throws IOException {
    this.rootDir = Paths.get(localDir).toAbsolutePath().normalize();
    this.publicBase =
        publicBase.endsWith("/") ? publicBase.substring(0, publicBase.length() - 1) : publicBase;
    Files.createDirectories(this.rootDir);
  }

  @Override
  public StoredObject store(MultipartFile file, String keyPrefix) throws IOException {
    String original = file.getOriginalFilename();
    String contentType = file.getContentType();
    return store(file.getInputStream(), original, contentType, keyPrefix);
  }

  @Override
  public StoredObject store(
      InputStream in, String originalFilename, String contentType, String keyPrefix)
      throws IOException {
    String ext = extname(originalFilename);
    String key = buildKey(keyPrefix, ext);
    Path target = rootDir.resolve(key).normalize();
    Files.createDirectories(target.getParent());
    long written = Files.copy(in, target);
    return new StoredObject(key, publicBase + "/" + key.replace('\\', '/'), written, contentType);
  }

  @Override
  public String getPublicUrl(String key) {
    String clean = key.startsWith("/") ? key.substring(1) : key;
    return publicBase + "/" + clean;
  }

  @Override
  public void delete(String key) throws IOException {
    Path p = rootDir.resolve(key).normalize();
    Files.deleteIfExists(p);
  }

  private static String buildKey(String keyPrefix, String ext) {
    LocalDate d = LocalDate.now();
    String datePath =
        String.format("%04d/%02d/%02d", d.getYear(), d.getMonthValue(), d.getDayOfMonth());
    String base = UUID.randomUUID().toString().replaceAll("-", "");
    String safePrefix =
        (keyPrefix == null || keyPrefix.isBlank()) ? "uploads" : sanitize(keyPrefix);
    String filename = base + (ext.isEmpty() ? "" : ("." + ext));
    return safePrefix + "/" + datePath + "/" + filename;
  }

  private static String sanitize(String s) {
    String out = s.replaceAll("[^a-zA-Z0-9/_-]", "").replaceAll("/{2,}", "/");
    if (out.startsWith("/")) out = out.substring(1);
    if (out.isBlank()) return "uploads";
    return out;
  }

  private static String extname(String filename) {
    if (!StringUtils.hasText(filename)) return "";
    int dot = filename.lastIndexOf('.');
    if (dot < 0 || dot == filename.length() - 1) return "";
    return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
  }
}
