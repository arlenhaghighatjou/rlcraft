package vazkii.quark.misc.feature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.item.ItemPickarang;
import vazkii.quark.world.client.render.RenderPickarang;
import vazkii.quark.world.entity.EntityPickarang;
import vazkii.quark.world.feature.Stonelings;

public class Pickarang extends Feature {
   public static int timeout;
   public static int harvestLevel;
   public static int durability;
   public static boolean neverUseHeartOfDiamond;
   public static boolean noCooldown;
   public static Item pickarang;
   public static double maxHardness;
   private static final ThreadLocal<EntityPickarang> ACTIVE_PICKARANG = new ThreadLocal();

   public Pickarang() {
   }

   public static boolean canSharpnessApply(ItemStack stack) {
      return stack.getItem() instanceof ItemPickarang;
   }

   public static void setActivePickarang(EntityPickarang pickarang) {
      ACTIVE_PICKARANG.set(pickarang);
   }

   public static DamageSource createDamageSource(EntityPlayer player) {
      EntityPickarang pickarang = (EntityPickarang)ACTIVE_PICKARANG.get();
      return pickarang == null ? null : (new EntityDamageSourceIndirect("player", pickarang, player)).setProjectile();
   }

   public void setupConfig() {
      timeout = this.loadPropInt("Timeout", "How long it takes for the pickarang to return to the player if it doesn't hit anything", 20);
      harvestLevel = this.loadPropInt("Harvest Level", "2 is Iron, 3 is Diamond", 3);
      durability = this.loadPropInt("Durability", "Set to -1 to have the Pickarang be unbreakable", 800);
      maxHardness = this.loadPropDouble("Max mining hardness", "22.5 is ender chests, 25.0 is monster boxes, 50 is obsidian. Most things are below 5.", 20.0D);
      neverUseHeartOfDiamond = this.loadPropBool("Never Use Heart of Diamond", "Set this to true to use the recipe without the Heart of Diamond, even if the Heart of Diamond is enabled", false);
      noCooldown = this.loadPropBool("No Cooldown", "Set this to true to disable the short cooldown between throwing pickarangs", false);
   }

   public void preInit(FMLPreInitializationEvent event) {
      pickarang = new ItemPickarang();
      String pickarangName = "quark:pickarang";
      EntityRegistry.registerModEntity(new ResourceLocation(pickarangName), EntityPickarang.class, pickarangName, 20, Quark.instance, 80, 3, true);
   }

   public void postPreInit() {
      if (Stonelings.diamond_heart != null && !neverUseHeartOfDiamond) {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(pickarang), new Object[]{"DWC", "  W", "  D", 'D', "gemDiamond", 'W', "plankWood", 'C', Stonelings.diamond_heart});
      } else {
         RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(pickarang), new Object[]{"DWD", "  W", "  D", 'D', "gemDiamond", 'W', "plankWood"});
      }

   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      RenderingRegistry.registerEntityRenderingHandler(EntityPickarang.class, RenderPickarang.FACTORY);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
