package com.endertech.minecraft.forge.economy;

import com.endertech.common.Args;
import com.endertech.common.CommonMath;
import com.endertech.minecraft.forge.ForgeBounds;
import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.items.ForgeStack;
import com.endertech.minecraft.forge.units.ItemState;
import com.endertech.minecraft.forge.units.UnitId;
import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public abstract class ForgeTrades {
   public static final float USED_ITEM_DISCOUNT = 0.75F;
   protected static final String KEY_BARGAINS = "bargains";
   protected final ForgeMod mod;
   private final Map<ForgeTrades.Careers, UnitConfig> configs = new EnumMap(ForgeTrades.Careers.class);
   private final Map<ForgeTrades.Careers, Map<ForgeTrades.Skills, List<ForgeTrades.Bargain>>> bargains = newCareersEnumMap();
   public final ForgeTrades.Definitions definitions = new ForgeTrades.Definitions();

   public ForgeTrades(ForgeMod mod) {
      this.mod = mod;
      Path baseDir = mod.getConfig().getConfigDir().resolve("Trades");

      for(ForgeTrades.Careers career : ForgeTrades.Careers.values()) {
         UnitConfig config = new UnitConfig(baseDir, career.name());
         this.configs.put(career, config);
      }

   }

   protected static <B> Map<ForgeTrades.Careers, Map<ForgeTrades.Skills, List<B>>> newCareersEnumMap() {
      Map<ForgeTrades.Careers, Map<ForgeTrades.Skills, List<B>>> careersMap = new EnumMap(ForgeTrades.Careers.class);

      for(ForgeTrades.Careers career : ForgeTrades.Careers.values()) {
         Map<ForgeTrades.Skills, List<B>> skillsMap = new EnumMap(ForgeTrades.Skills.class);

         for(ForgeTrades.Skills skill : ForgeTrades.Skills.values()) {
            skillsMap.put(skill, new ArrayList());
         }

         careersMap.put(career, skillsMap);
      }

      return careersMap;
   }

   public void init() {
      for(ForgeTrades.Careers career : ForgeTrades.Careers.values()) {
         this.clearCarrerTrades(career);
         UnitConfig config = (UnitConfig)this.configs.get(career);

         for(ForgeTrades.Skills skill : ForgeTrades.Skills.values()) {
            String[] array = config.getStrArray(skill.toString(), "bargains", this.definitions.toArray(career, skill), "List of all possible bargains for this career and skill.");
            if (array != null) {
               for(String string : array) {
                  this.addBargain(career, skill, ForgeTrades.Bargain.from(string));
               }
            }

            ForgeTrades.TradeList tradeList = new ForgeTrades.TradeList(skill, this.getBargainList(career, skill));
            career.instance.addTrade(skill.level, new ITradeList[]{tradeList});
         }
      }

   }

   protected List<ForgeTrades.Bargain> getBargainList(ForgeTrades.Careers career, ForgeTrades.Skills skill) {
      return (List)((Map)this.bargains.get(career)).get(skill);
   }

   protected void addBargain(ForgeTrades.Careers career, ForgeTrades.Skills skill, ItemState buy, ItemState sell, ForgeTrades.Exchange exchange) {
      ForgeTrades.Bargain bargain = ForgeTrades.Bargain.from(buy.toStack(exchange.buyAmount), sell.toStack(exchange.sellAmount));
      this.addBargain(career, skill, bargain);
   }

   protected void addBargain(ForgeTrades.Careers career, ForgeTrades.Skills skill, ForgeTrades.Bargain bargain) {
      if (bargain != null && bargain.isValid()) {
         this.getBargainList(career, skill).add(bargain);
      } else {
         this.mod.getLogger().error("Invalid bargain: {}", bargain);
      }

   }

   public void clearCarrerTrades(ForgeTrades.Careers career) {
      try {
         Field field = career.instance.getClass().getDeclaredField("trades");
         field.setAccessible(true);
         field.set(career.instance, Lists.newArrayList());
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public Map<ForgeTrades.Careers, UnitConfig> getConfigs() {
      return Collections.unmodifiableMap(this.configs);
   }

   public List<ForgeTrades.Bargain> getBargains(ForgeTrades.Careers career, ForgeTrades.Skills skill) {
      return Collections.unmodifiableList(this.getBargainList(career, skill));
   }

   public static class Bargain {
      public static final ForgeTrades.Bargain EMPTY = from(ForgeStack.EMPTY, ForgeStack.EMPTY);
      public final ForgeStack buy;
      public final ForgeStack sell;

      protected Bargain(ForgeStack buy, ForgeStack sell) {
         this.buy = buy;
         this.sell = sell;
      }

      public static ForgeTrades.Bargain from(ForgeStack buy, ForgeStack sell) {
         return new ForgeTrades.Bargain(buy, sell);
      }

      public static String definition(UnitId buy, UnitId sell, ForgeTrades.Exchange exchange) {
         return Args.join(buy, exchange.buyAmount, sell, exchange.sellAmount);
      }

      public static ForgeTrades.Bargain from(String definition) {
         String[] args = Args.split(definition);

         try {
            int buyAmount = Integer.valueOf(args[1]).intValue();
            ForgeStack buy = UnitId.from(args[0]).getItemState().toStack(buyAmount);
            int sellAmount = Integer.valueOf(args[3]).intValue();
            ForgeStack sell = UnitId.from(args[2]).getItemState().toStack(sellAmount);
            if (buy.exists() && sell.exists()) {
               return new ForgeTrades.Bargain(buy, sell);
            }
         } catch (Exception var6) {
            ForgeMain.instance.getLogger().error("Wrong bargain info in string: '{}'", definition);
         }

         return EMPTY;
      }

      public boolean isValid() {
         return this.buy.exists() && ForgeBounds.STACK_SIZE.getIntBounds().encloses(Integer.valueOf(this.buy.getAmount())) && this.sell.exists() && ForgeBounds.STACK_SIZE.getIntBounds().encloses(Integer.valueOf(this.sell.getAmount()));
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (!(obj instanceof ForgeTrades.Bargain)) {
            return false;
         } else {
            ForgeTrades.Bargain bargain = (ForgeTrades.Bargain)obj;
            return bargain.buy.equals(this.buy) && bargain.sell.equals(this.sell);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.buy, this.sell});
      }

      public String toString() {
         return ForgeTrades.Bargain.class.getSimpleName() + Args.group(this.buy, this.sell);
      }
   }

   public static enum Careers {
      FARMER(ForgeTrades.Professions.FARMER, 0),
      FISHERMAN(ForgeTrades.Professions.FARMER, 1),
      SHEPHERD(ForgeTrades.Professions.FARMER, 2),
      FLETCHER(ForgeTrades.Professions.FARMER, 3),
      LIBRARIAN(ForgeTrades.Professions.LIBRARIAN, 0),
      CARTOGRAPHER(ForgeTrades.Professions.LIBRARIAN, 1),
      CLERIC(ForgeTrades.Professions.PRIEST, 0),
      ARMOR_SMITH(ForgeTrades.Professions.SMITH, 0),
      WEAPON_SMITH(ForgeTrades.Professions.SMITH, 1),
      TOOL_SMITH(ForgeTrades.Professions.SMITH, 2),
      BUTCHER(ForgeTrades.Professions.BUTCHER, 0),
      TANNER(ForgeTrades.Professions.BUTCHER, 1),
      NITWIT(ForgeTrades.Professions.NITWIT, 0);

      public final VillagerCareer instance;

      private Careers(ForgeTrades.Professions profession, int id) {
         this.instance = profession.instance.getCareer(id);
      }
   }

   public static class Definitions {
      private Map<ForgeTrades.Careers, Map<ForgeTrades.Skills, List<String>>> definitions = ForgeTrades.newCareersEnumMap();
      private ForgeTrades.Careers career;
      private ForgeTrades.Skills skill;

      public Definitions() {
      }

      public ForgeTrades.Definitions forCareer(ForgeTrades.Careers career) {
         this.career = career;
         return this;
      }

      public ForgeTrades.Definitions withSkill(ForgeTrades.Skills skill) {
         this.skill = skill;
         return this;
      }

      public ForgeTrades.Definitions add(String definition) {
         ((List)((Map)this.definitions.get(this.career)).get(this.skill)).add(definition);
         return this;
      }

      public String[] toArray(ForgeTrades.Careers career, ForgeTrades.Skills skill) {
         return (String[])((List)((Map)this.definitions.get(career)).get(skill)).toArray(new String[0]);
      }
   }

   public class Exchange {
      public final int buyAmount;
      public final int sellAmount;

      public Exchange(float buyPrice, float sellPrice) {
         if (buyPrice < sellPrice) {
            this.buyAmount = Math.round(sellPrice / buyPrice);
            this.sellAmount = 1;
         } else {
            this.buyAmount = 1;
            this.sellAmount = Math.round(buyPrice / sellPrice);
         }

      }
   }

   protected static enum Professions {
      FARMER,
      LIBRARIAN,
      PRIEST,
      SMITH,
      BUTCHER,
      NITWIT;

      public final VillagerProfession instance;

      private Professions() {
         VillagerRegistry.instance();
         this.instance = VillagerRegistry.getById(this.ordinal());
      }

      public ResourceLocation toResLoc() {
         UnitId id = UnitId.from("minecraft", this.name());
         return id.toResLoc();
      }
   }

   public static enum Skills {
      AMATEUR,
      SALESMAN,
      DEALER,
      FENCE,
      TREASURER,
      MAGNATE;

      public static final float BASE_DISCOUNT = 0.05F;
      public static final float BASE_USED_ITEM_FACTOR = 0.25F;
      private final int level = this.ordinal() + 1;

      private Skills() {
      }

      public float discountPrice(float price) {
         return price - price * 0.05F * (float)this.ordinal();
      }

      public float usedItemPrice(float price) {
         return price * 0.25F * (float)this.ordinal();
      }

      public String toString() {
         return String.format("LEVEL %d - %s", this.level, this.name());
      }
   }

   protected static class TradeList implements ITradeList {
      private final ForgeTrades.Skills skill;
      private final List<ForgeTrades.Bargain> bargains;

      public TradeList(ForgeTrades.Skills skill, List<ForgeTrades.Bargain> bargains) {
         this.skill = skill;
         this.bargains = bargains;
      }

      public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, EaglercraftRandom random) {
         List<ForgeTrades.Bargain> pickedBargains = new ArrayList();
         int amount = ForgeBounds.BARGAINS_PER_TRADER.getIntBounds().randomBetween().intValue();
         if (this.bargains.size() <= amount) {
            pickedBargains.addAll(this.bargains);
         } else {
            for(int count = 0; count < amount; ++count) {
               ForgeTrades.Bargain randomBargain = (ForgeTrades.Bargain)CommonMath.EaglercraftRandom.from(this.bargains);
               if (!pickedBargains.contains(randomBargain)) {
                  pickedBargains.add(randomBargain);
               }
            }
         }

         for(ForgeTrades.Bargain bargain : pickedBargains) {
            MerchantRecipe recipe = new MerchantRecipe(bargain.sell.toItemStack(), bargain.buy.toItemStack());
            recipeList.add(recipe);
         }

      }

      public ForgeTrades.Skills getSkill() {
         return this.skill;
      }
   }
}
