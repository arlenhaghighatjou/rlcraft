package com.lycanitesmobs.core.item;

import com.google.common.collect.Multimap;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.info.ModInfo;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Interface(
   iface = "thaumcraft.api.research.IScanThing",
   modid = "thaumcraft",
   striprefs = true
)
public class ItemBase extends Item {
   public static int DESCRIPTION_WIDTH = 200;
   public String itemName = "unamed_item";
   public ModInfo modInfo = LycanitesMobs.modInfo;

   public ItemBase() {
      this.setMaxStackSize(64);
   }

   public void setup() {
      this.setRegistryName(this.modInfo.modid, this.itemName);
      this.setUnlocalizedName(this.itemName);
      this.setCreativeTab(LycanitesMobs.itemsTab);
   }

   public String getItemStackDisplayName(ItemStack stack) {
      return LanguageManager.translate(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
   }

   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      String description = this.getDescription(stack, worldIn, tooltip, flagIn);
      if (!"".equalsIgnoreCase(description)) {
         FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

         for(Object formattedDescription : fontRenderer.listFormattedStringToWidth(description, DESCRIPTION_WIDTH)) {
            if (formattedDescription instanceof String) {
               tooltip.add("\u00a7a" + formattedDescription);
            }
         }
      }

      super.addInformation(stack, worldIn, tooltip, flagIn);
   }

   public String getDescription(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      return LanguageManager.translate(this.getUnlocalizedName() + ".description");
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
      return super.getAttributeModifiers(slot, stack);
   }

   public NBTTagCompound getTagCompound(ItemStack itemStack) {
      return itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
   }

   public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
      super.onUpdate(itemStack, world, entity, par4, par5);
   }

   public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      return super.onItemUse(playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ);
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
   }

   public void onItemLeftClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
   }

   public void onUsingTick(ItemStack itemStack, EntityLivingBase entity, int useRemaining) {
      super.onUsingTick(itemStack, entity, useRemaining);
   }

   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
      super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
   }

   public EnumAction getItemUseAction(ItemStack itemStack) {
      return super.getItemUseAction(itemStack);
   }

   public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
      return false;
   }

   public int getItemEnchantability() {
      return 0;
   }

   public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
      return super.getIsRepairable(itemStack, repairStack);
   }

   public void playSound(World world, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch) {
      world.playSound((EntityPlayer)null, x, y, z, sound, category, volume, pitch);
   }

   public void playSound(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
      world.playSound((EntityPlayer)null, pos, sound, category, volume, pitch);
   }

   @SideOnly(Side.CLIENT)
   public boolean isFull3D() {
      return super.isFull3D();
   }

   public ModelResourceLocation getModelResourceLocation() {
      return new ModelResourceLocation(this.getRegistryName(), "inventory");
   }

   public boolean useItemColors() {
      return false;
   }

   public int getColorFromItemstack(ItemStack itemStack, int tintIndex) {
      return 16777215;
   }

   @Method(
      modid = "thaumcraft"
   )
   public boolean checkThing(EntityPlayer player, Object obj) {
      return false;
   }

   @Method(
      modid = "thaumcraft"
   )
   public String getResearchKey(EntityPlayer player, Object object) {
      return null;
   }
}
