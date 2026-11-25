package c4.corpsecomplex.common.modules.effects;

import c4.corpsecomplex.common.Module;
import c4.corpsecomplex.common.Submodule;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.block.BlockCake;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

public class MoriModule extends Submodule {
   public static boolean registerPotion;
   @ObjectHolder("corpsecomplex:mori")
   static MoriPotion moriPotion;
   static double modHealth;
   static double modArmor;
   static double modToughness;
   static double modMove;
   static double modDamage;
   static double modSpeed;
   static boolean doRecover;
   static int duration;
   private static boolean noFood;
   private static boolean noXP;
   private static String[] curativeItems;
   private static ArrayList<ItemStack> cureList;
   private static boolean cfgEnabled;

   @SubscribeEvent
   public void onPlayerRespawnFinish(PlayerRespawnEvent e) {
      if (!e.player.world.isRemote && !e.isEndConquered()) {
         PotionEffect moriEffect = new PotionEffect(moriPotion, duration * 20, 0, false, true);
         moriEffect.setCurativeItems(cureList);
         e.player.addPotionEffect(moriEffect);
         if (e.player.getHealth() < e.player.getMaxHealth()) {
            e.player.setHealth(e.player.getMaxHealth());
         }
      }

   }

   @SubscribeEvent
   public void onPlayerXPPickUp(PlayerPickupXpEvent e) {
      if (noXP) {
         if (e.getEntityPlayer().isPotionActive(moriPotion)) {
            e.setCanceled(true);
         }

      }
   }

   @SubscribeEvent
   public void onEatingFood(RightClickItem e) {
      if (noFood) {
         if (e.getEntityPlayer().isPotionActive(moriPotion) && e.getItemStack().getItemUseAction() == EnumAction.EAT) {
            e.setCanceled(true);
         }

      }
   }

   @SubscribeEvent
   public void onEatingCake(RightClickBlock e) {
      if (noFood && e.getEntityPlayer().isPotionActive(moriPotion)) {
         if (e.getWorld().getBlockState(e.getPos()).getBlock() instanceof BlockCake) {
            e.setCanceled(true);
         }

      }
   }

   public MoriModule(Module parentModule) {
      super(parentModule, "Custom Respawn Effect");
      this.configCategory.setComment("Customize your own respawn effect for modifying player attributes");
   }

   public void loadModuleConfig() {
      this.setCategoryComment();
      cfgEnabled = this.getBool("Enable Custom Respawn Effect", false, "Set to true to enable custom effect applied on respawn", true);
      modHealth = this.getDouble("Maximum Health Modifier", 0.0D, -1024.0F, 1024.0F, "Set maximum health modifier", false);
      modArmor = this.getDouble("Armor Modifier", 0.0D, -30.0F, 30.0F, "Set armor modifier", false);
      modToughness = this.getDouble("Armor Toughness Modifier", 0.0D, -20.0F, 20.0F, "Set armor toughness modifier", false);
      modMove = this.getDouble("Movement Speed Percent Modifier", 0.0D, -1.0F, 1.0F, "Set movement speed percent modifier", false);
      modDamage = this.getDouble("Attack Damage Modifier", 0.0D, -2048.0F, 2048.0F, "Set attack damage modifier", false);
      modSpeed = this.getDouble("Attack Speed Percent Modifier", 0.0D, -1.0F, 1.0F, "Set attack speed percent modifier", false);
      doRecover = this.getBool("Gradual Recovery", false, "Set to true to enable gradual recovery (modifiers will diminish gradually as the effect goes on)", false);
      duration = this.getInt("Duration", 0, 0, 1600, "Set duration (seconds) for the effect", false);
      noFood = this.getBool("Cannot Eat Food", false, "Set to true to disable eating food while effect is active", false);
      noXP = this.getBool("Cannot Gain XP", false, "Set to true to disable gaining experience while effect is active", false);
      curativeItems = this.getStringList("Curative Items", new String[]{"minecraft:milk_bucket"}, "List of items that can cure the effect", false);
      MoriPotion.setModifiers();
      initCureList();
   }

   private static void initCureList() {
      cureList = new ArrayList();

      for(String s : curativeItems) {
         Item item = Item.getByNameOrId(s);
         if (item != null) {
            cureList.add(new ItemStack(item));
         }
      }

   }

   public void setEnabled() {
      this.enabled = registerPotion = cfgEnabled && this.parentModule.enabled;
   }

   public void initPropOrder() {
      this.propOrder = new ArrayList(Arrays.asList("Enable Custom Respawn Effect", "Duration", "Curative Items", "Gradual Recovery", "Maximum Health Modifier", "Armor Modifier", "Armor Toughness Modifier", "Attack Damage Modifier", "Attack Speed Percent Modifier", "Movement Speed Percent Modifier"));
   }

   public boolean hasEvents() {
      return true;
   }
}
