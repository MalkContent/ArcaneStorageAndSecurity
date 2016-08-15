package arcaneStorageAndSecurity.eventHandlers;

import akka.actor.dsl.Inbox.Select;
import arcaneStorageAndSecurity.config.ConfigItems;
import arcaneStorageAndSecurity.items.ToolRing;
import arcaneStorageAndSecurity.network.PacketHandler;
import arcaneStorageAndSecurity.network.PacketToolRingChangeToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyHandler {

	private KeyBinding keyF;
	private KeyBinding keyG;
	private KeyBinding keyH;
	private boolean keyPressedF = false;
	private boolean keyPressedH = false;
	private boolean keyPressedG = false;
	public static boolean radialActive = false;
	public static boolean radialLock = false;
	public static long lastPressF = 0L;
	public static long lastPressH = 0L;
	public static long lastPressG = 0L;

	public KeyHandler() {
		int matchNum = 0;
		for (KeyBinding kb : Minecraft.getMinecraft().gameSettings.keyBindings) {
			String keyDesc = kb.getKeyDescription();
			if (keyDesc.equals("Change Wand Focus")) {
				keyF = kb;
				matchNum++;
			}
			if (keyDesc.equals("Misc Wand Toggle")) {
				keyG = kb;
				matchNum++;
			}
			if (keyDesc.equals("Activate Hover Harness")) {
				keyH = kb;
				matchNum++;
			}
			if (matchNum >= 3)
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		if (!event.player.getEntityWorld().isRemote)
			return;
		if (event.phase == TickEvent.Phase.START) {
			if (this.keyF.isKeyDown()) {
				if (FMLClientHandler.instance().getClient().inGameHasFocus) {
					EntityPlayer player = event.player;
					if (player != null) {
						if (!this.keyPressedF) {
							lastPressF = System.currentTimeMillis();
							radialLock = false;
						}
						if ((!radialLock) && (player.getHeldItem() != null)
								&& (ConfigItems.toolRing.isToolRing(player.getHeldItem()))) {
							if (player.isSneaking()) {
								if (!(player.getHeldItem().getItem() instanceof ToolRing))
									PacketHandler.INSTANCE.sendToServer(new PacketToolRingChangeToServer(player, -1));
							} else
								radialActive = true;
						}
					}
					this.keyPressedF = true;
				}
			} else {
				radialActive = false;
				if (this.keyPressedF) {
					lastPressF = System.currentTimeMillis();
				}
				this.keyPressedF = false;
			}
		}
	}

}
