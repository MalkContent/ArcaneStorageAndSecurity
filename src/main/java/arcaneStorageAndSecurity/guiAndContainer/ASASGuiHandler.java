package arcaneStorageAndSecurity.guiAndContainer;

import arcaneStorageAndSecurity.items.ToolRing;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ASASGuiHandler implements IGuiHandler {

	public static final int GUI_ID_TOOLRING = 0;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GUI_ID_TOOLRING && player.getCurrentEquippedItem() != null
				&& player.getCurrentEquippedItem().getItem() instanceof ToolRing)
			return new ToolRingContainer(player.inventory, player.getCurrentEquippedItem(), world);

		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		ItemStack curItem = player.getCurrentEquippedItem();
		if (ID == GUI_ID_TOOLRING && player.getCurrentEquippedItem() != null && curItem.getItem() instanceof ToolRing) {
			return new ToolRingGui(new ToolRingContainer(player.inventory, player.getCurrentEquippedItem(), world),
					curItem.getMetadata() == 1);
		}
		return null;
	}

}
