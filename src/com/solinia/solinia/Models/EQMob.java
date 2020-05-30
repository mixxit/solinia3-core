package com.solinia.solinia.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EQMob {
 private float id;
 private String name;
@SerializedName("class")
@Expose
 private float classes;
 private float level;
 private float hp;
 private float hp_regen_rate;
 private float mana;
 private float mana_regen_rate;
 private float npc_spells_id;
 private float mindmg;
 private float maxdmg;
 private float MR;
 private float CR;
 private float DR;
 private float FR;
 private float PR;
 private float Corrup;
 private float PhR;
 private float see_invis;
 private float AC;
 private float attack_speed;
 private float attack_delay;
 private float STR;
 private float STA;
 private float DEX;
 private float AGI;
 private float _INT;
 private float WIS;
 private float CHA;
 private float ATK;
 private float Accuracy;
 private float Avoidance;
 private String npcspecialattks;
 private String special_abilities;


 // Getter Methods 

 public float getId() {
  return id;
 }

 public String getName() {
  return name;
 }

 public float getClasses() {
  return classes;
 }

 public float getLevel() {
  return level;
 }

 public float getHp() {
  return hp;
 }

 public float getHp_regen_rate() {
  return hp_regen_rate;
 }

 public float getMana() {
  return mana;
 }

 public float getMana_regen_rate() {
  return mana_regen_rate;
 }

 public float getNpc_spells_id() {
  return npc_spells_id;
 }

 public float getMindmg() {
  return mindmg;
 }

 public float getMaxdmg() {
  return maxdmg;
 }

 public float getMR() {
  return MR;
 }

 public float getCR() {
  return CR;
 }

 public float getDR() {
  return DR;
 }

 public float getFR() {
  return FR;
 }

 public float getPR() {
  return PR;
 }

 public float getCorrup() {
  return Corrup;
 }

 public float getPhR() {
  return PhR;
 }

 public float getSee_invis() {
  return see_invis;
 }

 public float getAC() {
  return AC;
 }

 public float getAttack_speed() {
  return attack_speed;
 }

 public float getAttack_delay() {
  return attack_delay;
 }

 public float getSTR() {
  return STR;
 }

 public float getSTA() {
  return STA;
 }

 public float getDEX() {
  return DEX;
 }

 public float getAGI() {
  return AGI;
 }

 public float get_INT() {
  return _INT;
 }

 public float getWIS() {
  return WIS;
 }

 public float getCHA() {
  return CHA;
 }

 public float getATK() {
  return ATK;
 }

 public float getAccuracy() {
  return Accuracy;
 }

 public float getAvoidance() {
  return Avoidance;
 }

 public String getNpcspecialattks() {
  return npcspecialattks;
 }

 public String getSpecial_abilities() {
  return special_abilities;
 }

 // Setter Methods 

 public void setId(float id) {
  this.id = id;
 }

 public void setName(String name) {
  this.name = name;
 }

 public void setClasses(float classes) {
  this.classes = classes;
 }

 public void setLevel(float level) {
  this.level = level;
 }

 public void setHp(float hp) {
  this.hp = hp;
 }

 public void setHp_regen_rate(float hp_regen_rate) {
  this.hp_regen_rate = hp_regen_rate;
 }

 public void setMana(float mana) {
  this.mana = mana;
 }

 public void setMana_regen_rate(float mana_regen_rate) {
  this.mana_regen_rate = mana_regen_rate;
 }

 public void setNpc_spells_id(float npc_spells_id) {
  this.npc_spells_id = npc_spells_id;
 }

 public void setMindmg(float mindmg) {
  this.mindmg = mindmg;
 }

 public void setMaxdmg(float maxdmg) {
  this.maxdmg = maxdmg;
 }

 public void setMR(float MR) {
  this.MR = MR;
 }

 public void setCR(float CR) {
  this.CR = CR;
 }

 public void setDR(float DR) {
  this.DR = DR;
 }

 public void setFR(float FR) {
  this.FR = FR;
 }

 public void setPR(float PR) {
  this.PR = PR;
 }

 public void setCorrup(float Corrup) {
  this.Corrup = Corrup;
 }

 public void setPhR(float PhR) {
  this.PhR = PhR;
 }

 public void setSee_invis(float see_invis) {
  this.see_invis = see_invis;
 }

 public void setAC(float AC) {
  this.AC = AC;
 }

 public void setAttack_speed(float attack_speed) {
  this.attack_speed = attack_speed;
 }

 public void setAttack_delay(float attack_delay) {
  this.attack_delay = attack_delay;
 }

 public void setSTR(float STR) {
  this.STR = STR;
 }

 public void setSTA(float STA) {
  this.STA = STA;
 }

 public void setDEX(float DEX) {
  this.DEX = DEX;
 }

 public void setAGI(float AGI) {
  this.AGI = AGI;
 }

 public void set_INT(float _INT) {
  this._INT = _INT;
 }

 public void setWIS(float WIS) {
  this.WIS = WIS;
 }

 public void setCHA(float CHA) {
  this.CHA = CHA;
 }

 public void setATK(float ATK) {
  this.ATK = ATK;
 }

 public void setAccuracy(float Accuracy) {
  this.Accuracy = Accuracy;
 }

 public void setAvoidance(float Avoidance) {
  this.Avoidance = Avoidance;
 }

 public void setNpcspecialattks(String npcspecialattks) {
  this.npcspecialattks = npcspecialattks;
 }

 public void setSpecial_abilities(String special_abilities) {
  this.special_abilities = special_abilities;
 }
}