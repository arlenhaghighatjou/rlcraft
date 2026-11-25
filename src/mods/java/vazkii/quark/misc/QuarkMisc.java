package vazkii.quark.misc;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.misc.feature.AncientTomes;
import vazkii.quark.misc.feature.BlackAsh;
import vazkii.quark.misc.feature.ColorRunes;
import vazkii.quark.misc.feature.EnderdragonScales;
import vazkii.quark.misc.feature.EndermitesIntoShulkers;
import vazkii.quark.misc.feature.ExtraArrows;
import vazkii.quark.misc.feature.ExtraPotions;
import vazkii.quark.misc.feature.HorseWhistle;
import vazkii.quark.misc.feature.LockDirectionHotkey;
import vazkii.quark.misc.feature.MapMarkers;
import vazkii.quark.misc.feature.NoteBlockInterface;
import vazkii.quark.misc.feature.NoteBlocksMobSounds;
import vazkii.quark.misc.feature.ParrotEggs;
import vazkii.quark.misc.feature.Pickarang;
import vazkii.quark.misc.feature.PlaceVanillaDusts;
import vazkii.quark.misc.feature.PoisonPotatoUsage;
import vazkii.quark.misc.feature.ReacharoundPlacing;
import vazkii.quark.misc.feature.SlimeBucket;
import vazkii.quark.misc.feature.SnowGolemPlayerHeads;
import vazkii.quark.misc.feature.SoulPowder;
import vazkii.quark.misc.feature.ThrowableDragonBreath;
import vazkii.quark.misc.feature.UtilityRecipes;

public class QuarkMisc extends Module {
   public QuarkMisc() {
   }

   public void addFeatures() {
      this.registerFeature(new AncientTomes());
      this.registerFeature(new ColorRunes());
      this.registerFeature(new EndermitesIntoShulkers());
      this.registerFeature(new ExtraArrows());
      this.registerFeature(new NoteBlocksMobSounds(), "Note blocks play mob sounds if there's a head attached");
      this.registerFeature(new SlimeBucket());
      this.registerFeature(new SnowGolemPlayerHeads(), "Named snow golems with pumpkins drop player heads if killed by a witch");
      this.registerFeature(new NoteBlockInterface());
      this.registerFeature(new SoulPowder());
      this.registerFeature(new LockDirectionHotkey());
      this.registerFeature(new EnderdragonScales());
      this.registerFeature(new PoisonPotatoUsage());
      this.registerFeature(new ThrowableDragonBreath());
      this.registerFeature(new BlackAsh());
      this.registerFeature(new PlaceVanillaDusts());
      this.registerFeature(new MapMarkers());
      this.registerFeature(new ExtraPotions());
      this.registerFeature(new UtilityRecipes());
      this.registerFeature(new ParrotEggs());
      this.registerFeature(new Pickarang());
      this.registerFeature(new HorseWhistle());
      this.registerFeature(new ReacharoundPlacing());
   }

   public ItemStack getIconStack() {
      return new ItemStack(Items.CARROT_ON_A_STICK);
   }
}
