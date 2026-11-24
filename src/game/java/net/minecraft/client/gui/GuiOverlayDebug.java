package net.minecraft.client.gui;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.ext.dynamiclights.DynamicLightsStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.biome.Biome;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Display;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.HString;
import net.lax1dude.eaglercraft.v1_8.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import org.apache.commons.lang3.StringUtils;

@SideOnly(Side.CLIENT)
public class GuiOverlayDebug extends Gui
{
    private final Minecraft mc;
    private final FontRenderer fontRenderer;
    public int playerOffset = 0;
    public static final int ticksAtMidnight = 18000;
    public static final int ticksPerDay = 24000;
    public static final int ticksPerHour = 1000;
    public static final double ticksPerMinute = 1000d / 60d;
    public static final double ticksPerSecond = 1000d / 60d / 60d;
    private static final SimpleDateFormat SDFTwentyFour = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
    private static final SimpleDateFormat SDFTwelve = new SimpleDateFormat("h:mm aa", Locale.ENGLISH);

    public GuiOverlayDebug(Minecraft mc)
    {
        this.mc = mc;
        this.fontRenderer = mc.fontRenderer;
    }

    public void renderDebugInfo(ScaledResolution scaledResolutionIn)
    {
        this.mc.mcProfiler.startSection("debug");

        if (this.mc.gameSettings.showDebugInfo)
        {
            GlStateManager.pushMatrix();
            this.renderDebugInfoLeft();
            this.renderDebugInfoRight(scaledResolutionIn);
            GlStateManager.popMatrix();

            if (this.mc.gameSettings.showLagometer)
            {
                this.renderLagometer();
            }
        }

        this.renderEaglerHUD(scaledResolutionIn);
        this.mc.mcProfiler.endSection();
    }

    public void renderEaglerHUD(ScaledResolution scaledResolutionIn)
    {
        this.playerOffset = 0;
        int ww = scaledResolutionIn.getScaledWidth();
        int hh = scaledResolutionIn.getScaledHeight();

        if (!this.mc.gameSettings.showDebugInfo)
        {
            int i = 2;

            if (this.mc.gameSettings.hudFps)
            {
                this.drawFPS(2, i);
                this.playerOffset = this.drawSingleplayerStats(scaledResolutionIn);
                i += 9;
            }

            if (this.mc.gameSettings.hudCoords)
            {
                this.drawXYZ(2, i);
            }
        }

        if (this.mc.currentScreen == null || !(this.mc.currentScreen instanceof GuiChat))
        {
            if (this.mc.gameSettings.hudStats)
            {
                this.drawStatsHUD(ww - 2, hh - 2);
            }

            if (this.mc.gameSettings.hudWorld)
            {
                this.drawWorldHUD(2, hh - 2);
            }
        }

        if (this.mc.gameSettings.hudCoords && this.mc.joinWorldTickCounter < 80)
        {
            if (this.mc.joinWorldTickCounter > 70)
            {
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }

            int i = this.mc.joinWorldTickCounter - 70;

            if (i < 0)
            {
                i = 0;
            }

            this.drawHideHUD(ww / 2, hh - 70, (10 - i) * 0xFF / 10);

            if (this.mc.joinWorldTickCounter > 70)
            {
                GlStateManager.disableBlend();
            }
        }
    }

    private void drawFPS(int x, int y)
    {
        this.fontRenderer.drawStringWithShadow(this.mc.renderGlobal.getDebugInfoShort(), (float)x, (float)y, 0xFFFFFF);
    }

    private void drawXYZ(int x, int y)
    {
        Entity e = this.mc.getRenderViewEntity();
        BlockPos blockpos = new BlockPos(e.posX, e.getEntityBoundingBox().minY, e.posZ);
        this.fontRenderer.drawStringWithShadow("x: " + blockpos.getX() + ", y: " + blockpos.getY() + ", z: " + blockpos.getZ(), (float)x, (float)y, 0xFFFFFF);
    }

    private void drawStatsHUD(int x, int y)
    {
        int i = 9;
        String line = "Walk: " + TextFormatting.YELLOW + HString.format("%.2f", this.mc.player.getAIMoveSpeed()) + TextFormatting.WHITE + " Flight: " + (this.mc.player.capabilities.allowFlying ? ("" + TextFormatting.YELLOW + this.mc.player.capabilities.getFlySpeed()) : TextFormatting.RED + "No");
        int lw = this.fontRenderer.getStringWidth(line);
        this.fontRenderer.drawStringWithShadow(line, (float)(x - lw), (float)(y - i), 0xFFFFFF);
        i += 11;
        line = "Food: " + TextFormatting.YELLOW + this.mc.player.getFoodStats().getFoodLevel() + TextFormatting.WHITE + ", Sat: " + TextFormatting.YELLOW + HString.format("%.1f", this.mc.player.getFoodStats().getSaturationLevel());
        lw = this.fontRenderer.getStringWidth(line);
        this.fontRenderer.drawStringWithShadow(line, (float)(x - lw), (float)(y - i), 0xFFFFFF);
        i += 11;
        line = "Amr: " + TextFormatting.YELLOW + this.mc.player.getTotalArmorValue() + TextFormatting.WHITE + ", Health: " + TextFormatting.RED + HString.format("%.1f", this.mc.player.getHealth());
        lw = this.fontRenderer.getStringWidth(line);
        this.fontRenderer.drawStringWithShadow(line, (float)(x - lw), (float)(y - i), 0xFFFFFF);
        i += 11;
        int xpc = this.mc.player.xpBarCap();
        line = "XP: " + TextFormatting.GREEN + MathHelper.floor(this.mc.player.experience * (float)xpc) + TextFormatting.WHITE + " / " + TextFormatting.GREEN + xpc;
        lw = this.fontRenderer.getStringWidth(line);
        this.fontRenderer.drawStringWithShadow(line, (float)(x - lw), (float)(y - i), 0xFFFFFF);
        i += 11;

        for (PotionEffect e : this.mc.player.getActivePotionEffects())
        {
            i += 11;
            int t = e.getDuration() / 20;
            int m = t / 60;
            int sec = t % 60;
            int j = e.getAmplifier();

            if (j > 0)
            {
                line = I18n.format(e.getEffectName()) + " " + TextFormatting.YELLOW + TextFormatting.BOLD + I18n.format("potion.potency." + j) + TextFormatting.RESET + " [" + TextFormatting.YELLOW + HString.format("%02d:%02d", m, sec) + TextFormatting.RESET + "]";
            }
            else
            {
                line = I18n.format(e.getEffectName()) + " [" + TextFormatting.YELLOW + HString.format("%02d:%02d", m, sec) + TextFormatting.RESET + "]";
            }

            lw = this.fontRenderer.getStringWidth(line);
            this.fontRenderer.drawStringWithShadow(line, (float)(x - lw), (float)(y - i), 0xFFFFFF);
        }
    }

    private void drawWorldHUD(int x, int y)
    {
        /*
         * Math was taken from: https://github.com/EssentialsX/Essentials/blob/
         * dc7fb919391d62de45e17b51ae1e6fe3e66d7ac6/Essentials/src/main/java/com/
         * earth2me/essentials/utils/DescParseTickFormat.java
         */
        long totalTicks = this.mc.world.getWorldTime();
        long ticks = totalTicks;
        ticks = ticks - ticksAtMidnight + ticksPerDay;
        final long days = ticks / ticksPerDay;
        ticks -= days * ticksPerDay;
        final long hours = ticks / ticksPerHour;
        ticks -= hours * ticksPerHour;
        final long minutes = (long)Math.floor(ticks / ticksPerMinute);
        final double dticks = ticks - minutes * ticksPerMinute;
        final long seconds = (long)Math.floor(dticks / ticksPerSecond);
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.ENGLISH);
        cal.setLenient(true);
        cal.set(0, Calendar.JANUARY, 1, 0, 0, 0);
        cal.add(Calendar.DAY_OF_YEAR, (int)days);
        cal.add(Calendar.HOUR_OF_DAY, (int)hours);
        cal.add(Calendar.MINUTE, (int)minutes);
        cal.add(Calendar.SECOND, (int)seconds + 1);
        SimpleDateFormat fmt = this.mc.gameSettings.hud24h ? SDFTwentyFour : SDFTwelve;
        fmt.setCalendar(cal);
        String timeString = TextFormatting.WHITE + "Day " + ((totalTicks + 30000L) / 24000L) + " (" + TextFormatting.YELLOW + fmt.format(cal.getTime()) + TextFormatting.WHITE + ")";
        Entity e = this.mc.getRenderViewEntity();
        BlockPos blockpos = new BlockPos(e.posX, MathHelper.clamp(e.getEntityBoundingBox().minY, 0.0D, 254.0D), e.posZ);
        Biome biome = this.mc.world.getBiome(blockpos);
        Chunk c = this.mc.world.getChunkFromBlockCoords(blockpos);
        int blockLight = c.getLightFor(EnumSkyBlock.BLOCK, blockpos);
        int skyLight = c.getLightFor(EnumSkyBlock.SKY, blockpos) - this.mc.world.calculateSkylightSubtracted(1.0F);
        int totalLight = Math.max(blockLight, skyLight);
        TextFormatting lightColor = blockLight < 8 ? ((skyLight < 8 || !this.mc.world.isDaytime()) ? TextFormatting.RED : TextFormatting.YELLOW) : TextFormatting.GREEN;
        String lightString = "Light: " + lightColor + totalLight + TextFormatting.WHITE;
        float temp = biome.getTemperature(blockpos);
        String tempString = "Temp: " + ((blockLight > 11 || temp > 0.15F) ? TextFormatting.YELLOW : TextFormatting.AQUA) + HString.format("%.2f", temp) + TextFormatting.WHITE;
        this.fontRenderer.drawStringWithShadow(timeString, (float)x, (float)(y - 30), 0xFFFFFF);
        this.fontRenderer.drawStringWithShadow("Biome: " + TextFormatting.AQUA + biome.getBiomeName(), (float)x, (float)(y - 19), 0xFFFFFF);
        this.fontRenderer.drawStringWithShadow(lightString + " " + tempString, (float)x, (float)(y - 8), 0xFFFFFF);
    }

    private void drawHideHUD(int x, int y, int fade)
    {
        this.drawCenteredString(this.fontRenderer, I18n.format("options.hud.note"), x, y, 0xEECC00 | (fade << 24));
    }

    private int drawSingleplayerStats(ScaledResolution parScaledResolution)
    {
        if (this.mc.isDemo())
        {
            return 13;
        }

        int i = 0;

        if (SingleplayerServerController.isWorldRunning())
        {
            long tpsAge = SingleplayerServerController.getTPSAge();

            if (tpsAge < 20000L)
            {
                int color = tpsAge > 2000L ? 0x777777 : 0xFFFFFF;
                List<String> strs = SingleplayerServerController.getTPS();

                if (SingleplayerServerController.isRunningSingleThreadMode())
                {
                    strs = Lists.newArrayList(strs);
                    strs.add("");
                    strs.add(I18n.format("singleplayer.tpscounter.singleThreadMode"));
                }

                boolean first = true;

                for (int j = 0, m = strs.size(); j < m; ++j)
                {
                    String str = strs.get(j);

                    if (!StringUtils.isAllEmpty(str))
                    {
                        int l = (int)((float)this.fontRenderer.getStringWidth(str) * (!first ? 0.5F : 1.0F));
                        GlStateManager.pushMatrix();
                        GlStateManager.translate((float)(parScaledResolution.getScaledWidth() - 2 - l), (float)(i + 2), 0.0F);

                        if (!first)
                        {
                            GlStateManager.scale(0.5F, 0.5F, 0.5F);
                        }

                        this.fontRenderer.drawStringWithShadow(str, 0.0F, 0.0F, color);
                        GlStateManager.popMatrix();

                        if (color == 0xFFFFFF)
                        {
                            color = 14737632;
                        }
                    }

                    i += (int)((float)this.fontRenderer.FONT_HEIGHT * (!first ? 0.5F : 1.0F));
                    first = false;
                }
            }
        }

        return i > 0 ? i + 2 : i;
    }

    protected void renderDebugInfoLeft()
    {
        List<String> list = this.call();
        list.add("");
        list.add("Debug: Pie [shift]: " + (this.mc.gameSettings.showDebugProfilerChart ? "visible" : "hidden") + " FPS [alt]: " + (this.mc.gameSettings.showLagometer ? "visible" : "hidden"));
        list.add("For help: press F3 + Q");

        for (int i = 0; i < list.size(); ++i)
        {
            String s = list.get(i);

            if (!Strings.isNullOrEmpty(s))
            {
                int j = this.fontRenderer.FONT_HEIGHT;
                int k = this.fontRenderer.getStringWidth(s);
                int l = 2;
                int i1 = 2 + j * i;
                drawRect(1, i1 - 1, 2 + k + 1, i1 + j - 1, -1873784752);
                this.fontRenderer.drawString(s, 2, i1, 14737632);
            }
        }
    }

    protected void renderDebugInfoRight(ScaledResolution scaledRes)
    {
        List<String> list = this.getDebugInfoRight();

        for (int i = 0; i < list.size(); ++i)
        {
            String s = list.get(i);

            if (!Strings.isNullOrEmpty(s))
            {
                int j = this.fontRenderer.FONT_HEIGHT;
                int k = this.fontRenderer.getStringWidth(s);
                int l = scaledRes.getScaledWidth() - 2 - k;
                int i1 = 2 + j * i;
                drawRect(l - 1, i1 - 1, l + k + 1, i1 + j - 1, -1873784752);
                this.fontRenderer.drawString(s, l, i1, 14737632);
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
    protected List<String> call()
    {
        BlockPos blockpos = new BlockPos(this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ);

        if (this.mc.isReducedDebug())
        {
            return Lists.newArrayList("Minecraft 1.12.2 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.world.getDebugLoadedEntities(), this.mc.world.getProviderName(), "", HString.format("Chunk-relative: %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15));
        }
        else
        {
            Entity entity = this.mc.getRenderViewEntity();
            EnumFacing enumfacing = entity.getHorizontalFacing();
            String s = "Invalid";

            switch (enumfacing)
            {
                case NORTH:
                    s = "Towards negative Z";
                    break;
                case SOUTH:
                    s = "Towards positive Z";
                    break;
                case WEST:
                    s = "Towards negative X";
                    break;
                case EAST:
                    s = "Towards positive X";
            }

            List<String> list = Lists.newArrayList("Minecraft 1.12.2 (" + this.mc.getVersion() + "/" + ClientBrandRetriever.getClientModName() + ("release".equalsIgnoreCase(this.mc.getVersionType()) ? "" : "/" + this.mc.getVersionType()) + ")", this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(), "P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.world.getDebugLoadedEntities(), this.mc.world.getProviderName(), "", HString.format("XYZ: %.3f / %.5f / %.3f", this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ), HString.format("Block: %d %d %d", blockpos.getX(), blockpos.getY(), blockpos.getZ()), HString.format("Chunk: %d %d %d in %d %d %d", blockpos.getX() & 15, blockpos.getY() & 15, blockpos.getZ() & 15, blockpos.getX() >> 4, blockpos.getY() >> 4, blockpos.getZ() >> 4), HString.format("Facing: %s (%s) (%.1f / %.1f)", enumfacing, s, MathHelper.wrapDegrees(entity.rotationYaw), MathHelper.wrapDegrees(entity.rotationPitch)));

            if (DynamicLightsStateManager.isDynamicLightsRender())
            {
                list.add(6, DynamicLightsStateManager.getF3String());
            }

            if (this.mc.world != null)
            {
                Chunk chunk = this.mc.world.getChunkFromBlockCoords(blockpos);

                if (this.mc.world.isBlockLoaded(blockpos) && blockpos.getY() >= 0 && blockpos.getY() < 256)
                {
                    if (!chunk.isEmpty())
                    {
                        list.add("Biome: " + chunk.getBiome(blockpos, this.mc.world.getBiomeProvider()).getBiomeName());
                        list.add("Light: " + chunk.getLightSubtracted(blockpos, 0) + " (" + chunk.getLightFor(EnumSkyBlock.SKY, blockpos) + " sky, " + chunk.getLightFor(EnumSkyBlock.BLOCK, blockpos) + " block)");
                        DifficultyInstance difficultyinstance = this.mc.world.getDifficultyForLocation(blockpos);

                        list.add(HString.format("Local Difficulty: %.2f // %.2f (Day %d)", difficultyinstance.getAdditionalDifficulty(), difficultyinstance.getClampedAdditionalDifficulty(), this.mc.world.getWorldTime() / 24000L));
                    }
                    else
                    {
                        list.add("Waiting for chunk...");
                    }
                }
                else
                {
                    list.add("Outside of world...");
                }
            }

            if (net.lax1dude.eaglercraft.v1_8.opengl.ext.deferred.EaglerDeferredPipeline.instance != null
                    && this.mc.gameSettings.deferredShaderConf.shaderPackInfo != null)
            {
                list.add("Shader: " + this.mc.gameSettings.deferredShaderConf.shaderPackInfo.name);
            }

            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && this.mc.objectMouseOver.getBlockPos() != null)
            {
                BlockPos blockpos1 = this.mc.objectMouseOver.getBlockPos();
                list.add(HString.format("Looking at: %d %d %d", blockpos1.getX(), blockpos1.getY(), blockpos1.getZ()));
            }

            return list;
        }
    }

    protected <T extends Comparable<T>> List<String> getDebugInfoRight()
    {
        List<String> list;

        if (EagRuntime.getPlatformType() == EnumPlatformType.DESKTOP)
        {
            long i = EagRuntime.maxMemory();
            long j = EagRuntime.totalMemory();
            long k = EagRuntime.freeMemory();
            long l = j - k;
            list = Lists.newArrayList("Platform: Desktop", HString.format("Java: %s %dbit", System.getProperty("java.version"), this.mc.isJava64bit() ? 64 : 32), HString.format("Mem: % 2d%% %03d/%03dMB", l * 100L / i, bytesToMb(l), bytesToMb(i)), HString.format("Allocated: % 2d%% %03dMB", j * 100L / i, bytesToMb(j)), "", HString.format("CPU: %s", "eaglercraft"), "", HString.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), EaglercraftGPU.glGetString(7936)), EaglercraftGPU.glGetString(7937), EaglercraftGPU.glGetString(7938));
        }
        else
        {
            list = Lists.newArrayList("Platform: " + EagRuntime.getPlatformType().getName(), "Java: TeaVM", "", HString.format("CPU: %s", "eaglercraft"), "", HString.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), EaglercraftGPU.glGetString(7936)), EaglercraftGPU.glGetString(7937), EaglercraftGPU.glGetString(7938));
        }

        list.add("");
        list.addAll(net.minecraftforge.fml.common.FMLCommonHandler.instance().getBrandings(false));

        if (this.mc.isReducedDebug())
        {
            return list;
        }
        else
        {
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && this.mc.objectMouseOver.getBlockPos() != null)
            {
                BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
                IBlockState iblockstate = this.mc.world.getBlockState(blockpos);

                if (this.mc.world.getWorldType() != WorldType.DEBUG_ALL_BLOCK_STATES)
                {
                    iblockstate = iblockstate.getActualState(this.mc.world, blockpos);
                }

                list.add("");
                list.add(String.valueOf(Block.REGISTRY.getNameForObject(iblockstate.getBlock())));
                IProperty<T> iproperty;
                String s;

                for (UnmodifiableIterator unmodifiableiterator = iblockstate.getProperties().entrySet().iterator(); unmodifiableiterator.hasNext(); list.add(iproperty.getName() + ": " + s))
                {
                    Entry < IProperty<?>, Comparable<? >> entry = (Entry)unmodifiableiterator.next();
                    iproperty = (IProperty)entry.getKey();
                    T t = (T)entry.getValue();
                    s = iproperty.getName(t);

                    if (Boolean.TRUE.equals(t))
                    {
                        s = TextFormatting.GREEN + s;
                    }
                    else if (Boolean.FALSE.equals(t))
                    {
                        s = TextFormatting.RED + s;
                    }
                }
            }

            return list;
        }
    }

    public void renderLagometer()
    {
        GlStateManager.disableDepth();
        FrameTimer frametimer = this.mc.getFrameTimer();
        int i = frametimer.getLastIndex();
        int j = frametimer.getIndex();
        long[] along = frametimer.getFrames();
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        int k = i;
        int l = 0;
        drawRect(0, scaledresolution.getScaledHeight() - 60, 240, scaledresolution.getScaledHeight(), -1873784752);

        while (k != j)
        {
            int i1 = frametimer.getLagometerValue(along[k], 30);
            int j1 = this.getFrameColor(MathHelper.clamp(i1, 0, 60), 0, 30, 60);
            this.drawVerticalLine(l, scaledresolution.getScaledHeight(), scaledresolution.getScaledHeight() - i1, j1);
            ++l;
            k = frametimer.parseIndex(k + 1);
        }

        drawRect(1, scaledresolution.getScaledHeight() - 30 + 1, 14, scaledresolution.getScaledHeight() - 30 + 10, -1873784752);
        this.fontRenderer.drawString("60", 2, scaledresolution.getScaledHeight() - 30 + 2, 14737632);
        this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 30, -1);
        drawRect(1, scaledresolution.getScaledHeight() - 60 + 1, 14, scaledresolution.getScaledHeight() - 60 + 10, -1873784752);
        this.fontRenderer.drawString("30", 2, scaledresolution.getScaledHeight() - 60 + 2, 14737632);
        this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 60, -1);
        this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 1, -1);
        this.drawVerticalLine(0, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);
        this.drawVerticalLine(239, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);

        if (this.mc.gameSettings.limitFramerate <= 120)
        {
            this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 60 + this.mc.gameSettings.limitFramerate / 2, -16711681);
        }

        GlStateManager.enableDepth();
    }

    private int getFrameColor(int p_181552_1_, int p_181552_2_, int p_181552_3_, int p_181552_4_)
    {
        return p_181552_1_ < p_181552_3_ ? this.blendColors(-16711936, -256, (float)p_181552_1_ / (float)p_181552_3_) : this.blendColors(-256, -65536, (float)(p_181552_1_ - p_181552_3_) / (float)(p_181552_4_ - p_181552_3_));
    }

    private int blendColors(int p_181553_1_, int p_181553_2_, float p_181553_3_)
    {
        int i = p_181553_1_ >> 24 & 255;
        int j = p_181553_1_ >> 16 & 255;
        int k = p_181553_1_ >> 8 & 255;
        int l = p_181553_1_ & 255;
        int i1 = p_181553_2_ >> 24 & 255;
        int j1 = p_181553_2_ >> 16 & 255;
        int k1 = p_181553_2_ >> 8 & 255;
        int l1 = p_181553_2_ & 255;
        int i2 = MathHelper.clamp((int)((float)i + (float)(i1 - i) * p_181553_3_), 0, 255);
        int j2 = MathHelper.clamp((int)((float)j + (float)(j1 - j) * p_181553_3_), 0, 255);
        int k2 = MathHelper.clamp((int)((float)k + (float)(k1 - k) * p_181553_3_), 0, 255);
        int l2 = MathHelper.clamp((int)((float)l + (float)(l1 - l) * p_181553_3_), 0, 255);
        return i2 << 24 | j2 << 16 | k2 << 8 | l2;
    }

    private static long bytesToMb(long bytes)
    {
        return bytes / 1024L / 1024L;
    }
}
