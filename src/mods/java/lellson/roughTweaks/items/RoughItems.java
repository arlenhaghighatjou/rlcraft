package lellson.roughTweaks.items;

import java.util.ArrayList;
import java.util.List;
import lellson.roughTweaks.misc.RoughConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class RoughItems {
   public static final List<Item> ITEMS = new ArrayList();
   public static final ItemHeal SALVE = new ItemHeal("salve", RoughConfig.salveHealCount, 5, RoughConfig.salveHealAmount, (PotionEffect)null, new ItemStack(Items.BOWL));
   public static final ItemHeal PLASTER = new ItemHeal("plaster", RoughConfig.plasterHealCount, 10, RoughConfig.plasterHealAmount, (PotionEffect)null, (ItemStack)null);
   public static final ItemHeal BANDAGE = new ItemHeal("bandage", RoughConfig.bandageHealCount, 20, RoughConfig.bandageHealAmount, (PotionEffect)null, (ItemStack)null);
   public static final ItemHeal MEDIKIT = new ItemHeal("medikit", RoughConfig.medikitHealCount, 40, RoughConfig.medikitHealAmount, (PotionEffect)null, (ItemStack)null);
   public static final ItemHeal ENCHANTED_MEDIKIT = new ItemHeal("medikitEnchanted", RoughConfig.medikitEnchantedHealCount, 40, RoughConfig.medikitEnchantedHealAmount, new PotionEffect(MobEffects.ABSORPTION, 1200), (ItemStack)null);

   public static void registerRenderers() {
      for(Item item : ITEMS) {
         Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
      }

   }
}
