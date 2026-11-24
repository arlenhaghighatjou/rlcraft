package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.item.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class EntityList
{
    public static final ResourceLocation LIGHTNING_BOLT = new ResourceLocation("lightning_bolt");
    private static final ResourceLocation PLAYER = new ResourceLocation("player");
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Map<ResourceLocation, EntityList.EntityEggInfo> ENTITY_EGGS = Maps.<ResourceLocation, EntityList.EntityEggInfo>newLinkedHashMap();
    private static final Set<ResourceLocation> EXTRA_NAMES = Sets.newHashSet();

    @Nullable
    public static ResourceLocation getKey(Entity entityIn)
    {
        return getKey(entityIn.getClass());
    }

    @Nullable
    public static ResourceLocation getKey(Class <? extends Entity > entityIn)
    {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.EntityRegistry.getEntry(entityIn);
        return entry == null ? null : entry.getRegistryName();
    }

    @Nullable
    public static String getEntityString(Entity entityIn)
    {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.EntityRegistry.getEntry(entityIn.getClass());
        return entry == null ? null : entry.getName();
    }

    @Nullable
    public static String getTranslationName(@Nullable ResourceLocation entityType)
    {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getValue(entityType);
        return entry == null ? null : entry.getName();
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public static Class <? extends Entity > getClassFromID(int entityID)
    {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.registries.GameData.getEntityRegistry().getValue(entityID);
        return entry == null ? null : entry.getEntityClass();
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public static Class <? extends Entity > getClassFromName(String p_192839_0_)
    {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getValue(new ResourceLocation(p_192839_0_));
        return entry == null ? null : entry.getEntityClass();
    }

    public static int getID(Class<? extends Entity> cls)
    {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.EntityRegistry.getEntry(cls);
        return entry == null ? -1 : net.minecraftforge.registries.GameData.getEntityRegistry().getID(entry);
    }

    @Nullable
    public static Class<? extends Entity> getClass(ResourceLocation key)
    {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getValue(key);
        return entry == null ? null : entry.getEntityClass();
    }

    @Nullable
    public static Entity newEntity(@Nullable Class <? extends Entity > clazz, World worldIn)
    {
        if (clazz == null)
        {
            return null;
        }
        else
        {
            try
            {
                net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.EntityRegistry.getEntry(clazz);
				if (entry != null) return entry.newInstance(worldIn);
				return clazz.getConstructor(World.class).newInstance(worldIn);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
                return null;
            }
        }
    }

    @Nullable
    @SideOnly(Side.CLIENT)
    public static Entity createEntityByID(int entityID, World worldIn)
    {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.registries.GameData.getEntityRegistry().getValue(entityID);
        return entry == null ? null : entry.newInstance(worldIn);
    }

    @Nullable
    public static Entity createEntityByIDFromName(ResourceLocation name, World worldIn)
    {
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getValue(name);
        return entry == null ? null : entry.newInstance(worldIn);
    }

    @Nullable
    public static Entity createEntityFromNBT(NBTTagCompound nbt, World worldIn)
    {
        ResourceLocation resourcelocation = new ResourceLocation(nbt.getString("id"));
        Entity entity = createEntityByIDFromName(resourcelocation, worldIn);

        if (entity == null)
        {
            LOGGER.warn("Skipping Entity with id {}", (Object)resourcelocation);
        }
        else
        {
            try
            {
            entity.readFromNBT(nbt);
            }
            catch (Exception e)
            {
                net.minecraftforge.fml.common.FMLLog.log.error("An Entity {}({}) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
                        nbt.getString("id"), entity.getName(), e);
                entity = null;
            }
        }

        return entity;
    }

    public static Set<ResourceLocation> getEntityNameList()
    {
        return Sets.union(net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getKeys(), EXTRA_NAMES);
    }

    public static boolean isMatchingName(Entity entityIn, ResourceLocation entityName)
    {
        ResourceLocation resourcelocation = getKey(entityIn.getClass());

        if (resourcelocation != null)
        {
            return resourcelocation.equals(entityName);
        }
        else if (entityIn instanceof EntityPlayer)
        {
            return PLAYER.equals(entityName);
        }
        else
        {
            return entityIn instanceof EntityLightningBolt ? LIGHTNING_BOLT.equals(entityName) : false;
        }
    }

    public static boolean isRegistered(ResourceLocation entityName)
    {
        return PLAYER.equals(entityName) || getEntityNameList().contains(entityName);
    }

    public static String getValidTypeNames()
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (ResourceLocation resourcelocation : getEntityNameList())
        {
            stringbuilder.append((Object)resourcelocation).append(", ");
        }

        stringbuilder.append((Object)PLAYER);
        return stringbuilder.toString();
    }

    public static void init()
    {
        register(1, "item", EntityItem.class, EntityItem::new, "Item");
        register(2, "xp_orb", EntityXPOrb.class, EntityXPOrb::new, "XPOrb");
        register(3, "area_effect_cloud", EntityAreaEffectCloud.class, EntityAreaEffectCloud::new, "AreaEffectCloud");
        register(4, "elder_guardian", EntityElderGuardian.class, EntityElderGuardian::new, "ElderGuardian");
        register(5, "wither_skeleton", EntityWitherSkeleton.class, EntityWitherSkeleton::new, "WitherSkeleton");
        register(6, "stray", EntityStray.class, EntityStray::new, "Stray");
        register(7, "egg", EntityEgg.class, EntityEgg::new, "ThrownEgg");
        register(8, "leash_knot", EntityLeashKnot.class, EntityLeashKnot::new, "LeashKnot");
        register(9, "painting", EntityPainting.class, EntityPainting::new, "Painting");
        register(10, "arrow", EntityTippedArrow.class, EntityTippedArrow::new, "Arrow");
        register(11, "snowball", EntitySnowball.class, EntitySnowball::new, "Snowball");
        register(12, "fireball", EntityLargeFireball.class, EntityLargeFireball::new, "Fireball");
        register(13, "small_fireball", EntitySmallFireball.class, EntitySmallFireball::new, "SmallFireball");
        register(14, "ender_pearl", EntityEnderPearl.class, EntityEnderPearl::new, "ThrownEnderpearl");
        register(15, "eye_of_ender_signal", EntityEnderEye.class, EntityEnderEye::new, "EyeOfEnderSignal");
        register(16, "potion", EntityPotion.class, EntityPotion::new, "ThrownPotion");
        register(17, "xp_bottle", EntityExpBottle.class, EntityExpBottle::new, "ThrownExpBottle");
        register(18, "item_frame", EntityItemFrame.class, EntityItemFrame::new, "ItemFrame");
        register(19, "wither_skull", EntityWitherSkull.class, EntityWitherSkull::new, "WitherSkull");
        register(20, "tnt", EntityTNTPrimed.class, EntityTNTPrimed::new, "PrimedTnt");
        register(21, "falling_block", EntityFallingBlock.class, EntityFallingBlock::new, "FallingSand");
        register(22, "fireworks_rocket", EntityFireworkRocket.class, EntityFireworkRocket::new, "FireworksRocketEntity");
        register(23, "husk", EntityHusk.class, EntityHusk::new, "Husk");
        register(24, "spectral_arrow", EntitySpectralArrow.class, EntitySpectralArrow::new, "SpectralArrow");
        register(25, "shulker_bullet", EntityShulkerBullet.class, EntityShulkerBullet::new, "ShulkerBullet");
        register(26, "dragon_fireball", EntityDragonFireball.class, EntityDragonFireball::new, "DragonFireball");
        register(27, "zombie_villager", EntityZombieVillager.class, EntityZombieVillager::new, "ZombieVillager");
        register(28, "skeleton_horse", EntitySkeletonHorse.class, EntitySkeletonHorse::new, "SkeletonHorse");
        register(29, "zombie_horse", EntityZombieHorse.class, EntityZombieHorse::new, "ZombieHorse");
        register(30, "armor_stand", EntityArmorStand.class, EntityArmorStand::new, "ArmorStand");
        register(31, "donkey", EntityDonkey.class, EntityDonkey::new, "Donkey");
        register(32, "mule", EntityMule.class, EntityMule::new, "Mule");
        register(33, "evocation_fangs", EntityEvokerFangs.class, EntityEvokerFangs::new, "EvocationFangs");
        register(34, "evocation_illager", EntityEvoker.class, EntityEvoker::new, "EvocationIllager");
        register(35, "vex", EntityVex.class, EntityVex::new, "Vex");
        register(36, "vindication_illager", EntityVindicator.class, EntityVindicator::new, "VindicationIllager");
        register(37, "illusion_illager", EntityIllusionIllager.class, EntityIllusionIllager::new, "IllusionIllager");
        register(40, "commandblock_minecart", EntityMinecartCommandBlock.class, EntityMinecartCommandBlock::new, EntityMinecart.Type.COMMAND_BLOCK.getName());
        register(41, "boat", EntityBoat.class, EntityBoat::new, "Boat");
        register(42, "minecart", EntityMinecartEmpty.class, EntityMinecartEmpty::new, EntityMinecart.Type.RIDEABLE.getName());
        register(43, "chest_minecart", EntityMinecartChest.class, EntityMinecartChest::new, EntityMinecart.Type.CHEST.getName());
        register(44, "furnace_minecart", EntityMinecartFurnace.class, EntityMinecartFurnace::new, EntityMinecart.Type.FURNACE.getName());
        register(45, "tnt_minecart", EntityMinecartTNT.class, EntityMinecartTNT::new, EntityMinecart.Type.TNT.getName());
        register(46, "hopper_minecart", EntityMinecartHopper.class, EntityMinecartHopper::new, EntityMinecart.Type.HOPPER.getName());
        register(47, "spawner_minecart", EntityMinecartMobSpawner.class, EntityMinecartMobSpawner::new, EntityMinecart.Type.SPAWNER.getName());
        register(50, "creeper", EntityCreeper.class, EntityCreeper::new, "Creeper");
        register(51, "skeleton", EntitySkeleton.class, EntitySkeleton::new, "Skeleton");
        register(52, "spider", EntitySpider.class, EntitySpider::new, "Spider");
        register(53, "giant", EntityGiantZombie.class, EntityGiantZombie::new, "Giant");
        register(54, "zombie", EntityZombie.class, EntityZombie::new, "Zombie");
        register(55, "slime", EntitySlime.class, EntitySlime::new, "Slime");
        register(56, "ghast", EntityGhast.class, EntityGhast::new, "Ghast");
        register(57, "zombie_pigman", EntityPigZombie.class, EntityPigZombie::new, "PigZombie");
        register(58, "enderman", EntityEnderman.class, EntityEnderman::new, "Enderman");
        register(59, "cave_spider", EntityCaveSpider.class, EntityCaveSpider::new, "CaveSpider");
        register(60, "silverfish", EntitySilverfish.class, EntitySilverfish::new, "Silverfish");
        register(61, "blaze", EntityBlaze.class, EntityBlaze::new, "Blaze");
        register(62, "magma_cube", EntityMagmaCube.class, EntityMagmaCube::new, "LavaSlime");
        register(63, "ender_dragon", EntityDragon.class, EntityDragon::new, "EnderDragon");
        register(64, "wither", EntityWither.class, EntityWither::new, "WitherBoss");
        register(65, "bat", EntityBat.class, EntityBat::new, "Bat");
        register(66, "witch", EntityWitch.class, EntityWitch::new, "Witch");
        register(67, "endermite", EntityEndermite.class, EntityEndermite::new, "Endermite");
        register(68, "guardian", EntityGuardian.class, EntityGuardian::new, "Guardian");
        register(69, "shulker", EntityShulker.class, EntityShulker::new, "Shulker");
        register(90, "pig", EntityPig.class, EntityPig::new, "Pig");
        register(91, "sheep", EntitySheep.class, EntitySheep::new, "Sheep");
        register(92, "cow", EntityCow.class, EntityCow::new, "Cow");
        register(93, "chicken", EntityChicken.class, EntityChicken::new, "Chicken");
        register(94, "squid", EntitySquid.class, EntitySquid::new, "Squid");
        register(95, "wolf", EntityWolf.class, EntityWolf::new, "Wolf");
        register(96, "mooshroom", EntityMooshroom.class, EntityMooshroom::new, "MushroomCow");
        register(97, "snowman", EntitySnowman.class, EntitySnowman::new, "SnowMan");
        register(98, "ocelot", EntityOcelot.class, EntityOcelot::new, "Ozelot");
        register(99, "villager_golem", EntityIronGolem.class, EntityIronGolem::new, "VillagerGolem");
        register(100, "horse", EntityHorse.class, EntityHorse::new, "Horse");
        register(101, "rabbit", EntityRabbit.class, EntityRabbit::new, "Rabbit");
        register(102, "polar_bear", EntityPolarBear.class, EntityPolarBear::new, "PolarBear");
        register(103, "llama", EntityLlama.class, EntityLlama::new, "Llama");
        register(104, "llama_spit", EntityLlamaSpit.class, EntityLlamaSpit::new, "LlamaSpit");
        register(105, "parrot", EntityParrot.class, EntityParrot::new, "Parrot");
        register(120, "villager", EntityVillager.class, EntityVillager::new, "Villager");
        register(200, "ender_crystal", EntityEnderCrystal.class, EntityEnderCrystal::new, "EnderCrystal");
        addSpawnInfo("bat", 4996656, 986895);
        addSpawnInfo("blaze", 16167425, 16775294);
        addSpawnInfo("cave_spider", 803406, 11013646);
        addSpawnInfo("chicken", 10592673, 16711680);
        addSpawnInfo("cow", 4470310, 10592673);
        addSpawnInfo("creeper", 894731, 0);
        addSpawnInfo("donkey", 5457209, 8811878);
        addSpawnInfo("elder_guardian", 13552826, 7632531);
        addSpawnInfo("enderman", 1447446, 0);
        addSpawnInfo("endermite", 1447446, 7237230);
        addSpawnInfo("evocation_illager", 9804699, 1973274);
        addSpawnInfo("ghast", 16382457, 12369084);
        addSpawnInfo("guardian", 5931634, 15826224);
        addSpawnInfo("horse", 12623485, 15656192);
        addSpawnInfo("husk", 7958625, 15125652);
        addSpawnInfo("llama", 12623485, 10051392);
        addSpawnInfo("magma_cube", 3407872, 16579584);
        addSpawnInfo("mooshroom", 10489616, 12040119);
        addSpawnInfo("mule", 1769984, 5321501);
        addSpawnInfo("ocelot", 15720061, 5653556);
        addSpawnInfo("parrot", 894731, 16711680);
        addSpawnInfo("pig", 15771042, 14377823);
        addSpawnInfo("polar_bear", 15921906, 9803152);
        addSpawnInfo("rabbit", 10051392, 7555121);
        addSpawnInfo("sheep", 15198183, 16758197);
        addSpawnInfo("shulker", 9725844, 5060690);
        addSpawnInfo("silverfish", 7237230, 3158064);
        addSpawnInfo("skeleton", 12698049, 4802889);
        addSpawnInfo("skeleton_horse", 6842447, 15066584);
        addSpawnInfo("slime", 5349438, 8306542);
        addSpawnInfo("spider", 3419431, 11013646);
        addSpawnInfo("squid", 2243405, 7375001);
        addSpawnInfo("stray", 6387319, 14543594);
        addSpawnInfo("vex", 8032420, 15265265);
        addSpawnInfo("villager", 5651507, 12422002);
        addSpawnInfo("vindication_illager", 9804699, 2580065);
        addSpawnInfo("witch", 3407872, 5349438);
        addSpawnInfo("wither_skeleton", 1315860, 4672845);
        addSpawnInfo("wolf", 14144467, 13545366);
        addSpawnInfo("zombie", 44975, 7969893);
        addSpawnInfo("zombie_horse", 3232308, 9945732);
        addSpawnInfo("zombie_pigman", 15373203, 5009705);
        addSpawnInfo("zombie_villager", 5651507, 7969893);
        EXTRA_NAMES.add(LIGHTNING_BOLT);
    }

	private static <T extends Entity> void register(int id, String name, Class<T> type,
			java.util.function.Function<World, T> factory, String legacyName)
	{
		net.minecraftforge.registries.GameData.registerEntity(id, new ResourceLocation(name), type, legacyName, factory);
	}

    protected static EntityList.EntityEggInfo addSpawnInfo(String id, int primaryColor, int secondaryColor)
    {
        ResourceLocation resourcelocation = new ResourceLocation(id);
        EntityList.EntityEggInfo egg = new EntityList.EntityEggInfo(resourcelocation, primaryColor, secondaryColor);
        net.minecraftforge.fml.common.registry.EntityEntry entry = net.minecraftforge.fml.common.registry.ForgeRegistries.ENTITIES.getValue(resourcelocation);
        if (entry != null) entry.setEgg(egg);
        return (EntityList.EntityEggInfo)ENTITY_EGGS.put(resourcelocation, egg);
    }

    public static class EntityEggInfo
        {
            public final ResourceLocation spawnedID;
            public final int primaryColor;
            public final int secondaryColor;
            public final StatBase killEntityStat;
            public final StatBase entityKilledByStat;

            public EntityEggInfo(ResourceLocation idIn, int primaryColorIn, int secondaryColorIn)
            {
                this.spawnedID = idIn;
                this.primaryColor = primaryColorIn;
                this.secondaryColor = secondaryColorIn;
                this.killEntityStat = StatList.getStatKillEntity(this);
                this.entityKilledByStat = StatList.getStatEntityKilledBy(this);
            }

            // Forge start
            public EntityEggInfo(ResourceLocation id, int primaryColor, int secondaryColor, StatBase killEntityStatistic, StatBase entityKilledByStatistic)
            {
                this.spawnedID = id;
                this.primaryColor = primaryColor;
                this.secondaryColor = secondaryColor;
                this.killEntityStat = killEntityStatistic;
                this.entityKilledByStat = entityKilledByStatistic;
            }
            // Forge end
        }
}
