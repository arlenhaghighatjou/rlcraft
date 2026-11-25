package me.lucko.spark.lib.okhttp3;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import me.lucko.spark.lib.okhttp3.internal.Util;
import me.lucko.spark.lib.okio.Buffer;
import me.lucko.spark.lib.okio.BufferedSink;

public final class FormBody extends RequestBody {
   private static final MediaType CONTENT_TYPE = MediaType.get("application/x-www-form-urlencoded");
   private final List<String> encodedNames;
   private final List<String> encodedValues;

   FormBody(List<String> encodedNames, List<String> encodedValues) {
      this.encodedNames = Util.immutableList(encodedNames);
      this.encodedValues = Util.immutableList(encodedValues);
   }

   public int size() {
      return this.encodedNames.size();
   }

   public String encodedName(int index) {
      return (String)this.encodedNames.get(index);
   }

   public String name(int index) {
      return HttpUrl.percentDecode(this.encodedName(index), true);
   }

   public String encodedValue(int index) {
      return (String)this.encodedValues.get(index);
   }

   public String value(int index) {
      return HttpUrl.percentDecode(this.encodedValue(index), true);
   }

   public MediaType contentType() {
      return CONTENT_TYPE;
   }

   public long contentLength() {
      return this.writeOrCountBytes((BufferedSink)null, true);
   }

   public void writeTo(BufferedSink sink) throws IOException {
      this.writeOrCountBytes(sink, false);
   }

   private long writeOrCountBytes(@Nullable BufferedSink sink, boolean countBytes) {
      long byteCount = 0L;
      Buffer buffer;
      if (countBytes) {
         buffer = new Buffer();
      } else {
         buffer = sink.buffer();
      }

      int i = 0;

      for(int size = this.encodedNames.size(); i < size; ++i) {
         if (i > 0) {
            buffer.writeByte(38);
         }

         buffer.writeUtf8((String)this.encodedNames.get(i));
         buffer.writeByte(61);
         buffer.writeUtf8((String)this.encodedValues.get(i));
      }

      if (countBytes) {
         byteCount = buffer.size();
         buffer.clear();
      }

      return byteCount;
   }

   public static final class Builder {
      private final List<String> names;
      private final List<String> values;
      @Nullable
      private final Charset charset;

      public Builder() {
         this((Charset)null);
      }

      public Builder(@Nullable Charset charset) {
         this.names = new ArrayList();
         this.values = new ArrayList();
         this.charset = charset;
      }

      public FormBody.Builder add(String name, String value) {
         if (name == null) {
            throw new NullPointerException("name == null");
         } else if (value == null) {
            throw new NullPointerException("value == null");
         } else {
            this.names.add(HttpUrl.canonicalize(name, " \"':;<=>@[]^`{}|/\\?#&!$(),~", false, false, true, true, this.charset));
            this.values.add(HttpUrl.canonicalize(value, " \"':;<=>@[]^`{}|/\\?#&!$(),~", false, false, true, true, this.charset));
            return this;
         }
      }

      public FormBody.Builder addEncoded(String name, String value) {
         if (name == null) {
            throw new NullPointerException("name == null");
         } else if (value == null) {
            throw new NullPointerException("value == null");
         } else {
            this.names.add(HttpUrl.canonicalize(name, " \"':;<=>@[]^`{}|/\\?#&!$(),~", true, false, true, true, this.charset));
            this.values.add(HttpUrl.canonicalize(value, " \"':;<=>@[]^`{}|/\\?#&!$(),~", true, false, true, true, this.charset));
            return this;
         }
      }

      public FormBody build() {
         return new FormBody(this.names, this.values);
      }
   }
}
