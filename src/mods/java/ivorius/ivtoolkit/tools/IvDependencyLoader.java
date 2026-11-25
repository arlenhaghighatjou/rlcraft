package ivorius.ivtoolkit.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IvDependencyLoader {
   private String importStart;
   private String importEnd;
   private final Map<String, String> cachedResources = new HashMap();

   public IvDependencyLoader(String importStart, String importEnd) {
      this.importStart = importStart;
      this.importEnd = importEnd;
   }

   public String getImportStart() {
      return this.importStart;
   }

   public void setImportStart(String importStart) {
      this.importStart = importStart;
   }

   public String getImportEnd() {
      return this.importEnd;
   }

   public void setImportEnd(String importEnd) {
      this.importEnd = importEnd;
   }

   public String loadResource(String k) throws IvDependencyLoader.LoadException, IvDependencyLoader.RecursionException {
      return this.loadResource(k, new ArrayList());
   }

   private String loadResource(String k, List<String> loading) throws IvDependencyLoader.LoadException, IvDependencyLoader.RecursionException {
      String res = (String)this.cachedResources.get(k);
      if (res != null) {
         return res;
      } else if (loading.contains(k)) {
         throw new IvDependencyLoader.RecursionException();
      } else {
         loading.add(k);

         int startIndex;
         int endIndex;
         String dep;
         for(res = this.loadResourceRaw(k); (startIndex = res.indexOf(this.importStart)) >= 0 && (endIndex = res.indexOf(this.importEnd)) >= 0; res = res.substring(0, startIndex) + dep + res.substring(endIndex + this.importEnd.length())) {
            dep = this.loadResource(res.substring(startIndex + this.importStart.length(), endIndex), loading);
         }

         this.cachedResources.put(k, res);
         return res;
      }
   }

   protected abstract String loadResourceRaw(String var1) throws IvDependencyLoader.LoadException;

   public static class LoadException extends Exception {
      public LoadException() {
      }

      public LoadException(String message) {
         super(message);
      }

      public LoadException(String message, Throwable cause) {
         super(message, cause);
      }

      public LoadException(Throwable cause) {
         super(cause);
      }

      public LoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
         super(message, cause, enableSuppression, writableStackTrace);
      }
   }

   public static class RecursionException extends Exception {
      public RecursionException() {
      }

      public RecursionException(String message) {
         super(message);
      }

      public RecursionException(String message, Throwable cause) {
         super(message, cause);
      }

      public RecursionException(Throwable cause) {
         super(cause);
      }

      public RecursionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
         super(message, cause, enableSuppression, writableStackTrace);
      }
   }
}
