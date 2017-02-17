package de.mirb.util.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InvocationWrapper {

  public static <T> Builder<T> forClass(Class<T> cls) {
    return new Builder<T>(cls);
  }

  public static class Builder<T> {
    private final Class<T> wrappedClass;
    private ProcessorInvocationHandler handler;
    private ExceptionHandler exceptionHandler;

    public Builder(Class<T> cls) {
      this.wrappedClass = cls;
    }

    public Builder<T> exceptionHandler(ExceptionHandler handler) {
      this.exceptionHandler = handler;
      return this;
    }

    public T wrap(T instance) {
      Class<?> aClass = instance.getClass();
      Class<?>[] interfaces = aClass.getInterfaces();
      if(interfaces.length == 0) {
        interfaces = new Class[]{aClass.getClass()};
      }
      handler = new ProcessorInvocationHandler(instance, exceptionHandler);
      Object proxyInstance = Proxy.newProxyInstance(aClass.getClassLoader(), interfaces, handler);
      return wrappedClass.cast(proxyInstance);
    }
  }

  public static final class ExceptionHandlerResult {
    private boolean rethrow;
    private Exception exception;
    private Object result;

    private ExceptionHandlerResult(boolean rethrow, Exception exception, Object result) {
      this.rethrow = rethrow;
      this.exception = exception;
      this.result = result;
    }

    public boolean isRethrow() {
      return rethrow;
    }

    public Exception getException() {
      return exception;
    }

    public Object getResult() {
      return result;
    }
  }

  public static abstract class ExceptionHandler {
    abstract ExceptionHandlerResult handleException(InvocationTargetException ex);

    public static ExceptionHandlerResult rethrow() {
      return new ExceptionHandlerResult(true, null, null);
    }
    public static ExceptionHandlerResult rethrow(Exception ex) {
      return new ExceptionHandlerResult(true, ex, null);
    }
    public static ExceptionHandlerResult replacedResult(Object obj) {
      return new ExceptionHandlerResult(false, null, obj);
    }
  }

  private static class ProcessorInvocationHandler implements InvocationHandler {
    private final Object wrappedInstance;
    private final ExceptionHandler exceptionHandler;

    public ProcessorInvocationHandler(Object wrappedInstance, ExceptionHandler exceptionHandler) {
      this.wrappedInstance = wrappedInstance;
      this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
      try {
        final Object invokeResult = method.invoke(wrappedInstance, objects);
        return invokeResult;
      } catch (InvocationTargetException exception) {
        if(exceptionHandler == null) {
          throw exception.getTargetException();
        } else {
          ExceptionHandlerResult result = exceptionHandler.handleException(exception);
          if(result.isRethrow()) {
            if(result.getException() == null) {
              throw exception.getTargetException();
            }
            throw result.getException();
          }
          return result.getResult();
        }
      }
    }
  }

}
