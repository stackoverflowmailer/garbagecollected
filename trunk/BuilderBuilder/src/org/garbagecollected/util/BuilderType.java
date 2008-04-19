package org.garbagecollected.util;

public enum BuilderType {
  /** Getters and Setters. */
  GETTER_SETTER, 
  
  /** Expects that build "reader" and "writer" methods have the same name. */
  SIMPLE, 
  
  /** Getters, but simple setters (e.g. getName() and name("John")). */
  SIMPLE_SETTER
}
