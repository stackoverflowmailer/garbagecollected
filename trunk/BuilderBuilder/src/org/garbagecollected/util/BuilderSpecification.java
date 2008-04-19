package org.garbagecollected.util;

import java.lang.reflect.Method;

public interface BuilderSpecification {
  boolean isReader(Method method, Object[] args);
  boolean isWriter(Method method, Object[] args);
}
