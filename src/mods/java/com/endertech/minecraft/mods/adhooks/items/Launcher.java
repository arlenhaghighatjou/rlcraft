package com.endertech.minecraft.mods.adhooks.items;

import com.endertech.common.FloatBounds;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.ForgeKeys;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.configs.ForgeKeys.KeyCategories;
import com.endertech.minecraft.forge.entities.ForgeEntity;
import com.endertech.minecraft.forge.items.DamageableItem;
import com.endertech.minecraft.forge.messages.ForgeNetMsg;
import com.endertech.minecraft.forge.messages.ForgeNetMsgHandler;
import com.endertech.minecraft.forge.messages.Tooltip;
import com.endertech.minecraft.forge.units.IUnitWithMeshDefinition;
import com.endertech.minecraft.forge.units.UnitId;
import com.endertech.minecraft.forge.units.UnitVariant;
import com.endertech.minecraft.mods.adhooks.init.HookType;
import com.endertech.minecraft.mods.adhooks.shot.HookShot;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Launcher extends DamageableItem implements IUnitWithMeshDefinition {
   public static final String TAG_HOOKSHOT_ID = "hookShotId";
   public static final FloatBounds REELING_SPEED_BOUNDS = new FloatBounds(0.1F, 4.0F);
   public static final FloatBounds BOOST_STRENGTH_BOUNDS = new FloatBounds(0.0F, 3.0F);
   public static final int DAMAGE_ON_SHOT = 1;
   @SideOnly(Side.CLIENT)
   protected static KeyBinding keyBindUserDrop;
   private Map<KeyBinding, Launcher.WatchedKey> watchedKeys = new ConcurrentHashMap();
   private Set<ItemStack> queuedShots = new HashSet();
   public final HookType hookType;
   protected float reelingSpeed;
   protected float boostStrength;

   public Launcher(ForgeMod mod, String name, UnitConfig config, HookType hookType, int durability, float reelingSpeed, float boostStrength) {
      super(mod, config, name, CreativeTabs.TOOLS, durability, UnitId.from("string"));
      this.hookType = hookType;
      this.reelingSpeed = ForgeConfig.getFloat(config, this.getUnitCategory(), "reelingSpeed", reelingSpeed, REELING_SPEED_BOUNDS, "Defines how fast the launcher reels in the rope.");
      this.boostStrength = ForgeConfig.getFloat(config, this.getUnitCategory(), "boostStrength", boostStrength, BOOST_STRENGTH_BOUNDS, "Defines how high you will be thrown upward when boost is activated");
      this.setUnitVariants(Launcher.Variants.class);
   }

   public float getReelingSpeed() {
      return this.reelingSpeed;
   }

   public boolean canItemEditBlocks() {
      return false;
   }

   public float getBoostStrength() {
      return this.boostStrength;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> lines, ITooltipFlag flagIn) {
      GameSettings gs = ForgeKeys.getGameSettings();
      Tooltip tip = Tooltip.from(this.getClass(), lines);
      if (ForgeKeys.isDown(gs.keyBindSprint)) {
         tip.add(TextFormatting.RED, "hook_strength", Float.valueOf(this.hookType.hook.getStrength()));
         tip.add(TextFormatting.GREEN, "rope_elasticity", Float.valueOf(this.hookType.rope.getElasticity()));
         tip.add(TextFormatting.YELLOW, "rope_max_length", Integer.valueOf(this.hookType.rope.getMaxLength()));
         tip.add(TextFormatting.AQUA, "reeling_speed", Float.valueOf(this.getReelingSpeed()));
         tip.add(TextFormatting.BLUE, "boostup_strength", Float.valueOf(this.getBoostStrength()));
      } else if (ForgeKeys.isDown(gs.keyBindSneak)) {
         tip.add("launch", new Object[]{TextFormatting.RED, ForgeKeys.getName(gs.keyBindAttack)});
         tip.add("reel_unhook", new Object[]{TextFormatting.GREEN, ForgeKeys.getName(gs.keyBindDrop), ForgeKeys.getName(keyBindUserDrop)});
         tip.add("pull", new Object[]{TextFormatting.YELLOW, ForgeKeys.getName(gs.keyBindUseItem)});
         tip.add("loosen", new Object[]{TextFormatting.AQUA, ForgeKeys.getName(gs.keyBindAttack)});
         tip.add("boost_up", new Object[]{TextFormatting.GOLD, ForgeKeys.getName(gs.keyBindDrop), ForgeKeys.getName(keyBindUserDrop)});
         tip.add("release", new Object[]{TextFormatting.LIGHT_PURPLE, ForgeKeys.getName(gs.keyBindAttack), ForgeKeys.getName(gs.keyBindSneak)});
      } else {
         tip.add("more_info", new Object[]{ForgeKeys.getName(gs.keyBindSprint)});
         tip.add("controls", new Object[]{ForgeKeys.getName(gs.keyBindSneak)});
      }

   }

   public static boolean isAttached(ItemStack launcher) {
      return stackHasLauncher(launcher) && launcher.hasTagCompound() && launcher.getTagCompound().hasKey("hookShotId");
   }

   public static boolean isAttachedToHookShot(ItemStack launcher, HookShot hookShot) {
      return isAttached(launcher) && hookShot != null && getAttachedHookShotId(launcher) == hookShot.getEntityId();
   }

   public static int getAttachedHookShotId(ItemStack launcher) {
      return launcher != null && launcher.hasTagCompound() ? launcher.getTagCompound().getInteger("hookShotId") : 0;
   }

   public static boolean stackHasLauncher(ItemStack stack) {
      return stack != null && stack.getItem() instanceof Launcher;
   }

   @Nullable
   public static HookShot findAttachedHookShot(ItemStack launcher, World world) {
      if (isAttached(launcher) && world != null) {
         Entity entity = world.getEntityByID(getAttachedHookShotId(launcher));
         if (entity instanceof HookShot) {
            return (HookShot)entity;
         }
      }

      return null;
   }

   @Nullable
   public static ItemStack findAttachedLauncher(InventoryPlayer inventory, HookShot hookShot) {
      if (inventory != null && inventory.mainInventory != null && hookShot != null) {
         for(ItemStack stack : inventory.mainInventory) {
            if (isAttachedToHookShot(stack, hookShot)) {
               return stack;
            }
         }
      }

      return null;
   }

   public static void attachToHookShot(ItemStack launcher, HookShot hookShot) {
      if (stackHasLauncher(launcher) && hookShot != null) {
         ensureHasNBT(launcher);
         NBTTagCompound tag = launcher.getTagCompound();
         tag.setInteger("hookShotId", hookShot.getEntityId());
      }

   }

   public static void unattach(ItemStack launcher) {
      if (stackHasLauncher(launcher) && hasNBT(launcher)) {
         launcher.getTagCompound().removeTag("hookShotId");
      }

   }

   public void onCreated(ItemStack stack, World world, EntityPlayer player) {
      ensureHasNBT(stack);
   }

   public boolean onDroppedByPlayer(ItemStack launcher, EntityPlayer player) {
      if (ForgeEntity.isServerSide(player)) {
         HookShot hookShot = findAttachedHookShot(launcher, player.world);
         if (hookShot != null) {
            return false;
         }
      }

      return player.isHandActive() ? false : super.onDroppedByPlayer(launcher, player);
   }

   public boolean onLeftClickEntity(ItemStack launcher, EntityPlayer player, Entity entity) {
      return this.hookType != HookType.PUDGE;
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BOW;
   }

   public void queueShot(ItemStack launcher) {
      this.queuedShots.add(launcher);
   }

   protected void makeShot(ItemStack launcher, World world, EntityLivingBase shooter) {
      if (this.isServerSide(world) && this.hookType != HookType.PROTOTYPE && notBroken(launcher)) {
         HookShot hookShot = new HookShot(world, shooter, this.hookType);
         world.spawnEntity(hookShot);
         attachToHookShot(launcher, hookShot);
         damageItem(1, launcher, shooter, false);
      }

   }

   public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
      return false;
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      player.setActiveHand(hand);
      return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 72000;
   }

   @SideOnly(Side.CLIENT)
   public void onInitClient() {
      if (keyBindUserDrop == null) {
         keyBindUserDrop = ForgeKeys.createBindingFor("unhook", 45, KeyCategories.GAMEPLAY);
      }

      GameSettings gs = ForgeKeys.getGameSettings();
      this.watchedKeys.put(gs.keyBindAttack, Launcher.WatchedKey.of(HookShot.WatchedBool.LOOSENING));
      this.watchedKeys.put(gs.keyBindUseItem, Launcher.WatchedKey.of(HookShot.WatchedBool.PULLING));
      this.watchedKeys.put(gs.keyBindDrop, Launcher.WatchedKey.of(HookShot.WatchedBool.DROPPING));
      this.watchedKeys.put(keyBindUserDrop, Launcher.WatchedKey.of(HookShot.WatchedBool.DROPPING));
      this.watchedKeys.put(gs.keyBindForward, Launcher.WatchedKey.of(HookShot.WatchedBool.MOVING_FORWARD));
      this.watchedKeys.put(gs.keyBindBack, Launcher.WatchedKey.of(HookShot.WatchedBool.MOVING_BACKWARD));
      this.watchedKeys.put(gs.keyBindLeft, Launcher.WatchedKey.of(HookShot.WatchedBool.MOVING_LEFT));
      this.watchedKeys.put(gs.keyBindRight, Launcher.WatchedKey.of(HookShot.WatchedBool.MOVING_RIGHT));
      this.watchedKeys.put(gs.keyBindJump, Launcher.WatchedKey.of(HookShot.WatchedBool.JUMPING));
   }

   public UnitVariant getVariantFor(ItemStack stack) {
      return !isBroken(stack) && !isAttached(stack) ? this.getVariant(Launcher.Variants.NORMAL) : this.getVariant(Launcher.Variants.EMPTY);
   }

   @SideOnly(Side.CLIENT)
   public void processPressedKeys() {
      if (ForgeKeys.affectPlayer()) {
         for(Entry<KeyBinding, Launcher.WatchedKey> entry : this.watchedKeys.entrySet()) {
            KeyBinding keyBinding = (KeyBinding)entry.getKey();
            Launcher.WatchedKey watchedKey = (Launcher.WatchedKey)entry.getValue();
            boolean isKeyDown = ForgeKeys.isDown(keyBinding);
            if (watchedKey.value != isKeyDown) {
               watchedKey.value = isKeyDown;
               Launcher.KeyPressedMsg message = new Launcher.KeyPressedMsg(watchedKey);
               this.getMod().getConnection().sendToServer(message);
            }
         }

      }
   }

   public void onUpdate(ItemStack launcher, World world, Entity shooter, int inventorySlot, boolean isCurrentItem) {
      if (this.isClientSide(world) && isCurrentItem) {
         this.processPressedKeys();
      }

      if (this.isServerSide(world) && shooter instanceof EntityLivingBase && this.queuedShots.contains(launcher)) {
         this.makeShot(launcher, world, (EntityLivingBase)shooter);
         this.queuedShots.remove(launcher);
      }

      super.onUpdate(launcher, world, shooter, inventorySlot, isCurrentItem);
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldRotateAroundWhenRendering() {
      return true;
   }

   public static class KeyPressedMsg extends ForgeNetMsg {
      public HookShot.WatchedBool parameter;
      public boolean value;

      public KeyPressedMsg() {
      }

      public KeyPressedMsg(Launcher.WatchedKey key) {
         this.parameter = key.parameter;
         this.value = key.value;
      }

      public static class Handler extends ForgeNetMsgHandler<Launcher.KeyPressedMsg> {
         public Handler() {
         }

         public IMessage onMessage(Launcher.KeyPressedMsg message, MessageContext ctx) {
            World world = this.getWorld(ctx);
            EntityPlayer player = this.getPlayer(ctx);
            if (world != null && player != null) {
               this.addScheduledTask(ctx, () -> {
                  ItemStack stack = player.inventory.getCurrentItem();
                  if (Launcher.stackHasLauncher(stack)) {
                     if (Launcher.isAttached(stack)) {
                        HookShot hookShot = Launcher.findAttachedHookShot(stack, world);
                        if (hookShot != null) {
                           hookShot.getDataManager().set(message.parameter.key, Boolean.valueOf(message.value));
                        } else {
                           Launcher.unattach(stack);
                        }
                     } else if (message.parameter == HookShot.WatchedBool.LOOSENING && message.value) {
                        Launcher launcher = (Launcher)stack.getItem();
                        launcher.queueShot(stack);
                     }
                  }

               });
            }

            return null;
         }
      }
   }

   private static enum Variants implements IForgeEnum {
      NORMAL,
      EMPTY;

      private Variants() {
      }

      public String getName() {
         return this.getLowerCase(this.name());
      }
   }

   static class WatchedKey {
      public final HookShot.WatchedBool parameter;
      public boolean value = false;

      public WatchedKey(HookShot.WatchedBool parameter, boolean value) {
         this.parameter = parameter;
         this.value = value;
      }

      public static Launcher.WatchedKey of(HookShot.WatchedBool parameter) {
         return new Launcher.WatchedKey(parameter, false);
      }
   }
}
