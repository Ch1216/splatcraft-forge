package com.cibernet.splatcraft.blocks;

import com.cibernet.splatcraft.utils.InkColors;
import com.cibernet.splatcraft.tileentities.TileEntityInkedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockInked extends Block
{
	public BlockInked()
	{
		super(Material.CLAY, MapColor.CLAY);
		setUnlocalizedName("inkedBlock");
		setRegistryName("inked_block");
		setTickRandomly(true);
	}
	
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		return ItemStack.EMPTY;
	}
	
	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		if(!(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
			return super.getMapColor(state, worldIn, pos);
		
		TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
		InkColors color = InkColors.getByColor(te.getColor());
		if(color == null)
			return super.getMapColor(state, worldIn, pos);
		else return color.getMapColor();
		
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{

		if((worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock))
		{
			TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
			worldIn.setBlockState(pos, te.getSavedState(), 3);
		}
		else worldIn.setBlockState(pos, Blocks.SAND.getDefaultState(), 3);
	}

	protected boolean tryTouchWater(World worldIn, BlockPos pos, IBlockState state)
	{
		boolean touchingWater = false;

		for (EnumFacing enumfacing : EnumFacing.values())
		{
			if (enumfacing != EnumFacing.DOWN)
			{
				BlockPos blockpos = pos.offset(enumfacing);

				if (worldIn.getBlockState(blockpos).getMaterial() == Material.WATER)
				{
					touchingWater = true;
					break;
				}
			}
		}

		if (touchingWater)
		{
			if(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock)
			{
				TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
				worldIn.setBlockState(pos, te.getSavedState(), 3);
			}
		}

		return touchingWater;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		//worldIn.setBlockState(pos, Blocks.SAND.getDefaultState(), 3);

		if(worldIn.getBlockState(fromPos).getMaterial().equals(Material.WATER))
			if(worldIn.getTileEntity(pos) instanceof TileEntityInkedBlock)
			{
				TileEntityInkedBlock te = (TileEntityInkedBlock) worldIn.getTileEntity(pos);
				worldIn.setBlockState(pos, te.getSavedState(), 3);
			}



	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		if (!this.tryTouchWater(worldIn, pos, state))
		{
			super.onBlockAdded(worldIn, pos, state);
		}
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityInkedBlock();
	}
}
