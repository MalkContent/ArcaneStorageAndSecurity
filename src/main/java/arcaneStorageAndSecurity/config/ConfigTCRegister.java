package arcaneStorageAndSecurity.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.OreDictionaryEntries;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchHelper;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.research.ScanItem;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;

public class ConfigTCRegister {

	private final String key_alchmanu = "ALCHEMICALMANUFACTURE";
	private final String key_alchdupe = "ALCHEMICALDUPLICATION";
	private final String key_entropro = "ENTROPICPROCESSING";
	private final String catK_artifice = "ARTIFICE";
	private final String catK_eldritch = "ELDRITCH";
	private final String catK_asto = "CAT_ARCANESTORAGE";
	private final String key_toolring_minor = "TOOLRING_MINOR";
	private final String key_toolring_major = "TOOLRING_MAJOR";
	private final String key_voidmetal = "VOIDMETAL";
	private final String key_focuspouch = "FOCUSPOUCH";
	private final String key_aspect_vacuus = "!" + Aspect.VOID.getTag();
	private final String key_elementaltools = "ELEMENTALTOOLS";
	private final String scanK_voidmetalingot = "!VOIDMETALINGOT";
	// private final String key_astoVirtual = "ARCANESTORAGE";

	private HashMap<String, Object> recipes = new HashMap<String, Object>();

	public void initTC() {
		registerCategories();
		registerRecipes();
		registerResearch();
	}

	public void registerCategories() {
		if (!Config.invasive)
			ResearchCategories.registerCategory(catK_asto, null,
					new ResourceLocation("thaumcraft", "textures/aspects/vacuos.png"),
					new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_4.jpg"),
					new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png"));
	}

	private String invadeCat(String targetCat) {
		return Config.invasive ? targetCat : catK_asto;
	}

	public void registerResearch() {
		new ResearchItem(key_toolring_minor, invadeCat(catK_artifice),
				new AspectList().add(Aspect.TOOL, 4).add(Aspect.ORDER, 4).add(Aspect.VOID, 4), Config.invasive ? 5 : 0,
				Config.invasive ? 5 : 0, 2, new ItemStack(ConfigItems.toolRing))
						.setPages(
								new ResearchPage[] { new ResearchPage("tc.research_page." + key_toolring_minor + ".1"),
										new ResearchPage((IArcaneRecipe) (recipes.get(key_toolring_minor))),
										new ResearchPage("tc.research_page." + key_toolring_minor + ".2") })
						.setParents(key_focuspouch, key_elementaltools).registerResearchItem();
		ScanningManager.addScannableThing(new ScanItem(scanK_voidmetalingot, new ItemStack(ItemsTC.ingots, 1, 1)));
		new ResearchItem(key_toolring_major, invadeCat(catK_eldritch),
				new AspectList().add(Aspect.TOOL, 4).add(Aspect.ORDER, 4).add(Aspect.ELDRITCH, 4).add(Aspect.MIND, 4)
						.add(Aspect.VOID, 4),
				Config.invasive ? 1 : 0, Config.invasive ? -4 : 2, 3, new ItemStack(ConfigItems.toolRing, 1, 1))
						.setPages(
								new ResearchPage[] { new ResearchPage("tc.research_page." + key_toolring_major + ".1"),
										new ResearchPage((InfusionRecipe) (recipes.get(key_toolring_major))),
										new ResearchPage("tc.research_page." + key_toolring_major + ".2") })
						.setParents(key_toolring_minor, key_voidmetal, scanK_voidmetalingot).setHidden()
						.registerResearchItem();
	}

	public void registerRecipes() {
		ItemStack crystalEssenceTool = new ItemStack(ItemsTC.crystalEssence);
		((ItemGenericEssentiaContainer) (ItemsTC.crystalEssence)).setAspects(crystalEssenceTool,
				new AspectList().add(Aspect.TOOL, 1));
		recipes.put(key_toolring_minor,
				ThaumcraftApi.addArcaneCraftingRecipe(key_toolring_minor, new ItemStack(ConfigItems.toolRing, 1,
						0),
				new AspectList().add(Aspect.ORDER, 200).add(Aspect.EARTH, 60).add(Aspect.FIRE, 120),
				new Object[] { "ABA", "CDC", "CEC", Character.valueOf('A'), new ItemStack(ItemsTC.gear),
						Character.valueOf('B'), crystalEssenceTool, Character.valueOf('C'),
						new ItemStack(BlocksTC.plank), Character.valueOf('D'), new ItemStack(ItemsTC.shard, 1, 4),
						Character.valueOf('E'), new ItemStack(ItemsTC.ingots) }));
		recipes.put(key_toolring_major,
				ThaumcraftApi.addInfusionCraftingRecipe(key_toolring_major, new ItemStack(ConfigItems.toolRing, 1, 1),
						9,
						new AspectList().add(Aspect.TOOL, 40).add(Aspect.EXCHANGE, 64).add(Aspect.MIND, 24)
								.add(Aspect.ELDRITCH, 24).add(Aspect.ORDER, 32),
				new ItemStack(ItemsTC.baubles, 1, 10),
				new Object[] { new ItemStack(ItemsTC.focusEqualTrade), new ItemStack(ItemsTC.ingots, 1, 1),
						new ItemStack(ItemsTC.voidSeed), new ItemStack(ItemsTC.quicksilver),
						new ItemStack(BlocksTC.brainBox), new ItemStack(ItemsTC.ingots, 1, 1),
						new ItemStack(ItemsTC.voidSeed), new ItemStack(ItemsTC.quicksilver) }));
	}

	private boolean injectSiblings(String targetResearch, String... newSiblings) {
		if (newSiblings != null) {
			int nLength = newSiblings.length;
			if (nLength > 0) {
				ResearchItem target = ResearchCategories.getResearch(targetResearch);
				if (target != null) {
					String[] oldSiblings = target.siblings;
					String[] mergedSiblings;
					if (oldSiblings == null) {
						mergedSiblings = newSiblings;
					} else {
						int oLength = oldSiblings.length;
						mergedSiblings = new String[oLength + nLength];
						System.arraycopy(oldSiblings, 0, mergedSiblings, 0, oLength);
						System.arraycopy(newSiblings, 0, mergedSiblings, oLength, nLength);
					}
					target.setSiblings(mergedSiblings);
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * public void aspectManipulation() { attachAspects(new
	 * ItemStack(Items.glass_bottle), new AspectList().add(Aspect.CRYSTAL, 1));
	 * attachAspects(new ItemStack(ItemsTC.coin), new
	 * AspectList().add(Aspect.DESIRE, 1)); attachAspects(new
	 * ItemStack(Items.dye, 1, 0), new AspectList().add(Aspect.WATER, 1));
	 * attachAspects(new ItemStack(Items.dye, 1, 4), new
	 * AspectList().add(Aspect.EARTH, 1)); attachAspects(new
	 * ItemStack(Items.dye, 1, 15), new AspectList().add(Aspect.LIFE, 1)); }
	 * 
	 * public void attachAspects(ItemStack itemStack, AspectList aspList) {
	 * attachAspects(itemStack, aspList, false); }
	 * 
	 * public void attachAspects(ItemStack itemStack, AspectList aspList,
	 * boolean overwrite) { List<Object> keyItemObj = Arrays.asList(new Object[]
	 * { itemStack.getItem(), itemStack.getMetadata() }); if(!overwrite) {
	 * aspList.add(ThaumcraftApi.objectTags.get(keyItemObj)); }
	 * ThaumcraftApi.objectTags.put(keyItemObj, aspList); }
	 * 
	 * public void attachRecipes() { String curKey = key_alchmanu;
	 * CrucibleRecipe dirt = ThaumcraftApi.addCrucibleRecipe(curKey, new
	 * ItemStack(Blocks.dirt), "logWood", new AspectList().add(Aspect.DEATH,
	 * 2).add(Aspect.LIFE, 1)); injectCrucibleRecipe(dirt, curKey);
	 * CrucibleRecipe mossybricks = ThaumcraftApi.addCrucibleRecipe(curKey, new
	 * ItemStack(Blocks.stonebrick, 1, 1), new ItemStack(Blocks.stonebrick), new
	 * AspectList().add(Aspect.PLANT, 2)); injectCrucibleRecipe(mossybricks,
	 * curKey, getFirstIndexOfOutputPage(curKey, new
	 * ItemStack(Blocks.mossy_cobblestone))); CrucibleRecipe netherrack =
	 * ThaumcraftApi.addCrucibleRecipe(curKey, new ItemStack(Blocks.netherrack,
	 * 4), new ItemStack(Blocks.cobblestone), new
	 * AspectList().add(Aspect.ELDRITCH, 2).add(Aspect.FIRE, 4)); CrucibleRecipe
	 * granite = ThaumcraftApi.addCrucibleRecipe(curKey, new
	 * ItemStack(Blocks.stone, 1, 1), new ItemStack(Blocks.stone), new
	 * AspectList().add(Aspect.EARTH, 2).add(Aspect.CRYSTAL, 1)); CrucibleRecipe
	 * diorite = ThaumcraftApi.addCrucibleRecipe(curKey, new
	 * ItemStack(Blocks.stone, 1, 3), new ItemStack(Blocks.stone), new
	 * AspectList().add(Aspect.FIRE, 2).add(Aspect.EARTH, 1)); CrucibleRecipe
	 * andesite = ThaumcraftApi.addCrucibleRecipe(curKey, new
	 * ItemStack(Blocks.stone, 1, 5), new ItemStack(Blocks.stone), new
	 * AspectList().add(Aspect.FIRE, 2).add(Aspect.AIR, 1));
	 * injectCrucibleRecipe(new CrucibleRecipe[]{granite, diorite, andesite} ,
	 * curKey);
	 * 
	 * curKey = key_entropro; CrucibleRecipe deadbush =
	 * ThaumcraftApi.addCrucibleRecipe(curKey, new ItemStack(Blocks.deadbush),
	 * "treeSapling", new AspectList().add(Aspect.DEATH, 1));
	 * injectCrucibleRecipe(deadbush, curKey, 0); CrucibleRecipe coal =
	 * ThaumcraftApi.addCrucibleRecipe(curKey, new ItemStack(Items.coal), new
	 * ItemStack(Items.coal, 1, 1), new AspectList().add(Aspect.EARTH,
	 * 4).add(Aspect.ENTROPY, 1)); injectCrucibleRecipe(coal, curKey, 1);
	 * CrucibleRecipe podzol = ThaumcraftApi.addCrucibleRecipe(curKey, new
	 * ItemStack(Blocks.dirt, 1, 2), new ItemStack(Blocks.dirt), new
	 * AspectList().add(Aspect.ENTROPY, 1)); injectCrucibleRecipe(podzol,
	 * curKey); CrucibleRecipe sand = ThaumcraftApi.addCrucibleRecipe(curKey,
	 * new ItemStack(Blocks.sand), new ItemStack(Blocks.stone), new
	 * AspectList().add(Aspect.ENTROPY, 2)); injectCrucibleRecipe(sand, curKey);
	 * CrucibleRecipe redsand = ThaumcraftApi.addCrucibleRecipe(curKey, new
	 * ItemStack(Blocks.sand, 1, 1), new ItemStack(Blocks.stone, 1, 1), new
	 * AspectList().add(Aspect.ENTROPY, 2)); injectCrucibleRecipe(redsand,
	 * curKey); // CrucibleRecipe gravel =
	 * ThaumcraftApi.addCrucibleRecipe(curKey, new ItemStack(Blocks.gravel), new
	 * ItemStack(Blocks.cobblestone), new AspectList().add(Aspect.ENTROPY, 1));
	 * // injectCrucibleRecipe(gravel, curKey); CrucibleRecipe crackedBrick =
	 * ThaumcraftApi.addCrucibleRecipe(curKey, new ItemStack(Blocks.stonebrick,
	 * 1, 2), new ItemStack(Blocks.stonebrick), new
	 * AspectList().add(Aspect.ENTROPY, 1).add(Aspect.FIRE, 1));
	 * injectCrucibleRecipe(crackedBrick, curKey);
	 * 
	 * curKey = key_alchdupe; CrucibleRecipe lapis =
	 * ThaumcraftApi.addCrucibleRecipe(curKey, new ItemStack(Items.dye, 2, 4),
	 * new ItemStack(Items.dye, 1, 4), new AspectList().add(Aspect.SENSES,
	 * 1).add(Aspect.EARTH, 1).add(Aspect.CRYSTAL, 2).add(Aspect.ENERGY, 1));
	 * injectCrucibleRecipe(lapis, curKey); CrucibleRecipe redstone =
	 * ThaumcraftApi.addCrucibleRecipe(curKey, new ItemStack(Items.redstone, 2),
	 * new ItemStack(Items.redstone), new AspectList().add(Aspect.ENERGY,
	 * 3).add(Aspect.SENSES, 1)); injectCrucibleRecipe(redstone, curKey); }
	 * 
	 * private int getFirstIndexOfOutputPage(String researchKey, ItemStack
	 * output) { ResearchPage[] pages =
	 * ResearchCategories.getResearch(researchKey).getPages(); for(int i = 0; i
	 * < pages.length; i++) { Object curOutput = pages[i].recipeOutput;
	 * if(curOutput != null && curOutput instanceof ItemStack &&
	 * ((ItemStack)curOutput).isItemEqual(output) &&
	 * ItemStack.areItemStackTagsEqual((ItemStack)curOutput, output)) return i;
	 * } return -1; }
	 * 
	 * private void injectCrucibleRecipe(Object cruRec, String researchKey) {
	 * injectCrucibleRecipe(cruRec, researchKey, -1); }
	 * 
	 * private void injectCrucibleRecipe(Object cruRec, String researchKey, int
	 * index) { injectCrucibleRecipe(cruRec, researchKey, index, false); }
	 * 
	 * private void injectCrucibleRecipe(Object cruRec, String researchKey, int
	 * index, boolean before) { if(cruRec instanceof CrucibleRecipe || cruRec
	 * instanceof CrucibleRecipe[]) { ResearchItem ri =
	 * ResearchCategories.getResearch(researchKey); ArrayList<ResearchPage>
	 * pages = new ArrayList<ResearchPage>(Arrays.asList(ri.getPages()));
	 * pages.add(index < 0 ? pages.size() : before ? index : index + 1, cruRec
	 * instanceof CrucibleRecipe ? new ResearchPage((CrucibleRecipe)cruRec) :
	 * new ResearchPage((CrucibleRecipe[])cruRec));
	 * ri.setPages(pages.toArray(new ResearchPage[pages.size()])); } }
	 */
}
