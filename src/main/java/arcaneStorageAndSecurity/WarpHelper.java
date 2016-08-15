package arcaneStorageAndSecurity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import thaumcraft.api.items.IWarpingGear;

public class WarpHelper {
	public final static String tcWarpingTag = "TC.WARP";
	
	public static int getItemWarpingNBT(ItemStack stack) {
		if ((stack.hasTagCompound()) && (stack.getTagCompound().hasKey(tcWarpingTag))) {
			return stack.getTagCompound().getByte(tcWarpingTag);
		}
		return 0;
	}

	public static void setItemWarpingNBT(ItemStack stack, int warping) {
		
		if(warping == 0 && stack.hasTagCompound()) {
			stack.getTagCompound().removeTag(tcWarpingTag);
			if(stack.getTagCompound().hasNoTags())
				stack.setTagCompound(null);
		}
		else
			stack.setTagInfo(tcWarpingTag, new NBTTagByte((byte)warping));
	}
	
	public static void transferWarpingNBT(ItemStack target, ItemStack provider) {
		int targetNewWarping = getItemWarpingTotal(provider) - getItemWarpingItembased(target);
		setItemWarpingNBT(provider, getItemWarpingNBT(target));
		setItemWarpingNBT(target, targetNewWarping);
	}
	
	public static int getItemWarpingItembased(ItemStack stack) {
		return (stack.getItem() instanceof IWarpingGear)?((IWarpingGear)stack.getItem()).getWarp(stack, null) : 0;
	}

	public static int getItemWarpingTotal(ItemStack stack) {
		return getItemWarpingNBT(stack) + getItemWarpingItembased(stack);
	}
}
