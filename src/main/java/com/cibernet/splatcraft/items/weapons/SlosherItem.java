package com.cibernet.splatcraft.items.weapons;

import com.cibernet.splatcraft.entities.InkProjectileEntity;
import com.cibernet.splatcraft.handlers.PlayerPosingHandler;
import com.cibernet.splatcraft.registries.SplatcraftSounds;
import com.cibernet.splatcraft.util.InkBlockUtils;
import com.cibernet.splatcraft.util.PlayerCooldown;
import com.cibernet.splatcraft.util.WeaponStat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class SlosherItem extends WeaponBaseItem
{
    public float projectileSize;
    public float projectileSpeed;
    public float damage;
    public int startupTicks;
    public int projectileCount;
    public float diffAngle;
    public float inkConsumption;

    public SlosherItem(String name, float projectileSize, float projectileSpeed, int projectileCount, float offsetBetweenProj, float damage, int startupTicks, float inkConsumption)
    {
        super();
        setRegistryName(name);

        this.projectileSize = projectileSize;
        this.projectileSpeed = projectileSpeed;
        this.damage = damage;
        this.startupTicks = startupTicks;
        this.projectileCount = projectileCount;
        this.diffAngle = offsetBetweenProj;
        this.inkConsumption = inkConsumption;


        addStat(new WeaponStat("range", (stack, level) -> (int) (projectileSpeed / 1.2f * 100)));
        addStat(new WeaponStat("damage", (stack, level) -> (int) (damage / 20 * 100)));
        addStat(new WeaponStat("handling", (stack, level) -> (11 - startupTicks) * 10));
    }

    public SlosherItem(String name, SlosherItem parent)
    {
        this(name, parent.projectileSize, parent.projectileSpeed, parent.projectileCount, parent.diffAngle, parent.damage, parent.startupTicks, parent.inkConsumption);
    }

    @Override
    public void weaponUseTick(World level, LivingEntity entity, ItemStack stack, int timeLeft)
    {
        if (getInkAmount(entity, stack) >= inkConsumption)
        {
            if (entity instanceof PlayerEntity && getUseDuration(stack) - timeLeft < startupTicks)
            {
                PlayerCooldown.setPlayerCooldown((PlayerEntity) entity, new PlayerCooldown(startupTicks, ((PlayerEntity) entity).inventory.selected, entity.getUsedItemHand(), true, false, true, entity.isOnGround()));
            }
        } else
        {
            sendNoInkMessage(entity, null);
        }
    }

    @Override
    public void onPlayerCooldownEnd(World level, PlayerEntity player, ItemStack stack, PlayerCooldown cooldown)
    {
        if (getInkAmount(player, stack) >= inkConsumption)
        {
            if (!level.isClientSide)
            {
                for (int i = 0; i < projectileCount; i++)
                {
                    boolean hasTrail = i == Math.floor((projectileCount - 1) / 2f) || i == Math.ceil((projectileCount - 1) / 2f);
                    float angle = diffAngle * i - diffAngle * (projectileCount - 1) / 2;

                    InkProjectileEntity proj = new InkProjectileEntity(level, player, stack, InkBlockUtils.getInkType(player), projectileSize * (hasTrail ? 1 : 0.8f), damage);
                    proj.setShooterTrail();
                    proj.shootFromRotation(player, player.xRot, player.yRot + angle, 0.0f, projectileSpeed, 2);
                    level.addFreshEntity(proj);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SplatcraftSounds.slosherShot, SoundCategory.PLAYERS, 0.7F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.1F + 1.0F) * 0.95F);
                reduceInk(player, inkConsumption);
            }
        } else
        {
            sendNoInkMessage(player, null);
        }
    }

    @Override
    public PlayerPosingHandler.WeaponPose getPose()
    {
        return PlayerPosingHandler.WeaponPose.BUCKET_SWING;
    }
}
