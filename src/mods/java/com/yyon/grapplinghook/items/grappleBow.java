package com.yyon.grapplinghook.items;

import com.yyon.grapplinghook.ClientProxyClass;
import com.yyon.grapplinghook.CommonProxyClass;
import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.network.DetachSingleHookMessage;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import com.yyon.grapplinghook.network.KeypressMessage;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

public class grappleBow extends Item implements KeypressItem {
   public static HashMap<Entity, grappleArrow> grapplearrows1 = new HashMap();
   public static HashMap<Entity, grappleArrow> grapplearrows2 = new HashMap();

   public grappleBow() {
      this.maxStackSize = 1;
      this.setFull3D();
      this.setUnlocalizedName("grapplinghook");
      this.setMaxDamage(GrappleConfig.getconf().default_durability);
      this.setCreativeTab(grapplemod.tabGrapplemod);
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 72000;
   }

   public boolean hasArrow(Entity entity) {
      grappleArrow arrow1 = this.getArrowLeft(entity);
      grappleArrow arrow2 = this.getArrowRight(entity);
      return arrow1 != null || arrow2 != null;
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      ItemStack mat = new ItemStack(Items.LEATHER, 1);
      return mat != null && OreDictionary.itemMatches(mat, repair, false) ? true : super.getIsRepairable(toRepair, repair);
   }

   public void setArrowLeft(Entity entity, grappleArrow arrow) {
      grapplearrows1.put(entity, arrow);
   }

   public void setArrowRight(Entity entity, grappleArrow arrow) {
      grapplearrows2.put(entity, arrow);
   }

   public grappleArrow getArrowLeft(Entity entity) {
      if (grapplearrows1.containsKey(entity)) {
         grappleArrow arrow = (grappleArrow)grapplearrows1.get(entity);
         if (arrow != null && !arrow.isDead) {
            return arrow;
         }
      }

      return null;
   }

   public grappleArrow getArrowRight(Entity entity) {
      if (grapplearrows2.containsKey(entity)) {
         grappleArrow arrow = (grappleArrow)grapplearrows2.get(entity);
         if (arrow != null && !arrow.isDead) {
            return arrow;
         }
      }

      return null;
   }

   public void dorightclick(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand) {
      if (!worldIn.isRemote) {
         ;
      }

   }

   public void throwBoth(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand) {
      grappleArrow arrow_left = this.getArrowLeft(entityLiving);
      grappleArrow arrow_right = this.getArrowRight(entityLiving);
      if (arrow_left == null && arrow_right == null) {
         GrappleCustomization custom = this.getCustomization(stack);
         double angle = custom.angle;
         double verticalangle = custom.verticalthrowangle;
         if (entityLiving.isSneaking()) {
            angle = custom.sneakingangle;
            verticalangle = custom.sneakingverticalthrowangle;
         }

         if (custom.doublehook && angle != 0.0D) {
            this.throwLeft(stack, worldIn, entityLiving, righthand);
         }

         this.throwRight(stack, worldIn, entityLiving, righthand);
         stack.damageItem(1, entityLiving);
         worldIn.playSound((EntityPlayer)null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 1.0F);
      } else {
         this.detachBoth(entityLiving);
      }
   }

   public boolean throwLeft(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand) {
      GrappleCustomization custom = this.getCustomization(stack);
      double angle = custom.angle;
      double verticalangle = custom.verticalthrowangle;
      if (entityLiving.isSneaking()) {
         angle = custom.sneakingangle;
         verticalangle = custom.sneakingverticalthrowangle;
      }

      vec anglevec = vec.fromAngles(Math.toRadians(-angle), Math.toRadians(verticalangle));
      anglevec = anglevec.rotate_pitch(Math.toRadians((double)(-entityLiving.rotationPitch)));
      anglevec = anglevec.rotate_yaw(Math.toRadians((double)entityLiving.rotationYaw));
      float velx = -MathHelper.sin((float)anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float)anglevec.getPitch() * 0.017453292F);
      float vely = -MathHelper.sin((float)anglevec.getPitch() * 0.017453292F);
      float velz = MathHelper.cos((float)anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float)anglevec.getPitch() * 0.017453292F);
      grappleArrow entityarrow = this.createarrow(stack, worldIn, entityLiving, false, true);
      float extravelocity = (float)vec.motionvec(entityLiving).dist_along(new vec((double)velx, (double)vely, (double)velz));
      if (extravelocity < 0.0F) {
         extravelocity = 0.0F;
      }

      entityarrow.shoot((double)velx, (double)vely, (double)velz, entityarrow.getVelocity() + extravelocity, 0.0F);
      worldIn.spawnEntity(entityarrow);
      this.setArrowLeft(entityLiving, entityarrow);
      return true;
   }

   public void throwRight(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand) {
      GrappleCustomization custom = this.getCustomization(stack);
      double angle = custom.angle;
      double verticalangle = custom.verticalthrowangle;
      if (entityLiving.isSneaking()) {
         angle = custom.sneakingangle;
         verticalangle = custom.sneakingverticalthrowangle;
      }

      if (custom.doublehook && angle != 0.0D) {
         vec anglevec = vec.fromAngles(Math.toRadians(angle), Math.toRadians(verticalangle));
         anglevec = anglevec.rotate_pitch(Math.toRadians((double)(-entityLiving.rotationPitch)));
         anglevec = anglevec.rotate_yaw(Math.toRadians((double)entityLiving.rotationYaw));
         float velx = -MathHelper.sin((float)anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float)anglevec.getPitch() * 0.017453292F);
         float vely = -MathHelper.sin((float)anglevec.getPitch() * 0.017453292F);
         float velz = MathHelper.cos((float)anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float)anglevec.getPitch() * 0.017453292F);
         grappleArrow entityarrow = this.createarrow(stack, worldIn, entityLiving, true, true);
         float extravelocity = (float)vec.motionvec(entityLiving).dist_along(new vec((double)velx, (double)vely, (double)velz));
         if (extravelocity < 0.0F) {
            extravelocity = 0.0F;
         }

         entityarrow.shoot((double)velx, (double)vely, (double)velz, entityarrow.getVelocity() + extravelocity, 0.0F);
         worldIn.spawnEntity(entityarrow);
         this.setArrowRight(entityLiving, entityarrow);
      } else {
         grappleArrow entityarrow = this.createarrow(stack, worldIn, entityLiving, righthand, false);
         vec anglevec = (new vec(0.0D, 0.0D, 1.0D)).rotate_pitch(Math.toRadians(verticalangle));
         anglevec = anglevec.rotate_pitch(Math.toRadians((double)(-entityLiving.rotationPitch)));
         anglevec = anglevec.rotate_yaw(Math.toRadians((double)entityLiving.rotationYaw));
         float velx = -MathHelper.sin((float)anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float)anglevec.getPitch() * 0.017453292F);
         float vely = -MathHelper.sin((float)anglevec.getPitch() * 0.017453292F);
         float velz = MathHelper.cos((float)anglevec.getYaw() * 0.017453292F) * MathHelper.cos((float)anglevec.getPitch() * 0.017453292F);
         float extravelocity = (float)vec.motionvec(entityLiving).dist_along(new vec((double)velx, (double)vely, (double)velz));
         if (extravelocity < 0.0F) {
            extravelocity = 0.0F;
         }

         entityarrow.shoot((double)velx, (double)vely, (double)velz, entityarrow.getVelocity() + extravelocity, 0.0F);
         this.setArrowRight(entityLiving, entityarrow);
         worldIn.spawnEntity(entityarrow);
      }

   }

   public void detachBoth(EntityLivingBase entityLiving) {
      grappleArrow arrow1 = this.getArrowLeft(entityLiving);
      grappleArrow arrow2 = this.getArrowRight(entityLiving);
      this.setArrowLeft(entityLiving, (grappleArrow)null);
      this.setArrowRight(entityLiving, (grappleArrow)null);
      if (arrow1 != null) {
         arrow1.removeServer();
      }

      if (arrow2 != null) {
         arrow2.removeServer();
      }

      int id = entityLiving.getEntityId();
      grapplemod.sendtocorrectclient(new GrappleDetachMessage(id), id, entityLiving.world);
      if (grapplemod.attached.contains(Integer.valueOf(id))) {
         grapplemod.attached.remove(new Integer(id));
      }

   }

   public void detachLeft(EntityLivingBase entityLiving) {
      grappleArrow arrow1 = this.getArrowLeft(entityLiving);
      this.setArrowLeft(entityLiving, (grappleArrow)null);
      if (arrow1 != null) {
         arrow1.removeServer();
      }

      int id = entityLiving.getEntityId();
      if (this.getArrowRight(entityLiving) == null) {
         grapplemod.sendtocorrectclient(new GrappleDetachMessage(id), id, entityLiving.world);
      } else {
         grapplemod.sendtocorrectclient(new DetachSingleHookMessage(id, arrow1.getEntityId()), id, entityLiving.world);
      }

      if (grapplemod.attached.contains(Integer.valueOf(id))) {
         grapplemod.attached.remove(new Integer(id));
      }

   }

   public void detachRight(EntityLivingBase entityLiving) {
      grappleArrow arrow2 = this.getArrowRight(entityLiving);
      this.setArrowRight(entityLiving, (grappleArrow)null);
      if (arrow2 != null) {
         arrow2.removeServer();
      }

      int id = entityLiving.getEntityId();
      if (this.getArrowLeft(entityLiving) == null) {
         grapplemod.sendtocorrectclient(new GrappleDetachMessage(id), id, entityLiving.world);
      } else {
         grapplemod.sendtocorrectclient(new DetachSingleHookMessage(id, arrow2.getEntityId()), id, entityLiving.world);
      }

      if (grapplemod.attached.contains(Integer.valueOf(id))) {
         grapplemod.attached.remove(new Integer(id));
      }

   }

   public double getAngle(EntityLivingBase entity, ItemStack stack) {
      GrappleCustomization custom = this.getCustomization(stack);
      return entity.isSneaking() ? custom.sneakingangle : custom.angle;
   }

   public grappleArrow createarrow(ItemStack stack, World worldIn, EntityLivingBase entityLiving, boolean righthand, boolean isdouble) {
      grappleArrow arrow = new grappleArrow(worldIn, entityLiving, righthand, this.getCustomization(stack), isdouble);
      grapplemod.addarrow(entityLiving.getEntityId(), arrow);
      return arrow;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer entityLiving, EnumHand hand) {
      ItemStack stack = entityLiving.getHeldItem(hand);
      if (!worldIn.isRemote) {
         this.dorightclick(stack, worldIn, entityLiving, hand == EnumHand.MAIN_HAND);
      }

      entityLiving.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, stack);
   }

   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
      if (!worldIn.isRemote) {
         ;
      }

      super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
   }

   public EnumAction getItemUseAction(ItemStack par1ItemStack) {
      return EnumAction.NONE;
   }

   public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
      return true;
   }

   public void onCustomKeyDown(ItemStack stack, EntityPlayer player, KeypressItem.Keys key, boolean ismainhand) {
      if (player.world.isRemote) {
         if (key == KeypressItem.Keys.LAUNCHER) {
            if (this.getCustomization(stack).enderstaff) {
               grapplemod.proxy.launchplayer(player);
            }
         } else if (key != KeypressItem.Keys.THROWLEFT && key != KeypressItem.Keys.THROWRIGHT && key != KeypressItem.Keys.THROWBOTH) {
            if (key == KeypressItem.Keys.ROCKET) {
               GrappleCustomization custom = this.getCustomization(stack);
               if (custom.rocket) {
                  grapplemod.proxy.startrocket(player, custom);
               }
            }
         } else {
            grapplemod.network.sendToServer(new KeypressMessage(key, true));
         }
      } else if (key == KeypressItem.Keys.THROWBOTH) {
         this.throwBoth(stack, player.world, player, ismainhand);
      } else if (key == KeypressItem.Keys.THROWLEFT) {
         grappleArrow arrow1 = this.getArrowLeft(player);
         if (arrow1 != null) {
            this.detachLeft(player);
            return;
         }

         boolean threw = this.throwLeft(stack, player.world, player, ismainhand);
         if (threw) {
            stack.damageItem(1, player);
            player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 1.0F);
         }
      } else if (key == KeypressItem.Keys.THROWRIGHT) {
         grappleArrow arrow2 = this.getArrowRight(player);
         if (arrow2 != null) {
            this.detachRight(player);
            return;
         }

         this.throwRight(stack, player.world, player, ismainhand);
         stack.damageItem(1, player);
         player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 1.0F);
      }

   }

   public void onCustomKeyUp(ItemStack stack, EntityPlayer player, KeypressItem.Keys key, boolean ismainhand) {
      if (player.world.isRemote) {
         if (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT || key == KeypressItem.Keys.THROWBOTH) {
            grapplemod.network.sendToServer(new KeypressMessage(key, false));
         }
      } else {
         GrappleCustomization custom = this.getCustomization(stack);
         if (custom.detachonkeyrelease) {
            grappleArrow arrow_left = this.getArrowLeft(player);
            grappleArrow arrow_right = this.getArrowRight(player);
            if (key == KeypressItem.Keys.THROWBOTH) {
               this.detachBoth(player);
            } else if (key == KeypressItem.Keys.THROWLEFT) {
               if (arrow_left != null) {
                  this.detachLeft(player);
               }
            } else if (key == KeypressItem.Keys.THROWRIGHT && arrow_right != null) {
               this.detachRight(player);
            }
         }
      }

   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      return true;
   }

   public boolean onBlockStartBreak(ItemStack itemstack, BlockPos k, EntityPlayer player) {
      return true;
   }

   public GrappleCustomization getCustomization(ItemStack itemstack) {
      if (itemstack.hasTagCompound()) {
         GrappleCustomization custom = new GrappleCustomization();
         custom.loadNBT(itemstack.getTagCompound());
         return custom;
      } else {
         GrappleCustomization custom = this.getDefaultCustomization();
         NBTTagCompound nbt = custom.writeNBT();
         itemstack.setTagCompound(nbt);
         return custom;
      }
   }

   public GrappleCustomization getDefaultCustomization() {
      return new GrappleCustomization();
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag par4) {
      GrappleCustomization custom = this.getCustomization(stack);
      if (Keyboard.isKeyDown(42)) {
         if (!custom.detachonkeyrelease) {
            list.add(ClientProxyClass.key_boththrow.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.throw.desc"));
            list.add(ClientProxyClass.key_boththrow.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.release.desc"));
            list.add(grapplemod.proxy.localize("grappletooltip.double.desc") + ClientProxyClass.key_boththrow.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.releaseandthrow.desc"));
         } else {
            list.add(ClientProxyClass.key_boththrow.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.throwhold.desc"));
         }

         list.add(grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindForward) + ", " + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindLeft) + ", " + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindBack) + ", " + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindRight) + " " + grapplemod.proxy.localize("grappletooltip.swing.desc"));
         list.add(ClientProxyClass.key_jumpanddetach.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.jump.desc"));
         list.add(ClientProxyClass.key_slow.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.slow.desc"));
         list.add(ClientProxyClass.key_climb.getDisplayName() + " + " + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindForward) + " / " + ClientProxyClass.key_climbup.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.climbup.desc"));
         list.add(ClientProxyClass.key_climb.getDisplayName() + " + " + grapplemod.proxy.getkeyname(CommonProxyClass.keys.keyBindBack) + " / " + ClientProxyClass.key_climbdown.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.climbdown.desc"));
         if (custom.enderstaff) {
            list.add(ClientProxyClass.key_enderlaunch.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.enderlaunch.desc"));
         }

         if (custom.rocket) {
            list.add(ClientProxyClass.key_rocket.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.rocket.desc"));
         }

         if (custom.motor) {
            if (custom.motorwhencrouching && !custom.motorwhennotcrouching) {
               list.add(ClientProxyClass.key_motoronoff.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.motoron.desc"));
            } else if (!custom.motorwhencrouching && custom.motorwhennotcrouching) {
               list.add(ClientProxyClass.key_motoronoff.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.motoroff.desc"));
            }
         }

         if (custom.doublehook) {
            if (!custom.detachonkeyrelease) {
               list.add(ClientProxyClass.key_leftthrow.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.throwleft.desc"));
               list.add(ClientProxyClass.key_rightthrow.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.throwright.desc"));
            } else {
               list.add(ClientProxyClass.key_leftthrow.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.throwlefthold.desc"));
               list.add(ClientProxyClass.key_rightthrow.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.throwrighthold.desc"));
            }
         } else {
            list.add(ClientProxyClass.key_rightthrow.getDisplayName() + " " + grapplemod.proxy.localize("grappletooltip.throwalt.desc"));
         }
      } else if (Keyboard.isKeyDown(29)) {
         for(String option : GrappleCustomization.booleanoptions) {
            if (custom.isoptionvalid(option) && custom.getBoolean(option)) {
               list.add(grapplemod.proxy.localize(custom.getName(option)));
            }
         }

         for(String option : GrappleCustomization.doubleoptions) {
            if (custom.isoptionvalid(option)) {
               list.add(grapplemod.proxy.localize(custom.getName(option)) + ": " + Math.floor(custom.getDouble(option) * 100.0D) / 100.0D);
            }
         }
      } else {
         if (custom.doublehook) {
            list.add(grapplemod.proxy.localize(custom.getName("doublehook")));
         }

         if (custom.motor) {
            if (custom.smartmotor) {
               list.add(grapplemod.proxy.localize(custom.getName("smartmotor")));
            } else {
               list.add(grapplemod.proxy.localize(custom.getName("motor")));
            }
         }

         if (custom.enderstaff) {
            list.add(grapplemod.proxy.localize(custom.getName("enderstaff")));
         }

         if (custom.rocket) {
            list.add(grapplemod.proxy.localize(custom.getName("rocket")));
         }

         if (custom.attract) {
            list.add(grapplemod.proxy.localize(custom.getName("attract")));
         }

         if (custom.repel) {
            list.add(grapplemod.proxy.localize(custom.getName("repel")));
         }

         list.add("");
         list.add(grapplemod.proxy.localize("grappletooltip.shiftcontrols.desc"));
         list.add(grapplemod.proxy.localize("grappletooltip.controlconfiguration.desc"));
      }

   }

   @SideOnly(Side.CLIENT)
   public ItemStack getDefaultInstance() {
      ItemStack stack = new ItemStack(this);
      this.getCustomization(stack);
      return stack;
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         ItemStack stack = new ItemStack(this);
         this.getCustomization(stack);
         items.add(stack);
      }

   }

   public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
      int id = player.getEntityId();
      grapplemod.sendtocorrectclient(new GrappleDetachMessage(id), id, player.world);
      if (grapplemod.attached.contains(Integer.valueOf(id))) {
         grapplemod.attached.remove(Integer.valueOf(id));
      }

      if (grapplearrows1.containsKey(player)) {
         grappleArrow arrow1 = (grappleArrow)grapplearrows1.get(player);
         this.setArrowLeft(player, (grappleArrow)null);
         if (arrow1 != null) {
            arrow1.removeServer();
         }
      }

      if (grapplearrows2.containsKey(player)) {
         grappleArrow arrow2 = (grappleArrow)grapplearrows2.get(player);
         this.setArrowLeft(player, (grappleArrow)null);
         if (arrow2 != null) {
            arrow2.removeServer();
         }
      }

      return super.onDroppedByPlayer(item, player);
   }
}
