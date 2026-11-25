## TODO
* improve: ForgeWorld class so that all raytrace methods return optionals instead of nullable values
* fix: glass meter taking damage when hitting non-collidable block

## 4.5.6.1
* fixed: compatibility issues with Weather 2 - Remastered (thanks Desoroxxx)

## 4.5.6.0
+ added: IReloadableData interface
+ added: AbstractParsableList class
+ added: AbstractParsableBlackWhiteList class
+ added: UnitId.toConfigName method
* fixed: inability to create config file for biome with inappropriate characters in the name (thanks to yikeslolsosad)
* fixed: naming of vanilla biome config files

## 4.5.5.0
+ added: missing srg name for World.getBlockState method
* changed: WorldSearch.BlockChain algorithm to use iteration instead of recursion
* fixed: java.lang.StackOverflowError when block chain is too long (thanks to vizthex)
* fixed: ForgeEntity.hasStack method not searching in the armor inventory

## 4.5.4.0 
+ added: ability to define several repair materials for a damageable item (thanks to StormbringerGT)
* fixed: durability of damageable items being not synchronized with clients (thanks to Splutty)
* fixed: incorrect DUP_V3 instruction

## 4.5.3.0
+ added: new coremod predefined instructions (DUP2, DUP_V2, DUP_V3, ISUB, FSUB)
* improved: ForgeClassTransformer debug messages

## 4.5.2.0
+ added: missing logo
+ implemented: IPole interface
+ implemented: WorldPredicate functional interface
* made: some refactoring

## 4.5.1.0
+ added: IBounds.approxTo method for quick approximation between different bounds
+ added: "maxBlocksInMultiblock" config parameter (thanks to NullMagic1)
* implemented: Ordered interface for better comparison of comparable classes
* improved: Percentage class to implement Ordered interface
* improved: CommonMath.Approx class

## 4.5.0.0
* lots of refactorings come from 1.14
+ added: Percentage.parse and Percentage.value methods
+ implemented: Percentage.equals && Percentage.compareTo methods
* __requires: updating all mods that use this library__

## 4.4.26.0
+ added: ForgeWorld.rayTraceEntities method
+ added: ForgeWorld.rayTraceAll method
+ added: ForgeWorld.getClosestHit method
* fixed: issues in ForgeWorld.hitThroughBlock method

## 4.4.25.0
+ added: ForgeWorld.rayTraceBlocks method with the ability to filter blocks
+ added: ForgeWorld.hitThroughBlock method
+ added: ForgeTileEntity.syncWithClients method
+ added: ForgeMath.isNan method for vectors
+ added: ForgeEnergyStorage.Properties class
* improved: ForgeEnergyStorage serialization 

## 4.4.24.0
+ added: CommonString.Joiner class for better string concatenation
+ added: missing @SideOnly annotations for GUI classes and methods
+ added: better static initializer for WeightedList class

## 4.4.23.0
+ added: classes for working with tile inventory and its GUI
* improved: UnitId class for quick matching with block and item states

## 4.4.22.2
* fixed: CatServer crash on startup with InvocationTargetException (thanks to curse654321)

## 4.4.22.1
* fixed: incorrect identification of emitters that use meta from a dropped item instead of a block state (IC2 machines) (thanks to Wootaboot2)

## 4.4.22.0
+ added: ForgeEnergyStorage class for working with Forge Energy
+ added: ISmokeContainer.isActive method for determining the active state of the pump
* moved: "invertedPumpState" config parameter to adchimneys.cfg and renamed to "invertedRedstoneSignal"
* changed: the default value of "maxVentPipeLength" from 16 to 32
* __requires: updating Advanced Chimneys to version 3.5.4.0 or higher__

## 4.4.21.1
* fixed: an issue where invokeInterface method acted as invokeVirtual
* fixed: incorrect catching of some non-critical exceptions

## 4.4.21.0
+ added: InstructList.invokeInterface method
+ added: InstructList.invokeSpecial method

## 4.4.20.1
* fixed: an issue where the ForgeBlock.isRegistered method returned "true" for unregistered blocks in some cases

## 4.4.20.0
* improved: UnitConfig class for better interaction with config files

## 4.4.19.1
* fixed: incorrect converting UnitId to String when meta=0

## 4.4.19.0
+ added: InstructList.VarNode class
+ added: new coremod predefined instructions (IFQE & IFNE)
+ added: ForgeClassTransformer.isCompatibilityMode() method to get rid of the lousy ClassNotFound error
* improved: InstructList class to work with VarNodes 
- removed: forgotten debug messages
* __requires: updating Pollution of the Realms to version 1.0.13.0 or higher__

## 4.4.18.0
+ added: ForgeEmitter class as a base implementation of IEmitter interface
* improved: IEmitter interface for better compatibility with multi-blocks
* improved: WorldSearch.TileNeighbors class
* __requires: updating Advanced Chimneys to version 3.4.0.0 or higher__
* __requires: updating Pollution of the Realms to version 1.0.12.0 or higher__
* __requires: updating Large Ore Deposits to version 1.0.4.1 or higher__

## 4.4.17.1
* fixed: not always correct detection of hollows in multi-block structures

## 4.4.17.0
+ added: TileNeighbors.isMultiblockHollow method
+ added: EndertechMods enum
* moved: classes from the "explosive" package to a separate mod

## 4.4.16.0
+ added: ForgeLocating.environmentTexture method
+ added: descriptors for ResourceLocation, EntityRenderer and TextureManager classes
+ added: predefined signature for TextureManager.bindTexture method
+ added: Common.CachedValue class
+ added: new coremod predefined instructions
* fixed: invalid predefined signatures with srg names instead of normal ones

## 4.4.15.1
* optimized: CommonCollect.WeightedList
* fixed: log being spammed with "Unable to parse ':'. Invalid id format" (thanks to Osmuni)
* fixed: converting empty id to string

## 4.4.15.0
+ added: IPollutant.canPassThrough method with direction parameters
+ added: AABB.isFullBlockingPanel method
+ added: AABB.isFullBlockingFace method
+ added: block collision bounding box info to developer tooltips

## 4.4.14.0
+ added: Biomes.readFrom method for reading biome black/white lists from configs
+ added: BlackWhiteList.isEmpty method

## 4.4.13.0
+ added: DevInfo class
+ added: VanillaMaterials class
+ added: Position.withY method
+ added: MultiConfigProperty.StrArrayProperty class
+ added: CommonCollect.BlackWhiteList class
+ added: CommonCollect.WeightedList class
+ added: CommonMath.getFloor methods
+ added: Dimensions.readFrom method
+ added: Percentage.takeChance method
+ added: ForgeConfig.getIntBounds, getFloatBounds and getPercentage methods
+ added: ForgeNBT.hasCompoundTag method
* improved: ForgeWorld.Position class to work with chunks
* improved: WorldBounds class to work with relative coords
* fixed: ForgeNBT.writeBlockPos saving doubles instead of integers

## 4.4.12.0
+ added: INBTSerializable interface
+ added: INetSerializable interface
+ added: Connection.sendToAllInChunk method
* improved: ForgeNetMsg class to work with INetSerializable implementations
* reworked: ChunkLoc class
* fixed: incorrect serialization of ChunkLoc class
* fixed: sending incorrect World instance when executing server commands
* __requires: updating Pollution of the Realms to version 1.0.3.0 or higher__

## 4.4.11.0
+ added: IWind interface
+ added: StaticWind class
+ added: compatibility with Weather2 wind (thanks to CynicalBrat and Corosus)
* changed: ForgeWorld.getWindAt method return type
* __requires: updating Advanced Chimneys to version 3.3.19.0 or higher__
* __requires: updating Pollution of the Realms to version 1.0.2.0 or higher__

## 4.4.10.0
+ added: ForgeEntity.isUnderOpenSky method
* changed: IPollutant.canAffectBlock method signature

## 4.4.9.0
+ added: support for nested NBT tags (thanks to LordPINE)
+ added: IBaubleWear interface
+ added: RepairMatcher class
+ added: ForgeEntity.getEquipmentOn method
+ added: ForgeNBT.KeyPathPair class
- removed: IProtectiveWear interface

## 4.4.8.0
+ added: FluidBlock class
+ added: ForgeEntity.rayTraceBlockBeingLookedAt method
+ added: ForgeLocating.fluidTexture method
+ added: INeedRender interface
* fixed: unit ID pattern for accepting "/", "." characters

## 4.4.7.0
+ added: ForgeEntity.isUnderRain method
+ added: ForgeBlock.isUnderRain method
+ added: ForgeEntity.getPositionTop method
+ added: ForgeBlock.isUnderOpenSky method
* changed: IPollutant.canAffectEntity method signature
* fixed: an issue where some emitters were not receiving random updates (thanks to Ghost8909)

## 4.4.6.0
+ added: StateHashMap class
+ added: UnitId.getIBlockState method
+ added: ClassName.join method
+ added: InstructList.dup method
+ added: predefined descriptor for Predicate class
+ added: UnitId.withMetaData method
+ added: ForgeWorld.Position.getLastInLine method
* fixed: an issue where the pattern for unit ID was not allowing non-letter characters (thanks to golrith)

## 4.4.5.0
+ added: redstone power bounds
* improved: UnitId class for working with block properties

## 4.4.4.0
+ added: predefined signatures for ItemStack.setCount and Entity.OnUpdate methods
+ added: swapPositions method to ForgeEntity class
+ added: EmittersInit class
* improved: IEmitter interface to work with entities
* improved: RelatedUnitsInit class for better inheritance

## 4.4.3.0
+ added: PercentageSliderEntry class
* improved: Percentage class
* improved: ModConfig class
* fixed: showing of child categories in the config root menu

## 4.4.2.0
+ added: Tooltip class for better handling and formatting of tooltips
* improved: IEmitter interface (added getIdentityTag() and isIdentified() methods)

## 4.4.1.0
+ added: Column class that extends BlockChain class
+ added: IWithMeta interface
+ added: StateHashSet class

## 4.4.0.0
+ added: RelatedBlock class
+ added: CommonCollect class
+ added: MeterIndicators class
+ added: methods to ForgeCreativeTab class for proper setting tab icon
+ added: toBlockPos method to Vect3d class
* improved: UnitsInit and UnitsInitWithEnum classes
* changed: behavior of UnitId.from(Enum) method
* fixed: configs being not properly saved for units initialized from enums
* fixed: RenderItemHook injection before model rendering
* __note: update all related mods to the latest versions__

## 4.3.21.0
+ added: method sortDirections() for Wind class
* fixed: current wind motion dancing around target value 

## 4.3.20.1
* fixed: server crash with NoClassDefFoundError: com.endertech.minecraft.forge.configs.ForgeKeys

## 4.3.20.0
+ added: durability bounds const
+ added: method to join arrays 
+ added: AxisAlignedBB helper class
* fixed: ForgeTileEntity.shouldRefresh method

## 4.3.19.0
+ added: IUnitWithMeshDefinition interface
+ added: GlassMeter abstract class
* improved: UnitVariant class to match forge blockstate format

## 4.3.18.0
+ added: config option to remove repair penalty for breakable items
* fixed: getting incorrect variants from ModelResourceLocation
* fixed: incorrect caching for ModelResourceLocation

## 4.3.17.0
+ added: Multi-config String property
+ added: Multi-config UnitId property

## 4.3.16.0
+ added: predefined signature for NonNullList.get method
+ added: LadderShapedCharge abstract class for shaped explosions
+ added: WorldBounds class for working with 3d bounds
+ added: IEmitter interface
* implemented: IntBounds class as Iterable
* improved: IBounds<T> interface

## 4.3.15.0
* extended: IPollutant interface for better performance

## 4.3.14.0
+ added: new methods to IPollutant interface
+ added: new predefined method descriptors

## 4.3.13.0
+ implemented: asynchronous timers for periodic updates and better performance

## 4.3.12.0
+ added: "enabled" option for all biome configs to prevent overriding default parameters
+ added: "debug" option to the main config
* moved: most of the info messages to the debug level to reduce console spam

## 4.3.11.0
+ added: MultiConfigProperty class
* improved: ForgeMaterial class
* fixed: inability to repair armor and tools with ingots

## 4.3.10.0
+ added: config parameter for defining repair material on an anvil for all damageable items
* made: lots of refactoring

## 4.3.9.0
+ added: LavaTouchesWater hook
+ added: CustomArmorModel class
+ added: ChestWearItem class
+ added: Percentage class

## 4.3.8.0
+ added: config option to completely remove disabled units (thanks to Ilvrak) 

## 4.3.7.0
+ added: scheduled explosions
* fixed: registration of recipes for disabled units (thanks to Ilvrak)

## 4.3.6.1
* fixed: server crash on startup with NoClassDefFoundError (thanks to DeltaWolf7)

## 4.3.6.0
+ added: mod config GUI
+ added: maxVentPipeLength config option
+ added: invertedPumpState config option
+ added: ventInhaleDistance config option
* improved: GUI controls (input boxes for integer values replaced with sliders)

## 4.3.5.0
+ added: IPollutant interface
* made: lots of refactoring
* moved: ISmokeContainer interface to api package
* improved: BlockChain class implementations
* fixed: Position.getAroundCube() method

## 4.3.4.0
+ added: RelativeUnit abstract class
+ added: IRespirator interface
+ added: ModConfigGUI class
+ added: a bunch of new bytecode operations
+ implemented: VentDuct class for vent systems
* made: lots of refactoring
* improved: initialization classes for relative units
* improved: ModConfig class

## 4.3.3.0
+ added: NBT compound methods for working with string arrays
+ added: predefined signatures for ItemStack.shrink and ItemStack.splitStack methods
+ implemented: hashCode and equals methods of IntBounds and FloatBounds classes
* improved: config system for biomes
* fixed: beach biome name 
* fixed: disabled units not being registered instead of hiding from creative tabs

## 4.3.2.0
+ added: BlockCaughtFire hook

## 4.3.1.0
+ added: ShapedCharge abstract class
+ added: invertStackValue asm instruction
+ implemented: smoke outlets searching algorithm
* improved: explosives
* improved: rails
* improved: lots of refactoring
* fixed: normal names being used when invoking obfuscated methods
* fixed: shapeless recipe validating
* fixed: improper processing of ore dictionary names in crafting recipes

## 4.3.0.0
+ added: shared code to recognize chimneys and conductors
+ added: reader and writer for enum fields in network packets
+ added: Connection class for simple network messaging
+ added: new super classes for explosives, potions and consumable items
+ added: methods for saving time intervals in configs
* improved: IBounds interface and its implementations
* restructured: item and block super classes
* fixed: item configs not being saved on initialization
* fixed: field cache not working in ForgeNetMsg class

## 4.2.0.1
* fixed: wind synchronization on clients

## 4.2.0.0
+ implemented: global wind simulation with configs for each biome
+ implemented: reading obfuscated names from notch-mcp.srg
* improved: class transformers

## 4.1.0.0
* reworked: class transformers
* reworked: network messages and handlers
* updated to recommended forge-1.12.2-14.23.0.2491

## 4.0.0.0
* ported to Minecraft 1.12.1
* updated all srgs

## 3.0.2.0
* improved: threading classes

## 3.0.1.0
* improved: all network messages are synchronized with a main thread

## 3.0.0.0
* ported to Minecraft 1.12

## 2.2.0.0
* improved: asm hooks and class transformers