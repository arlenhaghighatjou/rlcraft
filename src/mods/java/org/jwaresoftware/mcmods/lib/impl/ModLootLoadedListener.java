package org.jwaresoftware.mcmods.lib.impl;

import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jwaresoftware.mcmods.lib.api.mod.IModInfo;
import org.jwaresoftware.mcmods.lib.loot.Loot;

public final class ModLootLoadedListener {
   private final String _mod_id;

   public ModLootLoadedListener(IModInfo owner) {
      this._mod_id = owner.mod_id();
   }

   @SubscribeEvent
   public void onLootTableLoaded(LootTableLoadEvent event) {
      Loot.merge(this._mod_id, event);
   }
}
