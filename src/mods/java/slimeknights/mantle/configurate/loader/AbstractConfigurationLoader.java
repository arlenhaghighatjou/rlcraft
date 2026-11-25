package slimeknights.mantle.configurate.loader;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import slimeknights.mantle.configurate.ConfigurationNode;
import slimeknights.mantle.configurate.ConfigurationOptions;

public abstract class AbstractConfigurationLoader<NodeType extends ConfigurationNode> implements ConfigurationLoader<NodeType> {
   public static final String CONFIGURATE_LINE_SEPARATOR = "\n";
   protected static final Splitter LINE_SPLITTER = Splitter.on("\n");
   protected static final String SYSTEM_LINE_SEPARATOR = System.lineSeparator();
   protected final Callable<BufferedReader> source;
   private final Callable<BufferedWriter> sink;
   private final CommentHandler[] commentHandlers;
   private final HeaderMode headerMode;
   private final ConfigurationOptions defaultOptions;

   protected AbstractConfigurationLoader(AbstractConfigurationLoader.Builder<?> builder, CommentHandler[] commentHandlers) {
      this.source = builder.getSource();
      this.sink = builder.getSink();
      this.headerMode = builder.getHeaderMode();
      this.defaultOptions = builder.getDefaultOptions();
      this.commentHandlers = commentHandlers;
   }

   public CommentHandler getDefaultCommentHandler() {
      return this.commentHandlers[0];
   }

   public NodeType load(ConfigurationOptions options) throws IOException {
      if (!this.canLoad()) {
         throw new IOException("No source present to read from!");
      } else {
         try {
            BufferedReader reader = (BufferedReader)this.source.call();
            Throwable var3 = null;

            ConfigurationNode var20;
            try {
               if (this.headerMode == HeaderMode.PRESERVE || this.headerMode == HeaderMode.NONE) {
                  String comment = CommentHandlers.extractComment(reader, this.commentHandlers);
                  if (comment != null && comment.length() > 0) {
                     options = options.setHeader(comment);
                  }
               }

               NodeType node = this.createEmptyNode(options);
               this.loadInternal(node, reader);
               var20 = node;
            } catch (Throwable var16) {
               var3 = var16;
               throw var16;
            } finally {
               if (reader != null) {
                  if (var3 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var15) {
                        var3.addSuppressed(var15);
                     }
                  } else {
                     reader.close();
                  }
               }

            }

            return (NodeType)var20;
         } catch (NoSuchFileException | FileNotFoundException var18) {
            return (NodeType)this.createEmptyNode(options);
         } catch (Exception var19) {
            if (var19 instanceof IOException) {
               throw (IOException)var19;
            } else {
               throw new IOException(var19);
            }
         }
      }
   }

   protected abstract void loadInternal(NodeType var1, BufferedReader var2) throws IOException;

   public void save(ConfigurationNode node) throws IOException {
      if (!this.canSave()) {
         throw new IOException("No sink present to write to!");
      } else {
         try {
            Writer writer = (Writer)this.sink.call();
            Throwable var3 = null;

            try {
               if (this.headerMode != HeaderMode.NONE) {
                  String header = node.getOptions().getHeader();
                  if (header != null && !header.isEmpty()) {
                     for(String line : this.getDefaultCommentHandler().toComment(ImmutableList.copyOf(LINE_SPLITTER.split(header)))) {
                        writer.write(line);
                        writer.write(SYSTEM_LINE_SEPARATOR);
                     }

                     writer.write(SYSTEM_LINE_SEPARATOR);
                  }
               }

               this.saveInternal(node, writer);
            } catch (Throwable var15) {
               var3 = var15;
               throw var15;
            } finally {
               if (writer != null) {
                  if (var3 != null) {
                     try {
                        writer.close();
                     } catch (Throwable var14) {
                        var3.addSuppressed(var14);
                     }
                  } else {
                     writer.close();
                  }
               }

            }

         } catch (Exception var17) {
            if (var17 instanceof IOException) {
               throw (IOException)var17;
            } else {
               throw new IOException(var17);
            }
         }
      }
   }

   protected abstract void saveInternal(ConfigurationNode var1, Writer var2) throws IOException;

   public ConfigurationOptions getDefaultOptions() {
      return this.defaultOptions;
   }

   public boolean canLoad() {
      return this.source != null;
   }

   public boolean canSave() {
      return this.sink != null;
   }

   protected abstract static class Builder<T extends AbstractConfigurationLoader.Builder> {
      protected HeaderMode headerMode = HeaderMode.PRESERVE;
      protected Callable<BufferedReader> source;
      protected Callable<BufferedWriter> sink;
      protected ConfigurationOptions defaultOptions = ConfigurationOptions.defaults();

      protected Builder() {
      }

      private T self() {
         return (T)this;
      }

      public T setFile(File file) {
         return (T)this.setPath(file.toPath());
      }

      public T setPath(Path path) {
         Path absPath = ((Path)Objects.requireNonNull(path, "path")).toAbsolutePath();
         this.source = () -> {
            return (T)Files.newBufferedReader(absPath, StandardCharsets.UTF_8);
         };
         this.sink = AtomicFiles.createAtomicWriterFactory(absPath, StandardCharsets.UTF_8);
         return (T)this.self();
      }

      public T setURL(URL url) {
         this.source = () -> {
            return (T)(new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8)));
         };
         return (T)this.self();
      }

      public T setSource(Callable<BufferedReader> source) {
         this.source = source;
         return (T)this.self();
      }

      public T setSink(Callable<BufferedWriter> sink) {
         this.sink = sink;
         return (T)this.self();
      }

      public Callable<BufferedReader> getSource() {
         return this.source;
      }

      public Callable<BufferedWriter> getSink() {
         return this.sink;
      }

      /** @deprecated */
      @Deprecated
      public T setPreservesHeader(boolean preservesHeader) {
         this.headerMode = preservesHeader ? HeaderMode.PRESERVE : HeaderMode.PRESET;
         return (T)this.self();
      }

      /** @deprecated */
      @Deprecated
      public boolean preservesHeader() {
         return this.headerMode == HeaderMode.PRESERVE;
      }

      public T setHeaderMode(HeaderMode mode) {
         this.headerMode = mode;
         return (T)this.self();
      }

      public HeaderMode getHeaderMode() {
         return this.headerMode;
      }

      public T setDefaultOptions(ConfigurationOptions defaultOptions) {
         this.defaultOptions = (ConfigurationOptions)Objects.requireNonNull(defaultOptions, "defaultOptions");
         return (T)this.self();
      }

      public ConfigurationOptions getDefaultOptions() {
         return this.defaultOptions;
      }

      public abstract AbstractConfigurationLoader build();
   }
}
