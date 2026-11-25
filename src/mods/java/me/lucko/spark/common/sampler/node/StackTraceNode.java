package me.lucko.spark.common.sampler.node;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import me.lucko.spark.common.util.MethodDisambiguator;
import me.lucko.spark.proto.SparkProtos;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class StackTraceNode extends AbstractNode implements Comparable<StackTraceNode> {
   public static final int NULL_LINE_NUMBER = -1;
   private final StackTraceNode.Description description;

   public StackTraceNode(StackTraceNode.Description description) {
      this.description = description;
   }

   public String getClassName() {
      return this.description.className;
   }

   public String getMethodName() {
      return this.description.methodName;
   }

   public String getMethodDescription() {
      return this.description.methodDescription;
   }

   public int getLineNumber() {
      return this.description.lineNumber;
   }

   public int getParentLineNumber() {
      return this.description.parentLineNumber;
   }

   public SparkProtos.StackTraceNode toProto(MergeMode mergeMode) {
      SparkProtos.StackTraceNode.Builder proto = SparkProtos.StackTraceNode.newBuilder().setTime(this.getTotalTime()).setClassName(this.description.className).setMethodName(this.description.methodName);
      if (this.description.lineNumber >= 0) {
         proto.setLineNumber(this.description.lineNumber);
      }

      if (mergeMode.separateParentCalls() && this.description.parentLineNumber >= 0) {
         proto.setParentLineNumber(this.description.parentLineNumber);
      }

      if (this.description.methodDescription != null) {
         proto.setMethodDesc(this.description.methodDescription);
      } else {
         Optional var10000 = mergeMode.getMethodDisambiguator().disambiguate(this).map(MethodDisambiguator.MethodDescription::getDesc);
         Objects.requireNonNull(proto);
         var10000.ifPresent(proto::setMethodDesc);
      }

      for(StackTraceNode child : this.exportChildren(mergeMode)) {
         proto.addChildren(child.toProto(mergeMode));
      }

      return (SparkProtos.StackTraceNode)proto.build();
   }

   public int compareTo(StackTraceNode that) {
      if (this == that) {
         return 0;
      } else {
         int i = -Double.compare(this.getTotalTime(), that.getTotalTime());
         return i != 0 ? i : this.description.compareTo(that.description);
      }
   }

   @FunctionalInterface
   public interface Describer<T> {
      StackTraceNode.Description describe(T var1, @Nullable T var2);
   }

   public static final class Description implements Comparable<StackTraceNode.Description> {
      private final String className;
      private final String methodName;
      private final String methodDescription;
      private final int lineNumber;
      private final int parentLineNumber;
      private final int hash;

      public Description(String className, String methodName, int lineNumber, int parentLineNumber) {
         this.className = className;
         this.methodName = methodName;
         this.methodDescription = null;
         this.lineNumber = lineNumber;
         this.parentLineNumber = parentLineNumber;
         this.hash = Objects.hash(new Object[]{this.className, this.methodName, this.lineNumber, this.parentLineNumber});
      }

      public Description(String className, String methodName, String methodDescription) {
         this.className = className;
         this.methodName = methodName;
         this.methodDescription = methodDescription;
         this.lineNumber = -1;
         this.parentLineNumber = -1;
         this.hash = Objects.hash(new Object[]{this.className, this.methodName, this.methodDescription});
      }

      private static <T extends Comparable<T>> int nullCompare(T a, T b) {
         if (a == null && b == null) {
            return 0;
         } else if (a == null) {
            return -1;
         } else {
            return b == null ? 1 : a.compareTo(b);
         }
      }

      public int compareTo(StackTraceNode.Description that) {
         if (this == that) {
            return 0;
         } else {
            int i = this.className.compareTo(that.className);
            if (i != 0) {
               return i;
            } else {
               i = this.methodName.compareTo(that.methodName);
               if (i != 0) {
                  return i;
               } else {
                  i = nullCompare(this.methodDescription, that.methodDescription);
                  if (i != 0) {
                     return i;
                  } else {
                     if (this.methodDescription != null && that.methodDescription != null) {
                        i = this.methodDescription.compareTo(that.methodDescription);
                        if (i != 0) {
                           return i;
                        }
                     }

                     i = Integer.compare(this.lineNumber, that.lineNumber);
                     return i != 0 ? i : Integer.compare(this.parentLineNumber, that.parentLineNumber);
                  }
               }
            }
         }
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            StackTraceNode.Description description = (StackTraceNode.Description)o;
            return this.hash == description.hash && this.lineNumber == description.lineNumber && this.parentLineNumber == description.parentLineNumber && this.className.equals(description.className) && this.methodName.equals(description.methodName) && Objects.equals(this.methodDescription, description.methodDescription);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.hash;
      }
   }
}
