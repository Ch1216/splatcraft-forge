package com.cibernet.splatcraft.items;

import com.cibernet.splatcraft.blocks.IColoredBlock;
import com.cibernet.splatcraft.blocks.InkedBlock;
import com.cibernet.splatcraft.blocks.InkwellBlock;
import com.cibernet.splatcraft.data.capabilities.playerinfo.PlayerInfoCapability;
import com.cibernet.splatcraft.registries.SplatcraftItemGroups;
import com.cibernet.splatcraft.registries.SplatcraftItems;
import com.cibernet.splatcraft.tileentities.InkColorTileEntity;
import com.cibernet.splatcraft.tileentities.InkwellTileEntity;
import com.cibernet.splatcraft.util.ColorUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ColoredBlockItem extends BlockItem implements IColoredItem
{

    private final Item clearItem;
    private boolean addStartersToTab = false;

    public ColoredBlockItem(Block block, String name, Properties properties, Item clearItem)
    {
        super(block, properties);
        SplatcraftItems.inkColoredItems.add(this);
        InkwellTileEntity.inkCoatingRecipes.put(clearItem, this);
        setRegistryName(name);
        this.clearItem = clearItem;
    }

    public ColoredBlockItem(Block block, String name, Properties properties)
    {
        this(block, name, properties, null);
    }

    public ColoredBlockItem(Block block, String name, int stackSize, Item clearItem)
    {
        this(block, name, new Properties().stacksTo(stackSize).tab(SplatcraftItemGroups.GROUP_GENERAL), clearItem);
    }

    public ColoredBlockItem(Block block, String name)
    {
        this(block, name, 64, null);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        super.appendHoverText(stack, level, tooltip, flag);

        if (I18n.exists(getDescriptionId() + ".tooltip"))
            tooltip.add(new TranslationTextComponent(getDescriptionId() + ".tooltip").withStyle(TextFormatting.GRAY));

        if (ColorUtils.isColorLocked(stack))
            tooltip.add(ColorUtils.getFormatedColorName(ColorUtils.getInkColor(stack), true));
        else
            tooltip.add(new TranslationTextComponent( "item.splatcraft.tooltip.matches_color").withStyle(TextFormatting.GRAY));
    }

    public ColoredBlockItem addStarterColors()
    {
        addStartersToTab = true;
        return this;
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, World levelIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state)
    {
        MinecraftServer server = levelIn.getServer();
        if (server == null)
        {
            return false;
        }

        int color = ColorUtils.getInkColor(stack);

        TileEntity tileEntity = levelIn.getBlockEntity(pos);
        if(color != -1)
        {
            if(getBlock() instanceof IColoredBlock)
                ((IColoredBlock) getBlock()).setColor(levelIn, pos, color);
            else if (tileEntity instanceof InkColorTileEntity)
                ((InkColorTileEntity) tileEntity).setColor(color);
        }
        return super.updateCustomBlockEntityTag(pos, levelIn, player, stack, state);
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items)
    {
        if (allowdedIn(group))
        {
            items.add(ColorUtils.setColorLocked(new ItemStack(this), false));
            if (addStartersToTab)
            {
                for (int color : ColorUtils.STARTER_COLORS)
                    items.add(ColorUtils.setColorLocked(ColorUtils.setInkColor(new ItemStack(this), color), true));
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World levelIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        super.inventoryTick(stack, levelIn, entityIn, itemSlot, isSelected);

        if (ColorUtils.getInkColor(stack) == -1 || !ColorUtils.isColorLocked(stack))
        {
            ColorUtils.setInkColor(stack, entityIn instanceof PlayerEntity && PlayerInfoCapability.hasCapability((LivingEntity) entityIn) ?
                    ColorUtils.getPlayerColor((PlayerEntity) entityIn) : ColorUtils.DEFAULT);
        }
    }


    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity)
    {
        BlockPos pos = entity.blockPosition();

        if (entity.level.getBlockState(pos.below()).getBlock() instanceof InkwellBlock)
        {
            InkColorTileEntity te = (InkColorTileEntity) entity.level.getBlockEntity(pos.below());

            if (ColorUtils.getInkColor(stack) != ColorUtils.getInkColor(te))
            {
                ColorUtils.setInkColor(entity.getItem(), ColorUtils.getInkColor(te));
                ColorUtils.setColorLocked(entity.getItem(), true);
            }
        }
        else if (clearItem != null && InkedBlock.causesClear(entity.level.getBlockState(pos)))
            entity.setItem(new ItemStack(clearItem, stack.getCount()));

        return false;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.getBlock() instanceof CauldronBlock && context.getPlayer() != null && context.getPlayer().isCrouching())
        {
            int i = state.getValue(CauldronBlock.LEVEL);

            if (i > 0)
            {
                ItemStack itemstack1 = new ItemStack(clearItem, 1);
                World level = context.getLevel();
                PlayerEntity player = context.getPlayer();
                ItemStack stack = context.getItemInHand();

                context.getPlayer().awardStat(Stats.USE_CAULDRON);

                if (!player.isCreative())
                {
                    stack.shrink(1);
                    level.setBlock(context.getClickedPos(), state.setValue(CauldronBlock.LEVEL, MathHelper.clamp(i - 1, 0, 3)), 2);
                    level.updateNeighbourForOutputSignal(context.getClickedPos(), state.getBlock());
                }

                if (stack.isEmpty())
                {
                    player.setItemInHand(context.getHand(), itemstack1);
                } else if (!player.inventory.add(itemstack1))
                {
                    player.drop(itemstack1, false);
                } else if (player instanceof ServerPlayerEntity)
                {
                    ((ServerPlayerEntity) player).refreshContainer(player.containerMenu);
                }

                return ActionResultType.SUCCESS;
            }

        }

        return super.useOn(context);
    }

    public ColoredBlockItem addStarters(boolean b)
    {
        addStartersToTab = b;
        return this;
    }
}
