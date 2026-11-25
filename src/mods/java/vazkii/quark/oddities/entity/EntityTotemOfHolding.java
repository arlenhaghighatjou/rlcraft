package vazkii.quark.oddities.entity;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.quark.oddities.feature.TotemOfHolding;

public class EntityTotemOfHolding extends Entity {
   private static final String TAG_ITEMS = "storedItems";
   private static final String TAG_DYING = "dying";
   private static final String TAG_OWNER = "owner";
   private static final DataParameter<Boolean> DYING = EntityDataManager.createKey(EntityTotemOfHolding.class, DataSerializers.BOOLEAN);
   public static final int DEATH_TIME = 40;
   private int deathTicks = 0;
   private String owner;
   private List<ItemStack> storedItems = new LinkedList();

   public EntityTotemOfHolding(World worldIn) {
      super(worldIn);
      this.isImmuneToFire = true;
      this.setSize(0.5F, 1.0F);
   }

   protected void entityInit() {
      this.dataManager.register(DYING, Boolean.valueOf(false));
   }

   public void addItem(ItemStack stack) {
      this.storedItems.add(stack);
   }

   public void setOwner(EntityPlayer player) {
      this.owner = EntityPlayer.getUUID(player.getGameProfile()).toString();
   }

   EntityPlayer getOwnerEntity() {
      for(EntityPlayer player : this.world.playerEntities) {
         String uuid = EntityPlayer.getUUID(player.getGameProfile()).toString();
         if (uuid.equals(this.owner)) {
            return player;
         }
      }

      return null;
   }

   public boolean hitByEntity(Entity e) {
      if (!this.world.isRemote && e instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)e;
         if (!TotemOfHolding.anyoneCollect && !player.isCreative()) {
            EntityPlayer owner = this.getOwnerEntity();
            if (e != owner) {
               return false;
            }
         }

         int drops = Math.min(this.storedItems.size(), 3 + this.world.rand.nextInt(4));

         for(int i = 0; i < drops; ++i) {
            ItemStack stack = (ItemStack)this.storedItems.remove(0);
            if (stack.getItem() instanceof ItemArmor) {
               ItemArmor armor = (ItemArmor)stack.getItem();
               EntityEquipmentSlot slot = armor.armorType;
               ItemStack curr = player.getItemStackFromSlot(slot);
               if (curr.isEmpty()) {
                  player.setItemStackToSlot(slot, stack);
                  stack = null;
               } else if (EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, curr) == 0) {
                  player.setItemStackToSlot(slot, stack);
                  stack = curr;
               }
            }

            if (stack != null && !player.addItemStackToInventory(stack)) {
               this.entityDropItem(stack, 0.0F);
            }
         }

         if (this.world instanceof WorldServer) {
            ((WorldServer)this.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, false, this.posX, this.posY + 0.5D, this.posZ, drops, 0.1D, 0.5D, 0.1D, 0.0D, new int[0]);
            ((WorldServer)this.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC, false, this.posX, this.posY + 0.5D, this.posZ, drops, 0.4D, 0.5D, 0.4D, 0.0D, new int[0]);
         }
      }

      return false;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public void onEntityUpdate() {
      super.onEntityUpdate();
      if (!this.isDead) {
         if (TotemOfHolding.darkSoulsMode) {
            EntityPlayer owner = this.getOwnerEntity();
            if (owner != null && !this.world.isRemote) {
               String ownerTotem = TotemOfHolding.getTotemUUID(owner);
               if (!this.getUniqueID().toString().equals(ownerTotem)) {
                  this.dropEverythingAndDie();
               }
            }
         }

         if (this.storedItems.isEmpty() && !this.world.isRemote) {
            this.dataManager.set(DYING, Boolean.valueOf(true));
         }

         if (this.isDying()) {
            if (this.deathTicks > 40) {
               this.setDead();
            } else {
               ++this.deathTicks;
            }
         } else if (this.world.isRemote) {
            this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY + (Math.random() - 0.5D) * 0.2D, this.posZ, Math.random() - 0.5D, Math.random() - 0.5D, Math.random() - 0.5D, new int[0]);
         }

      }
   }

   private void dropEverythingAndDie() {
      if (!TotemOfHolding.destroyItems) {
         for(ItemStack storedItem : this.storedItems) {
            this.entityDropItem(storedItem, 0.0F);
         }
      }

      this.storedItems.clear();
      this.setDead();
   }

   public int getDeathTicks() {
      return this.deathTicks;
   }

   public boolean isDying() {
      return ((Boolean)this.dataManager.get(DYING)).booleanValue();
   }

   protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {
      NBTTagList list = compound.getTagList("storedItems", 10);
      this.storedItems = new LinkedList();

      for(int i = 0; i < list.tagCount(); ++i) {
         NBTTagCompound cmp = list.getCompoundTagAt(i);
         ItemStack stack = new ItemStack(cmp);
         this.storedItems.add(stack);
      }

      boolean dying = compound.getBoolean("dying");
      this.dataManager.set(DYING, Boolean.valueOf(dying));
      this.owner = compound.getString("owner");
   }

   protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {
      NBTTagList list = new NBTTagList();

      for(ItemStack stack : this.storedItems) {
         list.appendTag(stack.serializeNBT());
      }

      compound.setTag("storedItems", list);
      compound.setBoolean("dying", this.isDying());
      if (this.owner != null) {
         compound.setString("owner", this.owner);
      }

   }
}
