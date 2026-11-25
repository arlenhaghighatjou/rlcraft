package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

public class CFantasticPotionEffect extends Component {
   public FantasticPotionEffect value = null;

   public CFantasticPotionEffect() {
   }

   public CFantasticPotionEffect set(FantasticPotionEffect value) {
      this.value = value;
      return this;
   }

   public CFantasticPotionEffect write(ByteBuf buf) {
      (new CResourceLocation()).set(this.value.getPotion().getRegistryName()).write(buf);
      buf.writeInt(this.value.getDuration());
      buf.writeInt(this.value.getAmplifier());
      buf.writeBoolean(this.value.getIsAmbient());
      buf.writeBoolean(this.value.doesShowParticles());
      buf.writeBoolean(this.value.getIsPotionDurationMax());
      CItemStack cstack = new CItemStack();
      List<ItemStack> cures = this.value.getCurativeItems();
      buf.writeInt(cures.size());

      for(ItemStack stack : cures) {
         cstack.set(stack).write(buf);
      }

      buf.writeInt(this.value.interval);
      return this;
   }

   public CFantasticPotionEffect read(ByteBuf buf) {
      this.value = new FantasticPotionEffect((Potion)Potion.REGISTRY.getObject((new CResourceLocation()).read(buf).value), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBoolean());
      this.value.setPotionDurationMax(buf.readBoolean());
      CItemStack cstack = new CItemStack();
      ArrayList<ItemStack> cures = new ArrayList();

      for(int i = buf.readInt(); i > 0; --i) {
         cures.add(cstack.read(buf).value);
      }

      this.value.setCurativeItems(cures);
      this.value.interval = buf.readInt();
      return this;
   }

   public CFantasticPotionEffect save(OutputStream stream) {
      CInt ci = new CInt();
      CBoolean cb = new CBoolean();
      (new CResourceLocation()).set(this.value.getPotion().getRegistryName()).save(stream);
      ci.set(this.value.getDuration()).save(stream).set(this.value.getAmplifier()).save(stream);
      cb.set(this.value.getIsAmbient()).save(stream).set(this.value.doesShowParticles()).save(stream).set(this.value.getIsPotionDurationMax()).save(stream);
      CItemStack cstack = new CItemStack();
      List<ItemStack> cures = this.value.getCurativeItems();
      ci.set(cures.size()).save(stream);

      for(ItemStack stack : cures) {
         cstack.set(stack).save(stream);
      }

      ci.set(this.value.interval).save(stream);
      return this;
   }

   public CFantasticPotionEffect load(InputStream stream) {
      CInt ci = new CInt();
      CBoolean cb = new CBoolean();
      this.value = new FantasticPotionEffect((Potion)Potion.REGISTRY.getObject((new CResourceLocation()).load(stream).value), ci.load(stream).value, ci.load(stream).value, cb.load(stream).value, cb.load(stream).value);
      this.value.setPotionDurationMax(cb.load(stream).value);
      CItemStack cstack = new CItemStack();
      ArrayList<ItemStack> cures = new ArrayList();

      for(int i = ci.load(stream).value; i > 0; --i) {
         cures.add(cstack.load(stream).value);
      }

      this.value.setCurativeItems(cures);
      this.value.interval = ci.load(stream).value;
      return this;
   }
}
