package org.garbagecollected.util;

import java.lang.reflect.Method;

/**
 * Use setters for writers, and simple names for readers.
 * @author Robbie Vanbrabant
 */
public class SimpleSetterBuilderSpecification implements BuilderSpecification {
  private final Class<?> spec;
  
  public SimpleSetterBuilderSpecification(Class<?> spec) {
    this.spec = spec;
  }
  
  public boolean isWriter(Method method, Object[] args) {
    return (args != null && args.length == 1 
            && spec.isAssignableFrom(method.getReturnType()));
  }

  public boolean isReader(Method method, Object[] args) {
    return args == null
        && method.getName().startsWith("get")
        && !Void.TYPE.isAssignableFrom(method.getReturnType());
  }

  public String readerIdentity(Method reader) {
    return reader.getName().substring(3, reader.getName().length()).toLowerCase();
  }

  public String writerIdentity(Method writer) {
    return writer.getName();
  }
}
