package xzeroair.trinkets.capabilities.race;

import com.google.common.base.Objects;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.api.events.TransformationEvent;
import xzeroair.trinkets.attributes.JumpAttribute;
import xzeroair.trinkets.attributes.RaceAttribute.RaceAttribute;
import xzeroair.trinkets.capabilities.CapabilityBase;
import xzeroair.trinkets.entity.AlphaWolf;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.SyncRaceDataPacket;
import xzeroair.trinkets.network.trinketcontainer.OpenTrinketGui;
import xzeroair.trinkets.races.EntityRace;
import xzeroair.trinkets.races.EntityRacePropertiesHandler;
import xzeroair.trinkets.races.IRaceProvider;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.helpers.AttributeHelper;
import xzeroair.trinkets.util.helpers.StringUtils;
import xzeroair.trinkets.util.interfaces.IAccessoryInterface;

public class EntityProperties extends CapabilityBase<EntityProperties, EntityLivingBase> {
   protected boolean first_login = true;
   protected boolean login = true;
   protected boolean changed = false;
   protected boolean sync = false;
   protected EntityRace previous = EntityRaces.none;
   protected EntityRace original = EntityRaces.none;
   protected EntityRace imbued = EntityRaces.none;
   protected EntityRace current = EntityRaces.none;
   protected int widthValue = 100;
   protected int heightValue = 100;
   protected float defaultWidth = 1.8F;
   protected float defaultHeight = 0.6F;
   protected boolean traitShown = true;
   protected String traitColor = "16777215";
   protected boolean isFake = false;
   protected KeybindHandler keybindHandler;
   protected EntityRacePropertiesHandler properties;
   protected AbilityHandler abilities;
   protected float stepHeightPrev = 0.6F;
   protected boolean onGround;
   protected boolean isChild;
   protected BlockPos prevBlockpos;
   protected double prev_posx;
   protected double prev_posy;
   protected double prev_posz;

   public EntityProperties(EntityLivingBase e) {
      super(e);
      this.widthValue = 100;
      this.heightValue = 100;
      this.defaultHeight = e.height;
      this.defaultWidth = e.width;
      this.original = EntityRaces.none;
      this.previous = EntityRaces.none;
      this.imbued = EntityRaces.none;
      this.current = EntityRaces.none;
      this.abilities = new AbilityHandler((EntityLivingBase)this.object);
      this.properties = this.current.getRaceHandler((EntityLivingBase)this.object).setEntityProperties(this);
   }

   public NBTTagCompound getTag() {
      NBTTagCompound tag = ((EntityLivingBase)this.object).getEntityData();
      if (tag != null) {
         NBTTagCompound persistentData;
         if (this.object instanceof EntityPlayer) {
            if (!tag.hasKey("PlayerPersisted")) {
               tag.setTag("PlayerPersisted", new NBTTagCompound());
            }

            persistentData = tag.getCompoundTag("PlayerPersisted");
         } else {
            persistentData = tag;
         }

         String capTag = "xat.race";
         if (!persistentData.hasKey("xat.race")) {
            persistentData.setTag("xat.race", new NBTTagCompound());
         }

         return persistentData.getCompoundTag("xat.race");
      } else {
         return super.getTag();
      }
   }

   public AbilityHandler getAbilityHandler() {
      return this.abilities;
   }

   @SideOnly(Side.CLIENT)
   public void onRender(RenderLivingBase renderer, boolean isSlim, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.getRaceHandler().doRenderLayer(renderer, this.isFake(), isSlim, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
   }

   public void onUpdatePre() {
      World world = ((EntityLivingBase)this.object).getEntityWorld();
      boolean isClient = world.isRemote;
      if (!(this.object instanceof FakePlayer) && this.object instanceof EntityPlayer) {
         this.onGround = ((EntityLivingBase)this.object).onGround;
      }

   }

   public void onUpdate() {
      World world = ((EntityLivingBase)this.object).getEntityWorld();
      boolean isClient = world.isRemote;
      if (isClient && TrinketsConfig.CLIENT.debug.showMovementSpeed) {
         StringUtils.sendMessageToPlayer((Entity)this.object, "Bp/t:" + this.entitySpeed((EntityLivingBase)this.object), true);
      }

      this.stepHeightHandler();
      if (!(this.object instanceof FakePlayer) && this.object instanceof EntityPlayer) {
         this.onPlayerUpdate(world, (EntityPlayer)this.object);
      } else {
         this.onEntityUpdate(world, (EntityLivingBase)this.object);
      }

      if (this.sync) {
         this.sendInformationToTracking();
         this.sync = false;
      }

      this.setLogin(false);
      this.setFirstLogin(false);
      if (!isClient) {
         BlockPos blockpos = new BlockPos((Entity)this.object);
         if (!Objects.equal(this.prevBlockpos, blockpos)) {
            this.prevBlockpos = blockpos;
         }
      }

   }

   public void onPlayerUpdate(World world, EntityPlayer player) {
      boolean isClient = world.isRemote;
      if (!isClient && !this.getCurrentRace().isNone()) {
         player.onGround = this.onGround;
      }

      if (this.isLogin()) {
         this.getRaceHandler().setFirstUpdate(true);
      }

      this.updateRace();
      this.abilities.updateAbilityHandler();
      this.getRaceHandler().onTick();
      if (this.sync && !isClient) {
         this.sendInformationToPlayer(player);
      }

   }

   public void onEntityUpdate(World world, EntityLivingBase entity) {
      if (!((EntityLivingBase)this.object).isChild()) {
         boolean isClient = world.isRemote;
         ResourceLocation regName = EntityRegistry.getEntry(entity.getClass()).getRegistryName();
         String modID = regName.getResourceDomain();
         String name = regName.getResourcePath();
         if (!modID.equalsIgnoreCase("iceandfire")) {
            if (this.isFirstLogin()) {
               IAttributeInstance stepheight = ((EntityLivingBase)this.object).getAttributeMap().getAttributeInstance(JumpAttribute.stepHeight);
               if (stepheight != null) {
                  stepheight.setBaseValue((double)((EntityLivingBase)this.object).stepHeight);
               }

               this.stepHeightPrev = ((EntityLivingBase)this.object).stepHeight;
               this.setDefaultHeight(((EntityLivingBase)this.object).height);
               this.setDefaultWidth(((EntityLivingBase)this.object).width);
               this.saveToNBT(this.getTag());
            }

            if (this.isLogin()) {
               this.scheduleResync();
            }

            this.updateRace();
            this.getRaceHandler().onTick();
            if (!(this.object instanceof EntityPlayer)) {
               List<TrinketHelper.SlotInformation> equipment = TrinketHelper.getSlotInfoForArmor((EntityLivingBase)this.object, (s) -> {
                  return !s.isEmpty() && s.getItem() instanceof IAccessoryInterface;
               });
               if (!equipment.isEmpty()) {
                  for(TrinketHelper.SlotInformation info : equipment) {
                     ItemStack equipStack = info.getStackFromHandler((EntityLivingBase)this.object);
                     if (!equipStack.isEmpty() && equipStack.getItem() instanceof IAccessoryInterface) {
                        IAccessoryInterface item = (IAccessoryInterface)equipStack.getItem();
                        item.onEntityArmorTick(world, (EntityLivingBase)this.object, equipStack);
                     }
                  }
               }
            }

         }
      }
   }

   public void onChildUpdate(World world, EntityLivingBase entity) {
      boolean isClient = world.isRemote;
      if (this.isFirstLogin()) {
         IAttributeInstance stepheight = ((EntityLivingBase)this.object).getAttributeMap().getAttributeInstance(JumpAttribute.stepHeight);
         if (stepheight != null) {
            stepheight.setBaseValue((double)((EntityLivingBase)this.object).stepHeight);
         }

         this.stepHeightPrev = ((EntityLivingBase)this.object).stepHeight;
         this.setDefaultHeight(((EntityLivingBase)this.object).height);
         this.setDefaultWidth(((EntityLivingBase)this.object).width);
         this.saveToNBT(this.getTag());
      }

      if (this.isLogin()) {
         this.scheduleResync();
      }

      this.updateRace();
      this.getRaceHandler().onTick();
   }

   public void updateRace() {
      World world = ((EntityLivingBase)this.object).getEntityWorld();
      boolean isClient = world.isRemote;
      if (!isClient) {
         EntityRace race = this.getEntityRace();
         TransformationEvent.RaceUpdateEvent UpdateEvent = new TransformationEvent.RaceUpdateEvent((EntityLivingBase)this.object, this, this.getRaceHandler().getRace(), race);
         if (!MinecraftForge.EVENT_BUS.post(UpdateEvent) && UpdateEvent.raceChanged()) {
            EntityRace newRace = UpdateEvent.getNewRace();
            if (newRace == null) {
               newRace = EntityRaces.none;
            }

            EntityRacePropertiesHandler oldProperties = this.getRaceHandler();
            TransformationEvent.EndTransformation end = new TransformationEvent.EndTransformation((EntityLivingBase)this.object, this, oldProperties.getRace());
            if (MinecraftForge.EVENT_BUS.post(end)) {
               return;
            }

            oldProperties.onTransformEnd();
            AttributeHelper.removeAttributesByUUID((EntityLivingBase)this.object, this.getPreviousRace().getUUID(), oldProperties.getRace().getUUID());
            this.setPreviousRace(oldProperties.getRace());
            Entity mount = ((EntityLivingBase)this.object).getRidingEntity();
            if (mount instanceof AlphaWolf) {
               ((EntityLivingBase)this.object).dismountRidingEntity();
            }

            IAttributeInstance attribute = ((EntityLivingBase)this.object).getAttributeMap().getAttributeInstance(JumpAttribute.stepHeight);
            if (attribute != null && (double)((EntityLivingBase)this.object).stepHeight != attribute.getBaseValue()) {
               ((EntityLivingBase)this.object).stepHeight = (float)attribute.getBaseValue();
            }

            this.properties = newRace.getRaceHandler((EntityLivingBase)this.object).setEntityProperties(this);
            this.properties.onTransform();
            this.setCurrent(newRace);
            TransformationEvent.StartTransformation start = new TransformationEvent.StartTransformation((EntityLivingBase)this.object, this, this.properties.getRace());
            MinecraftForge.EVENT_BUS.post(start);
            this.scheduleResync();
         }

      }
   }

   public BlockPos getPrevBlockpos() {
      if (this.prevBlockpos == null) {
         this.prevBlockpos = ((EntityLivingBase)this.object).getPosition();
      }

      return this.prevBlockpos;
   }

   public KeybindHandler getKeybindHandler() {
      if (this.keybindHandler == null) {
         this.keybindHandler = new KeybindHandler();
      }

      return this.keybindHandler;
   }

   private EntityRace getEntityRace() {
      EntityRace potionRace = this.getPotionRace();
      if (potionRace != null && !potionRace.isNone()) {
         this.setFake(true);
         return potionRace;
      } else {
         ItemStack raceProvider = this.getRaceProvider();
         if (!raceProvider.isEmpty()) {
            this.setFake(true);
            return ((IRaceProvider)raceProvider.getItem()).getRace();
         } else {
            EntityRace imbued = this.getImbuedRace();
            if (imbued != null && !imbued.equals(EntityRaces.none)) {
               this.setFake(false);
               return imbued;
            } else {
               this.setFake(false);
               return this.getOriginalRace();
            }
         }
      }
   }

   protected EntityRace getPotionRace() {
      IAttributeInstance race = ((EntityLivingBase)this.object).getEntityAttribute(RaceAttribute.ENTITY_RACE);
      if (race != null && !race.getModifiers().isEmpty()) {
         Iterator var2 = race.getModifiers().iterator();
         if (var2.hasNext()) {
            AttributeModifier modifier = (AttributeModifier)var2.next();
            return EntityRace.getByUUID(modifier.getID());
         }
      }

      return null;
   }

   protected ItemStack getRaceProvider() {
      int count = TrinketHelper.countAccessories((EntityLivingBase)this.object, (stack) -> {
         return stack.getItem() instanceof IRaceProvider;
      });
      return count <= 1 && count >= 1 ? TrinketHelper.getAccessory((EntityLivingBase)this.object, (stack) -> {
         return stack.getItem() instanceof IRaceProvider;
      }) : ItemStack.EMPTY;
   }

   private void stepHeightHandler() {
      IAttributeInstance attribute = ((EntityLivingBase)this.object).getEntityAttribute(JumpAttribute.stepHeight);
      if (attribute != null && !attribute.getModifiers().isEmpty()) {
         double f = attribute.getAttributeValue();
         float defaultStepHeight = (float)attribute.getBaseValue();
         float result = (float)f;
         float step = ((EntityLivingBase)this.object).stepHeight;
         ((EntityLivingBase)this.object).stepHeight = defaultStepHeight;
         if (step - ((EntityLivingBase)this.object).stepHeight == 0.0F) {
            ((EntityLivingBase)this.object).stepHeight = result;
            this.stepHeightPrev = defaultStepHeight;
         } else if (step - ((EntityLivingBase)this.object).stepHeight - result == -defaultStepHeight) {
            ((EntityLivingBase)this.object).stepHeight = result;
            this.stepHeightPrev = defaultStepHeight;
         } else {
            float stepP = this.stepHeightPrev;
            this.stepHeightPrev = step;
            if (result - defaultStepHeight + step == result - defaultStepHeight + stepP) {
               ((EntityLivingBase)this.object).stepHeight = result - defaultStepHeight + this.stepHeightPrev;
            }
         }
      }

   }

   public void startVec(EntityLivingBase entity) {
      this.prev_posx = entity.posX;
      this.prev_posy = entity.posY;
      this.prev_posz = entity.posZ;
   }

   public Vec3d lastVec() {
      Vec3d lastPosVec = new Vec3d(this.prev_posx, this.prev_posy, this.prev_posz);
      return lastPosVec;
   }

   public double entitySpeed(EntityLivingBase entity) {
      Vec3d currentPosVec = new Vec3d(entity.posX, entity.posY, entity.posZ);
      double distanceTraveled = this.lastVec().distanceTo(currentPosVec);
      this.startVec(entity);
      return distanceTraveled;
   }

   public void onLogin() {
      World world = ((EntityLivingBase)this.object).getEntityWorld();
      if (!world.isRemote && this.object instanceof EntityPlayerMP) {
         if (this.isFirstLogin()) {
            if (TrinketsConfig.SERVER.races.selectionMenu) {
               NetworkHandler.sendTo(new OpenTrinketGui(4), (EntityPlayerMP)this.object);
            }
         } else {
            this.sendInformationToPlayer((EntityLivingBase)this.object, this.getTag());
         }
      }

   }

   public void onLogoff() {
      if (this.getCurrentRace().equals(EntityRaces.goblin) && ((EntityLivingBase)this.object).getRidingEntity() instanceof AlphaWolf) {
         ((EntityLivingBase)this.object).dismountRidingEntity();
      }

   }

   public void sendInformationToPlayer(EntityLivingBase receiver) {
      World world = ((EntityLivingBase)this.object).getEntityWorld();
      if (!world.isRemote) {
         this.sendInformationToPlayer(receiver, this.saveToNBT(this.getTag()));
      }

   }

   public void sendInformationToPlayer(EntityLivingBase receiver, NBTTagCompound tag) {
      World world = ((EntityLivingBase)this.object).getEntityWorld();
      if (!world.isRemote && receiver instanceof EntityPlayerMP) {
         NetworkHandler.sendTo(new SyncRaceDataPacket((EntityLivingBase)this.object, tag), (EntityPlayerMP)receiver);
      }

   }

   public void sendInformationToTracking() {
      World world = ((EntityLivingBase)this.object).getEntityWorld();
      if (!world.isRemote && world instanceof WorldServer) {
         WorldServer w = (WorldServer)world;
         NetworkHandler.sendToClients(w, ((EntityLivingBase)this.object).getPosition(), new SyncRaceDataPacket((EntityLivingBase)this.object, this.saveToNBT(this.getTag())));
      }

   }

   public void sendInformationToServer() {
      World world = ((EntityLivingBase)this.object).getEntityWorld();
      if (world.isRemote) {
         NetworkHandler.sendToServer(new SyncRaceDataPacket((EntityLivingBase)this.object, this.saveToNBT(this.getTag())));
      }

   }

   /** @deprecated */
   @Deprecated
   public int getSize() {
      return this.getHeightValue();
   }

   public boolean isNormalSize() {
      return ((EntityLivingBase)this.object).height == this.getDefaultHeight() && ((EntityLivingBase)this.object).width == this.getDefaultWidth();
   }

   public boolean hasRace() {
      return !this.current.equals(EntityRaces.none);
   }

   public EntityRacePropertiesHandler getRaceHandler() {
      return this.properties.setEntityProperties(this);
   }

   public EntityRace getPreviousRace() {
      return this.previous;
   }

   public void setPreviousRace(EntityRace race) {
      if (this.previous != race) {
         if (race != null) {
            this.previous = race;
         } else {
            this.previous = EntityRaces.none;
         }
      }

   }

   public EntityRace getCurrentRace() {
      return this.current;
   }

   public void setCurrent(EntityRace race) {
      if (this.current != race) {
         if (race != null) {
            this.current = race;
         } else {
            this.current = EntityRaces.none;
         }
      }

   }

   public EntityRace getImbuedRace() {
      return this.imbued;
   }

   public void setImbuedRace(EntityRace race) {
      if (this.imbued != race) {
         if (race != null) {
            this.imbued = race;
         } else {
            this.imbued = EntityRaces.none;
         }
      }

   }

   public EntityRace getOriginalRace() {
      return this.original;
   }

   public void setOriginalRace(EntityRace race) {
      if (this.original != race) {
         if (race != null) {
            this.original = race;
         } else {
            this.original = EntityRaces.none;
         }
      }

   }

   public float getDefaultWidth() {
      return this.defaultWidth;
   }

   private void setDefaultWidth(float defaultWidth) {
      if (this.defaultWidth != defaultWidth) {
         this.defaultWidth = defaultWidth;
      }

   }

   public float getDefaultHeight() {
      return this.defaultHeight;
   }

   private void setDefaultHeight(float defaultHeight) {
      if (this.defaultHeight != defaultHeight) {
         this.defaultHeight = defaultHeight;
      }

   }

   public int getHeightValue() {
      return this.heightValue;
   }

   public void setHeightValue(int height) {
      if (this.heightValue != height) {
         this.heightValue = height;
      }

   }

   public int getWidthValue() {
      return this.widthValue;
   }

   public void setWidthValue(int width) {
      if (this.widthValue != width) {
         this.widthValue = width;
      }

   }

   public boolean isLogin() {
      return this.login;
   }

   public void setLogin(boolean login) {
      if (this.login != login) {
         this.login = login;
      }

   }

   public boolean isFirstLogin() {
      return this.first_login;
   }

   public void setFirstLogin(boolean firstLogin) {
      if (this.first_login != firstLogin) {
         this.first_login = firstLogin;
      }

   }

   public boolean isFake() {
      return this.isFake;
   }

   public void setFake(boolean isFake) {
      if (this.isFake != isFake) {
         this.isFake = isFake;
         this.scheduleResync();
      }

   }

   public boolean isChild() {
      return this.isChild;
   }

   public void setChild(boolean isChild) {
      if (this.isChild != isChild) {
         this.isChild = isChild;
      }

   }

   public void scheduleResync() {
      this.sync = true;
   }

   public void copyFrom(EntityProperties source, boolean wasDeath, boolean keepInv) {
      this.first_login = source.first_login;
      this.login = source.login;
      this.defaultWidth = source.defaultWidth;
      this.defaultHeight = source.defaultHeight;
      this.original = source.original;
      this.imbued = source.imbued;
      this.isChild = source.isChild;
      if (wasDeath && !keepInv) {
         this.previous = source.current;
         this.current = this.imbued.isNone() ? this.original : this.imbued;
         this.heightValue = this.current.getRaceHeight();
         this.widthValue = this.current.getRaceWidth();
      } else {
         this.current = source.current;
         this.previous = source.previous;
         this.heightValue = source.heightValue;
         this.widthValue = source.widthValue;
      }

      this.properties = this.current.getRaceHandler((EntityLivingBase)this.object).setEntityProperties(this);
      this.properties.onTransform();

      try {
         this.getRaceHandler().copyFrom(source.getRaceHandler(), wasDeath, keepInv);
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      try {
         this.getAbilityHandler().copyFrom(source.getAbilityHandler(), wasDeath, keepInv);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      this.scheduleResync();
   }

   public NBTTagCompound saveToNBT(NBTTagCompound compound) {
      compound.setString("original_race", this.getOriginalRace().getRegistryName().toString());
      compound.setString("imbued_race", this.getImbuedRace().getRegistryName().toString());
      compound.setString("prev_race", this.getPreviousRace().getRegistryName().toString());
      compound.setString("current_race", this.getCurrentRace().getRegistryName().toString());
      compound.setInteger("heightValue", this.getHeightValue());
      compound.setInteger("widthValue", this.getWidthValue());
      compound.setFloat("default_height", this.getDefaultHeight());
      compound.setFloat("default_width", this.getDefaultWidth());
      compound.setBoolean("login", this.isLogin());
      compound.setBoolean("first_login", this.isFirstLogin());
      compound.setBoolean("fake", this.isFake());
      compound.setBoolean("child", this.isChild());
      this.getRaceHandler().savedNBTData(compound);
      this.getAbilityHandler().saveAbilitiesToNBT(compound);
      return compound;
   }

   public void loadFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("first_login")) {
         this.first_login = compound.getBoolean("first_login");
      }

      if (compound.hasKey("login")) {
         this.login = compound.getBoolean("login");
      }

      if (compound.hasKey("original_race")) {
         EntityRace originalRace = EntityRace.getByNameOrId(compound.getString("original_race"));
         if (originalRace == null) {
            this.original = EntityRaces.none;
         } else {
            this.original = originalRace;
         }
      }

      if (compound.hasKey("imbued_race")) {
         EntityRace imbuedRace = EntityRace.getByNameOrId(compound.getString("imbued_race"));
         if (imbuedRace == null) {
            this.imbued = EntityRaces.none;
         } else {
            this.imbued = imbuedRace;
         }
      }

      if (compound.hasKey("prev_race")) {
         EntityRace previousRace = EntityRace.getByNameOrId(compound.getString("prev_race"));
         if (previousRace == null) {
            this.previous = EntityRaces.none;
         } else {
            this.previous = previousRace;
         }
      }

      if (compound.hasKey("current_race")) {
         EntityRace currentRace = EntityRace.getByNameOrId(compound.getString("current_race"));
         if (currentRace == null) {
            this.current = EntityRaces.none;
         } else {
            this.current = currentRace;
         }

         if (this.properties.getRace() != this.current) {
            this.properties = this.current.getRaceHandler((EntityLivingBase)this.object).setEntityProperties(this);
            this.properties.onTransform();
         }
      }

      if (compound.hasKey("heightValue")) {
         this.heightValue = compound.getInteger("heightValue");
      }

      if (compound.hasKey("widthValue")) {
         this.widthValue = compound.getInteger("widthValue");
      }

      if (compound.hasKey("default_height")) {
         this.defaultHeight = compound.getFloat("default_height");
      }

      if (compound.hasKey("default_width")) {
         this.defaultWidth = compound.getFloat("default_width");
      }

      if (compound.hasKey("fake")) {
         this.isFake = compound.getBoolean("fake");
      }

      if (compound.hasKey("child")) {
         this.isChild = compound.getBoolean("child");
      }

      this.getRaceHandler().loadNBTData(compound);
      this.getAbilityHandler().loadAbilitiesFromNBT(compound);
   }

   private static class TempCache<A, B> {
      private final A first;
      private final B second;

      public TempCache(A first, B second) {
         this.first = first;
         this.second = second;
      }

      public A getFirst() {
         return this.first;
      }

      public B getSecond() {
         return this.second;
      }
   }
}
