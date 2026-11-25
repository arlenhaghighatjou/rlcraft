package xzeroair.trinkets.races;

import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.attributes.UpdatingAttribute;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.client.gui.entityPropertiesGui.GuiEntityProperties;
import xzeroair.trinkets.client.gui.hud.mana.ManaHud;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.network.IncreasedReachPacket;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.compat.artemislib.SizeAttribute;
import xzeroair.trinkets.util.config.ConfigHelper;
import xzeroair.trinkets.util.handlers.SizeHandler;
import xzeroair.trinkets.util.helpers.ColorHelper;
import xzeroair.trinkets.util.helpers.RayTraceHelper;
import xzeroair.trinkets.util.helpers.StringUtils;

public abstract class EntityRacePropertiesHandler implements IRaceHandler {
   protected boolean firstUpdate;
   protected boolean firstTransformUpdate;
   protected EntityLivingBase entity;
   protected int targetWidth = 100;
   protected int targetHeight = 100;
   protected EntityRace race;
   protected boolean showTraits;
   protected String traitColor;
   protected String traitColorAlt;
   protected int traitVariant;
   protected EntityProperties properties;
   protected float healthBeforeTransformation;
   protected float maxHealthBeforeTranformation;
   protected double progress = 0.0D;
   protected float cooldown = 0.0F;

   public EntityRacePropertiesHandler(EntityLivingBase e, EntityRace race) {
      this.entity = e;
      this.firstUpdate = true;
      this.firstTransformUpdate = true;
      this.showTraits = true;
      this.race = race;
      this.traitColor = ColorHelper.convertDecimalColorToHexadecimal(race.getPrimaryColor());
      this.traitColorAlt = ColorHelper.convertDecimalColorToHexadecimal(race.getSecondaryColor());
      this.traitVariant = 0;
      this.setTargetHeight(race.getRaceHeight());
      this.setTargetWidth(race.getRaceWidth());
   }

   protected void initAttributes() {
      double d = Double.parseDouble(Reference.DECIMALFORMAT.format(this.TransformationProgress()));
      if (d != 0.0D) {
         World world = this.entity.getEntityWorld();
         String[] raceAttributes = this.race.getRaceAttributes().getAttributes();
         if (raceAttributes.length > 0) {
            for(String entry : raceAttributes) {
               ConfigHelper.AttributeEntry attributeShell = ConfigHelper.getAttributeEntry(entry);
               if (attributeShell != null) {
                  String name = attributeShell.getAttribute();
                  double amount = attributeShell.getAmount();
                  int operation = attributeShell.getOperation();
                  boolean isSaved = attributeShell.isSaved();
                  UpdatingAttribute attribute = (new UpdatingAttribute(this.race.getName() + "." + name, this.race.getUUID(), name)).setSavedInNBT(true);
                  attribute.addModifier(this.entity, amount * d, operation);
               }
            }
         }
      }

   }

   public EntityRacePropertiesHandler setEntityProperties(EntityProperties properties) {
      this.properties = properties;
      return this;
   }

   public EntityRacePropertiesHandler setFirstUpdate(boolean firstUpdate) {
      this.firstUpdate = firstUpdate;
      return this;
   }

   public EntityRace getRace() {
      return this.race;
   }

   public void addAbility(IAbilityInterface ability) {
      if (this.properties != null) {
         this.properties.getAbilityHandler().registerRaceAbility(this.getRace().getRegistryName().toString(), ability);
      }

   }

   @Nullable
   public IAbilityInterface getAbility(String ability) {
      return this.properties != null ? this.properties.getAbilityHandler().getAbility(ability) : null;
   }

   public void removeAbility(String ability) {
      if (this.properties != null) {
         this.properties.getAbilityHandler().removeAbility(ability);
      }

   }

   public void onTransform() {
      this.firstTransformUpdate = true;
      this.healthBeforeTransformation = this.entity.getHealth();
      this.maxHealthBeforeTranformation = this.entity.getMaxHealth();
      if (this.properties == null) {
         this.properties = Capabilities.getEntityProperties(this.entity);
      }

      this.startTransformation();

      try {
         this.loadNBTData(this.properties.getTag());
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public void onTransformEnd() {
      this.endTransformation();
      SizeAttribute artemis = this.getArtemisAttributeSize();
      if (artemis != null) {
         artemis.removeModifiers();
      }

      try {
         this.savedNBTData(this.properties.getTag());
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public void onTick() {
      this.updateSize();
      SizeHandler.setSize(this.entity, this.getHeight(), this.getWidth());
      this.initAttributes();
      this.eyeHeightHandler();
      if (this.isTransformed()) {
         SizeAttribute artemis = this.getArtemisAttributeSize();
         if (artemis != null) {
            artemis.addModifiers();
         }

         this.whileTransformed();
         this.firstTransformUpdate = false;
      }

      if (this.cooldown > 0.0F) {
         --this.cooldown;
      } else {
         this.cooldown = 0.0F;
      }

   }

   public void interact(PlayerInteractEvent event) {
      if (TrinketsConfig.SERVER.misc.reach) {
         EntityPlayer player = event.getEntityPlayer();
         boolean isClient = player.world.isRemote;
         if (isClient) {
            if (this.cooldown != 0.0F) {
               return;
            }

            KeyBinding lClick = Minecraft.getMinecraft().gameSettings.keyBindAttack;
            KeyBinding rClick = Minecraft.getMinecraft().gameSettings.keyBindUseItem;
            IAttributeInstance reach = player.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE);
            if (reach.getAttributeValue() > 5.0D) {
               RayTraceResult result = RayTraceHelper.rayTrace(player, reach.getAttributeValue() * 0.8D);
               if (result != null && result.typeOfHit == Type.ENTITY) {
                  Entity entity = result.entityHit;
                  Vec3d vec = result.hitVec;
                  if (lClick.isKeyDown()) {
                     NetworkHandler.sendToServer(new IncreasedReachPacket(player, EnumHand.MAIN_HAND, entity, vec.x, vec.y, vec.z));
                     this.cooldown = player.getCooldownPeriod();
                     Trinkets.proxy.renderEffect(3, player.getEntityWorld(), result.hitVec.x, result.hitVec.y + (double)(entity.height * 0.5F), result.hitVec.z, 0.0D, 0.0D, 0.0D, 0, 1.0F, 1.0F);
                  } else if (rClick.isKeyDown()) {
                     NetworkHandler.sendToServer(new IncreasedReachPacket(player, EnumHand.OFF_HAND, entity, vec.x, vec.y, vec.z));
                  }
               }
            }
         }
      }

   }

   public boolean isTransforming() {
      return this.properties.getHeightValue() != this.getTargetHeight() || this.properties.getWidthValue() != this.getTargetWidth();
   }

   public boolean isTransformed() {
      return this.properties.getHeightValue() == this.getTargetHeight() && this.properties.getWidthValue() == this.getTargetWidth();
   }

   public double TransformationProgress() {
      return !this.isTransformed() && !this.isTransforming() ? 1.0D : this.progress;
   }

   protected void updateSize() {
      if (!this.isTransformed() && this.isTransforming() || this.TransformationProgress() < 1.0D) {
         int height = this.properties.getHeightValue();
         int width = this.properties.getWidthValue();
         BiFunction<Integer, Integer, Integer> increment = (x, y) -> {
            if (x.intValue() < y.intValue()) {
               return x.intValue() + 1;
            } else {
               return x.intValue() > y.intValue() ? x.intValue() - 1 : x;
            }
         };
         int h = ((Integer)increment.apply(Integer.valueOf(height), Integer.valueOf(this.getTargetHeight()))).intValue();
         this.properties.setHeightValue(h);
         int w = ((Integer)increment.apply(Integer.valueOf(width), Integer.valueOf(this.getTargetWidth()))).intValue();
         this.properties.setWidthValue(w);
         int previousRaceTargetHeight = this.properties.getPreviousRace().getRaceHeight();
         int previousRaceTargetWidth = this.properties.getPreviousRace().getRaceWidth();
         double heightProgress = this.transformProgress(previousRaceTargetHeight, this.getTargetHeight(), height);
         double widthProgress = this.transformProgress(previousRaceTargetWidth, this.getTargetWidth(), width);
         double finalValue = this.isTransformed() ? 1.0D : StringUtils.getAccurateDouble(heightProgress * widthProgress);
         if (finalValue >= 0.0D && finalValue <= 1.0D && this.progress != finalValue) {
            this.progress = finalValue;
         }

         if (this.TransformationProgress() >= 1.0D) {
            float heal = (float)StringUtils.getAccurateDouble((double)(this.entity.getMaxHealth() - this.maxHealthBeforeTranformation - (this.maxHealthBeforeTranformation - this.healthBeforeTransformation)));
            if (heal > 0.0F) {
               this.entity.heal(heal);
            }
         }
      }

   }

   protected double transformProgress(int previousTarget, int currentTarget, int currentValue) {
      double rtn = MathHelper.pct((double)currentValue + 0.0D, (double)previousTarget + 0.0D, (double)currentTarget + 0.0D);
      if (rtn < 0.01D) {
         return 0.0D;
      } else {
         return rtn > 1.0D ? 1.0D : rtn;
      }
   }

   protected void eyeHeightHandler() {
      if (this.entity instanceof EntityPlayer) {
         if (!TrinketsConfig.CLIENT.cameraHeight) {
            return;
         }

         EntityPlayer player = (EntityPlayer)this.entity;
         float defaultEyeHeight = player.getDefaultEyeHeight();
         if (!this.isTransforming() && !this.isTransformed()) {
            if (player.eyeHeight != defaultEyeHeight) {
               player.eyeHeight = defaultEyeHeight;
            }
         } else {
            float f = (float)StringUtils.getAccurateDouble((double)(this.getHeight() * 0.85F));
            if (player.isPlayerSleeping()) {
               f = 0.2F;
            } else if (!player.isSneaking()) {
               if (player.isElytraFlying()) {
                  f *= 0.2F;
               }
            } else {
               f -= f / 20.0F;
            }

            if (player.isRiding()) {
               Entity mount = player.getRidingEntity();
               if (mount != null) {
                  float mountHeight = mount.height;
                  double mountOffset = mount.getMountedYOffset();
                  double t = (double)mountHeight - mountOffset;
                  f = MathHelper.clamp(f, mountHeight, f);
               }
            }

            player.eyeHeight = f;
         }
      }

   }

   public int getTargetHeight() {
      return this.targetHeight;
   }

   public void setTargetHeight(int targetHeight) {
      this.targetHeight = targetHeight;
   }

   public int getTargetWidth() {
      return this.targetWidth;
   }

   public void setTargetWidth(int targetWidth) {
      this.targetWidth = targetWidth;
   }

   public float getHeight() {
      float TLHeight = (float)((double)this.properties.getDefaultHeight() * (double)this.properties.getHeightValue() * 0.01D);
      return TLHeight;
   }

   public float getWidth() {
      float TLWidth = (float)((double)this.properties.getDefaultWidth() * (double)this.properties.getWidthValue() * 0.01D);
      return TLWidth;
   }

   private SizeAttribute getArtemisAttributeSize() {
      if (Trinkets.ArtemisLib && TrinketsConfig.compat.artemislib) {
         double h = (double)(this.getTargetHeight() - 100) * 0.01D;
         double w = (double)(this.getTargetWidth() - 100) * 0.01D;
         return new SizeAttribute(this.entity, h, w, 0);
      } else {
         return null;
      }
   }

   public void copyFrom(EntityRacePropertiesHandler source, boolean wasDeath, boolean keepInv) {
      if (this.race == source.race) {
         boolean isNormal = this.race == null ? true : this.race.equals(EntityRaces.none);
         if (wasDeath && keepInv && isNormal) {
            return;
         }

         this.progress = source.progress;
         this.showTraits = source.showTraits;
         this.traitColor = source.traitColor;
         this.traitColorAlt = source.traitColorAlt;
         this.traitVariant = source.traitVariant;
         this.targetHeight = source.targetHeight;
         this.targetWidth = source.targetWidth;
      }

   }

   public boolean isCreativePlayer() {
      boolean flag = this.entity instanceof EntityPlayer && (((EntityPlayer)this.entity).isCreative() || ((EntityPlayer)this.entity).isSpectator());
      return flag;
   }

   public boolean canFly() {
      return this.race == null ? false : this.race.canFly();
   }

   public NBTTagCompound savedNBTData(NBTTagCompound compound) {
      if (this.race != null && !this.race.isNone()) {
         String key = this.race.getRegistryName().toString();
         if (!compound.hasKey(key)) {
            compound.setTag(key, new NBTTagCompound());
         }

         NBTTagCompound rTag = compound.getCompoundTag(key);
         rTag.setBoolean("trait_shown", this.showTraits);
         rTag.setString("trait_color", this.traitColor);
         rTag.setString("trait_color_alt", this.traitColorAlt);
         rTag.setInteger("trait_variant", this.traitVariant);
         rTag.setDouble("transformation_progress", this.progress);
      }

      return compound;
   }

   public void loadNBTData(NBTTagCompound compound) {
      if (this.race != null && !this.race.isNone()) {
         String key = this.race.getRegistryName().toString();
         if (compound.hasKey(key)) {
            NBTTagCompound rTag = compound.getCompoundTag(key);
            if (rTag.hasKey("trait_shown")) {
               this.showTraits = rTag.getBoolean("trait_shown");
            }

            if (rTag.hasKey("trait_color")) {
               this.traitColor = rTag.getString("trait_color");
            }

            if (rTag.hasKey("trait_color_alt")) {
               this.traitColorAlt = rTag.getString("trait_color_alt");
            }

            if (rTag.hasKey("trait_variant")) {
               this.traitVariant = rTag.getInteger("trait_variant");
            }

            if (rTag.hasKey("transformation_progress")) {
               this.progress = rTag.getDouble("transformation_progress");
            }
         }
      }

   }

   public boolean showTraits() {
      return this.showTraits;
   }

   public void setShowTraits(boolean showTraits) {
      this.showTraits = showTraits;
   }

   public String getTraitColor() {
      return this.traitColor;
   }

   public void setTraitColor(String color) {
      this.traitColor = color;
   }

   public String getAltTraitColor() {
      return this.traitColorAlt;
   }

   public void setAltTraitColor(String color) {
      this.traitColorAlt = color;
   }

   public int getTraitVariant() {
      return this.traitVariant;
   }

   public void setTraitVariant(int variant) {
      this.traitVariant = variant;
   }

   public void doRenderPlayerPre(EntityPlayer entity, double x, double y, double z, RenderPlayer renderer, float partialTick) {
      GuiScreen screen = Minecraft.getMinecraft().currentScreen;
      if (entity != Minecraft.getMinecraft().player || screen == null || screen instanceof GuiChat || screen instanceof GuiEntityProperties || screen instanceof ManaHud) {
         if ((this.isTransforming() || this.isTransformed()) && !this.properties.isNormalSize()) {
            double hScale = (double)this.properties.getHeightValue() * 0.01D;
            double wScale = (double)this.properties.getWidthValue() * 0.01D;
            double xLoc = x / wScale - x;
            double yLoc = y / hScale - y;
            double zLoc = z / wScale - z;
            double yOffset = entity.getYOffset();
            Entity mount = entity.getRidingEntity();
            double mountedOffset = entity.isRiding() && mount != null ? mount.getMountedYOffset() : 0.0D;
            double retMountedOffset = -(mountedOffset + yOffset) - 0.1D;
            if (entity.isRiding()) {
               GlStateManager.translate(0.0D, mountedOffset, 0.0D);
               GlStateManager.translate(0.0D, -yOffset, 0.0D);
               GlStateManager.translate(0.0D, retMountedOffset, 0.0D);
            }

            GlStateManager.scale(wScale, hScale, wScale);
            if (entity.isRiding()) {
               GlStateManager.translate(0.0D, -retMountedOffset, 0.0D);
               GlStateManager.translate(0.0D, yOffset, 0.0D);
               GlStateManager.translate(0.0D, -mountedOffset, 0.0D);
            }

            GlStateManager.translate(xLoc, yLoc, zLoc);
         }

      }
   }

   public void doRenderPlayerPost(EntityPlayer entity, double x, double y, double z, RenderPlayer renderer, float partialTick) {
   }

   public <T extends EntityLivingBase> void doRenderLivingSpecialsPre(EntityLivingBase entity, double x, double y, double z, RenderLivingBase<T> renderer, float partialTick) {
      if (entity instanceof EntityPlayer && (this.isTransforming() || this.isTransformed()) && !this.properties.isNormalSize()) {
         GlStateManager.pushMatrix();
         float t2 = this.properties.getDefaultHeight() - entity.height;
         GlStateManager.translate(0.0F, t2, 0.0F);
      }

   }

   public <T extends EntityLivingBase> void doRenderLivingSpecialsPost(EntityLivingBase entity, double x, double y, double z, RenderLivingBase<T> renderer, float partialTick) {
      if (entity instanceof EntityPlayer && (this.isTransforming() || this.isTransformed()) && !this.properties.isNormalSize()) {
         GlStateManager.popMatrix();
      }

   }

   public <T extends EntityLivingBase> void doRenderLivingPre(EntityLivingBase entity, double x, double y, double z, RenderLivingBase<T> renderer, float partialTick) {
      if ((this.isTransforming() || this.isTransformed()) && !this.properties.isNormalSize()) {
         GlStateManager.pushMatrix();
         double hScale = (double)this.properties.getHeightValue() * 0.01D;
         double wScale = (double)this.properties.getWidthValue() * 0.01D;
         double xLoc = x / wScale - x;
         double yLoc = y / hScale - y;
         double zLoc = z / wScale - z;
         double yOffset = entity.getYOffset();
         Entity mount = entity.getRidingEntity();
         double mountedOffset = entity.isRiding() && mount != null ? mount.getMountedYOffset() : 0.0D;
         double retMountedOffset = -(mountedOffset + yOffset) - 0.1D;
         if (entity.isRiding()) {
            GlStateManager.translate(0.0D, mountedOffset, 0.0D);
            GlStateManager.translate(0.0D, -yOffset, 0.0D);
            GlStateManager.translate(0.0D, retMountedOffset, 0.0D);
         }

         GlStateManager.scale(wScale, hScale, wScale);
         if (entity.isRiding()) {
            GlStateManager.translate(0.0D, -retMountedOffset, 0.0D);
            GlStateManager.translate(0.0D, yOffset, 0.0D);
            GlStateManager.translate(0.0D, -mountedOffset, 0.0D);
         }

         GlStateManager.translate(xLoc, yLoc, zLoc);
      }

   }

   public <T extends EntityLivingBase> void doRenderLivingPost(EntityLivingBase entity, double x, double y, double z, RenderLivingBase<T> renderer, float partialTick) {
      if ((this.isTransforming() || this.isTransformed()) && !this.properties.isNormalSize()) {
         GlStateManager.popMatrix();
      }

   }
}
