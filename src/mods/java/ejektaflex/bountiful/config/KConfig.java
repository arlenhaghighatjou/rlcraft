package ejektaflex.bountiful.config;

import java.io.File;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraftforge.common.config.Configuration;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0016\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\b\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u0010H\u0016R\u001a\u0010\u0007\u001a\u00020\bX\u0084\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\f¨\u0006\u0012"},
   d2 = {"Lejektaflex/bountiful/config/KConfig;", "", "folder", "Ljava/io/File;", "fileName", "", "(Ljava/io/File;Ljava/lang/String;)V", "config", "Lnet/minecraftforge/common/config/Configuration;", "getConfig", "()Lnet/minecraftforge/common/config/Configuration;", "setConfig", "(Lnet/minecraftforge/common/config/Configuration;)V", "hasChanged", "", "load", "", "save", "Bountiful"}
)
public class KConfig {
   @NotNull
   private Configuration config;

   @NotNull
   protected final Configuration getConfig() {
      return this.config;
   }

   protected final void setConfig(@NotNull Configuration var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      this.config = <set-?>;
   }

   public void load() {
      this.config.load();
   }

   public void save() {
      this.config.save();
   }

   public boolean hasChanged() {
      return this.config.hasChanged();
   }

   public KConfig(@NotNull File folder, @NotNull String fileName) {
      Intrinsics.checkParameterIsNotNull(folder, "folder");
      Intrinsics.checkParameterIsNotNull(fileName, "fileName");
      super();
      this.config = new Configuration(new File(folder, fileName));
   }
}
