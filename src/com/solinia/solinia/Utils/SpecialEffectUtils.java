package com.solinia.solinia.Utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaActiveSpell;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.effect.AnimatedBallEffect;
import de.slikey.effectlib.effect.BleedEffect;
import de.slikey.effectlib.effect.CircleEffect;
import de.slikey.effectlib.effect.FlameEffect;
import de.slikey.effectlib.effect.LoveEffect;
import de.slikey.effectlib.effect.MusicEffect;
import de.slikey.effectlib.effect.ShieldEffect;
import de.slikey.effectlib.effect.SmokeEffect;
import de.slikey.effectlib.effect.StarEffect;
import de.slikey.effectlib.effect.WarpEffect;
import de.slikey.effectlib.effect.WaveEffect;
import de.slikey.effectlib.util.ParticleEffect;

public class SpecialEffectUtils {

	public static void playLegacy(Entity entity, SoliniaActiveSpell activeSpell) {
		Effect effect = new BleedEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.iterations = 1 * 20;
		effect.start();
	}

	public static void playMusicEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		Effect effect = new MusicEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playPortalEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		Effect effect = new CircleEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playColdEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		// TODO Auto-generated method stub
		FlameEffect effect = new FlameEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.color = Color.AQUA;
		effect.iterations = 1 * 20;
		effect.start();
	}

	public static void playFlameEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		Effect effect = new FlameEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.iterations = 1 * 20;
		effect.start();
	}

	public static void playBleedEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		AnimatedBallEffect effect = new AnimatedBallEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.particle = ParticleEffect.DAMAGE_INDICATOR;
		effect.iterations = 1 * 20;
		effect.start();
	}

	public static void playPoisonEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		AnimatedBallEffect effect = new AnimatedBallEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.particle = ParticleEffect.SLIME;
		effect.iterations = 1 * 20;
		effect.start();
	}
	
	public static void playShieldEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		WarpEffect effect = new WarpEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity);
		effect.particle = ParticleEffect.ENCHANTMENT_TABLE; 
		effect.iterations = 1 * 20;
		effect.start();
	}

	public static void playSmokeEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		Effect effect = new SmokeEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.iterations = 1 * 20;
		effect.start();
	}

	public static void playStarEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		Effect effect = new StarEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.iterations = 1 * 20;
		effect.start();
	}

	public static void playLoveEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		Effect effect = new LoveEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.iterations = 1 * 20;
		effect.start();
	}

	
	
}
