package twopiradians.minewatch.common.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import twopiradians.minewatch.common.Minewatch;
import twopiradians.minewatch.common.hero.EnumHero;

public class ModSoundEvents {

	//PORT JUST CHANGE EVERYTHING
	
	public static SoundEvent[] multikill = new SoundEvent[5];
	public static SoundEvent kill;
	public static SoundEvent headshot;
	public static SoundEvent hurt;
	public static SoundEvent abilityRecharge;
	public static SoundEvent abilityMultiRecharge;
	public static SoundEvent abilityNotReady;
	public static SoundEvent wallClimb;
	public static SoundEvent anaShoot;
	public static SoundEvent anaHeal;
	public static SoundEvent anaSleepShoot;
	public static SoundEvent anaSleepHit;
	public static SoundEvent anaSleepVoice;
	public static SoundEvent reaperShoot;
	public static SoundEvent reaperTeleportStart;
	public static SoundEvent reaperTeleportDuring;
	public static SoundEvent reaperTeleportStop;
	public static SoundEvent reaperTeleportFinal;
	public static SoundEvent reaperTeleportVoice;
	public static SoundEvent reaperWraith;
	public static SoundEvent hanzoShoot;
	public static SoundEvent hanzoDraw;
	public static SoundEvent hanzoSonicArrow;
	public static SoundEvent hanzoScatterArrow;
	public static SoundEvent reinhardtWeapon;
	public static SoundEvent genjiShoot;
	public static SoundEvent genjiDeflect;
	public static SoundEvent genjiDeflectHit;
	public static SoundEvent genjiStrike;
	public static SoundEvent genjiJump;
	public static SoundEvent tracerShoot;
	public static SoundEvent tracerBlink;
	public static SoundEvent mccreeShoot;
	public static SoundEvent mccreeFlashbang;
	public static SoundEvent mccreeRoll;
	public static SoundEvent soldier76Shoot;
	public static SoundEvent soldier76Helix;
	public static SoundEvent bastionShoot;
	public static SoundEvent bastionTurretReload;
	public static SoundEvent meiShoot;
	public static SoundEvent meiIcicleShoot;
	public static SoundEvent meiFreeze;
	public static SoundEvent meiUnfreeze;
	public static SoundEvent widowmakerScopedShoot;
	public static SoundEvent widowmakerUnscopedShoot;
	public static SoundEvent widowmakerCharge;
	public static SoundEvent mercyShoot;
	public static SoundEvent mercyHeal;
	public static SoundEvent mercyDamage;
	public static SoundEvent mercyHover;
	public static SoundEvent mercyBeamStart;
	public static SoundEvent mercyBeamDuring;
	public static SoundEvent mercyBeamStop;

	public static void preInit() {
		for (int i=2; i<7; ++i)
			multikill[i-2] = registerSound("multikill_"+i);
		kill = registerSound("kill");
		headshot = registerSound("headshot");
		hurt = registerSound("hurt");
		abilityRecharge = registerSound("ability_recharge");
		abilityMultiRecharge = registerSound("ability_multi_recharge");
		abilityNotReady = registerSound("ability_not_ready");
		wallClimb = registerSound("wall_climb");
		anaShoot = registerSound("ana_shoot");
		anaHeal = registerSound("ana_heal");
		anaSleepShoot = registerSound("ana_sleep_shoot");
		anaSleepHit = registerSound("ana_sleep_hit");
		anaSleepVoice = registerSound("ana_sleep_voice");
		EnumHero.ANA.reloadSound = registerSound("ana_reload");
		reaperShoot = registerSound("reaper_shoot");
		reaperTeleportStart = registerSound("reaper_teleport_start");
		reaperTeleportDuring = registerSound("reaper_teleport_during");
		reaperTeleportStop = registerSound("reaper_teleport_stop");
		reaperTeleportFinal = registerSound("reaper_teleport_final");
		reaperTeleportVoice = registerSound("reaper_teleport_voice");
		reaperWraith = registerSound("reaper_wraith");
		EnumHero.REAPER.reloadSound = registerSound("reaper_reload");
		hanzoShoot = registerSound("hanzo_shoot");
		hanzoDraw = registerSound("hanzo_draw");
		hanzoSonicArrow = registerSound("hanzo_sonic_arrow");
		hanzoScatterArrow = registerSound("hanzo_scatter_arrow");
		reinhardtWeapon = registerSound("reinhardt_weapon");
		genjiShoot = registerSound("genji_shoot");
		genjiDeflect = registerSound("genji_deflect");
		genjiDeflectHit = registerSound("genji_deflect_hit");
		genjiStrike = registerSound("genji_strike");
		genjiJump = registerSound("genji_jump");
		EnumHero.GENJI.reloadSound = registerSound("genji_reload");
		tracerShoot = registerSound("tracer_shoot");
		tracerBlink = registerSound("tracer_blink");
		EnumHero.TRACER.reloadSound = registerSound("tracer_reload");
		mccreeShoot = registerSound("mccree_shoot");
		mccreeFlashbang = registerSound("mccree_flashbang");
		mccreeRoll = registerSound("mccree_roll");
		EnumHero.MCCREE.reloadSound = registerSound("mccree_reload");
		soldier76Shoot = registerSound("soldier76_shoot");
		soldier76Helix = registerSound("soldier76_helix");
		EnumHero.SOLDIER76.reloadSound = registerSound("soldier76_reload");
		bastionShoot = registerSound("bastion_shoot");
		EnumHero.BASTION.reloadSound = registerSound("bastion_reload_0");
		bastionTurretReload = registerSound("bastion_reload_1");
		meiShoot = registerSound("mei_shoot_0");
		EnumHero.MEI.reloadSound = registerSound("mei_reload");
		meiIcicleShoot = registerSound("mei_shoot_1");
		meiFreeze = registerSound("mei_freeze");
		meiUnfreeze = registerSound("mei_unfreeze");
		widowmakerUnscopedShoot = registerSound("widowmaker_shoot_0");
		widowmakerScopedShoot = registerSound("widowmaker_shoot_1");
		widowmakerCharge = registerSound("widowmaker_charge");
		EnumHero.WIDOWMAKER.reloadSound = registerSound("widowmaker_reload");
		mercyShoot = registerSound("mercy_shoot");
		EnumHero.MERCY.reloadSound = registerSound("mercy_reload");
		mercyHeal = registerSound("mercy_heal");
		mercyDamage = registerSound("mercy_damage");
		mercyHover = registerSound("mercy_hover");
		mercyBeamStart = registerSound("mercy_beam_start");
		mercyBeamDuring = registerSound("mercy_beam_during");
		mercyBeamStop = registerSound("mercy_beam_stop");
	}
	
	private static SoundEvent registerSound(String soundName) {
		ResourceLocation loc = new ResourceLocation(Minewatch.MODID, soundName);
		SoundEvent sound = new SoundEvent(loc);
		GameRegistry.register(sound, loc);
		return sound;
	}
}