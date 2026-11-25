package net.theawesomegem.fishingmadebetter.common.item;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.theawesomegem.fishingmadebetter.common.capability.world.ChunkCapabilityProvider;
import net.theawesomegem.fishingmadebetter.common.capability.world.IChunkFishingData;
import net.theawesomegem.fishingmadebetter.common.capability.world.PopulationData;
import net.theawesomegem.fishingmadebetter.common.configuration.CustomConfigurationHandler;
import net.theawesomegem.fishingmadebetter.common.data.FishData;
import net.theawesomegem.fishingmadebetter.common.registry.FMBCreativeTab;

public class ItemFishBucket extends Item {
   public ItemFishBucket() {
      this.maxStackSize = 1;
      this.setCreativeTab(FMBCreativeTab.instance);
      this.setRegistryName("fish_bucket");
      this.setUnlocalizedName("fishingmadebetter.fish_bucket");
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (raytraceresult != null && raytraceresult.typeOfHit == Type.BLOCK) {
         if (worldIn.isRemote) {
            return new ActionResult(EnumActionResult.FAIL, itemstack);
         } else {
            String fishId = getFishId(itemstack);
            if (fishId == null) {
               return new ActionResult(EnumActionResult.FAIL, itemstack);
            } else {
               FishData fishData = (FishData)CustomConfigurationHandler.fishDataMap.get(fishId);
               if (fishData == null) {
                  return new ActionResult(EnumActionResult.FAIL, itemstack);
               } else {
                  BlockPos blockpos = raytraceresult.getBlockPos();
                  IChunkFishingData chunkFishingData = this.getChunkFishingData(worldIn.getChunkFromBlockCoords(blockpos));
                  if (chunkFishingData == null) {
                     return new ActionResult(EnumActionResult.FAIL, itemstack);
                  } else {
                     IBlockState blockState = worldIn.getBlockState(blockpos);
                     Material material = blockState.getMaterial();
                     if (material != MaterialLiquid.WATER) {
                        playerIn.sendMessage(new TextComponentTranslation("notif.fishingmadebetter.fish_bucket.only_water", new Object[0]));
                        return new ActionResult(EnumActionResult.FAIL, itemstack);
                     } else {
                        int waterCount = 0;

                        for(BlockPos pos : BlockPos.getAllInBox(blockpos.getX() - 2, blockpos.getY() - 3, blockpos.getZ() - 2, blockpos.getX() + 2, blockpos.getY(), blockpos.getZ() + 2)) {
                           Material mat = worldIn.getBlockState(pos).getMaterial();
                           if (mat == MaterialLiquid.WATER) {
                              ++waterCount;
                           }

                           if (waterCount >= 25) {
                              break;
                           }
                        }

                        if (waterCount < 25) {
                           playerIn.sendMessage(new TextComponentTranslation("notif.fishingmadebetter.fish_bucket.small_water", new Object[0]));
                           return new ActionResult(EnumActionResult.FAIL, itemstack);
                        } else if (fishData.minYLevel <= blockpos.getY() && fishData.maxYLevel >= blockpos.getY()) {
                           Map<String, PopulationData> fishMap = chunkFishingData.getRawFishMap();
                           PopulationData populationData;
                           if (!fishMap.containsKey(fishId)) {
                              populationData = new PopulationData(fishId, 1, 0, worldIn.getTotalWorldTime());
                              fishMap.put(fishId, populationData);
                           } else {
                              populationData = (PopulationData)fishMap.get(fishId);
                              populationData.setQuantity(populationData.getQuantity() + 1);
                           }

                           chunkFishingData.setPopulationData(fishId, populationData, true);
                           worldIn.playSound(playerIn, blockpos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                           playerIn.setHeldItem(handIn, new ItemStack(Items.BUCKET));
                           return super.onItemRightClick(worldIn, playerIn, handIn);
                        } else {
                           playerIn.sendMessage(new TextComponentTranslation("notif.fishingmadebetter.fish_bucket.wrong_altitude", new Object[0]));
                           return new ActionResult(EnumActionResult.FAIL, itemstack);
                        }
                     }
                  }
               }
            }
         }
      } else {
         return new ActionResult(EnumActionResult.PASS, itemstack);
      }
   }

   @SideOnly(Side.CLIENT)
   public void registerItemModel(Item item) {
      ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
   }

   public static ItemStack getItemStack(String fishId) {
      ItemStack itemStack = new ItemStack(ItemManager.FISH_BUCKET, 1);
      NBTTagCompound tagCompound = new NBTTagCompound();
      tagCompound.setString("FishId", fishId);
      itemStack.setTagCompound(tagCompound);
      return itemStack;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack itemStack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      String fishId = getFishId(itemStack);
      if (fishId != null) {
         tooltip.add(TextFormatting.BLUE + I18n.format("item.fishingmadebetter.fish_bucket.tooltip", new Object[0]) + ": " + TextFormatting.BOLD + fishId + TextFormatting.RESET);
      }
   }

   @Nullable
   public static String getFishId(ItemStack itemStack) {
      if (!itemStack.hasTagCompound()) {
         return null;
      } else {
         NBTTagCompound tagCompound = itemStack.getTagCompound();
         return tagCompound.hasKey("FishId") ? tagCompound.getString("FishId") : null;
      }
   }

   @Nullable
   private IChunkFishingData getChunkFishingData(Chunk chunk) {
      return (IChunkFishingData)chunk.getCapability(ChunkCapabilityProvider.CHUNK_FISHING_DATA_CAP, (EnumFacing)null);
   }
}
