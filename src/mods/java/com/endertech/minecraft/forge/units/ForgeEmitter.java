package com.endertech.minecraft.forge.units;

import com.endertech.common.KeyValuePair;
import com.endertech.minecraft.forge.api.IEmitter;
import com.endertech.minecraft.forge.blocks.BlockState;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.UnitConfig;
import java.util.Collections;
import java.util.Set;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

public class ForgeEmitter extends RelatedUnit implements IEmitter {
   protected final Set<BlockState> relatedBlocks = new StateHashSet<BlockState>();
   private final String activeStateTag;
   private final KeyValuePair identityTag;
   private final boolean isEntity;
   private final boolean useDroppedItemMeta;
   private final String[] relatedBlocksArray;

   public ForgeEmitter(UnitConfig config, UnitId id, KeyValuePair identityTag, boolean isEntity, String activeStateTag, boolean useDroppedItemMeta, String... relatedBlocks) {
      super(config, id);
      this.activeStateTag = ForgeConfig.getStr(config, this.getClassCategory(), "activeStateTag", activeStateTag, "Defines the name of the NBT tag that stores info about tile entity active state.\nEmitter is considered active if the return value is greater than 0 or true.\nSet to an empty string if you don't need this function.");
      this.identityTag = ForgeConfig.getKeyValuePair(config, this.getClassCategory(), "identityTag", identityTag, "This parameter is required only for those blocks that use NBT tags to fully identify themselves.\nThe format is <tagKey>=<value>\nIf the value is a string, it must be enclosed in double quotes. Example: id=\"ic2:generator\"\nLeave this blank if you don't need it.");
      this.isEntity = ForgeConfig.getBool(config, this.getClassCategory(), "isEntity", isEntity, "Set this to True only if this emitter is not a block or a tile but an entity.");
      this.useDroppedItemMeta = ForgeConfig.getBool(config, this.getClassCategory(), "useDroppedItemMeta", useDroppedItemMeta, "If true, the meta data of the dropped item will be used instead of the block state meta.\nThis is usefull when the placed block has other meta than its item.");
      this.relatedBlocksArray = ForgeConfig.getStrArray(this.getConfig(), this.getClassCategory(), "relatedBlocks", relatedBlocks, "Defines a list of blocks which should be considered as part of this multiblock.");
      this.saveConfig();
   }

   public String getActiveStateTag() {
      return this.activeStateTag;
   }

   public KeyValuePair getIdentityTag() {
      return this.identityTag;
   }

   public boolean isEntity() {
      return this.isEntity;
   }

   public boolean useDroppedItemMeta() {
      return this.useDroppedItemMeta;
   }

   public Set<BlockState> getRelatedBlocks() {
      return Collections.unmodifiableSet(this.relatedBlocks);
   }

   public void onPostInit(FMLPostInitializationEvent event) {
      this.relatedBlocks.addAll(StateHashSet.parseBlockStatesFrom(this.relatedBlocksArray));
   }
}
