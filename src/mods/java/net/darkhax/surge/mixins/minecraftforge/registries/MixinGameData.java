package net.darkhax.surge.mixins.minecraftforge.registries;

import java.util.Locale;
import net.darkhax.surge.core.SurgeConfiguration;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.registries.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({GameData.class})
public class MixinGameData {
   public MixinGameData() {
   }

   @Inject(
      method = {"checkPrefix(Ljava/lang/String;)Lnet/minecraft/util/ResourceLocation;"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private static void checkPrefix(String name, CallbackInfoReturnable<ResourceLocation> info) {
      if (SurgeConfiguration.fastPrefixChecking) {
         int separator = name.lastIndexOf(58);
         String namespace = separator == -1 ? "" : name.substring(0, separator).toLowerCase(Locale.ROOT);
         String path = separator == -1 ? name : name.substring(separator + 1);
         if (namespace.isEmpty()) {
            ModContainer activeMod = Loader.instance().activeModContainer();
            if (activeMod != null) {
               namespace = activeMod instanceof InjectedModContainer && ((InjectedModContainer)activeMod).wrappedContainer instanceof FMLContainer ? "minecraft" : activeMod.getModId().toLowerCase(Locale.ROOT);
            }
         }

         info.setReturnValue(new ResourceLocation(namespace, path));
      }

   }
}
