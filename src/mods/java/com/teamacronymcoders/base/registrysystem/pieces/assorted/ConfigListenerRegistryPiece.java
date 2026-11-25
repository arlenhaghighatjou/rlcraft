package com.teamacronymcoders.base.registrysystem.pieces.assorted;

import com.teamacronymcoders.base.registrysystem.config.ConfigRegistry;
import com.teamacronymcoders.base.registrysystem.config.IConfigListener;
import com.teamacronymcoders.base.registrysystem.pieces.RegistryPiece;
import com.teamacronymcoders.base.registrysystem.pieces.RegistryPieceBase;
import net.minecraft.util.ResourceLocation;

@RegistryPiece
public class ConfigListenerRegistryPiece extends RegistryPieceBase<IConfigListener> {
   private ConfigRegistry configRegistry;

   public ConfigListenerRegistryPiece() {
      super(IConfigListener.class);
   }

   public void addEntry(ResourceLocation name, IConfigListener listener) {
      if (this.configRegistry == null) {
         this.configRegistry = (ConfigRegistry)this.getMod().getRegistryHolder().getRegistry(ConfigRegistry.class, "CONFIG");
      }

      this.configRegistry.addListener(listener);
   }
}
