package com.solinia.solinia.Utils;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;

import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaActiveSpell;

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

public class SpecialEffectUtils {

	public static void playLegacy(Entity entity, SoliniaActiveSpell activeSpell) {
		playCustomBallEffect(entity,activeSpell,Particle.FIREWORKS_SPARK, null);
	}

	public static void playMusicEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		playCustomMusicEffect(entity,activeSpell,null);
	}
	
	public static void playPortalEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		playCustomCircleEffect(entity,activeSpell,null,Color.MAROON);
	}
	
	public static void playColdEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		playCustomFlameEffect(entity,activeSpell,null,Color.AQUA);
	}

	public static void playFlameEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		playCustomFlameEffect(entity,activeSpell,null,null);
	}

	public static void playBleedEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		playCustomBallEffect(entity,activeSpell,Particle.DAMAGE_INDICATOR,null);
	}
	public static void playPoisonEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		playCustomBallEffect(entity,activeSpell,Particle.SLIME,null);
	}
	
	public static void playShieldEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		playCustomShieldEffect(entity,activeSpell,Particle.SPELL_MOB,Color.TEAL);
	}

	public static void playSmokeEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		playCustomSmokeEffect(entity,activeSpell,null);
	}

	public static void playStunEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		playCustomBallEffect(entity,activeSpell,Particle.SPELL_MOB,Color.SILVER);
	}
	
	public static void playLoveEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		playCustomLoveEffect(entity,activeSpell,null);
	}
	
	public static void playCustomCircleEffect(Entity entity, SoliniaActiveSpell activeSpell, Particle particle, Color color) {
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
	
	public static void playCustomArcEffect(Entity entity, SoliniaActiveSpell activeSpell, Particle particle, Color color) {
		ArcEffect effect = new ArcEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomTornadoEffect(Entity entity, SoliniaActiveSpell activeSpell, Color color) {
		TornadoEffect effect = new TornadoEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomEquationEffect(Entity entity, SoliniaActiveSpell activeSpell, Particle particle, Color color) {
		EquationEffect effect = new EquationEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomDragonEffect(Entity entity, SoliniaActiveSpell activeSpell, Particle particle, Color color) {
		DragonEffect effect = new DragonEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomDiscoBallEffect(Entity entity, SoliniaActiveSpell activeSpell, Color color) {
		DiscoBallEffect effect = new DiscoBallEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomConeEffect(Entity entity, SoliniaActiveSpell activeSpell, Particle particle, Color color) {
		ConeEffect effect = new ConeEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomVortexEffect(Entity entity, SoliniaActiveSpell activeSpell, Particle particle, Color color) {
		VortexEffect effect = new VortexEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomWaveEffect(Entity entity, SoliniaActiveSpell activeSpell, Particle particle, Color color) {
		WaveEffect effect = new WaveEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomFountainEffect(Entity entity, SoliniaActiveSpell activeSpell, Particle particle, Color color) {
		FountainEffect effect = new FountainEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomHelixEffect(Entity entity, SoliniaActiveSpell activeSpell, Particle particle, Color color) {
		HelixEffect effect = new HelixEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomStarEffect(Entity entity, SoliniaActiveSpell activeSpell, Particle particle, Color color) {
		StarEffect effect = new StarEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomBallEffect(Entity entity, SoliniaActiveSpell activeSpell, Particle particle, Color color) {
		AnimatedBallEffect effect = new AnimatedBallEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomLoveEffect(Entity entity, SoliniaActiveSpell activeSpell, Color color) {
		Effect effect = new LoveEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomShieldEffect(Entity entity, SoliniaActiveSpell activeSpell, Particle particle, Color color) {
		WarpEffect effect = new WarpEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity);
		if (particle != null)
			effect.particle = particle;
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}

	public static void playCustomSmokeEffect(Entity entity, SoliniaActiveSpell activeSpell, Color color) {
		Effect effect = new SmokeEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		if (color != null)
			effect.color = color;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playCustomFlameEffect(Entity entity, SoliniaActiveSpell activeSpell, Particle particle, Color color) {
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
	
	public static void playCustomMusicEffect(Entity entity, SoliniaActiveSpell activeSpell, Color color) {
		Effect effect = new MusicEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.iterations = 1 * 20;
		if (color != null)
			effect.color = color;
		effect.start();
	}
}
