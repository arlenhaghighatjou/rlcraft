package vazkii.quark.client;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.client.feature.AngryCreepers;
import vazkii.quark.client.feature.AutoJumpHotkey;
import vazkii.quark.client.feature.BetterFireEffect;
import vazkii.quark.client.feature.BetterVanillaTextures;
import vazkii.quark.client.feature.ChestSearchBar;
import vazkii.quark.client.feature.EnchantedBooksShowItems;
import vazkii.quark.client.feature.FoodTooltip;
import vazkii.quark.client.feature.GreenerGrass;
import vazkii.quark.client.feature.ImprovedMountHUD;
import vazkii.quark.client.feature.ImprovedSignEdit;
import vazkii.quark.client.feature.ItemsFlashBeforeExpiring;
import vazkii.quark.client.feature.LessIntrusiveShields;
import vazkii.quark.client.feature.MapTooltip;
import vazkii.quark.client.feature.NoPotionShift;
import vazkii.quark.client.feature.PanoramaMaker;
import vazkii.quark.client.feature.RandomAnimalTextures;
import vazkii.quark.client.feature.RenderItemsInChat;
import vazkii.quark.client.feature.ShowInvalidSlots;
import vazkii.quark.client.feature.ShulkerBoxTooltip;
import vazkii.quark.client.feature.UsageTicker;
import vazkii.quark.client.feature.VisualStatDisplay;

public class QuarkClient extends Module {
   public QuarkClient() {
   }

   public void addFeatures() {
      this.registerFeature(new ChestSearchBar());
      this.registerFeature(new AngryCreepers(), "Creepers turn red when they're exploding");
      this.registerFeature(new ShulkerBoxTooltip());
      this.registerFeature(new FoodTooltip());
      this.registerFeature(new GreenerGrass());
      this.registerFeature(new ImprovedMountHUD());
      this.registerFeature(new MapTooltip());
      this.registerFeature(new NoPotionShift());
      this.registerFeature(new RandomAnimalTextures());
      this.registerFeature(new LessIntrusiveShields());
      this.registerFeature(new BetterVanillaTextures());
      this.registerFeature(new VisualStatDisplay());
      this.registerFeature(new AutoJumpHotkey());
      this.registerFeature(new PanoramaMaker());
      this.registerFeature(new ImprovedSignEdit());
      this.registerFeature(new UsageTicker());
      this.registerFeature(new ItemsFlashBeforeExpiring());
      this.registerFeature(new BetterFireEffect());
      this.registerFeature(new EnchantedBooksShowItems());
      this.registerFeature(new ShowInvalidSlots());
      this.registerFeature(new RenderItemsInChat());
   }

   public ItemStack getIconStack() {
      return new ItemStack(Items.ENDER_EYE);
   }
}
