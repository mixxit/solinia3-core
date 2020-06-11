package com.solinia.solinia.Utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLightningStrike;
import org.bukkit.entity.Entity;

import com.solinia.solinia.Managers.StateManager;
import de.slikey.effectlib.Effect;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.effect.ArcEffect;
import de.slikey.effectlib.effect.CircleEffect;
import de.slikey.effectlib.effect.ConeEffect;
import de.slikey.effectlib.effect.DiscoBallEffect;
import de.slikey.effectlib.effect.DragonEffect;
import de.slikey.effectlib.effect.EquationEffect;
import de.slikey.effectlib.effect.FlameEffect;
import de.slikey.effectlib.effect.FountainEffect;
import de.slikey.effectlib.effect.HelixEffect;
import de.slikey.effectlib.effect.LoveEffect;
import de.slikey.effectlib.effect.MusicEffect;
import de.slikey.effectlib.effect.SmokeEffect;
import de.slikey.effectlib.effect.StarEffect;
import de.slikey.effectlib.effect.TornadoEffect;
import de.slikey.effectlib.effect.VortexEffect;
import de.slikey.effectlib.effect.WarpEffect;
import de.slikey.effectlib.effect.WaveEffect;
import net.minecraft.server.v1_15_R1.EntityLightning;

public class SpecialEffectUtils {

	public static void playLegacy(Entity entity) {
		playCustomBallEffect(entity,Particle.FIREWORKS_SPARK, null);
	}

	public static void playMusicEffect(Entity entity) {
		playCustomMusicEffect(entity,null);
	}
	
	public static void playPortalEffect(Entity entity) {
		playCustomCircleEffect(entity,null,Color.MAROON);
	}
	
	public static void playColdEffect(Entity entity) {
		playCustomFlameEffect(entity,null,Color.AQUA);
	}

	public static void playFlameEffect(Entity entity) {
		playCustomFlameEffect(entity,null,null);
	}

	public static void playBleedEffect(Entity entity) {
		playCustomBallEffect(entity,Particle.DAMAGE_INDICATOR,null);
	}
	public static void playPoisonEffect(Entity entity) {
		playCustomBallEffect(entity,Particle.SLIME,null);
	}
	
	public static void playShieldEffect(Entity entity) {
		playCustomShieldEffect(entity,Particle.SPELL_MOB,Color.TEAL);
	}

	public static void playSmokeEffect(Entity entity) {
		playCustomSmokeEffect(entity,null);
	}

	public static void playStunEffect(Entity entity) {
		playCustomBallEffect(entity,Particle.SPELL_MOB,Color.SILVER);
	}
	
	public static void playLoveEffect(Entity entity) {
		playCustomLoveEffect(entity,null);
	}
	
	public static void playCustomCircleEffect(Entity entity, Particle particle, Color color) {
		CircleEffect effect = new CircleEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.iterations = 1 * 20;
		effect.radius = 1.2f;
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.start();
	}
	
	public static void playCustomArcEffect(Entity entity, Particle particle, Color color) {
		ArcEffect effect = new ArcEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomTornadoEffect(Entity entity, Color color) {
		TornadoEffect effect = new TornadoEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomEquationEffect(Entity entity, Particle particle, Color color) {
		EquationEffect effect = new EquationEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomDragonEffect(Entity entity, Particle particle, Color color) {
		DragonEffect effect = new DragonEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomDiscoBallEffect(Entity entity, Color color) {
		DiscoBallEffect effect = new DiscoBallEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomConeEffect(Entity entity, Particle particle, Color color) {
		ConeEffect effect = new ConeEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomVortexEffect(Entity entity, Particle particle, Color color) {
		VortexEffect effect = new VortexEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomWaveEffect(Entity entity, Particle particle, Color color) {
		WaveEffect effect = new WaveEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomFountainEffect(Entity entity, Particle particle, Color color) {
		FountainEffect effect = new FountainEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomHelixEffect(Entity entity, Particle particle, Color color) {
		HelixEffect effect = new HelixEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomStarEffect(Entity entity, Particle particle, Color color) {
		StarEffect effect = new StarEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomBallEffect(Entity entity, Particle particle, Color color) {
		AnimatedBallEffect effect = new AnimatedBallEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomLoveEffect(Entity entity, Color color) {
		Effect effect = new LoveEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomShieldEffect(Entity entity, Particle particle, Color color) {
		WarpEffect effect = new WarpEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity);
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}

	public static void playCustomSmokeEffect(Entity entity, Color color) {
		Effect effect = new SmokeEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomFlameEffect(Entity entity, Particle particle, Color color) {
		// TODO Auto-generated method stub
		FlameEffect effect = new FlameEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomMusicEffect(Entity entity, Color color) {
		Effect effect = new MusicEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.iterations = 1 * 20;
		if (color != null)
			effect.color = color;
		effect.start();
	}

	public static void playLightningStrike(Entity entity) {
		try
		{
			Location loc = entity.getLocation();
			net.minecraft.server.v1_15_R1.WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
			EntityLightning lightning = new EntityLightning(world, loc.getX(), loc.getY(), loc.getZ(), true, true);
			world.strikeLightning(lightning);
		    new CraftLightningStrike(world.getServer(), lightning);
			return;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
