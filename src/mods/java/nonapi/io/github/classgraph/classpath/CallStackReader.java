package nonapi.io.github.classgraph.classpath;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.VersionFinder;

class CallStackReader {
   private CallStackReader() {
   }

   private static Class<?>[] getCallStackViaStackWalker() {
      try {
         Class<?> consumerClass = Class.forName("java.util.function.Consumer");
         final List<Class<?>> stackFrameClasses = new ArrayList();
         Class<?> stackWalkerOptionClass = Class.forName("java.lang.StackWalker$Option");
         Object retainClassReference = Class.forName("java.lang.Enum").getMethod("valueOf", Class.class, String.class).invoke((Object)null, stackWalkerOptionClass, "RETAIN_CLASS_REFERENCE");
         Class<?> stackWalkerClass = Class.forName("java.lang.StackWalker");
         Object stackWalkerInstance = stackWalkerClass.getMethod("getInstance", stackWalkerOptionClass).invoke((Object)null, retainClassReference);
         final Method stackFrameGetDeclaringClassMethod = Class.forName("java.lang.StackWalker$StackFrame").getMethod("getDeclaringClass");
         stackWalkerClass.getMethod("forEach", consumerClass).invoke(stackWalkerInstance, Proxy.newProxyInstance(consumerClass.getClassLoader(), new Class[]{consumerClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
               Class<?> declaringClass = (Class)stackFrameGetDeclaringClassMethod.invoke(args[0]);
               stackFrameClasses.add(declaringClass);
               return null;
            }
         }));
         return (Class[])stackFrameClasses.toArray(new Class[0]);
      } catch (LinkageError | Exception var7) {
         return null;
      }
   }

   private static Class<?>[] getCallStackViaSecurityManager(LogNode log) {
      try {
         return (new CallStackReader.CallerResolver()).getClassContext();
      } catch (SecurityException var2) {
         if (log != null) {
            log.log("Exception while trying to obtain call stack via SecurityManager", var2);
         }

         return null;
      }
   }

   static Class<?>[] getClassContext(final LogNode log) {
      Class<?>[] stackClasses = null;
      if (VersionFinder.JAVA_MAJOR_VERSION == 11 && (VersionFinder.JAVA_MINOR_VERSION >= 1 || VersionFinder.JAVA_SUB_VERSION >= 4) && !VersionFinder.JAVA_IS_EA_VERSION || VersionFinder.JAVA_MAJOR_VERSION == 12 && (VersionFinder.JAVA_MINOR_VERSION >= 1 || VersionFinder.JAVA_SUB_VERSION >= 2) && !VersionFinder.JAVA_IS_EA_VERSION || VersionFinder.JAVA_MAJOR_VERSION == 13 && !VersionFinder.JAVA_IS_EA_VERSION || VersionFinder.JAVA_MAJOR_VERSION > 13) {
         stackClasses = (Class[])AccessController.doPrivileged(new PrivilegedAction<Class<?>[]>() {
            public Class<?>[] run() {
               return CallStackReader.getCallStackViaStackWalker();
            }
         });
      }

      if (stackClasses == null || stackClasses.length == 0) {
         stackClasses = (Class[])AccessController.doPrivileged(new PrivilegedAction<Class<?>[]>() {
            public Class<?>[] run() {
               return CallStackReader.getCallStackViaSecurityManager(log);
            }
         });
      }

      if (stackClasses == null || stackClasses.length == 0) {
         StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
         if (stackTrace == null || stackTrace.length == 0) {
            try {
               throw new Exception();
            } catch (Exception var10) {
               stackTrace = var10.getStackTrace();
            }
         }

         List<Class<?>> stackClassesList = new ArrayList();

         for(StackTraceElement elt : stackTrace) {
            try {
               stackClassesList.add(Class.forName(elt.getClassName()));
            } catch (LinkageError | ClassNotFoundException var9) {
               ;
            }
         }

         if (!stackClassesList.isEmpty()) {
            stackClasses = (Class[])stackClassesList.toArray(new Class[0]);
         } else {
            stackClasses = new Class[]{CallStackReader.class};
         }
      }

      return stackClasses;
   }

   private static final class CallerResolver extends SecurityManager {
      private CallerResolver() {
      }

      protected Class<?>[] getClassContext() {
         return super.getClassContext();
      }
   }
}
