package com.teamacronymcoders.base.registrysystem.pieces.gameregistry;

import com.teamacronymcoders.base.blocks.IHasItemBlock;
import com.teamacronymcoders.base.registrysystem.ItemRegistry;
import com.teamacronymcoders.base.registrysystem.Registry;
import com.teamacronymcoders.base.registrysystem.pieces.RegistryPiece;
import com.teamacronymcoders.base.registrysystem.pieces.RegistryPieceBase;
import net.minecraft.util.ResourceLocation;

@RegistryPiece
public class ItemBlockRegisterRegistryPiece extends RegistryPieceBase<IHasItemBlock> {
   private ItemRegistry itemRegistry;

   public ItemBlockRegisterRegistryPiece() {
      super(IHasItemBlock.class);
   }

   public boolean acceptsRegistry(Registry registry) {
      return "BLOCK".equalsIgnoreCase(registry.getName());
   }

   public void addEntry(ResourceLocation name, IHasItemBlock block) {
      if (this.itemRegistry == null) {
         this.itemRegistry = (ItemRegistry)this.getMod().getRegistryHolder().getRegistry(ItemRegistry.class, "ITEM");
      }

      this.itemRegistry.register(name, block.getItemBlock());
   }
}
