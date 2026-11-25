package com.teamacronymcoders.base.materialsystem.compat.base;

import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.materialsystem.MaterialException;
import com.teamacronymcoders.base.materialsystem.MaterialSystem;
import com.teamacronymcoders.base.materialsystem.compat.IMaterialCompat;
import com.teamacronymcoders.base.materialsystem.compat.MaterialCompat;
import com.teamacronymcoders.base.materialsystem.parts.PartBuilder;
import com.teamacronymcoders.base.materialsystem.parttype.ArmorPartType;
import com.teamacronymcoders.base.materialsystem.parttype.BlockPartType;
import com.teamacronymcoders.base.materialsystem.parttype.FluidPartType;
import com.teamacronymcoders.base.materialsystem.parttype.ItemPartType;
import com.teamacronymcoders.base.materialsystem.parttype.MinecartPartType;
import com.teamacronymcoders.base.materialsystem.parttype.OrePartType;
import com.teamacronymcoders.base.materialsystem.parttype.OreSamplePartType;
import com.teamacronymcoders.base.materialsystem.parttype.PartType;
import com.teamacronymcoders.base.materialsystem.parttype.StoragePartType;

@MaterialCompat("base")
public class BaseCompat implements IMaterialCompat {
   public BaseCompat() {
   }

   public void doCompat() {
      PartType item = new ItemPartType();
      PartType block = new BlockPartType();
      PartType ore = new OrePartType();
      PartType oreSample = new OreSamplePartType();
      PartType fluid = new FluidPartType();
      PartType armor = new ArmorPartType();
      PartType minecart = new MinecartPartType();
      MaterialSystem.registerPartType(item);
      MaterialSystem.registerPartType(block);
      MaterialSystem.registerPartType(ore);
      MaterialSystem.registerPartType(oreSample);
      MaterialSystem.registerPartType(fluid);
      MaterialSystem.registerPartType(armor);
      MaterialSystem.registerPartType(minecart);
      MaterialSystem.registerPartType(new StoragePartType());
      this.registerPart("Ingot", item);
      this.registerPart("Beam", item);
      this.registerPart("Gear", item);
      this.registerPart("Bolt", item);
      this.registerPart("Dust", item);
      this.registerPart("Nugget", item);
      this.registerPart((new PartBuilder()).setName("Rod").setPartType(item).setAdditionalOreDictNames("stick", "partToolRod"));
      this.registerPart("Plate", item);
      this.registerPart((new PartBuilder()).setName("Dense Plate").setPartType(item).setAdditionalOreDictNames("plateDense"));
      this.registerPart("Crystal", item);
      this.registerPart("Crushed Ore", item);
      this.registerPart("Casing", item);
      this.registerPart("Clump", item);
      this.registerPart("Shard", item);
      this.registerPart("Dirty Dust", item);
      this.registerPart((new PartBuilder()).setName("Cluster").setPartType(item).setOverlay(true));
      this.registerPart((new PartBuilder()).setName("Rock").setOreDictName("rock").setPartType(item));
      this.registerPart((new PartBuilder()).setName("Ore Rock").setOreDictName("rock").setPartType(item).setOverlay(true));
      this.registerPart("Block", block);
      this.registerPart("Ore", ore);
      this.registerPart("Poor Ore", ore);
      this.registerPart("Dense Ore", ore);
      this.registerPart("Ore Sample", oreSample);
      this.registerPart((new PartBuilder()).setName("Molten").setPartType(fluid).setOreDictName(""));
      this.registerPart("Armor", armor);
      this.registerPart("Minecart", minecart);
      this.registerPart("Centrifuged Ore", item, "oreCentrifuged");
      this.registerPart("Chipped Gem", item, "gemChipped");
      this.registerPart("Flawed Gem", item, "gemFlawed");
      this.registerPart("Flawless Gem", item, "gemFlawless");
      this.registerPart("Large Spring", item, "springLarge");
      this.registerPart("Long Rod", item, "rodLong");
      this.registerPart("Purified Ore", item, "orePurified");
      this.registerPart("Ring", item);
      this.registerPart("Round", item);
      this.registerPart("Small Dust", item, "dustSmall");
      this.registerPart("Small Spring", item, "springSmall");
      this.registerPart("Tiny Dust", item, "dustTiny");
   }

   private void registerPart(String name, PartType partType) {
      this.registerPart((new PartBuilder()).setName(name).setPartType(partType));
   }

   private void registerPart(String name, PartType partType, String properOreDict) {
      this.registerPart((new PartBuilder()).setName(name).setOreDictName(properOreDict).setPartType(partType));
   }

   private void registerPart(PartBuilder partBuilder) {
      try {
         partBuilder.build();
      } catch (MaterialException var3) {
         Base.instance.getLogger().getLogger().error(var3);
      }

   }
}
