package arcaneStorageAndSecurity.eventHandlers;

import arcaneStorageAndSecurity.config.ConfigItems;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerEventHandler {

	@SubscribeEvent
	public void renderOverlay(PlayerDestroyItemEvent event) {
		if (!event.entityPlayer.getEntityWorld().isRemote && ConfigItems.toolRing.isToolRing(event.original)) {
			ConfigItems.toolRing.dialNewItem(event.entityPlayer, -2, event.original);
			// TODO: handle warped item gone
		}
	}

	@SubscribeEvent
	public void tooltipEvent(ItemTooltipEvent event) {
		if (ConfigItems.toolRing.hasToolRingInventory(event.itemStack)) {
			event.toolTip.add(EnumChatFormatting.DARK_BLUE + StatCollector.translateToLocal("misc.istoolring"));
		}
	}
}
