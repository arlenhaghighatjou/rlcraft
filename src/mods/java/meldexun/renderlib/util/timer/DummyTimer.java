package meldexun.renderlib.util.timer;

import java.util.stream.LongStream;

public class DummyTimer implements ITimer {
   private final String name;
   private final String value;

   public DummyTimer(String name, String value) {
      this.name = name;
      this.value = value;
   }

   public String getName() {
      return this.name;
   }

   public void update() {
   }

   public void start() {
   }

   public void stop() {
   }

   public LongStream results() {
      return LongStream.empty();
   }

   public String avgString() {
      return this.value;
   }

   public String minString() {
      return this.value;
   }

   public String maxString() {
      return this.value;
   }
}
