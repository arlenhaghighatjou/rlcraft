package com.teamacronymcoders.base.registrysystem.pieces.gameregistry;

import com.teamacronymcoders.base.items.IHasOreDict;
import com.teamacronymcoders.base.registrysystem.Registry;
import com.teamacronymcoders.base.registrysystem.pieces.RegistryPiece;
import com.teamacronymcoders.base.registrysystem.pieces.RegistryPieceBase;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.oredict.OreDictionary;

@RegistryPiece(
   priority = EventPriority.LOW
)
public class OreDictRegistryPiece extends RegistryPieceBase<IHasOreDict> {
   public OreDictRegistryPiece() {
      super(IHasOreDict.class);
   }

   public boolean acceptsRegistry(Registry registry) {
      return "ITEM".equalsIgnoreCase(registry.getName());
   }

   public void onRegistryEvent(ResourceLocation name, IHasOreDict entry) {
      Map<ItemStack, String> oreDict = entry.getOreDictNames(new HashMap());
      oreDict.forEach((itemStack, string) -> {
         OreDictionary.registerOre(string, itemStack);
      });
   }
}
