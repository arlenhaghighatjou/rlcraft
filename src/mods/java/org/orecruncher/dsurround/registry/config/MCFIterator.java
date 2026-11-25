package org.orecruncher.dsurround.registry.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.orecruncher.dsurround.ModBase;

class MCFIterator implements Iterator<ModConfiguration>, Closeable {
   private final Gson gson = (new GsonBuilder()).create();
   private final JsonReader reader;

   protected MCFIterator(@Nonnull byte[] bits) throws IOException {
      this.reader = new JsonReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(bits))));
      this.reader.beginArray();
   }

   public boolean hasNext() {
      try {
         return this.reader.hasNext();
      } catch (IOException var2) {
         ModBase.log().error("Unable to read from memory!", var2);
         return false;
      }
   }

   @Nullable
   public ModConfiguration next() {
      String source = null;

      try {
         source = (String)this.gson.fromJson(this.reader, String.class);
         ModConfiguration mcf = (ModConfiguration)this.gson.fromJson(this.reader, ModConfiguration.class);
         mcf.source = source;
         return mcf;
      } catch (JsonIOException | JsonSyntaxException var3) {
         ModBase.log().error(source != null ? source : "Unable to parse Json from memory!", var3);
         return null;
      }
   }

   public void close() throws IOException {
      this.reader.close();
   }
}
