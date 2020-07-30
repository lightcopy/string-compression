package com.github.sadikovi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BurrowsWheelerRleEncoder {
  // cannot have more than MAX_LEN characters in the string
  private static final int MIN_LEN = 10;
  private static final int MAX_LEN = 200000;

  private final int[] index;
  private final Random rand;

  BurrowsWheelerRleEncoder() {
    this.index = new int[MAX_LEN];
    this.rand = new Random();
  }

  /** Returns true if we can process the string of length "len" */
  public boolean canProcess(int len) {
    return len >= MIN_LEN && len <= MAX_LEN;
  }

  public void encode(byte[] value, OutputStream out) throws IOException {
    int len = value.length;

    if (len < MIN_LEN) {
      throw new IllegalArgumentException("Length " + len + " < MIN_LEN (" + MIN_LEN + ")");
    }
    if (len > MAX_LEN) {
      throw new IllegalArgumentException("Length " + len + " > MAX_LEN (" + MAX_LEN + ")");
    }

    for (int i = 0; i < len; i++) {
      index[i] = i;
    }

    shuffle(index, len);
    sort(index, 0, len - 1, value, len);

    int idx = 0;
    while (idx < len && index[idx] != 0) idx++;
    writeInt(out, idx);

    writeInt(out, len);

    rleEncode(index, len, value, out);
  }

  private static class Index {
    List<Integer> indices = new ArrayList<Integer>();
  }

  public byte[] decode(InputStream in) throws IOException {
    int first = readInt(in);
    int len = readInt(in);

    if (len < MIN_LEN) {
      throw new IllegalArgumentException("Length " + len + " < MIN_LEN (" + MIN_LEN + ")");
    }
    if (len > MAX_LEN) {
      throw new IllegalArgumentException("Length " + len + " > MAX_LEN (" + MAX_LEN + ")");
    }
    if (first < 0 || first >= len) throw new IllegalArgumentException("Invalid index " + first);

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

  private void shuffle(int[] a, int n) {
    for (int i = 0; i < n; i++) {
      int r = i + rand.nextInt(n - i); // between i and n-1
      int temp = a[i];
      a[i] = a[r];
      a[r] = temp;
    }
  }

  private void sort(int[] arr, int start, int end, byte[] value, int len) {
    if (start >= end) return;

    int lt = start, gt = end;
    int v = arr[lt];
    int i = lt + 1;

    while (i <= gt) {
      int cmp = compare(value, len, arr[i], v);
      if (cmp < 0) exchange(arr, lt++, i++);
      else if (cmp > 0) exchange(arr, i, gt--);
      else i++;
    }

    sort(arr, start, lt - 1, value, len);
    sort(arr, gt + 1, end, value, len);
  }

  private int compare(byte[] value, int len, int a, int b) {
    for (int i = 0; i < len; i++) {
      byte ca = value[(a + i) % len];
      byte cb = value[(b + i) % len];
      if (ca < cb) return -1;
      if (ca > cb) return 1;
    }
    return 0;
  }

  private void exchange(int[] arr, int a, int b) {
    int tmp = arr[a];
    arr[a] = arr[b];
    arr[b] = tmp;
  }

  /** Writes integer as 32-bit little endian value */
  private void writeInt(OutputStream out, int value) throws IOException {
    out.write(0xff & value);
    out.write(0xff & (value >> 8));
    out.write(0xff & (value >> 16));
    out.write(0xff & (value >> 24));
  }

  /** Reads integer as 32-bit little endian value */
  private int readInt(InputStream in) throws IOException {
    return in.read() & 0xff |
      (in.read() & 0xff) << 8 |
      (in.read() & 0xff) << 16 |
      (in.read() & 0xff) << 24;
  }

  /**
   * RLE encoding, only supports ASCII characters.
   * Format:
   * - [char byte]
   * - [length byte with MSB set to 1][char byte]
   */
  private void rleEncode(int[] index, int len, byte[] value, OutputStream out) throws IOException {
    byte curr = -1, cnt = 0;

    for (int i = 0; i < len; i++) {
      int ni = (index[i] + len - 1) % len; // new index for byte value
      byte v = value[ni];

      if (v < 0) throw new IllegalArgumentException("Cannot process byte value " + v);

      if (cnt < 127 && curr == v) {
        cnt++;
      } else {
        // flush the value and the count
        switch (cnt) {
          case 0: break;
          case 1: out.write(curr); break;
          case 2: out.write(curr); out.write(curr); break;
          // case 3: out.write(curr); out.write(curr); out.write(curr); break;
          default: out.write(0x80 | cnt); out.write(curr); break;
        }
        curr = v;
        cnt = 1;
      }
    }

    switch (cnt) {
      case 0: break;
      case 1: out.write(curr); break;
      case 2: out.write(curr); out.write(curr); break;
      // case 3: out.write(curr); out.write(curr); out.write(curr); break;
      default: out.write(0x80 | cnt); out.write(curr); break;
    }
  }

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
