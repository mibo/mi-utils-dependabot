package de.mirb.util.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InvocationWrapper {

  public static <T> Builder<T> forInterface(Class<T> cls) {
    return new Builder<T>(cls);
  }

  public static class Builder<T> {
    private final Class<T> wrappedClass;
    private WrapperInvocationHandler handler;
    private ExceptionHandler exceptionHandler;
    private InterceptionHandler interceptionHandler;

    public Builder(Class<T> cls) {
      this.wrappedClass = cls;
    }

    public Builder<T> exceptionHandler(ExceptionHandler handler) {
      this.exceptionHandler = handler;
      return this;
    }

    public Builder<T> interceptionHandler(InterceptionHandler handler) {
      this.interceptionHandler = handler;
      return this;
    }

    public T wrap(T instance) {
      Class<?> aClass = instance.getClass();
      Class<?>[] interfaces = aClass.getInterfaces();
      if(interfaces.length == 0) {
        interfaces = new Class[]{aClass.getClass()};
      }
      handler = new WrapperInvocationHandler(instance, exceptionHandler, interceptionHandler);
      Object proxyInstance = Proxy.newProxyInstance(aClass.getClassLoader(), interfaces, handler);
      return wrappedClass.cast(proxyInstance);
    }
  }

  public final static class ExceptionContext {
    private final Throwable ex;

    public ExceptionContext(Throwable ex) {
      this.ex = ex;
    }

    public Throwable getException() {
      return ex;
    }

    public ExceptionHandlerResult rethrow() {
      return new ExceptionHandlerResult(true, null, null);
    }
    public ExceptionHandlerResult rethrow(Exception ex) {
      return new ExceptionHandlerResult(true, ex, null);
    }
    public ExceptionHandlerResult returnResult(Object obj) {
      return new ExceptionHandlerResult(false, null, obj);
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

  interface ExceptionHandler {
    ExceptionHandlerResult handleException(ExceptionContext context);
  }

  interface InterceptionHandler {
    Object handleInterception(InterceptionHandlerContext context) throws Exception;
  }

  public static final class InterceptionHandlerContext {
    Object instance;
    Method method;
    Object[] parameters;

    public InterceptionHandlerContext(Object instance, Method method, Object[] parameters) {
      this.instance = instance;
      this.method = method;
      this.parameters = parameters;
    }

    public Object getInstance() {
      return instance;
    }

    public Method getMethod() {
      return method;
    }

    public Object[] getParameters() {
      return parameters;
    }

    public InterceptionHandlerContext setParameters(Object[] parameters) {
      this.parameters = parameters;
      return this;
    }

    public Object proceed() throws Exception {
      return method.invoke(instance, parameters);
    }
  }

  private static class WrapperInvocationHandler implements InvocationHandler {
    private final Object wrappedInstance;
    private final ExceptionHandler exceptionHandler;
    private final InterceptionHandler interceptionHandler;

    public WrapperInvocationHandler(Object wrappedInstance, ExceptionHandler exceptionHandler, InterceptionHandler interceptionHandler) {
      this.wrappedInstance = wrappedInstance;
      this.exceptionHandler = exceptionHandler;
      this.interceptionHandler = interceptionHandler;
    }

    @Override
    public Object invoke(Object proxyInstance, Method method, Object[] parameters) throws Throwable {
      try {
        if(interceptionHandler == null) {
          return method.invoke(wrappedInstance, parameters);
        } else {
          InterceptionHandlerContext context = new InterceptionHandlerContext(wrappedInstance, method, parameters);
          return interceptionHandler.handleInterception(context);
        }
      } catch (InvocationTargetException exception) {
        if(exceptionHandler == null) {
          throw exception.getTargetException();
        } else {
          ExceptionHandlerResult result = exceptionHandler.handleException(new ExceptionContext(exception.getTargetException()));
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
