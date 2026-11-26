package levelup2.skills.crafting;

import java.util.function.Consumer;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import levelup2.util.Library;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class FishingLootBonus extends BaseSkill {
   public FishingLootBonus() {
   }

   public String getSkillName() {
      return "levelup:fishbonus";
   }

   public byte getSkillType() {
      return 1;
   }

   public boolean hasSubscription() {
      return true;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.FISHING_ROD);
   }

   public final Consumer<RightClickItem> fishInteractHandler = this::onFishInteract;

   public void register(EventBus bus) {
      bus.register(RightClickItem.class, EventPriority.LOW, false, this.fishInteractHandler);
   }

   public void onFishInteract(RightClickItem evt) {
      if (this.isActive()) {
         if (evt.getResult() != Result.DENY) {
            EntityFishHook hook = evt.getEntityPlayer().fishEntity;
            if (hook != null && hook.caughtEntity == null && hook.ticksCatchable > 0) {
               ItemStack loot = this.getFishingLoot(evt.getWorld(), evt.getEntityPlayer());
               if (!loot.isEmpty()) {
                  ItemStack stack = evt.getEntityPlayer().inventory.getCurrentItem();
                  int i = stack.getCount();
                  int j = stack.getItemDamage();
                  stack.damageItem(1, evt.getEntityPlayer());
                  evt.getEntityPlayer().swingArm(evt.getHand());
                  evt.getEntityPlayer().inventory.setInventorySlotContents(evt.getEntityPlayer().inventory.currentItem, stack);
                  if (evt.getEntityPlayer().capabilities.isCreativeMode) {
                     stack.grow(i);
                     if (stack.isItemStackDamageable()) {
                        stack.setItemDamage(j);
                     }
                  }

                  if (stack.getCount() <= 0) {
                     evt.getEntityPlayer().inventory.setInventorySlotContents(evt.getEntityPlayer().inventory.currentItem, ItemStack.EMPTY);
                     MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(evt.getEntityPlayer(), stack, evt.getHand()));
                  }

                  if (!evt.getEntityPlayer().isHandActive() && evt.getEntityPlayer() instanceof EntityPlayerMP) {
                     ((EntityPlayerMP)evt.getEntityPlayer()).sendContainerToPlayer(evt.getEntityPlayer().inventoryContainer);
                  }

                  evt.setResult(Result.DENY);
                  if (!hook.world.isRemote) {
                     EntityItem item = new EntityItem(hook.world, hook.posX, hook.posY, hook.posZ, loot);
                     double d5 = hook.getAngler().posX - hook.posX;
                     double d6 = hook.getAngler().posY - hook.posY;
                     double d7 = hook.getAngler().posZ - hook.posZ;
                     double d8 = (double)MathHelper.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
                     double d9 = 0.1D;
                     item.motionX = d5 * d9;
                     item.motionY = d6 * d9 + (double)MathHelper.sqrt(d8) * 0.08D;
                     item.motionZ = d7 * d9;
                     hook.world.spawnEntity(item);
                     hook.getAngler().world.spawnEntity(new EntityXPOrb(hook.getAngler().world, hook.getAngler().posX, hook.getAngler().posY + 0.5D, hook.getAngler().posZ + 0.5D, evt.getEntityPlayer().getRNG().nextInt(6) + 1));
                  }
               }
            }
         }

      }
   }

   private ItemStack getFishingLoot(World world, EntityPlayer player) {
      if (!world.isRemote && player.getRNG().nextDouble() <= (double)SkillRegistry.getSkillLevel(player, this.getSkillName()) * 0.05D) {
         Builder build = new Builder((WorldServer)world);
         build.withLuck((float)EnchantmentHelper.getMaxEnchantmentLevel(Enchantment.getEnchantmentByLocation("luck_of_the_sea"), player) + player.getLuck());
         return ((ItemStack)Library.getLootManager().getLootTableFromLocation(new ResourceLocation("levelup", "fishing/fishing_loot")).generateLootForPools(player.getRNG(), build.build()).get(0)).copy();
      } else {
         return ItemStack.EMPTY;
      }
   }
}
