package com.alcatrazescapee.alcatrazcore.util;

import com.alcatrazescapee.alcatrazcore.block.BlockFluidCore;
import com.alcatrazescapee.alcatrazcore.client.IModelProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@ParametersAreNonnullByDefault
public final class RegistryHelper {
   private static final Map<String, RegistryHelper> MAP = new HashMap();
   private final String modID;
   private final List<Block> BLOCKS = new ArrayList();
   private final List<Item> ITEMS = new ArrayList();
   private final List<SoundEvent> SOUNDS = new ArrayList();

   @Nonnull
   public static RegistryHelper get(String modID) {
      if (!MAP.containsKey(modID)) {
         MAP.put(modID, new RegistryHelper(modID));
      }

      return (RegistryHelper)MAP.get(modID);
   }

   private RegistryHelper(String modID) {
      this.modID = modID;
   }

   public void initModels(ModelRegistryEvent event) {
      for(Block block : this.BLOCKS) {
         if (block instanceof IModelProvider) {
            ((IModelProvider)block).registerModel();
         }
      }

      for(Item item : this.ITEMS) {
         if (item instanceof IModelProvider) {
            ((IModelProvider)item).registerModel();
         }
      }

      this.BLOCKS.clear();
      this.ITEMS.clear();
   }

   public void initBlocks(Register<Block> event) {
      IForgeRegistry<Block> registry = event.getRegistry();

      for(Block block : this.BLOCKS) {
         registry.register(block);
      }

   }

   public void initItems(Register<Item> event) {
      IForgeRegistry<Item> registry = event.getRegistry();

      for(Item item : this.ITEMS) {
         registry.register(item);
      }

   }

   public void initSounds(Register<SoundEvent> event) {
      IForgeRegistry<SoundEvent> registry = event.getRegistry();

      for(SoundEvent sound : this.SOUNDS) {
         registry.register(sound);
      }

      this.SOUNDS.clear();
   }

   public <T extends Item> T registerItem(T item, String name, CreativeTabs tab) {
      item.setCreativeTab(tab);
      return (T)this.registerItem(item, name);
   }

   public <T extends Item> T registerItem(T item, String name) {
      item.setRegistryName(this.modID + ":" + name.toLowerCase());
      item.setUnlocalizedName(this.modID + ":" + name.toLowerCase().replace('/', '.'));
      this.ITEMS.add(item);
      return item;
   }

   public <T extends TileEntity> void registerTile(Class<T> clazz, String name) {
      TileEntity.register(this.modID + ":" + name.toLowerCase(), clazz);
   }

   public <T extends Fluid> T registerFluid(T fluid, String name) {
      return (T)this.registerFluid(fluid, name, Material.WATER, BlockFluidCore::new);
   }

   public <T extends Fluid> T registerFluid(T fluid, String name, Material material) {
      return (T)this.registerFluid(fluid, name, material, BlockFluidCore::new);
   }

   public <T extends Fluid> T registerFluid(T fluid, String name, Material material, @Nullable BiFunction<T, Material, BlockFluidBase> fluidBlockSupplier) {
      fluid.setUnlocalizedName(this.modID + "." + name.toLowerCase().replace('/', '.'));
      FluidRegistry.registerFluid(fluid);
      FluidRegistry.addBucketForFluid(fluid);
      if (fluidBlockSupplier != null) {
         BlockFluidBase fluidBlock = (BlockFluidBase)fluidBlockSupplier.apply(fluid, material);
         if (fluidBlock != null) {
            this.registerBlock(fluidBlock, (Function)null, "fluid/" + fluid.getName());
         }
      }

      return fluid;
   }

   public <T extends SoundEvent> T registerSound(T sound, String name) {
      sound.setRegistryName(new ResourceLocation(this.modID, name.toLowerCase()));
      this.SOUNDS.add(sound);
      return sound;
   }

   public SoundEvent registerSound(String name) {
      ResourceLocation loc = new ResourceLocation(this.modID, name.toLowerCase());
      SoundEvent sound = new SoundEvent(loc);
      sound.setRegistryName(loc);
      this.SOUNDS.add(sound);
      return sound;
   }

   public <T extends Block> T registerBlock(T block, String name, CreativeTabs tab) {
      return (T)this.registerBlock(block, ItemBlock::new, name, tab);
   }

   public <T extends Block> T registerBlock(T block, String name) {
      return (T)this.registerBlock(block, ItemBlock::new, name);
   }

   public <T extends Block> T registerBlock(T block, @Nullable Function<Block, ItemBlock> itemBlockSupplier, String name, CreativeTabs tab) {
      block.setCreativeTab(tab);
      return (T)this.registerBlock(block, itemBlockSupplier, name);
   }

   public <T extends Block> T registerBlock(T block, @Nullable Function<Block, ItemBlock> itemBlockSupplier, String name) {
      block.setRegistryName(this.modID + ":" + name.toLowerCase());
      block.setUnlocalizedName(this.modID + ":" + name.toLowerCase().replace('/', '.'));
      this.BLOCKS.add(block);
      if (itemBlockSupplier != null) {
         ItemBlock itemBlock = (ItemBlock)itemBlockSupplier.apply(block);
         if (itemBlock != null) {
            itemBlock.setRegistryName(this.modID + ":" + name.toLowerCase());
            itemBlock.setUnlocalizedName(this.modID + ":" + name.toLowerCase().replace('/', '.'));
            this.ITEMS.add(itemBlock);
         }
      }

      return block;
   }
}
