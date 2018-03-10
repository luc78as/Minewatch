package twopiradians.minewatch.common.item.weapon;

import javax.vecmath.Vector2f;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.minewatch.client.model.ModelMWArmor;
import twopiradians.minewatch.common.Minewatch;
import twopiradians.minewatch.common.entity.ability.EntityRoadhogHook;
import twopiradians.minewatch.common.entity.ability.EntityRoadhogScrap;
import twopiradians.minewatch.common.entity.ability.EntityWidowmakerHook;
import twopiradians.minewatch.common.entity.projectile.EntityRoadhogBullet;
import twopiradians.minewatch.common.hero.Ability;
import twopiradians.minewatch.common.hero.EnumHero;
import twopiradians.minewatch.common.item.ModItems;
import twopiradians.minewatch.common.sound.ModSoundEvents;
import twopiradians.minewatch.common.util.EntityHelper;
import twopiradians.minewatch.common.util.TickHandler;
import twopiradians.minewatch.common.util.TickHandler.Handler;
import twopiradians.minewatch.common.util.TickHandler.Identifier;
import twopiradians.minewatch.packet.SPacketSimple;

public class ItemRoadhogWeapon extends ItemMWWeapon {

	private static final ResourceLocation CHAIN = new ResourceLocation(Minewatch.MODID, "textures/entity/roadhog_chain.png");
	public static final Handler HEALING = new Handler(Identifier.ROADHOG_HEALING, true) {
		@Override
		@SideOnly(Side.CLIENT)
		public boolean onClientTick() {
			if (this.ticksLeft <= 25 && this.ticksLeft % 4 == 0)
				EntityHelper.spawnHealParticles(entity, true);
			return super.onClientTick();
		}
		@Override
		public boolean onServerTick() {
			if (this.ticksLeft <= 20)
				EntityHelper.heal(entityLiving, 15);
			return super.onServerTick();
		}
		@Override
		@SideOnly(Side.CLIENT)
		public Handler onClientRemove() {
			if (this.ticksLeft <= 0) // if completed fully, toss sound
				ModSoundEvents.ROADHOG_HEAL_TOSS.playFollowingSound(entity, 1.0f, 1.0f, false);
			else { // if interrupted, stop healing sounds
				ModSoundEvents.ROADHOG_HEAL_0.stopFollowingSound(entity);
				ModSoundEvents.ROADHOG_HEAL_1.stopFollowingSound(entity);
				ModSoundEvents.ROADHOG_HEAL_2.stopFollowingSound(entity);
			}
			return super.onClientRemove();
		}
		@Override
		public Handler onServerRemove() {
			EnumHero.ROADHOG.ability1.keybind.setCooldown(entityLiving, 160, false);
			return super.onServerRemove();
		}
	};
	public static final Handler HOOKING = new Handler(Identifier.ROADHOG_HOOKING, true) {
		@Override
		@SideOnly(Side.CLIENT)
		public boolean onClientTick() {

			return super.onClientTick();
		}
		@Override
		public boolean onServerTick() {

			return super.onServerTick();
		}
		@Override
		@SideOnly(Side.CLIENT)
		public Handler onClientRemove() {

			return super.onClientRemove();
		}
		@Override
		public Handler onServerRemove() {

			return super.onServerRemove();
		}
	};
	public static final Handler HOOKED = new Handler(Identifier.ROADHOG_HOOKED, false) {
		@Override
		@SideOnly(Side.CLIENT)
		public boolean onClientTick() {

			return super.onClientTick();
		}
		@Override
		public boolean onServerTick() {

			return super.onServerTick();
		}
		@Override
		@SideOnly(Side.CLIENT)
		public Handler onClientRemove() {

			return super.onClientRemove();
		}
		@Override
		public Handler onServerRemove() {

			return super.onServerRemove();
		}
	};

	public ItemRoadhogWeapon() {
		super(30);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onItemLeftClick(ItemStack stack, World world, EntityLivingBase player, EnumHand hand) { 
		// primary fire
		if (!world.isRemote && this.canUse(player, true, hand, false) && !TickHandler.hasHandler(player, Identifier.ROADHOG_HEALING)) {
			for (int i=0; i<25; ++i) {
				EntityRoadhogBullet projectile = new EntityRoadhogBullet(world, player, hand.ordinal());
				EntityHelper.setAim(projectile, player, player.rotationPitch, player.rotationYawHead, 60, 19F, hand, 10, 0);
				world.spawnEntity(projectile);
			}
			ModSoundEvents.ROADHOG_SHOOT_0.playSound(player, world.rand.nextFloat()+0.5F, world.rand.nextFloat()/3+0.8f);
			this.subtractFromCurrentAmmo(player, 1);
			if (world.rand.nextInt(25) == 0)
				player.getHeldItem(hand).damageItem(1, player);
			this.setCooldown(player, 26);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityLivingBase player, EnumHand hand) {
		// secondary fire
		if (!world.isRemote && this.canUse(player, true, hand, false) && !TickHandler.hasHandler(player, Identifier.ROADHOG_HEALING)) {
			EntityRoadhogScrap projectile = new EntityRoadhogScrap(world, player, hand.ordinal());
			EntityHelper.setAim(projectile, player, player.rotationPitch, player.rotationYawHead, 60, 0F, hand, 10, 0);
			world.spawnEntity(projectile);
			ModSoundEvents.ROADHOG_SHOOT_1.playSound(player, world.rand.nextFloat()+0.5F, world.rand.nextFloat()/3+0.8f);
			this.subtractFromCurrentAmmo(player, 1);
			if (world.rand.nextInt(25) == 0)
				player.getHeldItem(hand).damageItem(1, player);
			this.setCooldown(player, 26);
		}

		return super.onItemRightClick(world, player, hand);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
		super.onUpdate(stack, world, entity, slot, isSelected);

		if (isSelected && entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getHeldItemMainhand() == stack) {	
			EntityLivingBase player = (EntityLivingBase) entity;

			// hook
			if (!world.isRemote && hero.ability2.isSelected(player) && 
					this.canUse(player, true, EnumHand.MAIN_HAND, true)) { 
				EntityRoadhogHook projectile = new EntityRoadhogHook(world, player, EnumHand.OFF_HAND.ordinal());
				EntityHelper.setAim(projectile, player, player.rotationPitch, player.rotationYawHead, 40, 0F, EnumHand.OFF_HAND, 40, 0.35f);
				world.spawnEntity(projectile);
				int ticks = 10;
				TickHandler.register(false, HOOKING.setEntity(projectile).setEntityLiving(player).setTicks(ticks),
						Ability.ABILITY_USING.setEntity(player).setTicks(ticks).setAbility(hero.ability2));
				Minewatch.network.sendToDimension(new SPacketSimple(75, player, false, projectile, ticks, 0, 0), world.provider.getDimension());
				ModSoundEvents.ROADHOG_HOOK_THROW.playSound(player, world.rand.nextFloat()+0.5F, world.rand.nextFloat()/3+0.8f);
			}
			// health
			else if (!world.isRemote && (player.getHeldItemOffhand() == null || player.getHeldItemOffhand().isEmpty()) && 
					hero.ability1.isSelected(player) && 
					this.canUse(player, true, EnumHand.MAIN_HAND, false) && 
					!TickHandler.hasHandler(player, Identifier.ROADHOG_HEALING)) {
				ModSoundEvents.ROADHOG_HEAL_0.playFollowingSound(player, 1, 1, false);
				ModSoundEvents.ROADHOG_HEAL_1.playFollowingSound(player, 1, 1, false);
				ModSoundEvents.ROADHOG_HEAL_2.playFollowingSound(player, 1, 1, false);
				TickHandler.register(false, HEALING.setEntity(player).setTicks(36),
						Ability.ABILITY_USING.setEntity(player).setTicks(36).setAbility(hero.ability1));
				Minewatch.network.sendToDimension(new SPacketSimple(74, player, true), world.provider.getDimension());
				player.setHeldItem(EnumHand.OFF_HAND, new ItemStack(ModItems.roadhog_health));
			}

		}
	}	

	@Override
	@SideOnly(Side.CLIENT)
	public boolean preRenderArmor(EntityLivingBase entity, ModelMWArmor model) {
		// hack
		if (entity.getHeldItemOffhand() != null && entity.getHeldItemOffhand().getItem() == ModItems.roadhog_health) {
			model.bipedLeftArmwear.rotateAngleX = 5;
			model.bipedLeftArm.rotateAngleX = 5;
			model.bipedLeftArmwear.rotateAngleY = -0.2f;
			model.bipedLeftArm.rotateAngleY = -0.2f;
		}

		// health coloring
		Handler handler = TickHandler.getHandler(entity, Identifier.ROADHOG_HEALING);
		if (handler != null && handler.ticksLeft <= 25) {
			float percent = 1f - handler.ticksLeft / 25f;
			GlStateManager.color((255f-67f*percent)/255f, (255f-102f*percent)/255f, (255f-201f*percent)/255f);
			return true;
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRenderHand(AbstractClientPlayer player, EnumHand hand) {
		return hand == EnumHand.OFF_HAND &&
				TickHandler.hasHandler(handler -> handler.identifier == Identifier.ROADHOG_HOOKING && handler.entityLiving == Minecraft.getMinecraft().player, true);
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void handleHealth(LivingDamageEvent event) {
		if (TickHandler.hasHandler(event.getEntity(), Identifier.ROADHOG_HEALING))
			event.setAmount(event.getAmount() / 2f);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderHookChain(RenderWorldLastEvent event) {
		for (Handler handler : TickHandler.getHandlers(true, null, Identifier.ROADHOG_HOOKING, null)) {
			// rope
			if (handler.entity instanceof EntityRoadhogHook && handler.entity.isEntityAlive() && 
					((EntityRoadhogHook) handler.entity).getThrower() != null) {
				EntityRoadhogHook entity = (EntityRoadhogHook) handler.entity;
				Minecraft mc = Minecraft.getMinecraft();
				GlStateManager.pushMatrix();
				GlStateManager.enableLighting();
				mc.getTextureManager().bindTexture(CHAIN);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();
				buffer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_TEX);

				double width = 0.04d;
				Vec3d playerPos = EntityHelper.getEntityPartialPos(Minewatch.proxy.getRenderViewEntity());
				Vec3d throwerPos = EntityHelper.getEntityPartialPos(entity.getThrower());
				Vector2f rotations = EntityHelper.getEntityPartialRotations(entity.getThrower());
				Vec3d shooting = EntityHelper.getShootingPos(entity.getThrower(), rotations.x, rotations.y, EnumHand.OFF_HAND, 23, 0.7f).subtract(throwerPos);

				// translate to thrower
				Vec3d translate = throwerPos.subtract(playerPos);
				GlStateManager.translate(translate.x, translate.y, translate.z);

				Vec3d hookLook = entity.getLook(mc.getRenderPartialTicks()).scale(0.3d);
				Vec3d hookPos = EntityHelper.getEntityPartialPos(entity).addVector(0, entity.height/2f, 0).subtract(hookLook).subtract(throwerPos);
				double v = hookPos.distanceTo(shooting)*2d;

				double deg_to_rad = 0.0174532925d;
				double precision = 0.013d;
				double degrees = 360d;
				double steps = Math.round(degrees*precision);
				degrees += 0.2d;
				double angle = 0;

				for (int i=1; i<=steps; i+=2) {
					angle = degrees/steps*i;
					double circleX = Math.cos(angle*deg_to_rad);
					double circleY = Math.sin(angle*deg_to_rad);
					double circleZ = 0;//Math.cos(angle*deg_to_rad);
					Vec3d vec = new Vec3d(circleX, circleY, circleZ).scale(width).add(hookPos);
					buffer.pos(vec.x, vec.y, vec.z).tex(i/steps, 0).endVertex();

					vec = new Vec3d(circleX, circleY, circleZ).scale(width).add(shooting);
					buffer.pos(vec.x, vec.y, vec.z).tex(i/steps, v).endVertex();

					angle = degrees/steps*(i+1);
					circleX = Math.cos(angle*deg_to_rad);
					circleY = Math.sin(angle*deg_to_rad);
					circleZ = 0;//Math.cos(angle*deg_to_rad);
					vec = new Vec3d(circleX, circleY, circleZ).scale(width).add(hookPos);
					buffer.pos(vec.x, vec.y, vec.z).tex((i+1)/steps, 0).endVertex();

					vec = new Vec3d(circleX, circleY, circleZ).scale(width).add(shooting);
					buffer.pos(vec.x, vec.y, vec.z).tex((i+1)/steps, v).endVertex();
				}


				tessellator.draw();
				GlStateManager.popMatrix();
			}
		}
	}

}