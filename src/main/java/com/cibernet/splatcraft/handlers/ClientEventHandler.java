package com.cibernet.splatcraft.handlers;

import com.cibernet.splatcraft.entities.renderers.RenderInklingSquid;
import com.cibernet.splatcraft.items.ItemWeaponBase;
import com.cibernet.splatcraft.network.PacketPlayerSetTransformed;
import com.cibernet.splatcraft.network.PacketWeaponLeftClick;
import com.cibernet.splatcraft.network.SplatCraftPacketHandler;
import com.cibernet.splatcraft.utils.SplatCraftPlayerData;
import com.cibernet.splatcraft.utils.SplatCraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEventHandler
{
	public static ClientEventHandler instance = new ClientEventHandler();

	public static final AttributeModifier IN_USE_SPEED_BOOST = (new AttributeModifier( "Weapon use speed boost", 4D, 2)).setSaved(false);
	private static final AttributeModifier SQUID_LAND_SPEED = (new AttributeModifier( "Squid in land speed boost", -0.4D, 2)).setSaved(false);
	private static final AttributeModifier SQUID_SWIM_SPEED = (new AttributeModifier( "Squid swim speed boost", 1.25D, 2)).setSaved(false);


	@SubscribeEvent
	public void cllientTick(TickEvent.ClientTickEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;

		if(player == null)
			return;

		IAttributeInstance attributeInstance = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		ItemStack weapon = player.getActiveItemStack();

		if(attributeInstance.hasModifier(SQUID_LAND_SPEED))
			attributeInstance.removeModifier(SQUID_LAND_SPEED);
		if(attributeInstance.hasModifier(SQUID_SWIM_SPEED))
			attributeInstance.removeModifier(SQUID_SWIM_SPEED);
		if(attributeInstance.hasModifier(IN_USE_SPEED_BOOST))
			attributeInstance.removeModifier(IN_USE_SPEED_BOOST);

		AttributeModifier weaponMod = getWeaponMod(attributeInstance);
		if(weaponMod != null)
			attributeInstance.removeModifier(weaponMod);

		boolean isSquid = SplatCraftPlayerData.getIsSquid(player);
		if(isSquid)
		{
			if(SplatCraftUtils.canSquidHide(player.world, player))
			{
				if(!attributeInstance.hasModifier(SQUID_SWIM_SPEED))
					attributeInstance.applyModifier(SQUID_SWIM_SPEED);
			}
			else if(!attributeInstance.hasModifier(SQUID_LAND_SPEED))
				attributeInstance.applyModifier(SQUID_LAND_SPEED);

		}
		if(weapon.getItem() instanceof ItemWeaponBase)
		{
			AttributeModifier speedMod = ((ItemWeaponBase) weapon.getItem()).getSpeedModifier();

			if(player.getItemInUseCount() > 0)
			{
				if(!attributeInstance.hasModifier(IN_USE_SPEED_BOOST))
					attributeInstance.applyModifier(IN_USE_SPEED_BOOST);
				if(!isSquid && speedMod != null && !attributeInstance.hasModifier(speedMod))
					attributeInstance.applyModifier(speedMod);
			}
		}
	}

	@SubscribeEvent
	public void updateFOV(FOVUpdateEvent event)
	{
		float fov = event.getNewfov();

		if(event.getEntity().getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(IN_USE_SPEED_BOOST))
			fov -= 2f;

		event.setNewfov(fov);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void renderPlayerPre(RenderPlayerEvent.Pre event)
	{
		
		EntityPlayer player = event.getEntityPlayer();
		if(SplatCraftPlayerData.getIsSquid(player))
		{
			event.setCanceled(true);
			
			if(!SplatCraftUtils.canSquidHide(player.world, player))
			{
				RenderInklingSquid render = new RenderInklingSquid(event.getRenderer().getRenderManager());
				render.doRender(player, event.getX(), event.getY(), event.getZ(), player.rotationYawHead, event.getPartialRenderTick());
			}
			
		}
	}

	@SubscribeEvent
	public void onLeftClick(PlayerInteractEvent.LeftClickEmpty event)
	{
		SplatCraftPacketHandler.instance.sendToServer(new PacketWeaponLeftClick(event.getEntityPlayer().getUniqueID()));
	}


	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		if(SplatCraftKeyHandler.squidKey.isPressed())
		{
			boolean isSquid = SplatCraftPlayerData.getIsSquid(player);
			SplatCraftPacketHandler.instance.sendToServer(new PacketPlayerSetTransformed(player.getUniqueID(), !isSquid));
		}
	}

	private AttributeModifier getWeaponMod(IAttributeInstance instance)
	{
		for(ItemWeaponBase item : ItemWeaponBase.weapons)
		{
			if(item.getSpeedModifier() == null)
				continue;
			if(instance.hasModifier(item.getSpeedModifier()))
				return item.getSpeedModifier();
		}
		return null;
	}
	
}
