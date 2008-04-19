package org.garbagecollected.util;

import java.lang.reflect.Method;

public class GetterSetterBuilderSpecification implements BuilderSpecification {
  private final Class<?> spec;
  
  public GetterSetterBuilderSpecification(Class<?> spec) {
    this.spec = spec;
  }
  
  public boolean isWriter(Method method, Object[] args) {
    return (args != null && args.length == 1 
            && method.getName().startsWith("set")
            && spec.isAssignableFrom(method.getReturnType()));
  }

  public boolean isReader(Method method, Object[] args) {
    return args == null
        && method.getName().startsWith("get")
        && !Void.TYPE.isAssignableFrom(method.getReturnType());
  }

  public String readerIdentity(Method reader) {
    return reader.getName().substring(3, reader.getName().length());
  }

  public String writerIdentity(Method writer) {
    return writer.getName().substring(3, writer.getName().length());
  }
}
