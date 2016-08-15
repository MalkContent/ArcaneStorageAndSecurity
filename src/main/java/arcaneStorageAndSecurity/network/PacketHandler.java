package arcaneStorageAndSecurity.network;

import arcaneStorageAndSecurity.config.ConfigItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler implements IMessageHandler<PacketToolRingChangeToServer, IMessage> {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("ArcaneStorageAndSecurity".toLowerCase());
	
	public static void init() {
		int idx = 0;
		INSTANCE.registerMessage(PacketHandler.class, PacketToolRingChangeToServer.class, idx++, Side.SERVER);
	}
	
	public IMessage onMessage(PacketToolRingChangeToServer message, MessageContext ctx) {
		World world = DimensionManager.getWorld(message.dim);
		if ((world == null) || ((ctx.getServerHandler().playerEntity != null)
				&& (ctx.getServerHandler().playerEntity.getEntityId() != message.playerid))) {
			return null;
		}
		Entity player = world.getEntityByID(message.playerid);
		if ((player != null) && ((player instanceof EntityPlayer)) && (((EntityPlayer) player).getHeldItem() != null)) {
			EntityPlayer castPlayer = (EntityPlayer)player;
			if (ConfigItems.toolRing.isToolRing(castPlayer.getHeldItem())) {
				ConfigItems.toolRing.dialNewItem(castPlayer, message.number);
			}
		}
		return null;
	}
}
