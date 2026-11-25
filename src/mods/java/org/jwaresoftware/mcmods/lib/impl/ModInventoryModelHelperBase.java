package org.jwaresoftware.mcmods.lib.impl;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.Validate;
import org.jwaresoftware.mcmods.lib.Fluids;
import org.jwaresoftware.mcmods.lib.SharedGlue;
import org.jwaresoftware.mcmods.lib.api.Gidable;
import org.jwaresoftware.mcmods.lib.api.IModBlock;
import org.jwaresoftware.mcmods.lib.api.IMultiTextured;
import org.jwaresoftware.mcmods.lib.api.mod.IModInfo;
import org.jwaresoftware.mcmods.lib.api.mod.IModInventoryRenderingHelper;

@SideOnly(Side.CLIENT)
public abstract class ModInventoryModelHelperBase implements IModInventoryRenderingHelper {
   protected final IModInfo _modinfo;

   protected ModInventoryModelHelperBase(@Nonnull IModInfo modinfo) {
      Validate.notNull(modinfo);
      this._modinfo = modinfo;
   }

   protected final String mod_id() {
      return this._modinfo.mod_id();
   }

   protected ResourceLocation mod_resource(String id) {
      return new ResourceLocation(this.mod_id(), id);
   }

   protected final Item findItem_(String oid) {
      return (Item)GameRegistry.findRegistry(Item.class).getValue(this.mod_resource(oid));
   }

   protected final Item findBlock_(String oid) {
      Block block = (Block)GameRegistry.findRegistry(Block.class).getValue(this.mod_resource(oid));
      return block == null ? Items.AIR : Item.getItemFromBlock(block);
   }

   protected final Block findBlockonly_(String oid) {
      return (Block)GameRegistry.findRegistry(Block.class).getValue(this.mod_resource(oid));
   }

   private void checkNonrenderingBlockProperties(Block singleton) {
      if (singleton instanceof IModBlock) {
         IProperty<?>[] properties = ((IModBlock)singleton).getNonRenderingProperties();
         if (properties != null) {
            IStateMapper custom_mapper = (new Builder()).ignore(properties).build();
            ModelLoader.setCustomStateMapper(singleton, custom_mapper);
         }
      }

   }

   public final void doRenderSetupOrFail(String oid, Item singleton) {
      if (singleton instanceof IMultiTextured) {
         String[] fmlids = ((IMultiTextured)singleton).getInventoryRenderingNames();
         int[] metas = ((IMultiTextured)singleton).getInventoryRenderingMetas();
         this.doRenderSetupOrFail(oid, singleton, fmlids, metas);
      } else {
         Item registered = this.findItem_(oid);
         Validate.validState(SharedGlue.isDefined(registered), "Mod item '%s' not registered with MC+FML runtimes (cached name=%s)", new Object[]{oid, singleton.getUnlocalizedName()});
         if (singleton instanceof Gidable) {
            String subdir = ((Gidable)singleton).gid(singleton);
            if (subdir != null && !subdir.isEmpty()) {
               oid = subdir + "/" + oid;
            }
         }

         ModelResourceLocation model = new ModelResourceLocation(this.mod_id() + ":" + oid, "inventory");
         ModelLoader.setCustomModelResourceLocation(singleton, 0, model);
      }

   }

   public final void doRenderSetupOrFail(String oid, Block singleton) {
      if (singleton instanceof IMultiTextured) {
         String[] fmlids = ((IMultiTextured)singleton).getInventoryRenderingNames();
         int[] metas = ((IMultiTextured)singleton).getInventoryRenderingMetas();
         this.doRenderSetupOrFail(oid, singleton, fmlids, metas);
      } else {
         Item registered = this.findBlock_(oid);
         Validate.validState(SharedGlue.isDefined(registered), "Mod block '%s' not registered with MC+FML runtimes (cached name=%s)", new Object[]{oid, singleton.getUnlocalizedName()});
         this.checkNonrenderingBlockProperties(singleton);
         if (singleton instanceof Gidable) {
            String subdir = ((Gidable)singleton).gid(singleton);
            if (subdir != null && !subdir.isEmpty()) {
               oid = subdir + "/" + oid;
            }
         }

         ModelResourceLocation model = new ModelResourceLocation(this.mod_id() + ":" + oid, "inventory");
         ModelLoader.setCustomModelResourceLocation(registered, 0, model);
      }

   }

   public final void doRenderSetupOrFail(String oid, Item singleton, String[] fmlids, int[] metas) {
      Validate.validState(fmlids != null && metas != null, "A valid set of meta information is required", new Object[0]);
      Item registered = this.findItem_(oid);
      Validate.validState(SharedGlue.isDefined(registered), "Mod multi-x item '%s' not registered with MC+FML runtimes (cached name=%s)", new Object[]{oid, singleton.getUnlocalizedName()});
      if (fmlids.length > 1) {
         for(int index = 0; index < metas.length; ++index) {
            ModelResourceLocation model = new ModelResourceLocation(fmlids[index], "inventory");
            ModelLoader.setCustomModelResourceLocation(singleton, metas[index], model);
         }
      } else {
         ModelResourceLocation model = new ModelResourceLocation(this.mod_id() + ":" + fmlids[0], "inventory");

         for(int index = 0; index < metas.length; ++index) {
            ModelLoader.setCustomModelResourceLocation(singleton, metas[index], model);
         }
      }

   }

   public final void doRenderSetupOrFail(String oid, Block singleton, String[] fmlids, int[] metas) {
      Validate.validState(fmlids != null && metas != null, "A valid set of meta information is required", new Object[0]);
      Item registered = this.findBlock_(oid);
      Validate.validState(SharedGlue.isDefined(registered), "Mod multi-x block '%s' not registered with MC+FML runtimes (cached name=%s)", new Object[]{oid, singleton.getUnlocalizedName()});
      this.checkNonrenderingBlockProperties(singleton);
      if (fmlids.length > 1) {
         for(int index = 0; index < metas.length; ++index) {
            ModelResourceLocation model = new ModelResourceLocation(fmlids[index], "inventory");
            ModelLoader.setCustomModelResourceLocation(registered, metas[index], model);
         }
      } else {
         ModelResourceLocation model = new ModelResourceLocation(this.mod_id() + ":" + fmlids[0], "inventory");

         for(int index = 0; index < metas.length; ++index) {
            ModelLoader.setCustomModelResourceLocation(registered, metas[index], model);
         }
      }

   }

   public final void doRenderFluidOrFail(String oid, Block singleton, Fluid fluid) {
      Block registered = this.findBlockonly_(oid);
      Validate.validState(registered != null, "Mod fluid (block) '%s' not registered with MC+FML runtimes (cached name=%s)", new Object[]{oid, singleton.getUnlocalizedName()});
      Fluids.StateMapper mapper = new Fluids.StateMapper(this.mod_id(), fluid, true);
      ModelLoader.setCustomStateMapper(registered, mapper);
      Item item = Item.getItemFromBlock(registered);
      if (SharedGlue.isDefined(item)) {
         ModelLoader.registerItemVariants(item, new ResourceLocation[0]);
         ModelLoader.setCustomMeshDefinition(item, mapper);
      }

   }
}
