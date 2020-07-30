package com.github.sadikovi;

import java.util.Random;

public class CircularSuffixArray {
  private final int len;
  private final int[] index;
  private static final Random rand = new Random();

  /** Creates a circular suffix array of string "s" */
  public CircularSuffixArray(String s) {
    if (s == null) throw new IllegalArgumentException("Input string is null");
    if (s.length() == 0) throw new IllegalArgumentException("Empty string");

    this.len = s.length();
    this.index = new int[len];

    for (int i = 0; i < len; i++) {
      this.index[i] = i;
    }

    // Use three-way quicksort to sort suffixes efficiently.
    shuffle(this.index);
    sort(this.index, 0, len - 1, s, len);

    // System.out.println("Original suffixes");
    // for (int i = 0; i < n; i++) {
    //   char[] r = rotate(s, i);
    //   System.out.println(i + ":\t" + java.util.Arrays.toString(r));
    // }
    // System.out.println("Sorted suffixes");
    // for (int i = 0; i < n; i++) {
    //   char[] r = rotate(s, index[i]);
    //   System.out.println(i + ":\t" + java.util.Arrays.toString(r));
    // }
    // System.out.println(java.util.Arrays.toString(index));
    // for (int i = 0; i < n; i++) {
    //   char[] r = rotate(s, index[i]);
    //   System.out.print(r[n - 1]);
    // }
    // System.out.println();
  }

  // Rotates the string at index i
  private char[] rotate(String s, int i) {
    char[] arr = new char[s.length()];
    int j = 0;

    while (j < s.length() - i) {
      arr[j] = s.charAt(i + j);
      j++;
    }

    while (j < s.length()) {
      arr[j] = s.charAt(i + j - s.length());
      j++;
    }

    return arr;
  }

  private static void shuffle(int[] a) {
    int n = a.length;
    for (int i = 0; i < n; i++) {
      int r = i + rand.nextInt(n - i); // between i and n-1
      int temp = a[i];
      a[i] = a[r];
      a[r] = temp;
    }
  }

  private static void sort(int[] arr, int start, int end, String s, int len) {
    if (start >= end) return;

    int lt = start, gt = end;
    int v = arr[lt];
    int i = lt + 1;

    while (i <= gt) {
      int cmp = compare(s, len, arr[i], v);
      if (cmp < 0) exchange(arr, lt++, i++);
      else if (cmp > 0) exchange(arr, i, gt--);
      else i++;
    }

    sort(arr, start, lt - 1, s, len);
    sort(arr, gt + 1, end, s, len);
  }

  private static int compare(String s, int len, int a, int b) {
    for (int i = 0; i < len; i++) {
      char ca = s.charAt((a + i) % len);
      char cb = s.charAt((b + i) % len);
      if (ca < cb) return -1;
      if (ca > cb) return 1;
    }
    return 0;
  }

  private static void exchange(int[] arr, int a, int b) {
    int tmp = arr[a];
    arr[a] = arr[b];
    arr[b] = tmp;
  }

  /** Returns the length of the input string */
  public int length() {
    return len;
  }

  /** Returns index of ith sorted suffix */
  public int index(int i) {
    if (i < 0 || i >= len) throw new IllegalArgumentException("Invalid index " + i);
    return index[i];
  }
}
