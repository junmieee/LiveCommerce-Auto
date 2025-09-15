package com.example.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoredObject {
  private String key; // relative key within provider
  private String url; // public URL to access
  private long size;
  private String contentType;
}
