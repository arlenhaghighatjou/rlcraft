package xzeroair.trinkets.traits.abilities;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.client.keybinds.ModKeyBindings;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.items.trinkets.TrinketPolarized;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IHeldAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IKeyBindInterface;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableInventoryAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IToggleAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigPolarizedStone;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class AbilityMagnetic extends Ability implements ITickableAbility, IHeldAbility, ITickableInventoryAbility, IToggleAbility, IKeyBindInterface {
   private static final ConfigPolarizedStone serverConfig;

   public AbilityMagnetic() {
      super("magnetic");
   }

   public void tickAbility(EntityLivingBase entity) {
      if (this.abilityEnabled()) {
         this.collectDrops(entity);
      }

   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean inHand) {
      Capabilities.getTrinketProperties(stack, (prop) -> {
         this.toggleAbility(prop.mainAbility());
      });
   }

   public void collectDrops(EntityLivingBase entity) {
      Predicate<EntityLivingBase> filter = Predicates.and(EntitySelectors.CAN_AI_TARGET, EntitySelectors.IS_ALIVE);
      boolean flag = filter.apply(entity);
      if (flag) {
         AxisAlignedBB bBox = entity.getEntityBoundingBox();
         Predicate<Entity> lootPredicate = Predicates.and(EntitySelectors.IS_ALIVE, (ent) -> {
            return ent instanceof EntityItem || ent instanceof EntityXPOrb && serverConfig.collectXP;
         });
         Predicate<EntityPlayer> otherPlayerPredicate = Predicates.and(new Predicate[]{EntitySelectors.CAN_AI_TARGET, EntitySelectors.IS_ALIVE, (ent) -> {
            if (ent != null && entity != ent && entity.getEntityId() != ent.getEntityId()) {
               return ent.getHeldItemMainhand().getItem() instanceof TrinketPolarized || TrinketHelper.AccessoryCheck(ent, ModItems.trinkets.TrinketPolarized);
            } else {
               return false;
            }
         }});
         List<Entity> Loot = entity.world.getEntitiesWithinAABB(Entity.class, bBox.grow((double)serverConfig.PR.HD, (double)serverConfig.PR.VD, (double)serverConfig.PR.HD), lootPredicate);
         List<EntityPlayer> others = entity.world.getEntitiesWithinAABB(EntityPlayer.class, bBox.grow((double)serverConfig.PR.HD, (double)serverConfig.PR.VD, (double)serverConfig.PR.HD), otherPlayerPredicate);

         for(Entity loot : Loot) {
            double distance = loot.getDistance(entity.posX, entity.posY, entity.posZ);
            boolean someonesCloser = false;

            for(EntityPlayer otherP : others) {
               float dist = loot.getDistance(otherP);
               if ((double)dist < distance) {
                  someonesCloser = true;
               }
            }

            if (!someonesCloser) {
               this.handleLoot(entity, loot);
            }
         }
      }

   }

   private void handleLoot(Entity entity, Entity drop) {
      if (drop instanceof EntityItem || serverConfig.collectXP && drop instanceof EntityXPOrb) {
         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            if (!player.world.isRemote) {
               if (drop instanceof EntityItem) {
                  if (serverConfig.instant_pickup) {
                     this.pickupItem(player, drop);
                  } else {
                     this.pull(drop, entity.posX, entity.posY, entity.posZ);
                  }
               } else if (serverConfig.collectXP && drop instanceof EntityXPOrb) {
                  if (serverConfig.instant_xp) {
                     this.pickupXP(player, drop);
                  } else {
                     this.pull(drop, entity.posX, entity.posY, entity.posZ);
                  }
               } else {
                  this.pull(drop, entity.posX, entity.posY, entity.posZ);
               }
            }
         } else {
            this.pull(drop, entity.posX, entity.posY, entity.posZ);
         }
      }

   }

   private void pickupItem(EntityPlayer player, Entity itemEntity) {
      if (itemEntity instanceof EntityItem) {
         EntityItem item = (EntityItem)itemEntity;
         if (!(item.getItem().getItem() instanceof TrinketPolarized)) {
            item.onCollideWithPlayer(player);
         }
      }

   }

   private void pickupXP(EntityPlayer player, Entity xpOrb) {
      if (xpOrb instanceof EntityXPOrb) {
         EntityXPOrb xp = (EntityXPOrb)xpOrb;
         player.xpCooldown = 0;
         xp.onCollideWithPlayer(player);
      }

   }

   public ItemStack getEnchantedItem(Enchantment enchantment, EntityLivingBase entity) {
      List<ItemStack> list = enchantment.getEntityEquipment(entity);
      if (list.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         List<ItemStack> list1 = Lists.newArrayList();

         for(ItemStack itemstack : list) {
            if (!itemstack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(enchantment, itemstack) > 0) {
               list1.add(itemstack);
            }
         }

         return list1.isEmpty() ? ItemStack.EMPTY : (ItemStack)list1.get(entity.getRNG().nextInt(list1.size()));
      }
   }

   public List<ItemStack> getEnchantedItems(Enchantment enchantment, EntityLivingBase entity) {
      List<ItemStack> list = enchantment.getEntityEquipment(entity);
      List<ItemStack> list1 = Lists.newArrayList();

      for(ItemStack itemstack : list) {
         if (!itemstack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(enchantment, itemstack) > 0) {
            list1.add(itemstack);
         }
      }

      return list1;
   }

   protected int roundAverage(float value) {
      double floor = Math.floor((double)value);
      return (int)floor + (Math.random() < (double)value - floor ? 1 : 0);
   }

   protected void pull(Entity ent, double x, double y, double z) {
      double spd = TrinketsConfig.SERVER.Items.POLARIZED_STONE.Polarized_Stone_Speed;
      double dX = x - 0.5D - (double)ent.getPosition().getX();
      double dY = y - (double)ent.getPosition().getY();
      double dZ = z - 0.5D - (double)ent.getPosition().getZ();
      double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
      double vel = 1.0D - dist / 15.0D;
      if (vel > 0.0D && vel < 0.95D) {
         vel = vel * vel;
         ent.motionX += dX / dist * vel * spd * MathHelper.clamp(dist - 0.5D, 0.0D, 1.0D);
         ent.motionY += dY / dist * vel * spd * 1.25D * MathHelper.clamp(dist - 0.5D, 0.0D, 1.0D);
         ent.motionZ += dZ / dist * vel * spd * MathHelper.clamp(dist - 0.5D, 0.0D, 1.0D);
      }

   }

   protected void push(Entity ent, double x, double y, double z) {
      double spd = TrinketsConfig.SERVER.Items.POLARIZED_STONE.Polarized_Stone_Speed;
      double dX = x - ent.posX;
      double dY = y - ent.posY;
      double dZ = z - ent.posZ;
      double dist = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
      double vel = 1.0D - dist / 15.0D;
      if (vel > 0.0D) {
         vel = vel * vel;
         ent.motionX -= dX / dist * vel * spd;
         ent.motionY -= dY / dist * vel * spd;
         ent.motionZ -= dZ / dist * vel * spd;
      }

   }

   public boolean abilityEnabled() {
      return this.enabled;
   }

   public IToggleAbility toggleAbility(boolean enabled) {
      this.enabled = enabled;
      return this;
   }

   public IToggleAbility toggleAbility(int value) {
      this.value = value;
      return this;
   }

   public boolean onKeyPress(Entity entity, boolean Aux) {
      if (!Aux) {
         boolean enabled = this.abilityEnabled();
         boolean client = entity.world.isRemote;
         this.toggleAbility(!enabled);
         if (client && entity instanceof EntityPlayer) {
            TranslationHelper helper = TranslationHelper.INSTANCE;
            ItemStack s = new ItemStack(ModItems.trinkets.TrinketPolarized);
            String magnetMode = (new TextComponentTranslation(s.getUnlocalizedName() + ".magnetmode", new Object[0])).getFormattedText();
            TranslationHelper.KeyEntry key = new TranslationHelper.OptionEntry("collecttoggle", serverConfig.collectXP, helper.toggleCheckTranslation(!enabled));
            ((EntityPlayer)entity).sendStatusMessage(new TextComponentString(helper.formatAddVariables(magnetMode, key)), true);
         }

         Capabilities.getEntityProperties(entity, (prop) -> {
            AbilityHandler.AbilityHolder holder = prop.getAbilityHandler().getAbilityHolder(this.getRegistryName().toString());
            TrinketHelper.SlotInformation info = holder != null ? holder.getInfo() : null;
            if (info != null && entity instanceof EntityLivingBase) {
               ItemStack stack = info.getStackFromHandler((EntityLivingBase)entity);
               if (stack.getItem() instanceof TrinketPolarized) {
                  Capabilities.getTrinketProperties(stack, (cap) -> {
                     cap.toggleMainAbility(this.abilityEnabled());
                     cap.sendInformationToPlayer((EntityLivingBase)entity, (EntityPlayer)entity);
                  });
               }
            }

         });
      }

      return true;
   }

   @SideOnly(Side.CLIENT)
   public String getKey() {
      return ModKeyBindings.POLARIZED_STONE_ABILITY.getDisplayName();
   }

   @SideOnly(Side.CLIENT)
   public String getAuxKey() {
      return ModKeyBindings.AUX_KEY.getDisplayName();
   }

   public NBTTagCompound saveStorage(NBTTagCompound compound) {
      compound.setBoolean("enabled", this.enabled);
      return compound;
   }

   public void loadStorage(NBTTagCompound compound) {
      if (compound.hasKey("enabled")) {
         this.enabled = compound.getBoolean("enabled");
      }

   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.POLARIZED_STONE;
   }
}
