package net.silentchaos512.lib.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.silentchaos512.lib.util.LogHelper;

public class ConfigLineParser {
   private static Map<Class<?>, Function<String, ?>> parsers = new HashMap();

   private ConfigLineParser() {
      throw new IllegalAccessError("Utility class");
   }

   public static <T, U> List<ConfigLineParser.Tuple2<T, U>> parse(String[] lines, String delimiter, String configId, LogHelper log) {
      List<ConfigLineParser.Tuple2<T, U>> list = new ArrayList();

      for(String line : lines) {
         String[] elements = line.split(delimiter);
         if (elements.length < 2) {
            log.warn("ConfigLineParser: Too few elements in line \"{}\". Ignoring entire line. Fix your config file!");
         } else if (elements.length > 2) {
            log.warn("ConfigLineParser: Too many elements in line \"{}\". Ignoring extra values. Fix your config file!");
         }
      }

      return list;
   }

   @Nullable
   private static Integer parseInt(String str) {
      try {
         return Integer.parseInt(str);
      } catch (NumberFormatException var2) {
         return null;
      }
   }

   @Nullable
   private static Float parseFloat(String str) {
      try {
         return Float.parseFloat(str);
      } catch (NumberFormatException var2) {
         return null;
      }
   }

   static {
      parsers.put(Integer.class, ConfigLineParser::parseInt);
      parsers.put(Float.class, ConfigLineParser::parseFloat);
   }

   public static class Tuple2<T, U> {
      public final T first;
      public final U second;

      public Tuple2(T first, U second) {
         this.first = first;
         this.second = second;
      }
   }

   public static class Tuple3<T, U, V> {
      public final T first;
      public final U second;
      public final V third;

      public Tuple3(T first, U second, V third) {
         this.first = first;
         this.second = second;
         this.third = third;
      }
   }

   public static class Tuple4<T, U, V, W> {
      public final T first;
      public final U second;
      public final V third;
      public final W fourth;

      public Tuple4(T first, U second, V third, W fourth) {
         this.first = first;
         this.second = second;
         this.third = third;
         this.fourth = fourth;
      }
   }
}
