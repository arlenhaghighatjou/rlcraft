package mcjty.incontrol.rules;

import com.google.gson.JsonElement;
import mcjty.incontrol.InControl;
import mcjty.incontrol.compat.ModRuleCompatibilityLayer;
import mcjty.incontrol.rules.support.GenericRuleEvaluator;
import mcjty.incontrol.rules.support.RuleKeys;
import mcjty.tools.rules.IEventQuery;
import mcjty.tools.rules.IModRuleCompatibilityLayer;
import mcjty.tools.rules.RuleBase;
import mcjty.tools.rules.RuleBase.EventGetter;
import mcjty.tools.typed.Attribute;
import mcjty.tools.typed.AttributeMap;
import mcjty.tools.typed.GenericAttributeMapFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class ExperienceRule extends RuleBase<EventGetter> {
   public static final IEventQuery<LivingExperienceDropEvent> EVENT_QUERY = new IEventQuery<LivingExperienceDropEvent>() {
      public World getWorld(LivingExperienceDropEvent o) {
         return o.getEntity().getEntityWorld();
      }

      public BlockPos getPos(LivingExperienceDropEvent o) {
         return o.getEntity().getPosition();
      }

      public BlockPos getValidBlockPos(LivingExperienceDropEvent o) {
         return o.getEntity().getPosition().down();
      }

      public int getY(LivingExperienceDropEvent o) {
         return o.getEntity().getPosition().getY();
      }

      public Entity getEntity(LivingExperienceDropEvent o) {
         return o.getEntity();
      }

      public DamageSource getSource(LivingExperienceDropEvent o) {
         return null;
      }

      public Entity getAttacker(LivingExperienceDropEvent o) {
         return o.getAttackingPlayer();
      }

      public EntityPlayer getPlayer(LivingExperienceDropEvent o) {
         return o.getAttackingPlayer();
      }

      public ItemStack getItem(LivingExperienceDropEvent o) {
         return ItemStack.EMPTY;
      }
   };
   private static final GenericAttributeMapFactory FACTORY = new GenericAttributeMapFactory();
   private final GenericRuleEvaluator ruleEvaluator;
   private Result result;
   private Integer xp = null;
   private float multxp = 1.0F;
   private float addxp = 0.0F;

   private ExperienceRule(AttributeMap map) {
      super(InControl.setup.getLogger());
      this.ruleEvaluator = new GenericRuleEvaluator(map);
      this.addActions(map, new ModRuleCompatibilityLayer());
   }

   public static ExperienceRule parse(JsonElement element) {
      if (element == null) {
         return null;
      } else {
         AttributeMap map = FACTORY.parse(element);
         return new ExperienceRule(map);
      }
   }

   public int modifyXp(int xpIn) {
      if (this.xp != null) {
         xpIn = this.xp.intValue();
      }

      return (int)((float)xpIn * this.multxp + this.addxp);
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

      if (map.has(RuleKeys.ACTION_SETXP)) {
         this.xp = (Integer)map.get(RuleKeys.ACTION_SETXP);
      }

      if (map.has(RuleKeys.ACTION_ADDXP)) {
         this.addxp = ((Float)map.get(RuleKeys.ACTION_ADDXP)).floatValue();
      }

      if (map.has(RuleKeys.ACTION_MULTXP)) {
         this.multxp = ((Float)map.get(RuleKeys.ACTION_MULTXP)).floatValue();
      }

   }

   public boolean match(LivingExperienceDropEvent event) {
      return this.ruleEvaluator.match(event, EVENT_QUERY);
   }

   public Result getResult() {
      return this.result;
   }

   static {
      FACTORY.attribute(Attribute.create(RuleKeys.MINTIME)).attribute(Attribute.create(RuleKeys.MAXTIME)).attribute(Attribute.create(RuleKeys.MINLIGHT)).attribute(Attribute.create(RuleKeys.MAXLIGHT)).attribute(Attribute.create(RuleKeys.MINHEIGHT)).attribute(Attribute.create(RuleKeys.MAXHEIGHT)).attribute(Attribute.create(RuleKeys.MINDIFFICULTY)).attribute(Attribute.create(RuleKeys.MAXDIFFICULTY)).attribute(Attribute.create(RuleKeys.MINSPAWNDIST)).attribute(Attribute.create(RuleKeys.MAXSPAWNDIST)).attribute(Attribute.create(RuleKeys.RANDOM)).attribute(Attribute.create(RuleKeys.INBUILDING)).attribute(Attribute.create(RuleKeys.INCITY)).attribute(Attribute.create(RuleKeys.INSTREET)).attribute(Attribute.create(RuleKeys.INSPHERE)).attribute(Attribute.create(RuleKeys.PASSIVE)).attribute(Attribute.create(RuleKeys.HOSTILE)).attribute(Attribute.create(RuleKeys.SEESKY)).attribute(Attribute.create(RuleKeys.WEATHER)).attribute(Attribute.create(RuleKeys.TEMPCATEGORY)).attribute(Attribute.create(RuleKeys.DIFFICULTY)).attribute(Attribute.create(RuleKeys.STRUCTURE)).attribute(Attribute.create(RuleKeys.PLAYER)).attribute(Attribute.create(RuleKeys.REALPLAYER)).attribute(Attribute.create(RuleKeys.FAKEPLAYER)).attribute(Attribute.create(RuleKeys.WINTER)).attribute(Attribute.create(RuleKeys.SUMMER)).attribute(Attribute.create(RuleKeys.SPRING)).attribute(Attribute.create(RuleKeys.AUTUMN)).attribute(Attribute.create(RuleKeys.STATE)).attribute(Attribute.create(RuleKeys.PSTATE)).attribute(Attribute.createMulti(RuleKeys.MOB)).attribute(Attribute.createMulti(RuleKeys.MOD)).attribute(Attribute.createMulti(RuleKeys.BLOCK)).attribute(Attribute.create(RuleKeys.BLOCKOFFSET)).attribute(Attribute.createMulti(RuleKeys.BIOME)).attribute(Attribute.createMulti(RuleKeys.BIOMETYPE)).attribute(Attribute.createMulti(RuleKeys.DIMENSION)).attribute(Attribute.createMulti(RuleKeys.HELDITEM)).attribute(Attribute.createMulti(RuleKeys.PLAYER_HELDITEM)).attribute(Attribute.createMulti(RuleKeys.OFFHANDITEM)).attribute(Attribute.createMulti(RuleKeys.BOTHHANDSITEM)).attribute(Attribute.create(RuleKeys.ACTION_RESULT)).attribute(Attribute.create(RuleKeys.ACTION_SETXP)).attribute(Attribute.create(RuleKeys.ACTION_ADDXP)).attribute(Attribute.create(RuleKeys.ACTION_MULTXP));
   }
}
