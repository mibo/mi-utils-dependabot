package de.mirb.util.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InvocationWrapperTest {

  @Test
  public void testSample() {
    final SampleClass instance = new SampleClass();
    SampleInterface wrapped = InvocationWrapper.forInterface(SampleInterface.class).wrap(instance);

    String hello = wrapped.sayHello();
    assertEquals("Hello", hello);
  }

  @Test
  public void testSampleWithParameter() {
    final SampleClass instance = new SampleClass();
    SampleInterface wrapped = InvocationWrapper.forInterface(SampleInterface.class).wrap(instance);

    String hello = wrapped.sayHello("Mibo");
    assertEquals("Hello Mibo", hello);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testException() {
    final SampleClass instance = new SampleClass();
    SampleInterface wrapped = InvocationWrapper.forInterface(SampleInterface.class)
        .wrap(instance);

    String hello = wrapped.sayHello("illegal");
  }

  @Test
  public void testExceptionHandlerUnused() {
    final SampleClass instance = new SampleClass();
    InvocationWrapper.ExceptionHandler handler = new InvocationWrapper.ExceptionHandler() {
      @Override
      public InvocationWrapper.ExceptionHandlerResult handleException(InvocationWrapper.ExceptionContext context) {
        return context.rethrow();
      }
    };
    SampleInterface wrapped = InvocationWrapper.forInterface(SampleInterface.class)
        .exceptionHandler(handler)
        .wrap(instance);

    String hello = wrapped.sayHello("Mibo");
    assertEquals("Hello Mibo", hello);
  }

  @Test
  public void testExceptionHandlerUsed() {
    final SampleClass instance = new SampleClass();
    InvocationWrapper.ExceptionHandler handler = new InvocationWrapper.ExceptionHandler() {
      @Override
      public InvocationWrapper.ExceptionHandlerResult handleException(InvocationWrapper.ExceptionContext context) {
        if (context.getException() instanceof IllegalArgumentException) {
          return context.returnResult("All fine with name");
        }
        return context.rethrow();
      }
    };
    SampleInterface wrapped = InvocationWrapper.forInterface(SampleInterface.class)
        .exceptionHandler(handler)
        .wrap(instance);

    String hello = wrapped.sayHello("illegal");
    assertEquals("All fine with name", hello);

    try {
      String npe = wrapped.sayHello("npe");
      fail("Expected a NPE");
    } catch (NullPointerException e) {
      // expected
    }
  }

  @Test
  public void interceptionHandler() {
    final SampleClass instance = new SampleClass();
    InvocationWrapper.InterceptionHandler handler = new InvocationWrapper.InterceptionHandler() {
      @Override
      public Object handleInterception(InvocationWrapper.InterceptionHandlerContext context) throws Exception {
        Object result = context.proceed();
        if("Hello Mibo".equals(result.toString())) {
          return "Hello Michael";
        }
        return result;
      }
    };
    SampleInterface wrapped = InvocationWrapper.forInterface(SampleInterface.class)
        .interceptionHandler(handler)
        .wrap(instance);

    String helloMichael = wrapped.sayHello("Mibo");
    assertEquals("Hello Michael", helloMichael);

    String helloDoris = wrapped.sayHello("Doris");
    assertEquals("Hello Doris", helloDoris);
  }

  interface SampleInterface {
    String sayHello();
    String sayHello(String name);
  }

    public final class SampleClass implements SampleInterface {
    public String sayHello() {
      return "Hello";
    }

    public String sayHello(String name) {
      if ("illegal".equals(name)) {
        throw new IllegalArgumentException("This name is not valid.");
      }
      if("npe".equals(name)) {
        throw new NullPointerException("npe");
      }
      return "Hello " + name;
    }
  }

}