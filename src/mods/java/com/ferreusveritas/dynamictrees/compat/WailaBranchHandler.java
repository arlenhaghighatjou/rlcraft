package com.ferreusveritas.dynamictrees.compat;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockTrunkShell;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeNetVolume;
import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.List;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class WailaBranchHandler implements IWailaDataProvider {
   private BlockPos lastPos = BlockPos.ORIGIN;
   private Species lastSpecies = Species.NULLSPECIES;
   private float lastVolume = 0.0F;

   public WailaBranchHandler() {
   }

   public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
      if (WailaOther.invalid) {
         this.lastPos = BlockPos.ORIGIN;
         this.lastSpecies = Species.NULLSPECIES;
         this.lastVolume = 0.0F;
         WailaOther.invalid = false;
      }

      NBTTagCompound nbtData = accessor.getNBTData();
      BlockPos pos = accessor.getPosition();
      Species species = Species.NULLSPECIES;
      if (nbtData.hasKey("species")) {
         species = TreeRegistry.findSpecies(new ResourceLocation(nbtData.getString("species")));
      }

      if (species == Species.NULLSPECIES && this.lastPos.equals(pos)) {
         species = this.lastSpecies;
      }

      if (species == Species.NULLSPECIES) {
         species = this.getWailaSpecies(accessor.getWorld(), pos);
      }

      if (!species.useDefaultWailaBody()) {
         return tooltip;
      } else {
         if (!this.lastPos.equals(pos)) {
            this.lastVolume = this.getTreeVolume(accessor.getWorld(), pos);
         }

         this.lastSpecies = species;
         this.lastPos = pos;
         if (species != Species.NULLSPECIES) {
            if (species.showSpeciesOnWaila()) {
               tooltip.add("Species: " + species.getLocalizedName());
            }

            if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips) {
               tooltip.add(TextFormatting.DARK_GRAY + species.getRegistryName().toString());
            }

            String renderString = "";
            ItemStack seedStack = species.getSeedStack(1);
            String seedName = seedStack.getItem().getRegistryName().toString();
            renderString = renderString + SpecialChars.getRenderString("waila.stack", new String[]{"1", seedName, String.valueOf(1), String.valueOf(seedStack.getItemDamage())});
            if (this.lastVolume > 0.0F) {
               Species.LogsAndSticks las = species.getLogsAndSticks(this.lastVolume);
               if (las.logs > 0) {
                  ItemStack logStack = species.getFamily().getPrimitiveLogItemStack(1);
                  String logName = logStack.getItem().getRegistryName().toString();
                  renderString = renderString + SpecialChars.getRenderString("waila.stack", new String[]{"1", logName, String.valueOf(las.logs), String.valueOf(logStack.getItemDamage())});
               }

               if (las.sticks > 0) {
                  ItemStack stickStack = species.getFamily().getStick(1);
                  String stickName = stickStack.getItem().getRegistryName().toString();
                  renderString = renderString + SpecialChars.getRenderString("waila.stack", new String[]{"1", stickName, String.valueOf(las.sticks), String.valueOf(stickStack.getItemDamage())});
               }
            }

            tooltip.add(renderString);
         }

         return tooltip;
      }
   }

   private float getTreeVolume(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      if (block instanceof BlockTrunkShell) {
         BlockTrunkShell.ShellMuse muse = ((BlockTrunkShell)block).getMuse(world, pos);
         if (muse != null) {
            state = muse.state;
            block = state.getBlock();
            pos = muse.pos;
         }
      }

      if (block instanceof BlockBranch) {
         BlockBranch branch = (BlockBranch)block;
         NodeNetVolume volumeSum = new NodeNetVolume();
         branch.analyse(state, world, pos, (EnumFacing)null, new MapSignal(new INodeInspector[]{volumeSum}));
         return volumeSum.getVolume() * ModConfigs.treeHarvestMultiplier;
      } else {
         return 0.0F;
      }
   }

   public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
      Species species = this.getWailaSpecies(world, pos);
      if (species != Species.NULLSPECIES) {
         tag.setString("species", species.getRegistryName().toString());
      }

      return tag;
   }

   private Species getWailaSpecies(World world, BlockPos pos) {
      return TreeHelper.getBestGuessSpecies(world, pos);
   }
}
