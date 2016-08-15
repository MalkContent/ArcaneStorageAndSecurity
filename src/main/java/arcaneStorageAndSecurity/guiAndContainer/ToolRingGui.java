package arcaneStorageAndSecurity.guiAndContainer;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class ToolRingGui extends GuiContainer {
	private static final ResourceLocation FocusPouchGui = new ResourceLocation("thaumcraft", "textures/gui/gui_focuspouch.png");
	private static final ResourceLocation ToolRingGuiMinor = new ResourceLocation("tca_arcsas", "textures/gui/gui_toolring_0.png");
	private static final ResourceLocation ToolRingGuiMajor = new ResourceLocation("tca_arcsas", "textures/gui/gui_toolring_1.png");
	private static final int tcPlayerInvHeigh = 175;
	private static final int tcPlayerInvYStart = 144;
	private boolean major;

	public ToolRingGui(ToolRingContainer inventorySlotsIn, boolean major) {
		super(inventorySlotsIn);
		this.xSize = 176;
        this.ySize = 176;
        this.major = major;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(major?ToolRingGuiMajor:ToolRingGuiMinor);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		drawTexturedModalRect(x, y-8, 0, 3, this.xSize, this.ySize);

		//TODO: create classes for bag items, catch blockedslot unknown, probably forget about this forever
        Slot blockedSlot = this.inventorySlots.getSlot(((ToolRingContainer)this.inventorySlots).getBlockedSlot());
        this.mc.getTextureManager().bindTexture(FocusPouchGui);
        GlStateManager.enableBlend();
        drawTexturedModalRect(x + blockedSlot.xDisplayPosition + 1, y + blockedSlot.yDisplayPosition + 1, 241, 1, 14, 14);
        GlStateManager.disableBlend();
	}

	
	
}
