package c4.champions.common.item;

import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.IAffix;
import c4.champions.common.capability.CapabilityChampionship;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import c4.champions.common.util.ChampionHelper;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemChampionPlacer extends Item {
   public ItemChampionPlacer() {
      this.setRegistryName(new ResourceLocation("champions", "champion_egg"));
      this.setUnlocalizedName("champions.champion_egg");
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      NBTTagCompound nbttagcompound = stack.getTagCompound();
      if (nbttagcompound != null && nbttagcompound.hasKey("ChampionInfo", 10)) {
         NBTTagCompound compound = nbttagcompound.getCompoundTag("ChampionInfo");
         int tier = compound.getInteger("tier");
         tooltip.add(I18n.format("champions.champion_egg.tooltip.tier", new Object[]{tier}));
         NBTTagList tagList = compound.getTagList("affixes", 8);

         for(int i = 0; i < tagList.tagCount(); ++i) {
            tooltip.add(I18n.format("champions.affix." + tagList.getStringTagAt(i), new Object[0]));
         }
      }

   }

   @Nonnull
   public String getItemStackDisplayName(@Nonnull ItemStack stack) {
      String s = ("" + net.minecraft.util.text.translation.I18n.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
      String s1 = EntityList.getTranslationName(ItemMonsterPlacer.getNamedIdFrom(stack));
      if (s1 != null) {
         s = s + " " + net.minecraft.util.text.translation.I18n.translateToLocal("entity." + s1 + ".name");
      }

      return s;
   }

   @Nonnull
   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (worldIn.isRemote) {
         return EnumActionResult.SUCCESS;
      } else if (!player.canPlayerEdit(pos.offset(facing), facing, itemstack)) {
         return EnumActionResult.FAIL;
      } else {
         BlockPos blockpos = pos.offset(facing);
         double d0 = this.getYOffset(worldIn, blockpos);
         Entity entity = createChampion(worldIn, ItemMonsterPlacer.getNamedIdFrom(itemstack), (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + d0, (double)blockpos.getZ() + 0.5D);
         if (entity != null) {
            if (entity instanceof EntityLivingBase && itemstack.hasDisplayName()) {
               entity.setCustomNameTag(itemstack.getDisplayName());
            }

            ItemMonsterPlacer.applyItemEntityDataToEntity(worldIn, player, itemstack, entity);
            if (ChampionHelper.isValidChampion(entity)) {
               EntityLiving living = (EntityLiving)entity;
               applyItemChampionDataToEntity(worldIn, player, itemstack, living);
               living.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(living)), (IEntityLivingData)null);
               worldIn.spawnEntity(entity);
               living.playLivingSound();
               if (!player.capabilities.isCreativeMode) {
                  itemstack.shrink(1);
               }

               return EnumActionResult.SUCCESS;
            } else {
               return EnumActionResult.FAIL;
            }
         } else {
            return EnumActionResult.SUCCESS;
         }
      }
   }

   protected double getYOffset(World world, BlockPos blockPos) {
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockPos)).expand(0.0D, -1.0D, 0.0D);
      List<AxisAlignedBB> list = world.getCollisionBoxes((Entity)null, axisalignedbb);
      if (list.isEmpty()) {
         return 0.0D;
      } else {
         double d0 = axisalignedbb.minY;

         for(AxisAlignedBB axisalignedbb1 : list) {
            d0 = Math.max(axisalignedbb1.maxY, d0);
         }

         return d0 - (double)blockPos.getY();
      }
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (worldIn.isRemote) {
         return new ActionResult(EnumActionResult.PASS, itemstack);
      } else {
         RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);
         if (raytraceresult != null && raytraceresult.typeOfHit == Type.BLOCK) {
            BlockPos blockpos = raytraceresult.getBlockPos();
            if (!(worldIn.getBlockState(blockpos).getBlock() instanceof BlockLiquid)) {
               return new ActionResult(EnumActionResult.PASS, itemstack);
            } else if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn.canPlayerEdit(blockpos, raytraceresult.sideHit, itemstack)) {
               Entity entity = createChampion(worldIn, ItemMonsterPlacer.getNamedIdFrom(itemstack), (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D);
               if (entity == null) {
                  return new ActionResult(EnumActionResult.PASS, itemstack);
               } else {
                  if (entity instanceof EntityLivingBase && itemstack.hasDisplayName()) {
                     entity.setCustomNameTag(itemstack.getDisplayName());
                  }

                  ItemMonsterPlacer.applyItemEntityDataToEntity(worldIn, playerIn, itemstack, entity);
                  if (ChampionHelper.isValidChampion(entity)) {
                     EntityLiving living = (EntityLiving)entity;
                     applyItemChampionDataToEntity(worldIn, playerIn, itemstack, living);
                     living.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(living)), (IEntityLivingData)null);
                     worldIn.spawnEntity(entity);
                     living.playLivingSound();
                     if (!playerIn.capabilities.isCreativeMode) {
                        itemstack.shrink(1);
                     }

                     return new ActionResult(EnumActionResult.SUCCESS, itemstack);
                  } else {
                     return new ActionResult(EnumActionResult.FAIL, itemstack);
                  }
               }
            } else {
               return new ActionResult(EnumActionResult.FAIL, itemstack);
            }
         } else {
            return new ActionResult(EnumActionResult.PASS, itemstack);
         }
      }
   }

   @Nullable
   public static Entity createChampion(World worldIn, @Nullable ResourceLocation entityID, double x, double y, double z) {
      if (entityID != null && EntityList.ENTITY_EGGS.containsKey(entityID)) {
         Entity entity = null;

         for(int i = 0; i < 1; ++i) {
            entity = EntityList.createEntityByIDFromName(entityID, worldIn);
            if (entity instanceof EntityLiving) {
               EntityLiving entityliving = (EntityLiving)entity;
               entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
               entityliving.rotationYawHead = entityliving.rotationYaw;
               entityliving.renderYawOffset = entityliving.rotationYaw;
            }
         }

         return entity;
      } else {
         return null;
      }
   }

   public static void applyItemChampionDataToEntity(World entityWorld, @Nullable EntityPlayer player, ItemStack stack, @Nullable EntityLiving targetEntity) {
      MinecraftServer minecraftserver = entityWorld.getMinecraftServer();
      if (minecraftserver != null && targetEntity != null) {
         NBTTagCompound nbttagcompound = stack.getTagCompound();
         if (nbttagcompound != null && nbttagcompound.hasKey("ChampionInfo", 10)) {
            if (!entityWorld.isRemote && (player == null || !minecraftserver.getPlayerList().canSendCommands(player.getGameProfile()))) {
               return;
            }

            IChampionship chp = CapabilityChampionship.getChampionship(targetEntity);
            if (chp != null) {
               NBTTagCompound compound = nbttagcompound.getCompoundTag("ChampionInfo");
               Rank rank = RankManager.getRankForTier(compound.getInteger("tier"));
               chp.setRank(rank);
               if (rank.getTier() > 0) {
                  Set<String> affixes = Sets.newHashSet();
                  NBTTagList tagList = compound.getTagList("affixes", 8);

                  for(int i = 0; i < tagList.tagCount(); ++i) {
                     String affix = tagList.getStringTagAt(i);
                     if (AffixRegistry.getAffix(affix) != null) {
                        affixes.add(affix);
                     }
                  }

                  if (affixes.isEmpty()) {
                     chp.setAffixes(ChampionHelper.generateAffixes(rank, targetEntity));
                  } else {
                     chp.setAffixes(affixes);
                  }

                  chp.setName(ChampionHelper.generateRandomName());
                  chp.getRank().applyGrowth(targetEntity);
                  UnmodifiableIterator var14 = chp.getAffixes().iterator();

                  while(var14.hasNext()) {
                     String s = (String)var14.next();
                     IAffix affix = AffixRegistry.getAffix(s);
                     if (affix != null) {
                        affix.onInitialSpawn(targetEntity, chp);
                     }
                  }
               }
            }
         }
      }

   }

   public static void applyEntityInfoToItemStack(ItemStack stack, ResourceLocation entityId, int tier, Set<String> affixes) {
      NBTTagCompound nbttagcompound = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
      NBTTagCompound nbttagcompound1 = new NBTTagCompound();
      nbttagcompound1.setString("id", entityId.toString());
      nbttagcompound.setTag("EntityTag", nbttagcompound1);
      NBTTagCompound nbttagcompound2 = new NBTTagCompound();
      nbttagcompound2.setInteger("tier", tier);
      NBTTagList taglist = new NBTTagList();

      for(String affix : affixes) {
         taglist.appendTag(new NBTTagString(affix));
      }

      nbttagcompound2.setTag("affixes", taglist);
      nbttagcompound.setTag("ChampionInfo", nbttagcompound2);
      stack.setTagCompound(nbttagcompound);
   }
}
