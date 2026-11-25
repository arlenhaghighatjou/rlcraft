package noppes.vc.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.EntityMagicProjectile;
import noppes.vc.EntityProjectile;
import noppes.vc.VCItems;
import noppes.vc.VCTabs;
import noppes.vc.VCWeapons;
import noppes.vc.VariedCommodities;
import noppes.vc.client.VCSounds;
import noppes.vc.enchants.VCEnchant;

public class ItemStaff extends ItemBasic {
   private VCToolMaterial material;

   public ItemStaff(VCToolMaterial material) {
      this.material = material;
      this.setCreativeTab(VCTabs.WEAPONS);
      this.setFull3D();
      this.setMaxStackSize(1);
      this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
            if (entityIn == null) {
               return 0.0F;
            } else {
               ItemStack itemstack = entityIn.getActiveItemStack();
               return itemstack != null && itemstack.getItem() == ItemStaff.this ? (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
            }
         }
      });
      this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
            return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
         }
      });
   }

   public boolean isEnchantable(ItemStack stack) {
      return true;
   }

   public void onPlayerStoppedUsing(final ItemStack stack, World worldObj, EntityLivingBase entity, int par4) {
      if (!worldObj.isRemote) {
         if (stack.getTagCompound() != null) {
            Entity proj = ((WorldServer)worldObj).getEntityByID(stack.getTagCompound().getInteger("MagicProjectile"));
            if (proj != null && proj instanceof EntityProjectile) {
               EntityProjectile item = (EntityProjectile)proj;
               item.callback = new EntityProjectile.IProjectileCallback() {
                  public boolean onImpact(EntityProjectile entityProjectile, BlockPos pos, Entity entity) {
                     if (entity != null && entity instanceof EntityLivingBase) {
                        EntityLivingBase living = (EntityLivingBase)entity;
                        int confusion = VCEnchant.getLevel(VCEnchant.Confusion, stack);
                        if (confusion > 0 && living.getRNG().nextInt(4) < confusion) {
                           living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 100));
                        }

                        int poison = VCEnchant.getLevel(VCEnchant.Poison, stack);
                        if (poison > 0 && living.getRNG().nextInt(4) < poison) {
                           living.addPotionEffect(new PotionEffect(MobEffects.POISON, 100));
                        }

                        return false;
                     } else {
                        return false;
                     }
                  }
               };
               item.explosiveDamage = false;
               item.explosiveRadius = 1;
               item.prevRotationYaw = item.rotationYaw = entity.rotationYaw;
               item.prevRotationPitch = item.rotationPitch = entity.rotationPitch;
               item.shoot(2.0F);
               playSound(entity, VCSounds.MAGIC_SHOT, 1.0F, 1.0F);
            }
         }
      }
   }

   public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
      int tick = this.getMaxItemUseDuration(stack) - count;
      if (entity.world.isRemote) {
         this.spawnParticle(stack, entity);
      } else {
         int chargeTime = 20 + this.material.getHarvestLevel() * 8;
         if (tick == chargeTime) {
            if (entity instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)entity;
               if (!player.capabilities.isCreativeMode && !this.hasInfinite(stack)) {
                  if (!this.hasItem(player, VCWeapons.mana)) {
                     return;
                  }

                  this.consumeItem(player, VCWeapons.mana);
               }
            }

            playSound(entity, VCSounds.MAGIC_CHARGE, 1.0F, 1.0F);
            int damage = 6 + this.material.getDamageVsEntity() + entity.world.rand.nextInt(4);
            damage = (int)((float)damage + (float)(damage * VCEnchant.getLevel(VCEnchant.Damage, stack)) * 0.5F);
            EntityProjectile projectile = new EntityMagicProjectile(entity.world, entity, this.getProjectile(stack));
            projectile.damage = (float)damage;
            projectile.setSpeed(25);
            double dx = (double)(-MathHelper.sin((float)((double)(entity.rotationYaw / 180.0F) * 3.141592653589793D)) * MathHelper.cos((float)((double)(entity.rotationPitch / 180.0F) * 3.141592653589793D)));
            double dz = (double)(MathHelper.cos((float)((double)(entity.rotationYaw / 180.0F) * 3.141592653589793D)) * MathHelper.cos((float)((double)(entity.rotationPitch / 180.0F) * 3.141592653589793D)));
            projectile.setPosition(entity.posX + dx * 0.8D, entity.posY + 1.5D - (double)(entity.rotationPitch / 80.0F), entity.posZ + dz * 0.8D);
            entity.world.spawnEntity(projectile);
            stack.setTagInfo("MagicProjectile", new NBTTagInt(projectile.getEntityId()));
         }

         if (tick > chargeTime && stack.getTagCompound() != null) {
            Entity proj = ((WorldServer)entity.world).getEntityByID(stack.getTagCompound().getInteger("MagicProjectile"));
            if (proj == null || !(proj instanceof EntityProjectile)) {
               return;
            }

            EntityProjectile item = (EntityProjectile)proj;
            item.ticksInAir = 0;
            double dx = (double)(-MathHelper.sin((float)((double)(entity.rotationYaw / 180.0F) * 3.141592653589793D)) * MathHelper.cos((float)((double)(entity.rotationPitch / 180.0F) * 3.141592653589793D)));
            double dz = (double)(MathHelper.cos((float)((double)(entity.rotationYaw / 180.0F) * 3.141592653589793D)) * MathHelper.cos((float)((double)(entity.rotationPitch / 180.0F) * 3.141592653589793D)));
            item.setPosition(entity.posX + dx * 0.8D, entity.posY + 1.5D - (double)(entity.rotationPitch / 80.0F), entity.posZ + dz * 0.8D);
         }

      }
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      player.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }

   public int getMaxItemUseDuration(ItemStack par1ItemStack) {
      return 72000;
   }

   public EnumAction getItemUseAction(ItemStack par1ItemStack) {
      return EnumAction.BOW;
   }

   public ItemStack getProjectile(ItemStack stack) {
      if (stack.getItem() == VCWeapons.wooden_staff) {
         return new ItemStack(VCItems.spell_nature);
      } else if (stack.getItem() != VCWeapons.stone_staff && stack.getItem() != VCWeapons.demonic_staff) {
         if (stack.getItem() != VCWeapons.iron_staff && stack.getItem() != VCWeapons.mithril_staff) {
            if (stack.getItem() == VCWeapons.bronze_staff) {
               return new ItemStack(VCItems.spell_lightning);
            } else if (stack.getItem() == VCWeapons.golden_staff) {
               return new ItemStack(VCItems.spell_fire);
            } else if (stack.getItem() != VCWeapons.diamond_staff && stack.getItem() != VCWeapons.frost_staff) {
               return stack.getItem() == VCWeapons.emerald_staff ? new ItemStack(VCItems.spell_arcane) : new ItemStack(VCItems.orb, 1, stack.getItemDamage());
            } else {
               return new ItemStack(VCItems.spell_ice);
            }
         } else {
            return new ItemStack(VCItems.spell_holy);
         }
      } else {
         return new ItemStack(VCItems.spell_dark);
      }
   }

   public void spawnParticle(ItemStack stack, EntityLivingBase entity) {
      if (stack.getItem() == VCWeapons.wooden_staff) {
         VariedCommodities.proxy.spawnParticle(entity, "Spell", Integer.valueOf(5), Integer.valueOf(2));
         VariedCommodities.proxy.spawnParticle(entity, "Spell", Integer.valueOf(12), Integer.valueOf(2));
      } else if (stack.getItem() != VCWeapons.stone_staff && stack.getItem() != VCWeapons.demonic_staff) {
         if (stack.getItem() == VCWeapons.bronze_staff) {
            VariedCommodities.proxy.spawnParticle(entity, "Spell", 8648694, Integer.valueOf(2));
            VariedCommodities.proxy.spawnParticle(entity, "Spell", 6091007, Integer.valueOf(2));
         } else if (stack.getItem() != VCWeapons.iron_staff && stack.getItem() != VCWeapons.mithril_staff) {
            if (stack.getItem() == VCWeapons.golden_staff) {
               VariedCommodities.proxy.spawnParticle(entity, "Spell", Integer.valueOf(1), Integer.valueOf(2));
               VariedCommodities.proxy.spawnParticle(entity, "Spell", Integer.valueOf(14), Integer.valueOf(2));
            } else if (stack.getItem() != VCWeapons.diamond_staff && stack.getItem() != VCWeapons.frost_staff) {
               if (stack.getItem() == VCWeapons.emerald_staff) {
                  VariedCommodities.proxy.spawnParticle(entity, "Spell", 16761831, Integer.valueOf(2));
                  VariedCommodities.proxy.spawnParticle(entity, "Spell", 16487167, Integer.valueOf(2));
               }
            } else {
               VariedCommodities.proxy.spawnParticle(entity, "Spell", 9756653, Integer.valueOf(2));
               VariedCommodities.proxy.spawnParticle(entity, "Spell", 4503295, Integer.valueOf(2));
            }
         } else {
            VariedCommodities.proxy.spawnParticle(entity, "Spell", 16580553, Integer.valueOf(2));
            VariedCommodities.proxy.spawnParticle(entity, "Spell", 15728535, Integer.valueOf(2));
         }
      } else {
         VariedCommodities.proxy.spawnParticle(entity, "Spell", 5649239, Integer.valueOf(2));
         VariedCommodities.proxy.spawnParticle(entity, "Spell", 4400964, Integer.valueOf(2));
      }

   }

   public int getItemEnchantability() {
      return this.material.getEnchantability();
   }

   public boolean hasInfinite(ItemStack stack) {
      return VCEnchant.getLevel(VCEnchant.Infinite, stack) > 0;
   }
}
