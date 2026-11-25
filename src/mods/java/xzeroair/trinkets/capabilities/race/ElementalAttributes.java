package xzeroair.trinkets.capabilities.race;

import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import xzeroair.trinkets.init.Elements;
import xzeroair.trinkets.traits.elements.Element;

public class ElementalAttributes {
   protected Element primary = Elements.NEUTRAL;
   protected Element secondary = Elements.NEUTRAL;
   protected Map<ResourceLocation, Element> subElements = new TreeMap();

   public ElementalAttributes() {
   }

   public ElementalAttributes setPrimaryElement(Element element) {
      if (element != null && element != this.primary) {
         this.primary = element;
      }

      return this;
   }

   public ElementalAttributes setSecendaryElement(Element element) {
      if (element != null && element != this.secondary) {
         this.secondary = element;
      }

      return this;
   }

   public ElementalAttributes addSubElements(Element... elements) {
      for(Element element : elements) {
         if (this.getPrimaryElement() != element && this.getSecondaryElement() != element) {
            this.subElements.put(element.getRegistryName(), element);
         }
      }

      return this;
   }

   @Nullable
   public Element removeSubElement(Element element) {
      return element != null ? (Element)this.subElements.remove(element.getRegistryName()) : null;
   }

   public Element getPrimaryElement() {
      return this.primary;
   }

   public Element getSecondaryElement() {
      return this.secondary;
   }

   public Map<ResourceLocation, Element> getSubElements() {
      return this.subElements;
   }

   public NBTTagCompound saveToNBT(NBTTagCompound compound) {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("primary", this.getPrimaryElement().getRegistryName().toString());
      tag.setString("secondary", this.getSecondaryElement().getRegistryName().toString());
      NBTTagCompound subs = new NBTTagCompound();

      for(Element subEle : this.getSubElements().values()) {
         subs.setString(subEle.getID() + "", subEle.getRegistryName().toString());
      }

      tag.setTag("sub", subs);
      compound.setTag("Elements", tag);
      return compound;
   }

   public void loadFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("Elements")) {
         NBTTagCompound tag = compound.getCompoundTag("Elements");
         if (tag.hasKey("primary")) {
            this.primary = Element.getByNameOrId(tag.getString("primary"));
         }

         if (tag.hasKey("secondary")) {
            this.secondary = Element.getByNameOrId(tag.getString("secondary"));
         }

         if (tag.hasKey("sub")) {
            NBTTagCompound subs = tag.getCompoundTag("sub");

            for(String entry : subs.getKeySet()) {
               String ele = subs.getString(entry);
               this.addSubElements(Element.getByNameOrId(ele));
            }
         }
      }

   }
}
