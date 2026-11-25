package com.lycanitesmobs.core.mobevent.effects;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class StructureBuilder {
   public static Map<String, StructureBuilder> STRUCTURE_BUILDERS = new HashMap();
   public String name;

   public StructureBuilder() {
   }

   public static StructureBuilder getStructureBuilder(String name) {
      return STRUCTURE_BUILDERS.containsKey(name) ? (StructureBuilder)STRUCTURE_BUILDERS.get(name) : null;
   }

   public static void addStructureBuilder(StructureBuilder structureBuilder) {
      STRUCTURE_BUILDERS.put(structureBuilder.name, structureBuilder);
   }

   public abstract void build(World var1, EntityPlayer var2, BlockPos var3, int var4, int var5, int var6);
}
