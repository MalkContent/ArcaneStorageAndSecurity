package arcaneStorageAndSecurity.network;

import arcaneStorageAndSecurity.config.ConfigItems;
import arcaneStorageAndSecurity.items.ToolRing;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketToolRingChangeToServer implements IMessage {
	public int dim;
	public int playerid;
	public int number;

	public PacketToolRingChangeToServer() {}
	
	public PacketToolRingChangeToServer(EntityPlayer player, int number) {
		this.dim = player.worldObj.provider.getDimensionId();
		this.playerid = player.getEntityId();
		this.number = number;
	}

	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(this.dim);
		buffer.writeInt(this.playerid);
		buffer.writeInt(this.number);
	}

	public void fromBytes(ByteBuf buffer) {
		this.dim = buffer.readInt();
		this.playerid = buffer.readInt();
		this.number = buffer.readInt();
	}

}
