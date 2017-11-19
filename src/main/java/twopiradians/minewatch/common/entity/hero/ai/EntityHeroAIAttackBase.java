package twopiradians.minewatch.common.entity.hero.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import twopiradians.minewatch.client.key.Keys.KeyBind;
import twopiradians.minewatch.common.entity.hero.EntityHero;
import twopiradians.minewatch.common.tickhandler.TickHandler;
import twopiradians.minewatch.common.tickhandler.TickHandler.Identifier;
import twopiradians.minewatch.common.util.EntityHelper;

public abstract class EntityHeroAIAttackBase extends EntityAIBase {

	public enum MovementType {
		STRAFING, MELEE, HEAL
	}

	protected final EntityHero entity;
	protected final float maxAttackDistance;
	protected int attackCooldown;
	protected int seeTime;
	protected boolean strafingClockwise;
	protected boolean strafingBackwards;
	protected int strafingTime = -1;
	protected MovementType movementType;
	protected float strafingBackwardsPercent = 0.8F;
	protected float strafingForwardsPercent = 0.1F;

	public EntityHeroAIAttackBase(EntityHero entity, MovementType type, float maxDistance) {
		this.entity = entity;
		this.movementType = type;
		this.maxAttackDistance = maxDistance * maxDistance; //XXX customizable
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		return EntityHelper.shouldHit(entity, entity.getAttackTarget(), false) && entity.getAttackTarget() != null && 
				entity.isEntityAlive() && entity.getAttackTarget().isEntityAlive() && 
				!TickHandler.hasHandler(entity.getAttackingEntity(), Identifier.ANA_SLEEP);
	}

	@Override
	public boolean continueExecuting() {
		return this.shouldExecute();
	}

	@Override
	public void resetTask() {
		super.resetTask();
		this.seeTime = 0;
		this.attackCooldown = 0;
		this.entity.resetActiveHand();
		this.resetKeybinds();
	}

	@Nullable
	public EntityLivingBase getTarget() {
		return this.entity.getAttackTarget();
	}

	@Override
	public void updateTask() {
		EntityLivingBase target = this.getTarget();

		if (target != null) {
			double distanceSq = this.entity.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
			boolean canSee = this.entity.getEntitySenses().canSee(target);
			boolean positiveSeeTime = this.seeTime > 0;

			if (canSee != positiveSeeTime)
				this.seeTime = 0;

			if (canSee)
				++this.seeTime;
			else
				--this.seeTime;

			this.move(target, canSee, distanceSq);

			this.attackTarget(target, canSee, Math.sqrt(distanceSq));
		}
	}

	protected void resetKeybinds() {
		this.entity.getDataManager().set(KeyBind.LMB.datamanager, false);
		this.entity.getDataManager().set(KeyBind.RMB.datamanager, false);
		this.entity.getDataManager().set(KeyBind.ABILITY_1.datamanager, false);
		this.entity.getDataManager().set(KeyBind.ABILITY_2.datamanager, false);
		this.entity.setSprinting(false);
	}

	protected void attackTarget(EntityLivingBase target, boolean canSee, double distance) {}

	protected boolean shouldUseAbility() {
		return entity.getRNG().nextInt(25) == 0; // XXX customizable
	}

	protected void move(EntityLivingBase target, boolean canSee, double distanceSq) {
		switch (movementType) {
		case STRAFING:
			if (distanceSq <= (double)this.maxAttackDistance && this.seeTime >= 20) {
				this.entity.getNavigator().clearPathEntity();
				++this.strafingTime;
			}
			else {
				this.entity.getNavigator().tryMoveToEntityLiving(target, 1);
				this.strafingTime = -1;
			}

			if (this.strafingTime >= 20) {
				if ((double)this.entity.getRNG().nextFloat() < 0.3D)
					this.strafingClockwise = !this.strafingClockwise;
				if ((double)this.entity.getRNG().nextFloat() < 0.3D)
					this.strafingBackwards = !this.strafingBackwards;
				this.strafingTime = 0;
			}

			if (this.strafingTime > -1) {
				if (distanceSq > (double)(this.maxAttackDistance * this.strafingBackwardsPercent ))
					this.strafingBackwards = false;
				else if (distanceSq < (double)(this.maxAttackDistance * this.strafingForwardsPercent))
					this.strafingBackwards = true;

				this.entity.getMoveHelper().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
			}
			this.entity.getLookHelper().setLookPosition(target.posX, target.posY+target.getEyeHeight(), target.posZ, 360, 360);
			this.entity.rotationYaw = this.entity.rotationYawHead;
			break;
		case MELEE:
			if (distanceSq <= (double)this.maxAttackDistance && this.seeTime >= 20) 
				this.entity.getNavigator().clearPathEntity();
			else
				this.entity.getNavigator().tryMoveToEntityLiving(target, 1);
			this.entity.getLookHelper().setLookPosition(target.posX, target.posY+target.getEyeHeight(), target.posZ, 360, 360);
			this.entity.rotationYaw = this.entity.rotationYawHead;
			break;
		case HEAL:
			if (distanceSq <= (double)this.maxAttackDistance && canSee) 
				this.entity.getNavigator().clearPathEntity();
			else
				this.entity.getNavigator().tryMoveToEntityLiving(target, 1);
			this.entity.getLookHelper().setLookPosition(target.posX, target.posY+target.getEyeHeight(), target.posZ, 360, 360);
			this.entity.rotationYaw = this.entity.rotationYawHead;
			break;
		}
	}

}