package com.teamacronymcoders.base.registrysystem;

import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.registrysystem.pieces.IRegistryPiece;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class BlockRegistry extends ModularRegistry<Block> {
   public BlockRegistry(IBaseMod mod, List<IRegistryPiece> registryPieces) {
      super("BLOCK", mod, registryPieces);
   }

   public void register(Block block) {
      String unlocalizedName = block.getUnlocalizedName();
      if (unlocalizedName.startsWith("tile.")) {
         unlocalizedName = unlocalizedName.substring(5);
      }

      if (!unlocalizedName.contains(this.mod.getID())) {
         block.setUnlocalizedName(this.mod.getID() + "." + unlocalizedName);
      }

      ResourceLocation name = block.getRegistryName();
      if (name == null) {
         name = new ResourceLocation(this.mod.getID(), unlocalizedName);
      }

      this.register(name, block);
   }
}
