package me.lucko.spark.lib.adventure.audience;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

final class Audiences {
   static final Collector<? super Audience, ?, ForwardingAudience> COLLECTOR = Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), (audiences) -> {
      return Audience.audience(Collections.unmodifiableCollection(audiences));
   });

   private Audiences() {
   }
}
