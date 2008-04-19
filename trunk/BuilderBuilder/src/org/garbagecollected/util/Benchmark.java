package org.garbagecollected.util;

import java.util.Arrays;

import org.garbagecollected.util.Example.ManualBuilder;

/** Comparing performance to regular builders. */
public class Benchmark {
  private static void proxy(int count) {
    long start = System.currentTimeMillis();
    int orig = count;
    while (count-->0) {
      Example
      .builder("Mandatory!")
      .optional1(35)
      .optional2('A')
      .build()
      .toString();
    }
    
    System.out.printf("%10s%,13d%8d%1s%n", "Proxy", orig, 
                      System.currentTimeMillis() - start, "ms");
  }

  private static void noProxy(int count) {
    long start = System.currentTimeMillis();
    int orig = count;
    while (count-->0) {
      new ManualBuilder("Mandatory!")
      .optional1(35)
      .optional2('A')
      .build()
      .toString();
    }

    System.out.printf("%10s%,13d%8d%1s%n", "Manual", orig, 
                      System.currentTimeMillis() - start, "ms");
  }
  
  public static void main(String[] args) {
    for (int n : Arrays.asList(10, 50, 100, 500, 1000)) {
      proxy(n * 1000);
      noProxy(n * 1000);
    }    
  }
}
