package com.github.sadikovi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/** Only supports ASCII characters for now */
public class BurrowsWheelerRleDecoder {
  public static final int MIN_LEN = BurrowsWheelerRleEncoder.MIN_LEN;
  public static final int MAX_LEN = BurrowsWheelerRleEncoder.MAX_LEN;

  BurrowsWheelerRleDecoder() {
  }

  private static class Index {
    List<Integer> indices = new ArrayList<Integer>();
  }

  public byte[] decode(InputStream in) throws IOException {
    int first = readInt(in);
    int len = readInt(in);

    if (len < MIN_LEN && len > MAX_LEN) {
      throw new IllegalArgumentException("Invalid length " + len);
    }
    if (first < 0 || first >= len) {
      throw new IllegalArgumentException("Invalid index " + first);
    }

    byte[] value = new byte[len];

    rleDecode(len, value, in);

    Index[] r = new Index[256]; // ASCII range

    for (int i = 0; i < len; i++) {
      if (r[value[i]] == null) {
        r[value[i]] = new Index();
      }
      r[value[i]].indices.add(i);
    }

    byte[] sorted = new byte[len];
    int k = 0;
    for (int i = 0; i < r.length; i++) {
      int size = r[i] == null ? 0 : r[i].indices.size();
      while (size > 0) {
        sorted[k++] = (byte) i;
        size--;
      }
    }

    int[] next = new int[len];
    int kn = 0;
    for (int i = 0; i < r.length; i++) {
      int size = r[i] == null ? 0 : r[i].indices.size();
      for (int j = 0; j < size; j++) {
        next[kn++] = r[i].indices.get(j);
      }
    }

    int tmp = first, iter = 0;
    while (iter < len) {
      value[iter] = sorted[tmp];
      tmp = next[tmp];
      iter++;
    }

    return value;
  }

  /** Reads integer as 32-bit little endian value */
  private int readInt(InputStream in) throws IOException {
    return in.read() & 0xff |
      (in.read() & 0xff) << 8 |
      (in.read() & 0xff) << 16 |
      (in.read() & 0xff) << 24;
  }

  /** RLE decoding, see rleEncode method for more information on the format */
  private void rleDecode(int len, byte[] value, InputStream in) throws IOException {
    int i = 0;
    while (i < len) {
      int b = in.read();
      if ((b & 0x80) == 0) {
        value[i++] = (byte) b;
      } else {
        int v = in.read();
        int cnt = b & 0x7F;
        while (cnt > 0) {
          value[i++] = (byte) v;
          cnt--;
        }
      }
    }
  }
}
