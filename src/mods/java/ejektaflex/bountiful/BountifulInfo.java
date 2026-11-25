package ejektaflex.bountiful;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000¨\u0006\f"},
   d2 = {"Lejektaflex/bountiful/BountifulInfo;", "", "()V", "ADAPTER", "", "CLIENT", "DEPENDS", "FORGE_DEP", "MODID", "NAME", "SERVER", "VERSION", "Bountiful"}
)
public final class BountifulInfo {
   @NotNull
   public static final String ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter";
   @NotNull
   public static final String MODID = "bountiful";
   @NotNull
   public static final String NAME = "Bountiful";
   @NotNull
   public static final String VERSION = "2.2.3";
   @NotNull
   public static final String FORGE_DEP = "required-after:forge@[14.23.4.2768,15.0.0.0);";
   @NotNull
   public static final String DEPENDS = "required-after:forge@[14.23.4.2768,15.0.0.0);required-after:forgelin@[1.8.0,1.9.0);";
   @NotNull
   public static final String CLIENT = "ejektaflex.bountiful.proxy.ClientProxy";
   @NotNull
   public static final String SERVER = "ejektaflex.bountiful.proxy.CommonProxy";
   public static final BountifulInfo INSTANCE;

   private BountifulInfo() {
   }

   static {
      BountifulInfo var0 = new BountifulInfo();
      INSTANCE = var0;
   }
}
