package vazkii.potionfingers;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;

@Mod(
   modid = "potionfingers",
   name = "Potion Fingers",
   version = "r1.0-8",
   dependencies = "required-after:autoreglib;required-after:baubles;",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class PotionFingers {
   public static final String MOD_ID = "potionfingers";
   public static final String MOD_NAME = "Potion Fingers";
   public static final String BUILD = "8";
   public static final String VERSION = "r1.0-8";
   public static final String DEPENDENCIES = "required-after:autoreglib;required-after:baubles;";
   public static final Potion[] DEFAULT_EFFECTS = new Potion[]{MobEffects.SPEED, MobEffects.HASTE, MobEffects.RESISTANCE, MobEffects.JUMP_BOOST, MobEffects.STRENGTH, MobEffects.REGENERATION};
   public static Item ring;

   public PotionFingers() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLPreInitializationEvent) {
         preInit((FMLPreInitializationEvent)event);
      }
   }

   public void preInit(FMLPreInitializationEvent event) {
      ring = new ItemRing();
      RecipeHandler.addOreDictRecipe(new ItemStack(ring), new Object[]{"LG ", "G G", " G ", 'L', new ItemStack(Blocks.LAPIS_BLOCK), 'G', "ingotGold"});
      addRingRecipe(MobEffects.SPEED, Items.SUGAR);
      addRingRecipe(MobEffects.HASTE, "gemEmerald");
      addRingRecipe(MobEffects.RESISTANCE, "ingotIron");
      addRingRecipe(MobEffects.JUMP_BOOST, Items.RABBIT_FOOT);
      addRingRecipe(MobEffects.STRENGTH, Items.BLAZE_POWDER);
      addRingRecipe(MobEffects.REGENERATION, Items.GHAST_TEAR);
   }

   private static void addRingRecipe(Potion potion, Object item) {
      RecipeHandler.addOreDictRecipe(ItemRing.getRingForPotion(potion), new Object[]{" I ", "IRI", " I ", 'R', new ItemStack(ring), 'I', item});
   }
}
