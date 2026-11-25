package rlmixins.mixin.lycanites.boundingbox;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityAbaia;
import com.lycanitesmobs.core.entity.creature.EntityAbtu;
import com.lycanitesmobs.core.entity.creature.EntityAegis;
import com.lycanitesmobs.core.entity.creature.EntityAfrit;
import com.lycanitesmobs.core.entity.creature.EntityAglebemu;
import com.lycanitesmobs.core.entity.creature.EntityArchvile;
import com.lycanitesmobs.core.entity.creature.EntityArgus;
import com.lycanitesmobs.core.entity.creature.EntityArisaur;
import com.lycanitesmobs.core.entity.creature.EntityArix;
import com.lycanitesmobs.core.entity.creature.EntityAspid;
import com.lycanitesmobs.core.entity.creature.EntityAstaroth;
import com.lycanitesmobs.core.entity.creature.EntityBalayang;
import com.lycanitesmobs.core.entity.creature.EntityBanshee;
import com.lycanitesmobs.core.entity.creature.EntityBarghest;
import com.lycanitesmobs.core.entity.creature.EntityBeholder;
import com.lycanitesmobs.core.entity.creature.EntityBobeko;
import com.lycanitesmobs.core.entity.creature.EntityCalpod;
import com.lycanitesmobs.core.entity.creature.EntityChupacabra;
import com.lycanitesmobs.core.entity.creature.EntityCinder;
import com.lycanitesmobs.core.entity.creature.EntityClink;
import com.lycanitesmobs.core.entity.creature.EntityCockatrice;
import com.lycanitesmobs.core.entity.creature.EntityConcapedeHead;
import com.lycanitesmobs.core.entity.creature.EntityConcapedeSegment;
import com.lycanitesmobs.core.entity.creature.EntityDawon;
import com.lycanitesmobs.core.entity.creature.EntityDjinn;
import com.lycanitesmobs.core.entity.creature.EntityDweller;
import com.lycanitesmobs.core.entity.creature.EntityEechetik;
import com.lycanitesmobs.core.entity.creature.EntityEnt;
import com.lycanitesmobs.core.entity.creature.EntityErepede;
import com.lycanitesmobs.core.entity.creature.EntityEttin;
import com.lycanitesmobs.core.entity.creature.EntityEyewig;
import com.lycanitesmobs.core.entity.creature.EntityFeradon;
import com.lycanitesmobs.core.entity.creature.EntityGeken;
import com.lycanitesmobs.core.entity.creature.EntityGeonach;
import com.lycanitesmobs.core.entity.creature.EntityGorger;
import com.lycanitesmobs.core.entity.creature.EntityGrell;
import com.lycanitesmobs.core.entity.creature.EntityGrue;
import com.lycanitesmobs.core.entity.creature.EntityHerma;
import com.lycanitesmobs.core.entity.creature.EntityIgnibus;
import com.lycanitesmobs.core.entity.creature.EntityIka;
import com.lycanitesmobs.core.entity.creature.EntityIoray;
import com.lycanitesmobs.core.entity.creature.EntityJabberwock;
import com.lycanitesmobs.core.entity.creature.EntityJengu;
import com.lycanitesmobs.core.entity.creature.EntityJoustAlpha;
import com.lycanitesmobs.core.entity.creature.EntityKhalk;
import com.lycanitesmobs.core.entity.creature.EntityKrake;
import com.lycanitesmobs.core.entity.creature.EntityLobber;
import com.lycanitesmobs.core.entity.creature.EntityLurker;
import com.lycanitesmobs.core.entity.creature.EntityMaka;
import com.lycanitesmobs.core.entity.creature.EntityMakaAlpha;
import com.lycanitesmobs.core.entity.creature.EntityMaug;
import com.lycanitesmobs.core.entity.creature.EntityMorock;
import com.lycanitesmobs.core.entity.creature.EntityPinky;
import com.lycanitesmobs.core.entity.creature.EntityQuetzodracl;
import com.lycanitesmobs.core.entity.creature.EntityQuillbeast;
import com.lycanitesmobs.core.entity.creature.EntityRaiko;
import com.lycanitesmobs.core.entity.creature.EntityReiver;
import com.lycanitesmobs.core.entity.creature.EntityRemobra;
import com.lycanitesmobs.core.entity.creature.EntityRoa;
import com.lycanitesmobs.core.entity.creature.EntityRoc;
import com.lycanitesmobs.core.entity.creature.EntitySalamander;
import com.lycanitesmobs.core.entity.creature.EntitySerpix;
import com.lycanitesmobs.core.entity.creature.EntityShade;
import com.lycanitesmobs.core.entity.creature.EntityShambler;
import com.lycanitesmobs.core.entity.creature.EntitySkylus;
import com.lycanitesmobs.core.entity.creature.EntitySpectre;
import com.lycanitesmobs.core.entity.creature.EntitySpriggan;
import com.lycanitesmobs.core.entity.creature.EntityStrider;
import com.lycanitesmobs.core.entity.creature.EntitySylph;
import com.lycanitesmobs.core.entity.creature.EntityThresher;
import com.lycanitesmobs.core.entity.creature.EntityTpumpkyn;
import com.lycanitesmobs.core.entity.creature.EntityTremor;
import com.lycanitesmobs.core.entity.creature.EntityTriffid;
import com.lycanitesmobs.core.entity.creature.EntityTroll;
import com.lycanitesmobs.core.entity.creature.EntityUvaraptor;
import com.lycanitesmobs.core.entity.creature.EntityVapula;
import com.lycanitesmobs.core.entity.creature.EntityVentoraptor;
import com.lycanitesmobs.core.entity.creature.EntityVespid;
import com.lycanitesmobs.core.entity.creature.EntityVespidQueen;
import com.lycanitesmobs.core.entity.creature.EntityVolcan;
import com.lycanitesmobs.core.entity.creature.EntityVorach;
import com.lycanitesmobs.core.entity.creature.EntityWarg;
import com.lycanitesmobs.core.entity.creature.EntityWildkin;
import com.lycanitesmobs.core.entity.creature.EntityWraith;
import com.lycanitesmobs.core.entity.creature.EntityXaphan;
import com.lycanitesmobs.core.entity.creature.EntityYale;
import com.lycanitesmobs.core.entity.creature.EntityZoataur;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import rlmixins.wrapper.LycanitesBBWrapper;

@Mixin({EntityAbaia.class, EntityAbtu.class, EntityAegis.class, EntityAfrit.class, EntityAglebemu.class, EntityArchvile.class, EntityArgus.class, EntityArix.class, EntityAspid.class, EntityAstaroth.class, EntityArisaur.class, EntityBalayang.class, EntityBanshee.class, EntityBarghest.class, EntityBeholder.class, EntityBobeko.class, EntityCalpod.class, EntityChupacabra.class, EntityCinder.class, EntityClink.class, EntityCockatrice.class, EntityConcapedeHead.class, EntityConcapedeSegment.class, EntityDawon.class, EntityDjinn.class, EntityDweller.class, EntityEechetik.class, EntityEnt.class, EntityErepede.class, EntityEttin.class, EntityEyewig.class, EntityFeradon.class, EntityGeken.class, EntityGeonach.class, EntityGorger.class, EntityGrell.class, EntityGrue.class, EntityHerma.class, EntityIgnibus.class, EntityIka.class, EntityIoray.class, EntityJabberwock.class, EntityJengu.class, EntityJoustAlpha.class, EntityKhalk.class, EntityKrake.class, EntityLobber.class, EntityLurker.class, EntityMaka.class, EntityMakaAlpha.class, EntityMaug.class, EntityMorock.class, EntityPinky.class, EntityQuetzodracl.class, EntityQuillbeast.class, EntityRaiko.class, EntityReiver.class, EntityRemobra.class, EntityRoa.class, EntityRoc.class, EntitySalamander.class, EntitySerpix.class, EntityShade.class, EntityShambler.class, EntitySkylus.class, EntitySpectre.class, EntitySpriggan.class, EntityStrider.class, EntitySylph.class, EntityThresher.class, EntityTpumpkyn.class, EntityTremor.class, EntityTriffid.class, EntityTroll.class, EntityUvaraptor.class, EntityVapula.class, EntityVentoraptor.class, EntityVespid.class, EntityVespidQueen.class, EntityVolcan.class, EntityVorach.class, EntityWarg.class, EntityWildkin.class, EntityWraith.class, EntityXaphan.class, EntityYale.class, EntityZoataur.class})
public abstract class UndersizedBBRenderMixin extends BaseCreatureEntity {
   public UndersizedBBRenderMixin(World world) {
      super(world);
   }

   public AxisAlignedBB getRenderBoundingBox() {
      return (AxisAlignedBB)execNullable(LycanitesBBWrapper.lookup.get(this.getClass().getSimpleName()), (b) -> {
         return this.getEntityBoundingBox().grow(b[0], b[1], b[0]).offset(0.0D, b[2], 0.0D);
      }, super.getRenderBoundingBox());
   }

   private static <T, R> R execNullable(@Nullable T obj, Function<T, R> onNonNull, R orElse) {
      return (R)(obj != null ? onNonNull.apply(obj) : orElse);
   }
}
