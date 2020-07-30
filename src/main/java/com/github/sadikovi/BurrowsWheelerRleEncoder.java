package com.github.sadikovi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/** Only supports ASCII characters for now */
public class BurrowsWheelerRleEncoder {
  // cannot have more than MAX_LEN characters in the string
  public static final int MIN_LEN = 10;
  public static final int MAX_LEN = 1000000;

  private final int[] index;
  private final Random rand;

  BurrowsWheelerRleEncoder() {
    this.rand = new Random();
    this.index = new int[MAX_LEN]; // reused in encoding
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

  /** Shuffles array for [0, n), n is the length of the slice to shuffle */
  private void shuffle(int[] a, int n) {
    for (int i = 0; i < n; i++) {
      int r = i + rand.nextInt(n - i); // between i and n-1
      int temp = a[i];
      a[i] = a[r];
      a[r] = temp;
    }
  }

  /**
   * 3-way quicksort augmented for Burrows-Wheeler.
   * I found that 3-way string quicksort does not quite work here for long strings,
   * and this algorithm is good enough.
   */
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

  /**
   * Comparison function.
   * Here we compre rotations for Burrows-Wheeler.
   * I am not sure if there is more efficient implementation.
   */
  private int compare(byte[] value, int len, int a, int b) {
    for (int i = 0; i < len; i++) {
      byte ca = value[(a + i) % len];
      byte cb = value[(b + i) % len];
      if (ca < cb) return -1;
      if (ca > cb) return 1;
    }
    return 0;
  }

  /** Swaps arr[a] and arr[b] */
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
}
