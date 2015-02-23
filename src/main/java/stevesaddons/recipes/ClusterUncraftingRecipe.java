package stevesaddons.recipes;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import vswe.stevesfactory.blocks.ClusterRegistry;
import vswe.stevesfactory.blocks.ItemCluster;
import vswe.stevesfactory.blocks.ModBlocks;

public class ClusterUncraftingRecipe implements IRecipe
{
    private static ItemStack cluster = new ItemStack(ModBlocks.blockCableCluster);
    private static ItemStack advcluster = new ItemStack(ModBlocks.blockCableCluster, 1, 8);

    /**
     * Used to check if a recipe matches current crafting inventory
     *
     * @param crafting
     * @param world
     */
    @Override
    public boolean matches(InventoryCrafting crafting, World world)
    {
        return matches(crafting);
    }

    public boolean matches(IInventory crafting)
    {
        boolean cluster = false;
        for (int i = 0; i < crafting.getSizeInventory(); i++)
        {
            ItemStack stack = crafting.getStackInSlot(i);
            if (stack == null) continue;
            if (!cluster && (stack.isItemEqual(this.cluster) || stack.isItemEqual(this.advcluster)))
            {
                cluster = true;
                continue;
            }
            return false;
        }
        return cluster;
    }

    public ItemStack getCluster(IInventory crafting)
    {
        for (int i = 0; i < crafting.getSizeInventory(); i++)
        {
            ItemStack stack = crafting.getStackInSlot(i);
            if (stack == null) continue;
            if (stack.isItemEqual(this.cluster) || stack.isItemEqual(this.advcluster)) return stack;
        }
        return null;
    }

    /**
     * Returns an Item that is the result of this recipe
     *
     * @param crafting
     */
    @Override
    public ItemStack getCraftingResult(InventoryCrafting crafting)
    {
        ItemStack result = cluster.copy();
        result.setItemDamage(getCluster(crafting).getItemDamage());
        return result;
    }


    /**
     * Returns the size of the recipe area
     */
    @Override
    public int getRecipeSize()
    {
        return 1;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return null;
    }

    @SubscribeEvent
    public void onCrafting(PlayerEvent.ItemCraftedEvent e)
    {
        if (matches(e.craftMatrix))
        {
            ItemStack stack = getCluster(e.craftMatrix);
            if (stack != null)
            {
                if (stack.hasTagCompound())
                {
                    byte[] types = stack.getTagCompound().getCompoundTag(ItemCluster.NBT_CABLE).getByteArray(ItemCluster.NBT_TYPES);
                    int stackSize = e.crafting.stackSize;
                    stackSize = stackSize == 0 ? 1 : stackSize;
                    for (byte type : types)
                    {
                        ItemStack component = ClusterRegistry.getRegistryList().get(type).getItemStack().copy();
                        component.stackSize = stackSize;
                        if (!e.player.inventory.addItemStackToInventory(component))
                            e.player.dropPlayerItemWithRandomChoice(component, false);
                    }
                }
            }
        }
    }
}