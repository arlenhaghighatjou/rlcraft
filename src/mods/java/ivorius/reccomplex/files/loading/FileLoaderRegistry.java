package ivorius.reccomplex.files.loading;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.events.FileLoadEvent;
import ivorius.reccomplex.events.RCEventBus;
import java.nio.file.Path;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class FileLoaderRegistry<S> extends FileLoaderAdapter {
   public LeveledRegistry<? super S> registry;

   public FileLoaderRegistry(String suffix, LeveledRegistry<? super S> registry) {
      super(suffix);
      this.registry = registry;
   }

   @ParametersAreNonnullByDefault
   public boolean loadFile(Path path, String id, FileLoadContext context) {
      String domain = context.domain;
      boolean active = context.active;
      S s = null;

      try {
         s = (S)this.read(path, id);
      } catch (Exception var8) {
         RecurrentComplex.logger.error("Error reading file: " + path, var8);
      }

      if (s != null) {
         FileLoadEvent.Pre<S> event = new FileLoadEvent.Pre<S>(s, this.suffix, id, domain, path, active);
         if (!RCEventBus.INSTANCE.post(event) && RCConfig.shouldResourceLoad(this.suffix, id, domain)) {
            active = event.active;
            this.registry.register(id, domain, s, active, context.level);
            RCEventBus.INSTANCE.post(new FileLoadEvent.Post(s, this.suffix, id, domain, path, active));
         }

         return true;
      } else {
         return false;
      }
   }

   public abstract S read(Path var1, String var2) throws Exception;

   @ParametersAreNonnullByDefault
   public void clearFiles(LeveledRegistry.Level level) {
      this.registry.clear(level);
   }
}
