package org.garbagecollected.guice;

import junit.framework.Assert;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.binder.AnnotatedBindingBuilder;

public class ImmediateModuleTest {
  @Test
  public void useDoubleBracketSyntaxToCreate() {
    new ImmediateModule() {{ }};
  }
  
  @Test(expected=IllegalStateException.class)
  public void useDoubleBracketSyntaxWithoutBindings() {
    new ImmediateModule(){{
    }}.configure(null);
  }
  
  @Test
  public void replayLinkedBindingUsingImmediateModule() throws Exception {
    Module m = new ImmediateModule() {{
      bind(Interface.class).to(Clazz.class);
      bind(Interface2.class).to(Clazz2.class);
    }};
    Injector injector = Guice.createInjector(m);
    Interface instance = injector.getInstance(Interface.class);
    Assert.assertNotNull(instance);
    
    Interface2 instance2 = injector.getInstance(Interface2.class);
    Assert.assertNotNull(instance2);
  }
  
  @Test // make sure that methods on java.lang.Object somewhat work
  public void objectMethodsDontGoCrazy() throws Exception {
    Module m = new ImmediateModule() {{
      AnnotatedBindingBuilder<Clazz> builder = bind(Clazz.class);
      
      // These don't crash (but are not very useful)
      builder.equals(null);
      builder.toString().endsWith(" ");
      builder.hashCode();
      builder.getClass().getName();
      
      // The ones below just crash, which is acceptable
      try { builder.notify(); Assert.fail(); } catch(Exception e) {}
      try { builder.notifyAll(); Assert.fail(); } catch(Exception e) {}
      try { builder.wait(); Assert.fail(); } catch(Exception e) {}
      try { builder.wait(1); Assert.fail(); } catch(Exception e) {}
      try { builder.wait(1,2); Assert.fail(); } catch(Exception e) {}
    }};
    Guice.createInjector(m);
  }
  
  interface Interface {}
  static class Clazz implements Interface {}
  
  interface Interface2 {}
  static class Clazz2 implements Interface2 {}
}
