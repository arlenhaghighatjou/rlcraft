package io.github.classgraph;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.InputStreamOrByteBufferAdapter;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.URLPathEncoder;

public abstract class Resource implements Closeable, Comparable<Resource> {
   private final ClasspathElement classpathElement;
   protected InputStream inputStream;
   protected ByteBuffer byteBuffer;
   protected long length;
   private boolean isOpen;
   private String toString;
   private final long lastModified;
   private final Set<PosixFilePermission> posixFilePermissions;
   LogNode scanLog;

   public Resource(ClasspathElement classpathElement, long length) {
      this.classpathElement = classpathElement;
      this.length = length;
      this.lastModified = 0L;
      this.posixFilePermissions = null;
   }

   public Resource(ClasspathElement classpathElement, long length, long lastModified, Set<PosixFilePermission> posixFilePermissions) {
      this.classpathElement = classpathElement;
      this.length = length;
      this.lastModified = lastModified;
      this.posixFilePermissions = posixFilePermissions;
   }

   protected InputStream byteBufferToInputStream() {
      return this.inputStream == null ? (this.inputStream = FileUtils.byteBufferToInputStream(this.byteBuffer)) : this.inputStream;
   }

   protected ByteBuffer inputStreamToByteBuffer() throws IOException {
      return this.byteBuffer == null ? (this.byteBuffer = ByteBuffer.wrap(this.inputStreamToByteArray())) : this.byteBuffer;
   }

   protected byte[] inputStreamToByteArray() throws IOException {
      return FileUtils.readAllBytesAsArray(this.inputStream, this.length);
   }

   protected byte[] byteBufferToByteArray() {
      if (this.byteBuffer.hasArray()) {
         return this.byteBuffer.array();
      } else {
         byte[] byteArray = new byte[this.byteBuffer.remaining()];
         this.byteBuffer.get(byteArray);
         return byteArray;
      }
   }

   protected void markAsOpen() throws IOException {
      if (this.isOpen) {
         throw new IOException("Resource is already open -- cannot open it again without first calling close()");
      } else {
         this.isOpen = true;
      }
   }

   protected void markAsClosed() {
      this.isOpen = false;
   }

   private static URL uriToURL(URI uri) {
      if (uri.getScheme().equals("jrt")) {
         throw new IllegalArgumentException("Could not create URL from URI with \"jrt:\" scheme: " + uri);
      } else {
         try {
            return uri.toURL();
         } catch (MalformedURLException var2) {
            throw new IllegalArgumentException("Could not create URL from URI: " + uri + " -- " + var2);
         }
      }
   }

   public URI getURI() {
      URI locationURI = this.getClasspathElementURI();
      String locationURIStr = locationURI.toString();
      String resourcePath = this.getPathRelativeToClasspathElement();
      boolean isDir = locationURIStr.endsWith("/");

      try {
         return new URI((!isDir && !locationURIStr.startsWith("jar:") ? "jar:" : "") + locationURIStr + (isDir ? "" : "!/") + URLPathEncoder.encodePath(resourcePath));
      } catch (URISyntaxException var6) {
         throw new IllegalArgumentException("Could not form URL for classpath element: " + locationURIStr + " ; path: " + resourcePath + " : " + var6);
      }
   }

   public URL getURL() {
      return uriToURL(this.getURI());
   }

   public URI getClasspathElementURI() {
      return this.classpathElement.getURI();
   }

   public URL getClasspathElementURL() {
      return uriToURL(this.getClasspathElementURI());
   }

   public File getClasspathElementFile() {
      return this.classpathElement.getFile();
   }

   public ModuleRef getModuleRef() {
      return this.classpathElement instanceof ClasspathElementModule ? ((ClasspathElementModule)this.classpathElement).moduleRef : null;
   }

   public String getContentAsString() throws IOException {
      String var1;
      try {
         var1 = new String(this.load(), StandardCharsets.UTF_8);
      } finally {
         this.close();
      }

      return var1;
   }

   public abstract String getPath();

   public abstract String getPathRelativeToClasspathElement();

   public abstract InputStream open() throws IOException;

   public abstract ByteBuffer read() throws IOException;

   public abstract byte[] load() throws IOException;

   abstract InputStreamOrByteBufferAdapter openOrRead() throws IOException;

   public long getLength() {
      return this.length;
   }

   public long getLastModified() {
      return this.lastModified;
   }

   public Set<PosixFilePermission> getPosixFilePermissions() {
      return this.posixFilePermissions;
   }

   public String toString() {
      return this.toString != null ? this.toString : (this.toString = this.getURI().toString());
   }

   public int hashCode() {
      return this.toString().hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof Resource) ? false : this.toString().equals(obj.toString());
      }
   }

   public int compareTo(Resource o) {
      return this.toString().compareTo(o.toString());
   }

   public void close() {
      if (this.inputStream != null) {
         try {
            if (this.inputStream instanceof Resource.InputStreamResourceCloser) {
               ((Resource.InputStreamResourceCloser)this.inputStream).closeInputStream();
            } else {
               this.inputStream.close();
            }
         } catch (IOException var2) {
            ;
         }

         this.inputStream = null;
      }

   }

   protected class InputStreamResourceCloser extends InputStream {
      private InputStream inputStream;
      private Resource parentResource;

      protected InputStreamResourceCloser(Resource parentResource, InputStream inputStream) throws IOException {
         if (inputStream == null) {
            throw new IOException("InputStream cannot be null");
         } else {
            this.inputStream = inputStream;
            this.parentResource = parentResource;
         }
      }

      public int read() throws IOException {
         if (this.inputStream == null) {
            throw new IOException("InputStream is not open");
         } else {
            return this.inputStream.read();
         }
      }

      public int read(byte[] b, int off, int len) throws IOException {
         if (this.inputStream == null) {
            throw new IOException("InputStream is not open");
         } else {
            return this.inputStream.read(b, off, len);
         }
      }

      public int read(byte[] b) throws IOException {
         if (this.inputStream == null) {
            throw new IOException("InputStream is not open");
         } else {
            return this.inputStream.read(b);
         }
      }

      public int available() throws IOException {
         if (this.inputStream == null) {
            throw new IOException("InputStream is not open");
         } else {
            return this.inputStream.available();
         }
      }

      public long skip(long n) throws IOException {
         if (this.inputStream == null) {
            throw new IOException("InputStream is not open");
         } else {
            return this.inputStream.skip(n);
         }
      }

      public boolean markSupported() {
         return this.inputStream.markSupported();
      }

      public synchronized void mark(int readlimit) {
         this.inputStream.mark(readlimit);
      }

      public synchronized void reset() throws IOException {
         if (this.inputStream == null) {
            throw new IOException("InputStream is not open");
         } else {
            this.inputStream.reset();
         }
      }

      void closeInputStream() throws IOException {
         if (this.inputStream != null) {
            try {
               this.inputStream.close();
            } catch (IOException var2) {
               ;
            }

            this.inputStream = null;
         }

      }

      public void close() throws IOException {
         if (this.parentResource != null) {
            this.parentResource.close();
            this.parentResource = null;
         }

      }
   }
}
