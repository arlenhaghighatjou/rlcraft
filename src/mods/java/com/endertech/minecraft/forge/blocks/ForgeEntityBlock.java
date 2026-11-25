package com.endertech.minecraft.forge.blocks;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.regs.INeedRender;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;

public abstract class ForgeEntityBlock<E extends Entity> extends ForgeBlock implements INeedRender {
   private final Class<E> entityClass;

   public ForgeEntityBlock(ForgeMod mod, UnitConfig config, Class<E> entityClass, String name, Material material, CreativeTabs tab) {
      super(mod, config, name, material, tab);
      this.entityClass = entityClass;
   }

   public ForgeEntityBlock(ForgeMod mod, UnitConfig config, Class<E> entityClass, String name, Material material, float hardness, float resistance, CreativeTabs tab) {
      super(mod, config, name, material, hardness, resistance, tab);
      this.entityClass = entityClass;
   }

   public void onInit() {
      this.registerEntity();
      super.onInit();
   }

   protected abstract void registerEntity();

   public Class<E> getEntityClass() {
      return this.entityClass;
   }
}
