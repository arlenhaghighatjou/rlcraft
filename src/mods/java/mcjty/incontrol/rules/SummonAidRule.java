package mcjty.incontrol.rules;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import mcjty.incontrol.InControl;
import mcjty.incontrol.compat.ModRuleCompatibilityLayer;
import mcjty.incontrol.rules.support.GenericRuleEvaluator;
import mcjty.incontrol.rules.support.RuleKeys;
import mcjty.tools.rules.IEventQuery;
import mcjty.tools.rules.IModRuleCompatibilityLayer;
import mcjty.tools.rules.RuleBase;
import mcjty.tools.typed.Attribute;
import mcjty.tools.typed.AttributeMap;
import mcjty.tools.typed.GenericAttributeMapFactory;
import mcjty.tools.typed.Key;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

public class SummonAidRule extends RuleBase<SummonEventGetter> {
   public static final IEventQuery<SummonAidEvent> EVENT_QUERY = new IEventQuery<SummonAidEvent>() {
      public World getWorld(SummonAidEvent o) {
         return o.getWorld();
      }

      public BlockPos getPos(SummonAidEvent o) {
         return new BlockPos(o.getX(), o.getY(), o.getZ());
      }

      public BlockPos getValidBlockPos(SummonAidEvent o) {
         return new BlockPos(o.getX(), o.getY() - 1, o.getZ());
      }

      public int getY(SummonAidEvent o) {
         return o.getY();
      }

      public Entity getEntity(SummonAidEvent o) {
         return o.getEntity();
      }

      public DamageSource getSource(SummonAidEvent o) {
         return null;
      }

      public Entity getAttacker(SummonAidEvent o) {
         return null;
      }

      public EntityPlayer getPlayer(SummonAidEvent o) {
         return null;
      }

      public ItemStack getItem(SummonAidEvent o) {
         return ItemStack.EMPTY;
      }
   };
   private static final GenericAttributeMapFactory FACTORY = new GenericAttributeMapFactory();
   private static Random rnd = new Random();
   private final GenericRuleEvaluator ruleEvaluator;
   private Result result;

   private SummonAidRule(AttributeMap map) {
      super(InControl.setup.getLogger());
      this.ruleEvaluator = new GenericRuleEvaluator(map);
      this.addActions(map, new ModRuleCompatibilityLayer());
   }

   public static SummonAidRule parse(JsonElement element) {
      if (element == null) {
         return null;
      } else {
         AttributeMap map = FACTORY.parse(element);
         return new SummonAidRule(map);
      }
   }

   protected void addActions(AttributeMap map, IModRuleCompatibilityLayer layer) {
      super.addActions(map, layer);
      if (map.has(RuleKeys.ACTION_RESULT)) {
         String br = (String)map.get(RuleKeys.ACTION_RESULT);
         if (!"default".equals(br) && !br.startsWith("def")) {
            if (!"allow".equals(br) && !"true".equals(br)) {
               this.result = Result.DENY;
            } else {
               this.result = Result.ALLOW;
            }
         } else {
            this.result = Result.DEFAULT;
         }
      } else {
         this.result = Result.DEFAULT;
      }

      if (map.has(RuleKeys.ACTION_ANGRY)) {
         this.addAngryAction(map);
      }

      if (map.has(RuleKeys.ACTION_HELDITEM)) {
         this.addHeldItem(map);
      }

      if (map.has(RuleKeys.ACTION_ARMORBOOTS)) {
         this.addArmorItem(map, RuleKeys.ACTION_ARMORBOOTS, EntityEquipmentSlot.FEET);
      }

      if (map.has(RuleKeys.ACTION_ARMORLEGS)) {
         this.addArmorItem(map, RuleKeys.ACTION_ARMORLEGS, EntityEquipmentSlot.LEGS);
      }

      if (map.has(RuleKeys.ACTION_ARMORHELMET)) {
         this.addArmorItem(map, RuleKeys.ACTION_ARMORHELMET, EntityEquipmentSlot.HEAD);
      }

      if (map.has(RuleKeys.ACTION_ARMORCHEST)) {
         this.addArmorItem(map, RuleKeys.ACTION_ARMORCHEST, EntityEquipmentSlot.CHEST);
      }

      if (map.has(RuleKeys.ACTION_POTION)) {
         this.addPotionsAction(map);
      }

   }

   private void addPotionsAction(AttributeMap map) {
      List<PotionEffect> effects = new ArrayList();

      for(String p : map.getList(RuleKeys.ACTION_POTION)) {
         String[] splitted = StringUtils.split(p, ',');
         if (splitted != null && splitted.length == 3) {
            Potion potion = (Potion)ForgeRegistries.POTIONS.getValue(new ResourceLocation(splitted[0]));
            if (potion == null) {
               InControl.setup.getLogger().log(Level.ERROR, "Can't find potion '" + p + "'!");
            } else {
               int duration = 0;
               int amplifier = 0;

               try {
                  duration = Integer.parseInt(splitted[1]);
                  amplifier = Integer.parseInt(splitted[2]);
               } catch (NumberFormatException var10) {
                  InControl.setup.getLogger().log(Level.ERROR, "Bad duration or amplifier integer for '" + p + "'!");
                  continue;
               }

               effects.add(new PotionEffect(potion, duration, amplifier));
            }
         } else {
            InControl.setup.getLogger().log(Level.ERROR, "Bad potion specifier '" + p + "'! Use <potion>,<duration>,<amplifier>");
         }
      }

      if (!effects.isEmpty()) {
         this.actions.add((event) -> {
            EntityLivingBase living = event.getZombieHelper();

            for(PotionEffect effect : effects) {
               PotionEffect neweffect = new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier());
               living.addPotionEffect(neweffect);
            }

         });
      }

   }

   private void addArmorItem(AttributeMap map, Key<String> itemKey, EntityEquipmentSlot slot) {
      List<Pair<Float, ItemStack>> items = this.getItemsWeighted(map.getList(itemKey));
      if (!items.isEmpty()) {
         if (items.size() == 1) {
            Pair<Float, ItemStack> pair = (Pair)items.get(0);
            this.actions.add((event) -> {
               EntityZombie helper = event.getZombieHelper();
               helper.setItemStackToSlot(slot, ((ItemStack)pair.getRight()).copy());
            });
         } else {
            float total = this.getTotal(items);
            this.actions.add((event) -> {
               ItemStack item = this.getRandomItem(items, total);
               EntityZombie helper = event.getZombieHelper();
               helper.setItemStackToSlot(slot, item.copy());
            });
         }

      }
   }

   private void addHeldItem(AttributeMap map) {
      List<Pair<Float, ItemStack>> items = this.getItemsWeighted(map.getList(RuleKeys.ACTION_HELDITEM));
      if (!items.isEmpty()) {
         if (items.size() == 1) {
            Pair<Float, ItemStack> pair = (Pair)items.get(0);
            this.actions.add((event) -> {
               EntityZombie helper = event.getZombieHelper();
               helper.setHeldItem(EnumHand.MAIN_HAND, ((ItemStack)pair.getRight()).copy());
            });
         } else {
            float total = this.getTotal(items);
            this.actions.add((event) -> {
               ItemStack item = this.getRandomItem(items, total);
               EntityZombie helper = event.getZombieHelper();
               helper.setHeldItem(EnumHand.MAIN_HAND, item.copy());
            });
         }

      }
   }

   private void addAngryAction(AttributeMap map) {
      if (((Boolean)map.get(RuleKeys.ACTION_ANGRY)).booleanValue()) {
         this.actions.add((event) -> {
            EntityZombie helper = event.getZombieHelper();
            EntityPlayer player = event.getWorld().getClosestPlayerToEntity(helper, 50.0D);
            if (player != null) {
               helper.setAttackTarget(player);
            }

         });
      }

   }

   private void addHealthAction(AttributeMap map) {
      float m = map.has(RuleKeys.ACTION_HEALTHMULTIPLY) ? ((Float)map.get(RuleKeys.ACTION_HEALTHMULTIPLY)).floatValue() : 1.0F;
      float a = map.has(RuleKeys.ACTION_HEALTHADD) ? ((Float)map.get(RuleKeys.ACTION_HEALTHADD)).floatValue() : 0.0F;
      this.actions.add((event) -> {
         EntityZombie helper = event.getZombieHelper();
         IAttributeInstance entityAttribute = helper.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
         if (entityAttribute != null) {
            double newMax = entityAttribute.getBaseValue() * (double)m + (double)a;
            entityAttribute.setBaseValue(newMax);
            helper.setHealth((float)newMax);
         }

      });
   }

   private void addSpeedAction(AttributeMap map) {
      float m = map.has(RuleKeys.ACTION_SPEEDMULTIPLY) ? ((Float)map.get(RuleKeys.ACTION_SPEEDMULTIPLY)).floatValue() : 1.0F;
      float a = map.has(RuleKeys.ACTION_SPEEDADD) ? ((Float)map.get(RuleKeys.ACTION_SPEEDADD)).floatValue() : 0.0F;
      this.actions.add((event) -> {
         EntityZombie helper = event.getZombieHelper();
         IAttributeInstance entityAttribute = helper.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
         if (entityAttribute != null) {
            double newMax = entityAttribute.getBaseValue() * (double)m + (double)a;
            entityAttribute.setBaseValue(newMax);
         }

      });
   }

   private void addSizeActions(AttributeMap map) {
      InControl.setup.getLogger().log(Level.WARN, "Mob resizing not implemented yet!");
      if (map.has(RuleKeys.ACTION_SIZEMULTIPLY)) {
         ((Float)map.get(RuleKeys.ACTION_SIZEMULTIPLY)).floatValue();
      } else {
         float var10000 = 1.0F;
      }

      if (map.has(RuleKeys.ACTION_SIZEADD)) {
         ((Float)map.get(RuleKeys.ACTION_SIZEADD)).floatValue();
      } else {
         float var4 = 0.0F;
      }

      this.actions.add((event) -> {
         EntityZombie helper = event.getZombieHelper();
      });
   }

   private void addDamageAction(AttributeMap map) {
      float m = map.has(RuleKeys.ACTION_DAMAGEMULTIPLY) ? ((Float)map.get(RuleKeys.ACTION_DAMAGEMULTIPLY)).floatValue() : 1.0F;
      float a = map.has(RuleKeys.ACTION_DAMAGEADD) ? ((Float)map.get(RuleKeys.ACTION_DAMAGEADD)).floatValue() : 0.0F;
      this.actions.add((event) -> {
         EntityZombie helper = event.getZombieHelper();
         IAttributeInstance entityAttribute = helper.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
         if (entityAttribute != null) {
            double newMax = entityAttribute.getBaseValue() * (double)m + (double)a;
            entityAttribute.setBaseValue(newMax);
         }

      });
   }

   public boolean match(SummonAidEvent event) {
      return this.ruleEvaluator.match(event, EVENT_QUERY);
   }

   public void action(final SummonAidEvent event) {
      SummonEventGetter getter = new SummonEventGetter() {
         public EntityLivingBase getEntityLiving() {
            return event.getEntity() instanceof EntityLivingBase ? (EntityLivingBase)event.getEntity() : null;
         }

         public EntityPlayer getPlayer() {
            return null;
         }

         public World getWorld() {
            return event.getWorld();
         }

         public BlockPos getPosition() {
            return new BlockPos(event.getX(), event.getY(), event.getZ());
         }

         public EntityZombie getZombieHelper() {
            EntityZombie helper = event.getCustomSummonedAid();
            if (helper == null) {
               helper = new EntityZombie(event.getWorld());
            }

            return helper;
         }
      };

      for(Consumer<SummonEventGetter> action : this.actions) {
         action.accept(getter);
      }

   }

   public Result getResult() {
      return this.result;
   }

   static {
      FACTORY.attribute(Attribute.create(RuleKeys.MINTIME)).attribute(Attribute.create(RuleKeys.MAXTIME)).attribute(Attribute.create(RuleKeys.MINCOUNT)).attribute(Attribute.create(RuleKeys.MAXCOUNT)).attribute(Attribute.create(RuleKeys.MINLIGHT)).attribute(Attribute.create(RuleKeys.MAXLIGHT)).attribute(Attribute.create(RuleKeys.MINHEIGHT)).attribute(Attribute.create(RuleKeys.MAXHEIGHT)).attribute(Attribute.create(RuleKeys.MINDIFFICULTY)).attribute(Attribute.create(RuleKeys.MAXDIFFICULTY)).attribute(Attribute.create(RuleKeys.MINSPAWNDIST)).attribute(Attribute.create(RuleKeys.MAXSPAWNDIST)).attribute(Attribute.create(RuleKeys.RANDOM)).attribute(Attribute.create(RuleKeys.CANSPAWNHERE)).attribute(Attribute.create(RuleKeys.NOTCOLLIDING)).attribute(Attribute.create(RuleKeys.INBUILDING)).attribute(Attribute.create(RuleKeys.INCITY)).attribute(Attribute.create(RuleKeys.INSTREET)).attribute(Attribute.create(RuleKeys.INSPHERE)).attribute(Attribute.create(RuleKeys.PASSIVE)).attribute(Attribute.create(RuleKeys.HOSTILE)).attribute(Attribute.create(RuleKeys.SEESKY)).attribute(Attribute.create(RuleKeys.WEATHER)).attribute(Attribute.create(RuleKeys.TEMPCATEGORY)).attribute(Attribute.create(RuleKeys.DIFFICULTY)).attribute(Attribute.create(RuleKeys.STRUCTURE)).attribute(Attribute.create(RuleKeys.WINTER)).attribute(Attribute.create(RuleKeys.SUMMER)).attribute(Attribute.create(RuleKeys.SPRING)).attribute(Attribute.create(RuleKeys.AUTUMN)).attribute(Attribute.createMulti(RuleKeys.MOB)).attribute(Attribute.createMulti(RuleKeys.MOD)).attribute(Attribute.createMulti(RuleKeys.BLOCK)).attribute(Attribute.create(RuleKeys.BLOCKOFFSET)).attribute(Attribute.createMulti(RuleKeys.BIOME)).attribute(Attribute.createMulti(RuleKeys.BIOMETYPE)).attribute(Attribute.createMulti(RuleKeys.DIMENSION)).attribute(Attribute.create(RuleKeys.STATE)).attribute(Attribute.create(RuleKeys.ACTION_RESULT)).attribute(Attribute.create(RuleKeys.ACTION_MESSAGE)).attribute(Attribute.create(RuleKeys.ACTION_HEALTHMULTIPLY)).attribute(Attribute.create(RuleKeys.ACTION_HEALTHADD)).attribute(Attribute.create(RuleKeys.ACTION_SPEEDMULTIPLY)).attribute(Attribute.create(RuleKeys.ACTION_SPEEDADD)).attribute(Attribute.create(RuleKeys.ACTION_DAMAGEMULTIPLY)).attribute(Attribute.create(RuleKeys.ACTION_DAMAGEADD)).attribute(Attribute.create(RuleKeys.ACTION_SIZEMULTIPLY)).attribute(Attribute.create(RuleKeys.ACTION_SIZEADD)).attribute(Attribute.create(RuleKeys.ACTION_ANGRY)).attribute(Attribute.createMulti(RuleKeys.ACTION_HELDITEM)).attribute(Attribute.createMulti(RuleKeys.ACTION_ARMORBOOTS)).attribute(Attribute.createMulti(RuleKeys.ACTION_ARMORLEGS)).attribute(Attribute.createMulti(RuleKeys.ACTION_ARMORCHEST)).attribute(Attribute.createMulti(RuleKeys.ACTION_ARMORHELMET)).attribute(Attribute.createMulti(RuleKeys.ACTION_POTION));
   }
}
