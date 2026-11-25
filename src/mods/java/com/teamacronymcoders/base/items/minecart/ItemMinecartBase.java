package com.teamacronymcoders.base.items.minecart;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.IModAware;
import com.teamacronymcoders.base.client.models.IHasModel;
import com.teamacronymcoders.base.entities.EntityMinecartBase;
import com.teamacronymcoders.base.util.ItemStackUtils;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityMinecart.Type;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ItemMinecartBase extends ItemMinecart implements IHasModel, IModAware {
   private IBaseMod mod;
   private String name;

   public ItemMinecartBase(String name) {
      super(Type.TNT);
      this.name = "minecart_" + name;
      this.setUnlocalizedName(this.name);
      this.hasSubtypes = true;
      BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new DispenseItemMinecartBase());
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos blockPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack itemStack = player.getHeldItem(hand);
      return this.placeCart(itemStack, world, blockPos, this.getEntityFromItem(world, itemStack));
   }

   public EnumActionResult placeCart(ItemStack itemStack, World world, BlockPos blockPos, EntityMinecartBase entityMinecart) {
      if (ItemStackUtils.isValid(itemStack) && BlockRailBase.isRailBlock(world, blockPos)) {
         if (itemStack.hasDisplayName()) {
            entityMinecart.setCustomNameTag(itemStack.getDisplayName());
         }

         if (!world.isRemote) {
            entityMinecart.setPosition((double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F));
            world.spawnEntity(entityMinecart);
         }

         itemStack.shrink(1);
         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }

   public List<String> getModelNames(List<String> modelNames) {
      modelNames.add(this.name);
      return modelNames;
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         items.addAll(this.getAllSubItems(Lists.newArrayList()));
      }

   }

   public Item getItem() {
      return this;
   }

   public IBaseMod getMod() {
      return this.mod;
   }

   public void setMod(IBaseMod mod) {
      this.mod = mod;
   }

   @Nonnull
   public abstract EntityMinecartBase getEntityFromItem(World var1, ItemStack var2);
}
