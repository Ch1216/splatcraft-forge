package com.cibernet.splatcraft.proxy;

import com.cibernet.splatcraft.SplatCraft;
import com.cibernet.splatcraft.entities.SplatCraftEntities;
import com.cibernet.splatcraft.registries.SplatCraftBlocks;
import com.cibernet.splatcraft.registries.SplatCraftItems;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy
{
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(SplatCraftItems.class);
        MinecraftForge.EVENT_BUS.register(SplatCraftBlocks.class);

        SplatCraftEntities.registerEntities();

        GameRegistry.registerTileEntity(TileEntityInkedBlock.class, SplatCraft.MODID+ ":inked_block");
    }

    public void init()
    {

    }

    public void postInit()
    {

    }

}
