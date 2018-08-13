package com.solinia.solinia.Repositories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.solinia.solinia.Interfaces.IRepository;
import com.solinia.solinia.Interfaces.ISoliniaSpell;
import com.solinia.solinia.Models.SoliniaSpell;

public class JsonSpellRepository implements IRepository<ISoliniaSpell> {

	private String filePath;
	private ConcurrentHashMap<Integer, ISoliniaSpell> spells = new ConcurrentHashMap<Integer, ISoliniaSpell>();

	@Override
	public void add(ISoliniaSpell item) {
		this.spells.put(item.getId(), item);
	}

	@Override
	public void add(Iterable<ISoliniaSpell> items) {
		for(ISoliniaSpell i : items)
		{
			this.spells.put(i.getId(), i);
		}
	}

	@Override
	public void update(ISoliniaSpell item) {
		this.spells.put(item.getId(), item);
	}

	@Override
	public void remove(ISoliniaSpell item) {
		this.spells.remove(item.getId());
	}

	@Override
	public void remove(Predicate<ISoliniaSpell> filter) {
		for(ISoliniaSpell i : spells.values().stream().filter(filter).collect(Collectors.toList()))
		{
			spells.remove(i.getId());
		}
	}

	@Override
	public List<ISoliniaSpell> query(Predicate<ISoliniaSpell> filter) {
		return spells.values().stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void reload() {
		List<ISoliniaSpell> file = new ArrayList<ISoliniaSpell>();
		
		try {
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			file = gson.fromJson(br, new TypeToken<List<SoliniaSpell>>(){}.getType());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		spells.clear();
		for(ISoliniaSpell i : file)
		{
			spells.put(i.getId(), i);
		}
		
		System.out.println("Reloaded " + spells.size() + " spells");
	}	
	
	@Override
	public void commit() {
		// TODO Auto-generated method stub
		GsonBuilder gsonbuilder = new GsonBuilder();
		gsonbuilder.setPrettyPrinting();
		Gson gson = gsonbuilder.create();
		String jsonOutput = gson.toJson(spells.values(), new TypeToken<List<SoliniaSpell>>(){}.getType());
		try {
			
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();
	        
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outWriter = new OutputStreamWriter(fileOut);
	        outWriter.append(jsonOutput);
	        outWriter.close();
	        fileOut.close();
	        
	        System.out.println("Commited " + spells.size() + " spells");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setJsonFile(String filePath) {
		this.filePath = filePath;		
	}
	
	@Override
	public ISoliniaSpell getByKey(Object key) {
		return this.spells.get(key);
	}

	@Override
	public void writeCsv(String filePath)
	{
		try (
	            BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));
	            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
	            		"allowedClasses",
	            		"id",
	            		"name",
	            		"player_1",
	            		"teleport_zone",
	            		"you_cast",
	            		"other_casts",
	            		"cast_on_you",
	            		"cast_on_other",
	            		"spell_fades",
	            		"range",
	            		"aoerange",
	            		"pushback",
	            		"pushup",
	            		"cast_time",
	            		"recovery_time",
	            		"recast_time",
	            		"buffdurationformula",
	            		"buffduration",
	            		"AEDuration",
	            		"mana",
	            		"effect_base_value1",
	            		"effect_base_value2",
	            		"effect_base_value3",
	            		"effect_base_value4",
	            		"effect_base_value5",
	            		"effect_base_value6",
	            		"effect_base_value7",
	            		"effect_base_value8",
	            		"effect_base_value9",
	            		"effect_base_value10",
	            		"effect_base_value11",
	            		"effect_base_value12",
	            		"effect_limit_value1",
	            		"effect_limit_value2",
	            		"effect_limit_value3",
	            		"effect_limit_value4",
	            		"effect_limit_value5",
	            		"effect_limit_value6",
	            		"effect_limit_value7",
	            		"effect_limit_value8",
	            		"effect_limit_value9",
	            		"effect_limit_value10",
	            		"effect_limit_value11",
	            		"effect_limit_value12",
	            		"max1",
	            		"max2",
	            		"max3",
	            		"max4",
	            		"max5",
	            		"max6",
	            		"max7",
	            		"max8",
	            		"max9",
	            		"max10",
	            		"max11",
	            		"max12",
	            		"icon",
	            		"memicon",
	            		"components1",
	            		"components2",
	            		"components3",
	            		"components4",
	            		"component_counts1",
	            		"component_counts2",
	            		"component_counts3",
	            		"component_counts4",
	            		"NoexpendReagent1",
	            		"NoexpendReagent2",
	            		"NoexpendReagent3",
	            		"NoexpendReagent4",
	            		"formula1",
	            		"formula2",
	            		"formula3",
	            		"formula4",
	            		"formula5",
	            		"formula6",
	            		"formula7",
	            		"formula8",
	            		"formula9",
	            		"formula10",
	            		"formula11",
	            		"formula12",
	            		"LightType",
	            		"goodEffect",
	            		"Activated",
	            		"resisttype",
	            		"effectid1",
	            		"effectid2",
	            		"effectid3",
	            		"effectid4",
	            		"effectid5",
	            		"effectid6",
	            		"effectid7",
	            		"effectid8",
	            		"effectid9",
	            		"effectid10",
	            		"effectid11",
	            		"effectid12",
	            		"targettype",
	            		"basediff",
	            		"skill",
	            		"zonetype",
	            		"EnvironmentType",
	            		"TimeOfDay",
	            		"classes1",
	            		"classes2",
	            		"classes3",
	            		"classes4",
	            		"classes5",
	            		"classes6",
	            		"classes7",
	            		"classes8",
	            		"classes9",
	            		"classes10",
	            		"classes11",
	            		"classes12",
	            		"classes13",
	            		"classes14",
	            		"classes15",
	            		"classes16",
	            		"CastingAnim",
	            		"TargetAnim",
	            		"TravelType",
	            		"SpellAffectIndex",
	            		"disallow_sit",
	            		"deities0",
	            		"deities1",
	            		"deities2",
	            		"deities3",
	            		"deities4",
	            		"deities5",
	            		"deities6",
	            		"deities7",
	            		"deities8",
	            		"deities9",
	            		"deities10",
	            		"deities11",
	            		"deities12",
	            		"deities13",
	            		"deities14",
	            		"deities15",
	            		"deities16",
	            		"field142",
	            		"field143",
	            		"new_icon",
	            		"spellanim",
	            		"uninterruptable",
	            		"ResistDiff",
	            		"dot_stacking_exempt",
	            		"deleteable",
	            		"RecourseLink",
	            		"no_partial_resist",
	            		"field152",
	            		"field153",
	            		"short_buff_box",
	            		"descnum",
	            		"typedescnum",
	            		"effectdescnum",
	            		"effectdescnum2",
	            		"npc_no_los",
	            		"field160",
	            		"reflectable",
	            		"bonushate",
	            		"field163",
	            		"field164",
	            		"ldon_trap",
	            		"EndurCost",
	            		"EndurTimerIndex",
	            		"IsDiscipline",
	            		"field169",
	            		"field170",
	            		"field171",
	            		"field172",
	            		"HateAdded",
	            		"EndurUpkeep",
	            		"numhitstype",
	            		"numhits",
	            		"pvpresistbase",
	            		"pvpresistcalc",
	            		"pvpresistcap",
	            		"spell_category",
	            		"field181",
	            		"field182",
	            		"field183",
	            		"field184",
	            		"can_mgb",
	            		"nodispell",
	            		"npc_category",
	            		"npc_usefulness",
	            		"MinResist",
	            		"MaxResist",
	            		"viral_targets",
	            		"viral_timer",
	            		"nimbuseffect",
	            		"ConeStartAngle",
	            		"ConeStopAngle",
	            		"sneaking",
	            		"not_extendable",
	            		"field198",
	            		"field199",
	            		"suspendable",
	            		"viral_range",
	            		"songcap",
	            		"field203",
	            		"field204",
	            		"no_block",
	            		"field206",
	            		"spellgroup",
	            		"rank",
	            		"field209",
	            		"field210",
	            		"CastRestriction",
	            		"allowrest",
	            		"InCombat",
	            		"OutofCombat",
	            		"field215",
	            		"field216",
	            		"field217",
	            		"aemaxtargets",
	            		"maxtargets",
	            		"field220",
	            		"field221",
	            		"field222",
	            		"field223",
	            		"persistdeath",
	            		"field225",
	            		"field226",
	            		"min_dist",
	            		"min_dist_mod",
	            		"max_dist",
	            		"max_dist_mod",
	            		"min_range",
	            		"field232",
	            		"field233",
	            		"field234",
	            		"field235",
	            		"field236"
	            	));
	        ) 
		{
			for(Entry<Integer, ISoliniaSpell> keyValuePair : spells.entrySet())
			{
				ISoliniaSpell spell =  keyValuePair.getValue();
				
	            csvPrinter.printRecord(
	            		Arrays.asList(spell.getAllowedClasses().stream()
		  					      .map(t -> t.getClassname() + ":" + t.getMinlevel())
		  					      .collect(Collectors.joining(","))),
	            		spell.getId(),
	            		spell.getName(),
	            		spell.getPlayer1(),
	            		spell.getTeleportZone(),
	            		spell.getYouCast(),
	            		spell.getOtherCasts(),
	            		spell.getCastOnYou(),
	            		spell.getCastOnOther(),
	            		spell.getSpellFades(),
	            		spell.getRange(),
	            		spell.getAoerange(),
	            		spell.getPushback(),
	            		spell.getPushup(),
	            		spell.getCastTime(),
	            		spell.getRecoveryTime(),
	            		spell.getRecastTime(),
	            		spell.getBuffdurationformula(),
	            		spell.getBuffduration(),
	            		spell.getAEDuration(),
	            		spell.getMana(),
	            		spell.getEffectBaseValue1(),
	            		spell.getEffectBaseValue2(),
	            		spell.getEffectBaseValue3(),
	            		spell.getEffectBaseValue4(),
	            		spell.getEffectBaseValue5(),
	            		spell.getEffectBaseValue6(),
	            		spell.getEffectBaseValue7(),
	            		spell.getEffectBaseValue8(),
	            		spell.getEffectBaseValue9(),
	            		spell.getEffectBaseValue10(),
	            		spell.getEffectBaseValue11(),
	            		spell.getEffectBaseValue12(),
	            		spell.getEffectLimitValue1(),
	            		spell.getEffectLimitValue2(),
	            		spell.getEffectLimitValue3(),
	            		spell.getEffectLimitValue4(),
	            		spell.getEffectLimitValue5(),
	            		spell.getEffectLimitValue6(),
	            		spell.getEffectLimitValue7(),
	            		spell.getEffectLimitValue8(),
	            		spell.getEffectLimitValue9(),
	            		spell.getEffectLimitValue10(),
	            		spell.getEffectLimitValue11(),
	            		spell.getEffectLimitValue12(),
	            		spell.getMax1(),
	            		spell.getMax2(),
	            		spell.getMax3(),
	            		spell.getMax4(),
	            		spell.getMax5(),
	            		spell.getMax6(),
	            		spell.getMax7(),
	            		spell.getMax8(),
	            		spell.getMax9(),
	            		spell.getMax10(),
	            		spell.getMax11(),
	            		spell.getMax12(),
	            		spell.getIcon(),
	            		spell.getMemicon(),
	            		spell.getComponents1(),
	            		spell.getComponents2(),
	            		spell.getComponents3(),
	            		spell.getComponents4(),
	            		spell.getComponentCounts1(),
	            		spell.getComponentCounts2(),
	            		spell.getComponentCounts3(),
	            		spell.getComponentCounts4(),
	            		spell.getNoexpendReagent1(),
	            		spell.getNoexpendReagent2(),
	            		spell.getNoexpendReagent3(),
	            		spell.getNoexpendReagent4(),
	            		spell.getFormula1(),
	            		spell.getFormula2(),
	            		spell.getFormula3(),
	            		spell.getFormula4(),
	            		spell.getFormula5(),
	            		spell.getFormula6(),
	            		spell.getFormula7(),
	            		spell.getFormula8(),
	            		spell.getFormula9(),
	            		spell.getFormula10(),
	            		spell.getFormula11(),
	            		spell.getFormula12(),
	            		spell.getLightType(),
	            		spell.getGoodEffect(),
	            		spell.getActivated(),
	            		spell.getResisttype(),
	            		spell.getEffectid1(),
	            		spell.getEffectid2(),
	            		spell.getEffectid3(),
	            		spell.getEffectid4(),
	            		spell.getEffectid5(),
	            		spell.getEffectid6(),
	            		spell.getEffectid7(),
	            		spell.getEffectid8(),
	            		spell.getEffectid9(),
	            		spell.getEffectid10(),
	            		spell.getEffectid11(),
	            		spell.getEffectid12(),
	            		spell.getTargettype(),
	            		spell.getBasediff(),
	            		spell.getSkill(),
	            		spell.getZonetype(),
	            		spell.getEnvironmentType(),
	            		spell.getTimeOfDay(),
	            		spell.getClasses1(),
	            		spell.getClasses2(),
	            		spell.getClasses3(),
	            		spell.getClasses4(),
	            		spell.getClasses5(),
	            		spell.getClasses6(),
	            		spell.getClasses7(),
	            		spell.getClasses8(),
	            		spell.getClasses9(),
	            		spell.getClasses10(),
	            		spell.getClasses11(),
	            		spell.getClasses12(),
	            		spell.getClasses13(),
	            		spell.getClasses14(),
	            		spell.getClasses15(),
	            		spell.getClasses16(),
	            		spell.getCastingAnim(),
	            		spell.getTargetAnim(),
	            		spell.getTravelType(),
	            		spell.getSpellAffectIndex(),
	            		spell.getDisallowSit(),
	            		spell.getDeities0(),
	            		spell.getDeities1(),
	            		spell.getDeities2(),
	            		spell.getDeities3(),
	            		spell.getDeities4(),
	            		spell.getDeities5(),
	            		spell.getDeities6(),
	            		spell.getDeities7(),
	            		spell.getDeities8(),
	            		spell.getDeities9(),
	            		spell.getDeities10(),
	            		spell.getDeities11(),
	            		spell.getDeities12(),
	            		spell.getDeities13(),
	            		spell.getDeities14(),
	            		spell.getDeities15(),
	            		spell.getDeities16(),
	            		spell.getField142(),
	            		spell.getField143(),
	            		spell.getNewIcon(),
	            		spell.getSpellanim(),
	            		spell.getUninterruptable(),
	            		spell.getResistDiff(),
	            		spell.getDotStackingExempt(),
	            		spell.getDeleteable(),
	            		spell.getRecourseLink(),
	            		spell.getNoPartialResist(),
	            		spell.getField152(),
	            		spell.getField153(),
	            		spell.getShortBuffBox(),
	            		spell.getDescnum(),
	            		spell.getTypedescnum(),
	            		spell.getEffectdescnum(),
	            		spell.getEffectdescnum2(),
	            		spell.getNpcNoLos(),
	            		spell.getField160(),
	            		spell.getReflectable(),
	            		spell.getBonushate(),
	            		spell.getField163(),
	            		spell.getField164(),
	            		spell.getLdonTrap(),
	            		spell.getEndurCost(),
	            		spell.getEndurTimerIndex(),
	            		spell.getIsDiscipline(),
	            		spell.getField169(),
	            		spell.getField170(),
	            		spell.getField171(),
	            		spell.getField172(),
	            		spell.getHateAdded(),
	            		spell.getEndurUpkeep(),
	            		spell.getNumhitstype(),
	            		spell.getNumhits(),
	            		spell.getPvpresistbase(),
	            		spell.getPvpresistcalc(),
	            		spell.getPvpresistcap(),
	            		spell.getSpellCategory(),
	            		spell.getField181(),
	            		spell.getField182(),
	            		spell.getField183(),
	            		spell.getField184(),
	            		spell.getCanMgb(),
	            		spell.getNodispell(),
	            		spell.getNpcCategory(),
	            		spell.getNpcUsefulness(),
	            		spell.getMinResist(),
	            		spell.getMaxResist(),
	            		spell.getViralTargets(),
	            		spell.getViralTimer(),
	            		spell.getNimbuseffect(),
	            		spell.getConeStartAngle(),
	            		spell.getConeStopAngle(),
	            		spell.getSneaking(),
	            		spell.getNotExtendable(),
	            		spell.getField198(),
	            		spell.getField199(),
	            		spell.getSuspendable(),
	            		spell.getViralRange(),
	            		spell.getSongcap(),
	            		spell.getField203(),
	            		spell.getField204(),
	            		spell.getNoBlock(),
	            		spell.getField206(),
	            		spell.getSpellgroup(),
	            		spell.getRank(),
	            		spell.getField209(),
	            		spell.getField210(),
	            		spell.getCastRestriction(),
	            		spell.getAllowrest(),
	            		spell.getInCombat(),
	            		spell.getOutofCombat(),
	            		spell.getField215(),
	            		spell.getField216(),
	            		spell.getField217(),
	            		spell.getAemaxtargets(),
	            		spell.getMaxtargets(),
	            		spell.getField220(),
	            		spell.getField221(),
	            		spell.getField222(),
	            		spell.getField223(),
	            		spell.getPersistdeath(),
	            		spell.getField225(),
	            		spell.getField226(),
	            		spell.getMinDist(),
	            		spell.getMinDistMod(),
	            		spell.getMaxDist(),
	            		spell.getMaxDistMod(),
	            		spell.getMinRange(),
	            		spell.getField232(),
	            		spell.getField233(),
	            		spell.getField234(),
	            		spell.getField235(),
	            		spell.getField236()
	            		);
			}
			
			csvPrinter.flush();            
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
