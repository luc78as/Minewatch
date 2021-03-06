package twopiradians.minewatch.common.entity;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.minewatch.common.Minewatch;
import twopiradians.minewatch.common.util.EntityHelper;
import twopiradians.minewatch.packet.SPacketSimple;

public abstract class EntityMW extends Entity implements IThrowableEntity {

	public static final DataParameter<Rotations> VELOCITY_CLIENT = EntityDataManager.<Rotations>createKey(EntityMW.class, DataSerializers.ROTATIONS);
	public static final DataParameter<NBTTagCompound> POSITION_CLIENT = EntityDataManager.<NBTTagCompound>createKey(EntityMW.class, DataSerializers.COMPOUND_TAG);
	public static final DataParameter<Integer> HAND = EntityDataManager.<Integer>createKey(EntityMW.class, DataSerializers.VARINT);
	public boolean notDeflectible;
	public int lifetime;
	private EntityLivingBase thrower;
	public boolean isFriendly;
	protected boolean impactOnClient;
	/**if hitscan, use hitscan with deflect*/
	public boolean hitscan;

	public EntityMW(World worldIn) {
		this(worldIn, null, -1);
	}

	/**@param hand -1 no muzzle, 0 main-hand, 1 off-hand, 2 middle*/
	public EntityMW(World worldIn, @Nullable EntityLivingBase throwerIn, int hand) {
		super(worldIn);
		this.isImmuneToFire = true;
		if (throwerIn != null) {
			this.thrower = throwerIn;
			this.setPosition(throwerIn.posX, throwerIn.posY + (double)throwerIn.getEyeHeight() - 0.1D, throwerIn.posZ);
		}
		if (!worldIn.isRemote)
			this.dataManager.set(HAND, hand);
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(VELOCITY_CLIENT, new Rotations(0, 0, 0));
		this.dataManager.register(POSITION_CLIENT, new NBTTagCompound());
		this.dataManager.register(HAND, -1);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		EntityHelper.handleNotifyDataManagerChange(key, this);
	}

	public void spawnTrailParticles() {}

	/**Spawn muzzle particles when first spawning*/
	public void spawnMuzzleParticles(EnumHand hand, EntityLivingBase shooter) {}

	@Override
	public void onUpdate() {
		// muzzle particle
		if (this.firstUpdate && this.world.isRemote && !this.isDead && 
				this.dataManager.get(HAND) != -1 && this.getThrower() instanceof EntityLivingBase) {
			this.spawnMuzzleParticles(this.dataManager.get(HAND) >= 0 && this.dataManager.get(HAND) < EnumHand.values().length ? 
					EnumHand.values()[this.dataManager.get(HAND)] : null, this.getThrower());
		}

		// check for impacts
		if (!world.isRemote || this.impactOnClient) { 
			ArrayList<RayTraceResult> results = EntityHelper.checkForImpact(this, this.isFriendly);
			RayTraceResult nearest = EntityHelper.getNearestImpact(this, results);
			for (RayTraceResult result : results)
				if (result != null && isValidImpact(result, result == nearest))
					this.onImpact(result);
		}

		// set prev's
		this.prevPosX = this.posX; 
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotationYaw = this.rotationYaw;

		// move if still alive and has motion
		if ((!world.isRemote || this.ticksExisted > 1 || !this.hasNoGravity()) && 
				!this.isDead && Math.sqrt(motionX*motionX+motionY*motionY+motionZ*motionZ) > 0) {
			if (this.hasNoGravity())
				this.setPosition(this.posX+this.motionX, this.posY+this.motionY, this.posZ+this.motionZ);
			else // needed to set onGround / do block collisions
				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ); 
		}

		// set dead if needed
		if (((this.ticksExisted > lifetime) || 
				!(this.getThrower() instanceof EntityLivingBase) || 
				posY <= -64) ||
				(this.getThrower() instanceof EntityPlayerMP && ((EntityPlayerMP)this.getThrower()).hasDisconnected())) 
			this.setDead();

		// spawn trail particles
		if (this.world.isRemote)
			this.spawnTrailParticles();

		this.firstUpdate = false;
	}

	/**Should this result trigger onImpact*/
	protected boolean isValidImpact(RayTraceResult result, boolean nearest) {
		return result != null && result.typeOfHit != RayTraceResult.Type.MISS && nearest;
	}

	/**Called on impact - normally used to move to hit position of the RayTraceResult and kill on server*/
	protected void onImpactMoveToHitPosition(RayTraceResult result) {
		if (result != null) 
			if (!world.isRemote)
				EntityHelper.moveToHitPosition(this, result);
	}

	public void onImpact(RayTraceResult result) {
		if (!world.isRemote) { 
			this.onImpactMoveToHitPosition(result);
			Minewatch.network.sendToAllAround(new SPacketSimple(41, this, result), 
					new TargetPoint(world.provider.getDimension(), posX, posY, posZ, 200));
		}
		else {
			this.spawnTrailParticles();
			this.onImpactMoveToHitPosition(result);
		}
	}

	/**Called when deflected by Genji - on both sides*/
	public void onDeflect() {
		this.lifetime *= 2; 
		if (!world.isRemote)
			this.getDataManager().set(HAND, -1);
	}

	/**Used to check for impacts*/
	public AxisAlignedBB getImpactBoundingBox() {
		return this.getEntityBoundingBox();
	}

	@Override
	public float getEyeHeight() {
		return this.height/2f;
	}

	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance){
		return distance < 3000;
	}

	@Override
	public EntityLivingBase getThrower() {
		return this.thrower;
	}

	@Override
	public void setThrower(Entity entity) {
		if (entity instanceof EntityLivingBase) 
			this.thrower = (EntityLivingBase) entity;
	}

	@Override
	public boolean doesEntityNotTriggerPressurePlate() {return true;}
	@Override
	public boolean writeToNBTOptional(NBTTagCompound compound) {return false;}
	@Override
	public boolean writeToNBTAtomically(NBTTagCompound compound) {return false;}
	@Override
	public void readFromNBT(NBTTagCompound compound) {}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {return compound;}
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {}
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {}

}