package arcaneStorageAndSecurity.eventHandlers;

import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.glAlphaFunc;

import java.util.HashMap;
import java.util.TreeMap;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import arcaneStorageAndSecurity.config.ConfigItems;
import arcaneStorageAndSecurity.items.ToolRing;
import arcaneStorageAndSecurity.network.PacketHandler;
import arcaneStorageAndSecurity.network.PacketToolRingChangeToServer;
import baubles.api.BaublesApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.lib.UtilsFX;

public class RenderEventHandler {

	static float radialHudScale = 0.0F;
	TreeMap<Integer, Integer> dial = new TreeMap();
	HashMap<Integer, ItemStack> dialItem = new HashMap();
	HashMap<Integer, Boolean> dialHover = new HashMap();
	HashMap<Integer, Float> dialScale = new HashMap();
	long lastTime = 0L;
	boolean lastState = false;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		long time = System.nanoTime() / 1000000L;
		if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
			handleDialRadial(mc, time, event);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderOverlayPre(RenderGameOverlayEvent.Pre event) {
		if(KeyHandler.radialActive == true && event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && !dial.isEmpty())
			event.setCanceled(true);
	}

	@SideOnly(Side.CLIENT)
	private void handleDialRadial(Minecraft mc, long time, RenderGameOverlayEvent event) {
		if ((KeyHandler.radialActive) || (radialHudScale > 0.0F)) {
			long timeDiff = System.currentTimeMillis() - KeyHandler.lastPressF;
			if (KeyHandler.radialActive) {
				if (mc.currentScreen != null) {
					KeyHandler.radialActive = false;
					KeyHandler.radialLock = true;
					mc.setIngameFocus();
					mc.setIngameNotInFocus();
					return;
				}
				if (radialHudScale == 0.0F) {
					dial.clear();
					dialItem.clear();
					dialHover.clear();
					dialScale.clear();

					int pouchcount = 0;
					ItemStack item = null;
					ItemStack toolRing = mc.thePlayer.getCurrentEquippedItem();

					if (!ConfigItems.toolRing.isToolRing(toolRing))
						return;

					ItemStack[] items = ConfigItems.toolRing.getContainedItems(toolRing);
					for (int a = 0; a < items.length; a++) {
						item = items[a];
						if (item != null && !(item.getItem() instanceof ToolRing)) {
							dial.put(a, Integer.valueOf(a));
							dialItem.put(a, item.copy());
							dialScale.put(a, Float.valueOf(1.0F));
							dialHover.put(a, Boolean.valueOf(false));
						}
					}
				}
				if ((dial.size() > 0) && (mc.inGameHasFocus)) {
					mc.inGameHasFocus = false;
					mc.mouseHelper.ungrabMouseCursor();
				}
			} else if (mc.currentScreen == null) {
				if (this.lastState) {
					if (Display.isActive()) {
						if (!mc.inGameHasFocus) {
							mc.inGameHasFocus = true;
							mc.mouseHelper.grabMouseCursor();
						}
					}
					this.lastState = false;
				}
			}
			renderFocusRadialHUD(event.resolution.getScaledWidth_double(), event.resolution.getScaledHeight_double(),
					time, event.partialTicks);
			if (time > this.lastTime) {
				for (int key : dialHover.keySet()) {
					if (((Boolean) dialHover.get(key)).booleanValue()) {
						if ((!KeyHandler.radialActive) && (!KeyHandler.radialLock)) {
							PacketHandler.INSTANCE.sendToServer(new PacketToolRingChangeToServer(mc.thePlayer, dial.get(key)));
							KeyHandler.radialLock = true;
						}
						if (((Float) dialScale.get(key)).floatValue() < 1.3F) {
							dialScale.put(key, Float.valueOf(((Float) dialScale.get(key)).floatValue() + 0.025F));
						}
					} else if (((Float) dialScale.get(key)).floatValue() > 1.0F) {
						dialScale.put(key, Float.valueOf(((Float) dialScale.get(key)).floatValue() - 0.025F));
					}
				}
				if (!KeyHandler.radialActive) {
					radialHudScale -= 0.05F;
				} else if ((KeyHandler.radialActive) && (radialHudScale < 1.0F)) {
					radialHudScale += 0.05F;
				}
				if (radialHudScale > 1.0F) {
					radialHudScale = 1.0F;
				}
				if (radialHudScale < 0.0F) {
					radialHudScale = 0.0F;
					KeyHandler.radialLock = false;
				}
				this.lastTime = (time + 5L);
				this.lastState = KeyHandler.radialActive;
			}
		}
	}

	final ResourceLocation R1 = new ResourceLocation("thaumcraft", "textures/misc/radial.png");
	final ResourceLocation R2 = new ResourceLocation("thaumcraft", "textures/misc/radial2.png");

	@SideOnly(Side.CLIENT)
	private boolean shouldRender(ItemStack playerItem) {
		if (dialItem.isEmpty() || (playerItem == null)
				|| (!ConfigItems.toolRing.isToolRing(playerItem))) {
			return false;
		}
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	private void renderFocusRadialHUD(double sw, double sh, long time, float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		if(!shouldRender(mc.thePlayer.getCurrentEquippedItem()))
			return;

		ItemStack focus = mc.thePlayer.getCurrentEquippedItem(); //TODO: do me rename refactor?

		int i = (int) (Mouse.getEventX() * sw / mc.displayWidth);
		int j = (int) (sh - Mouse.getEventY() * sh / mc.displayHeight - 1.0D);
		int k = Mouse.getEventButton();

		GlStateManager.pushMatrix();
		
		GlStateManager.clear(GL11.GL_ACCUM);
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, sw, sh, 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);

		GlStateManager.pushMatrix();

		GlStateManager.translate(sw / 2.0D, sh / 2.0D, 0.0D);

		ItemStack tt = null;

		float width = 16.0F + this.dialItem.size() * 2.5F;

		mc.renderEngine.bindTexture(this.R1);
		GlStateManager.pushMatrix();
		GlStateManager.rotate(partialTicks + mc.thePlayer.ticksExisted % 720 / 2.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		UtilsFX.renderQuadCentered(1, 1, 0, width * 2.75F * radialHudScale, 0.5F, 0.5F, 0.5F, 200, 771, 0.5F);

		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.popMatrix();

		mc.renderEngine.bindTexture(this.R2);
		GlStateManager.pushMatrix();
		GlStateManager.rotate(-(partialTicks + mc.thePlayer.ticksExisted % 720 / 2.0F), 0.0F, 0.0F, 1.0F);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		UtilsFX.renderQuadCentered(1, 1, 0, width * 2.55F * radialHudScale, 0.5F, 0.5F, 0.5F, 200, 771, 0.5F);

		GlStateManager.disableBlend();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.popMatrix();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
//
		if (focus != null) {
			ItemStack item = focus.copy();
			UtilsFX.renderItemStack(mc.getMinecraft(), item, -8, -8, null);
			GlStateManager.disableLighting();
			int mx = (int) (i - sw / 2.0D);
			int my = (int) (j - sh / 2.0D);
			if ((mx >= -10) && (mx <= 10) && (my >= -10) && (my <= 10)) {
				tt = item;
			}
		}
		GlStateManager.scale(radialHudScale, radialHudScale, radialHudScale);

		float currentRot = -90.0F * radialHudScale;
		float pieSlice = 360.0F / dialItem.size();
		int key = (int) dial.firstKey();
		for (int a = 0; a < dialItem.size(); a++) {
			double xx = MathHelper.cos(currentRot / 180.0F * 3.141593F) * width;
			double yy = MathHelper.sin(currentRot / 180.0F * 3.141593F) * width;
			currentRot += pieSlice;

			GlStateManager.pushMatrix();
			GlStateManager.translate(xx, yy, 100.0D);
			GlStateManager.scale(((Float) dialScale.get(key)).floatValue(), ((Float) dialScale.get(key)).floatValue(),
					((Float) dialScale.get(key)).floatValue());
			GlStateManager.enableRescaleNormal();
			ItemStack item = ((ItemStack) this.dialItem.get(key)).copy();
			UtilsFX.renderItemStack(mc.getMinecraft(), item, -8, -8, null);
			GlStateManager.disableLighting();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
            
			if ((!KeyHandler.radialLock) && (KeyHandler.radialActive)) {
				int mx = (int) (i - sw / 2.0D - xx);
				int my = (int) (j - sh / 2.0D - yy);
				if ((mx >= -10) && (mx <= 10) && (my >= -10) && (my <= 10)) {
					dialHover.put(key, Boolean.valueOf(true));

					tt = (ItemStack) this.dialItem.get(key);
					if (k == 0) {
						KeyHandler.radialActive = false;
						KeyHandler.radialLock = true;
						PacketHandler.INSTANCE.sendToServer(new PacketToolRingChangeToServer(mc.thePlayer, dial.get(key)));
						break;
					}
				} else {
					dialHover.put(key, Boolean.valueOf(false));
				}
			}
			Integer nulltest = dial.higherKey(key);
			if(nulltest!=null)
				key = (int) nulltest;
		}
		GlStateManager.popMatrix();
		if (tt != null) {
			UtilsFX.drawCustomTooltip(mc.currentScreen, mc.fontRendererObj,
					tt.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips), -4, 20, 11);
			GlStateManager.disableLighting();
		}
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}
}
