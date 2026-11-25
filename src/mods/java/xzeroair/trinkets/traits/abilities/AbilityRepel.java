package xzeroair.trinkets.traits.abilities;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.client.keybinds.ModKeyBindings;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.items.trinkets.TrinketPolarized;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IHeldAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IKeyBindInterface;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableInventoryAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IToggleAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigPolarizedStone;
import xzeroair.trinkets.util.handlers.Counter;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class AbilityRepel extends Ability implements ITickableAbility, IHeldAbility, ITickableInventoryAbility, IToggleAbility, IKeyBindInterface {
   private static final ConfigPolarizedStone serverConfig;

   public AbilityRepel() {
      super("repel");
   }

   public void tickAbility(EntityLivingBase entity) {
      if (this.abilityEnabled()) {
         this.blockArrows(entity);
      }

   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean inHand) {
      if (stack.getItem() instanceof TrinketPolarized) {
         Capabilities.getTrinketProperties(stack, (prop) -> {
            this.toggleAbility(prop.altAbility());
         });
      }

   }

   public void blockArrows(EntityLivingBase entity) {
      if (serverConfig.exhaustion) {
         Predicate<EntityLivingBase> filter = Predicates.and(EntitySelectors.CAN_AI_TARGET, EntitySelectors.IS_ALIVE);
         boolean flag = filter.apply(entity);
         if (flag) {
            MagicStats magic = Capabilities.getMagicStats(entity);
            if (magic != null) {
               float exhaustRate = serverConfig.exhaust_rate;
               this.tickHandler.addCounter("repel.ticks", serverConfig.exhaust_ticks, false, true, false);
               Counter counter = this.tickHandler.getCounter("repel.ticks");
               if (exhaustRate > magic.getMana()) {
                  return;
               }

               if (counter != null && counter.Tick()) {
                  magic.spendMana(exhaustRate);
               }
            }
         }
      }

      try {
         AxisAlignedBB bBox = entity.getEntityBoundingBox();
         List<String> cfg = Arrays.asList(serverConfig.repelledEntities);
         Predicate<Entity> Targets = Predicates.and(new Predicate[]{EntitySelectors.CAN_AI_TARGET, EntitySelectors.IS_ALIVE, (ent) -> {
            return ent != null && !(ent instanceof EntityPlayer) && EntityRegistry.getEntry(ent.getClass()) != null && EntityRegistry.getEntry(ent.getClass()).getRegistryName() != null && cfg.contains(EntityRegistry.getEntry(ent.getClass()).getRegistryName().toString());
         }});

         for(Entity repelledEntity : entity.world.getEntitiesWithinAABB(Entity.class, bBox.grow(serverConfig.repelRange), Targets)) {
            Vec3d playerVec3 = entity.getLookVec();
            repelledEntity.motionX = playerVec3.x * 0.3D;
            repelledEntity.motionY = playerVec3.y * 0.3D;
            repelledEntity.motionZ = playerVec3.z * 0.3D;
         }
      } catch (Exception var9) {
         var9.printStackTrace();
      }

   }

   public boolean abilityEnabled() {
      return this.enabled;
   }

   public IToggleAbility toggleAbility(boolean enabled) {
      this.enabled = enabled;
      return this;
   }

   public IToggleAbility toggleAbility(int value) {
      this.value = value;
      return this;
   }

   public boolean onKeyPress(Entity entity, boolean Aux) {
      if (Aux) {
         boolean enabled = this.abilityEnabled();
         boolean client = entity.world.isRemote;
         this.toggleAbility(!enabled);
         if (client && entity instanceof EntityPlayer) {
            TranslationHelper helper = TranslationHelper.INSTANCE;
            ItemStack s = new ItemStack(ModItems.trinkets.TrinketPolarized);
            String repelMode = (new TextComponentTranslation(s.getUnlocalizedName() + ".repelmode", new Object[0])).getFormattedText();
            TranslationHelper.KeyEntry key = new TranslationHelper.OptionEntry("repeltoggle", serverConfig.repell, helper.toggleCheckTranslation(!enabled));
            ((EntityPlayer)entity).sendStatusMessage(new TextComponentString(helper.formatAddVariables(repelMode, key)), true);
         }

         Capabilities.getEntityProperties(entity, (prop) -> {
            AbilityHandler.AbilityHolder holder = prop.getAbilityHandler().getAbilityHolder(this.getRegistryName().toString());
            TrinketHelper.SlotInformation info = holder != null ? holder.getInfo() : null;
            if (info != null && entity instanceof EntityLivingBase) {
               ItemStack stack = info.getStackFromHandler((EntityLivingBase)entity);
               if (stack.getItem() instanceof TrinketPolarized) {
                  Capabilities.getTrinketProperties(stack, (cap) -> {
                     cap.toggleAltAbility(this.abilityEnabled());
                     cap.sendInformationToPlayer((EntityPlayer)entity, (EntityPlayer)entity);
                  });
               }
            }

         });
      }

      return true;
   }

   @SideOnly(Side.CLIENT)
   public String getKey() {
      return ModKeyBindings.POLARIZED_STONE_ABILITY.getDisplayName();
   }

   @SideOnly(Side.CLIENT)
   public String getAuxKey() {
      return ModKeyBindings.AUX_KEY.getDisplayName();
   }

   public NBTTagCompound saveStorage(NBTTagCompound compound) {
      compound.setBoolean("enabled", this.enabled);
      return compound;
   }

   public void loadStorage(NBTTagCompound compound) {
      if (compound.hasKey("enabled")) {
         this.enabled = compound.getBoolean("enabled");
      }

   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.POLARIZED_STONE;
   }
}
