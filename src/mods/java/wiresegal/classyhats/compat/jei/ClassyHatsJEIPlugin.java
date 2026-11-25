package wiresegal.classyhats.compat.jei;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import org.jetbrains.annotations.NotNull;
import wiresegal.classyhats.client.gui.GuiHatInventory;

@JEIPlugin
@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0007"},
   d2 = {"Lwiresegal/classyhats/compat/jei/ClassyHatsJEIPlugin;", "Lmezz/jei/api/IModPlugin;", "()V", "register", "", "registry", "Lmezz/jei/api/IModRegistry;", "classyhats"}
)
public final class ClassyHatsJEIPlugin implements IModPlugin {
   public void register(@NotNull IModRegistry registry) {
      Intrinsics.checkParameterIsNotNull(registry, "registry");
      registry.getRecipeTransferRegistry().addRecipeTransferHandler(new HatInvRecipeTransferInfo());
      registry.addRecipeClickArea(GuiHatInventory.class, 135, 29, 16, 13, new String[]{"minecraft.crafting"});
   }

   public ClassyHatsJEIPlugin() {
   }
}
