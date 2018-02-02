package com.solinia.solinia.Utils;

import org.bukkit.Material;
import org.bukkit.entity.Entity;

import com.solinia.solinia.Managers.StateManager;
import com.solinia.solinia.Models.SoliniaActiveSpell;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.effect.BleedEffect;
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
	
	public static void playWarpEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		Effect effect = new WarpEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
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
		BleedEffect effect = new BleedEffect(StateManager.getInstance().getEffectManager()); 
		effect.setEntity(entity); 
		effect.hurt = false;
		effect.iterations = 1 * 20;
		effect.start();
	}

	public static void playShieldEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		ShieldEffect effect = new ShieldEffect(StateManager.getInstance().getEffectManager()); 
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

	public static void playWaveEffect(Entity entity, SoliniaActiveSpell activeSpell) {
		// TODO Auto-generated method stub
		Effect effect = new WaveEffect(StateManager.getInstance().getEffectManager()); 
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
