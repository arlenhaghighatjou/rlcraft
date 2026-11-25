package me.lucko.spark.lib.adventure.examination;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ExaminableProperty {
   private ExaminableProperty() {
   }

   @NonNull
   public abstract String name();

   @NonNull
   public abstract <R> R examine(@NonNull Examiner<? extends R> var1);

   public String toString() {
      return "ExaminableProperty{" + this.name() + "}";
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, @Nullable final Object value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, @Nullable final String value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final boolean value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final boolean[] value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final byte value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final byte[] value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final char value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final char[] value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final double value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final double[] value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final float value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final float[] value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final int value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final int[] value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final long value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final long[] value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final short value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }

   @NonNull
   public static ExaminableProperty of(@NonNull final String name, final short[] value) {
      return new ExaminableProperty() {
         @NonNull
         public String name() {
            return name;
         }

         @NonNull
         public <R> R examine(@NonNull Examiner<? extends R> examiner) {
            return (R)examiner.examine(value);
         }
      };
   }
}
