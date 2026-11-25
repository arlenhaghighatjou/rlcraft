package nonapi.io.github.classgraph.fastzipfilereader;

import java.util.zip.Inflater;
import nonapi.io.github.classgraph.recycler.Resettable;

class RecyclableInflater implements Resettable, AutoCloseable {
   private final Inflater inflater = new Inflater(true);

   RecyclableInflater() {
   }

   public Inflater getInflater() {
      return this.inflater;
   }

   public void reset() {
      this.inflater.reset();
   }

   public void close() {
      this.inflater.end();
   }
}
