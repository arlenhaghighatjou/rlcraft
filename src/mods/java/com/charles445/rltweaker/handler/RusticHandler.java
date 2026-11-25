package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RusticHandler {
   public RusticHandler() {
      if (ModConfig.server.rustic.woodHarvestToolFix) {
         this.trySetAxe(new ResourceLocation("rustic", "log"));
         this.trySetAxe(new ResourceLocation("rustic", "planks"));
      }

      RusticHandler.RusticContainerValidator validator = new RusticHandler.RusticContainerValidator();
      MinecraftHandler.containerValidators.put("rustic.common.tileentity.BrewingBarrel", validator);
      MinecraftHandler.containerValidators.put("rustic.common.tileentity.ContainerCondenser", validator);
      MinecraftHandler.containerValidators.put("rustic.common.tileentity.ContainerCondenserAdvanced", validator);
   }

   private void trySetAxe(ResourceLocation rs) {
      if (Block.REGISTRY.containsKey(rs)) {
         Block block = (Block)Block.REGISTRY.getObject(rs);
         if (block.getRegistryName().equals(rs)) {
            RLTweaker.logger.info("Setting block " + rs.toString() + " harvest type to axe");
            block.setHarvestLevel("axe", 0);
         }
      }

   }

   public class RusticContainerValidator implements MinecraftHandler.IContainerValidator {
      @Nullable
      private Class c_BrewingBarrel;
      @Nullable
      private Field f_BrewingBarrel_te;
      @Nullable
      private Class c_ContainerCondenser;
      @Nullable
      private Field f_ContainerCondenser_te;
      @Nullable
      private Class c_ContainerCondenserAdvanced;
      @Nullable
      private Field f_ContainerCondenserAdvanced_te;

      public RusticContainerValidator() {
         try {
            this.c_BrewingBarrel = Class.forName("rustic.common.tileentity.ContainerBrewingBarrel");
            this.c_ContainerCondenser = Class.forName("rustic.common.tileentity.ContainerCondenser");
            this.c_ContainerCondenserAdvanced = Class.forName("rustic.common.tileentity.ContainerCondenserAdvanced");
            this.f_BrewingBarrel_te = ReflectUtil.findField(this.c_BrewingBarrel, "te");
            this.f_ContainerCondenser_te = ReflectUtil.findField(this.c_ContainerCondenser, "te");
            this.f_ContainerCondenserAdvanced_te = ReflectUtil.findField(this.c_ContainerCondenserAdvanced, "te");
         } catch (Exception var3) {
            if (ModConfig.server.rustic.validateContainers) {
               ErrorUtil.logSilent("Rustic Container Validation Reflection");
            }
         }

      }

      public boolean isValid(Container container) {
         if (!ModConfig.server.rustic.validateContainers) {
            return true;
         } else if (this.c_BrewingBarrel != null && this.c_BrewingBarrel.isInstance(container)) {
            return this.handleBrewingBarrel(container);
         } else if (this.c_ContainerCondenser != null && this.c_ContainerCondenser.isInstance(container)) {
            return this.handleContainerCondenser(container);
         } else {
            return this.c_ContainerCondenserAdvanced != null && this.c_ContainerCondenserAdvanced.isInstance(container) ? this.handleContainerCondenserAdvanced(container) : true;
         }
      }

      private boolean handleBrewingBarrel(Container container) {
         if (this.f_BrewingBarrel_te == null) {
            return true;
         } else {
            try {
               TileEntity te = (TileEntity)this.f_BrewingBarrel_te.get(container);
               if (te != null) {
                  return !te.isInvalid();
               }
            } catch (IllegalAccessException | IllegalArgumentException var3) {
               ErrorUtil.logSilent("Rustic Container Validation Brewing Barrel Invocation");
            }

            return true;
         }
      }

      private boolean handleContainerCondenser(Container container) {
         if (this.f_ContainerCondenser_te == null) {
            return true;
         } else {
            try {
               TileEntity te = (TileEntity)this.f_ContainerCondenser_te.get(container);
               if (te != null) {
                  return !te.isInvalid();
               }
            } catch (IllegalAccessException | IllegalArgumentException var3) {
               ErrorUtil.logSilent("Rustic Container Validation Condenser Invocation");
            }

            return true;
         }
      }

      private boolean handleContainerCondenserAdvanced(Container container) {
         if (this.f_ContainerCondenserAdvanced_te == null) {
            return true;
         } else {
            try {
               TileEntity te = (TileEntity)this.f_ContainerCondenserAdvanced_te.get(container);
               if (te != null) {
                  return !te.isInvalid();
               }
            } catch (IllegalAccessException | IllegalArgumentException var3) {
               ErrorUtil.logSilent("Rustic Container Validation Condenser Advanced Invocation");
            }

            return true;
         }
      }
   }
}
