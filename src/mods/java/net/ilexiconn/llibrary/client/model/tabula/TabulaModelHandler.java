package net.ilexiconn.llibrary.client.model.tabula;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.model.tabula.baked.VanillaTabulaModel;
import net.ilexiconn.llibrary.client.model.tabula.baked.deserializer.ItemCameraTransformsDeserializer;
import net.ilexiconn.llibrary.client.model.tabula.baked.deserializer.ItemTransformVec3fDeserializer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeGroupContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.block.model.ModelBlock.Deserializer;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum TabulaModelHandler implements ICustomModelLoader, JsonDeserializationContext {
   INSTANCE;

   private Gson gson = (new GsonBuilder()).registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3fDeserializer()).registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransformsDeserializer()).create();
   private JsonParser parser = new JsonParser();
   private Deserializer modelBlockDeserializer = new Deserializer();
   private IResourceManager manager;
   private final Set<String> enabledDomains = new HashSet();

   private TabulaModelHandler() {
   }

   public void addDomain(String domain) {
      this.enabledDomains.add(domain.toLowerCase(Locale.ROOT));
      LLibrary.LOGGER.info("TabulaModelHandler: Domain {} has been added.", domain.toLowerCase(Locale.ROOT));
   }

   public TabulaModelContainer loadTabulaModel(String path) throws IOException {
      if (!path.startsWith("/")) {
         path = "/" + path;
      }

      if (!path.endsWith(".tbl")) {
         path = path + ".tbl";
      }

      InputStream stream = TabulaModelHandler.class.getResourceAsStream(path);
      return INSTANCE.loadTabulaModel(this.getModelJsonStream(path, stream));
   }

   public TabulaModelContainer loadTabulaModel(InputStream stream) {
      return (TabulaModelContainer)this.gson.fromJson(new InputStreamReader(stream), TabulaModelContainer.class);
   }

   public TabulaCubeContainer getCubeByName(String name, TabulaModelContainer model) {
      for(TabulaCubeContainer cube : this.getAllCubes(model)) {
         if (cube.getName().equals(name)) {
            return cube;
         }
      }

      return null;
   }

   public TabulaCubeContainer getCubeByIdentifier(String identifier, TabulaModelContainer model) {
      for(TabulaCubeContainer cube : this.getAllCubes(model)) {
         if (cube.getIdentifier().equals(identifier)) {
            return cube;
         }
      }

      return null;
   }

   public List<TabulaCubeContainer> getAllCubes(TabulaModelContainer model) {
      List<TabulaCubeContainer> cubes = new ArrayList();

      for(TabulaCubeGroupContainer cubeGroup : model.getCubeGroups()) {
         cubes.addAll(this.traverse(cubeGroup));
      }

      for(TabulaCubeContainer cube : model.getCubes()) {
         cubes.addAll(this.traverse(cube));
      }

      return cubes;
   }

   private List<TabulaCubeContainer> traverse(TabulaCubeGroupContainer group) {
      List<TabulaCubeContainer> retCubes = new ArrayList();

      for(TabulaCubeContainer child : group.getCubes()) {
         retCubes.addAll(this.traverse(child));
      }

      for(TabulaCubeGroupContainer child : group.getCubeGroups()) {
         retCubes.addAll(this.traverse(child));
      }

      return retCubes;
   }

   private List<TabulaCubeContainer> traverse(TabulaCubeContainer cube) {
      List<TabulaCubeContainer> retCubes = new ArrayList();
      retCubes.add(cube);

      for(TabulaCubeContainer child : cube.getChildren()) {
         retCubes.addAll(this.traverse(child));
      }

      return retCubes;
   }

   public void onResourceManagerReload(IResourceManager manager) {
      this.manager = manager;
   }

   public boolean accepts(ResourceLocation modelLocation) {
      return this.enabledDomains.contains(modelLocation.getResourceDomain()) && modelLocation.getResourcePath().endsWith(".tbl");
   }

   public IModel loadModel(ResourceLocation modelLocation) throws IOException {
      String modelPath = modelLocation.getResourcePath();
      modelPath = modelPath.substring(0, modelPath.lastIndexOf(46)) + ".json";
      IResource resource = this.manager.getResource(new ResourceLocation(modelLocation.getResourceDomain(), modelPath));
      InputStreamReader jsonStream = new InputStreamReader(resource.getInputStream());
      JsonElement json = this.parser.parse(jsonStream);
      jsonStream.close();
      ModelBlock modelBlock = this.modelBlockDeserializer.deserialize(json, ModelBlock.class, this);
      String tblLocationStr = json.getAsJsonObject().get("tabula").getAsString() + ".tbl";
      ResourceLocation tblLocation = new ResourceLocation(tblLocationStr);
      IResource tblResource = this.manager.getResource(tblLocation);
      InputStream modelStream = this.getModelJsonStream(tblLocation.toString(), tblResource.getInputStream());
      TabulaModelContainer modelJson = INSTANCE.loadTabulaModel(modelStream);
      modelStream.close();
      Builder<ResourceLocation> builder = ImmutableList.builder();
      int layer = 0;

      String texture;
      while((texture = (String)modelBlock.textures.get("layer" + layer++)) != null) {
         builder.add(new ResourceLocation(texture));
      }

      String particle = (String)modelBlock.textures.get("particle");
      return new VanillaTabulaModel(modelJson, particle != null ? new ResourceLocation(particle) : null, builder.build(), PerspectiveMapWrapper.getTransforms(modelBlock.getAllTransforms()));
   }

   private InputStream getModelJsonStream(String name, InputStream file) throws IOException {
      ZipInputStream zip = new ZipInputStream(file);

      ZipEntry entry;
      while((entry = zip.getNextEntry()) != null) {
         if (entry.getName().equals("model.json")) {
            return zip;
         }
      }

      throw new RuntimeException("No model.json present in " + name);
   }

   public <T> T deserialize(JsonElement json, Type type) throws JsonParseException {
      return (T)this.gson.fromJson(json, type);
   }
}
