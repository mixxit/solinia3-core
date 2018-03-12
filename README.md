
# solinia3-core
Core RPG Plugin

# Commands

**/createitem**

Permission: solinia.createitem

Turns the item you are holding into a solinia item and returns the new Item ID

Example: 

    /createitem

**/listitems <search term>**

Permission solinia.listitems

Returns a list of item ids and names of items that match the search term by name

Example: 

    /listitems orc

**/edititem <itemid>**

permission: solinia.edititem

Returns all current settings on an item id

Example:

    /edititem 55

**/edititem <itemid> <setting> <value>**

permission: solinia.edititem

Changes the configuration setting on the item id

Example: 

    /edititem 55 displayname Sword_of_a_Thousands_Truths

Additional:

"displayname":

the name of the item (string, less than 26 characters, no spaces)

"worth":

the price for vendors (number)

"allowedclassnames":

a comma seperated list of classnames ie: WARRIOR,CLERIC,DRUID (comma seperated string, no spaces)

"color":

the integer byte id for the color (number)

"damage":

the damage of the weapon (number)

"baneundead":

the damage of the weapon against npcs marked as undead (number)

"hpregen":

the amount of regen per tick (number)

"mpregen":

the amount of mana regen per tick (number)

"strength":

the statistic amount granted (number)

"stamina":

the statistic amount granted (only valid for armour) (number)

"agility":

the statistic amount granted (number)

"dexterity":

the statistic amount granted (number)

"intelligence":

the statistic amount granted (number)

"wisdom":

the statistic amount granted (number)

"basename":

the core minecraft item type (minecraft material name)

"charisma":

the statistic amount granted (number)

"magicresist":

the amount of resist value (number)

"coldresist":

the amount of resist value (number)

"fireresist":

the amount of resist value (number)

"diseaseresist":

the amount of resist value (number)

"poisonresist":

the amount of resist value (number)

"temporary":

if this item will disappear after the world restarts (true/false)

"abilityid":

the spell that is used when right clicking this item (spell id number)

"weaponabilityid":

the spell that is used during combat procs (spell id number)

"petcontrolrod":

if this item acts as a pet control rod (true/false)

"consumable":

if this item is a consumable (dissapears on abilityid use) (true/false)

"crafting":

if this item is part of the crafting engine (true/false)

"quest":

if this item is used in a quest (true/false)

"augmentation":

if this item is an augmentation that can be attached to items (true/false)

"clearallowedclasses":

clears all classes (true)

"cleardiscoverer":

clears the person who discovered this item (true)

"minlevel":

minimum level to use this item (number)

"augmentationfitsslottype":

the slot type that can fit this item (NONE, WEAPON, HELMET, CHESTPLATE, LEGGINGS, BOOTS, SHIELD)

"ac":

the armour class granted by wearing this item (number)

"hp":

the bonus hp granted by wearing this item (armour only) (number)

"mana":

the bonus mana granted by wearing this item (armour only) (number)

"experiencebonus":

the experience bonus granted by consuming this item (number)

"skillmodtype":

the skill the modification will affect (Crushing,OneHandBlunt, Slashing, OneHandSlashing TwoHandBlunt, TwoHandSlashing, Abjuration, Alteration, ApplyPoison, Archery, Backstab, BindWound, Bash, Block, BrassInstruments, Channeling, Conjuration, Defense,
Disarm, DisarmTraps, Divination, Dodge, DoubleAttack,DragonPunch, TailRake, DualWield, EagleStrike, Evocation, FeignDeath,FlyingKick, Forage, HandtoHand, Hide, Kick,Meditation,Mend, Offense, Parry, PickLock, OneHandPiercing, Riposte, RoundKick, SafeFall, SenseHeading,Singing, Sneak, SpecialiseAbjuration, SpecialiseAlteration, SpecialiseConjuration,SpecialiseDivination, SpecialiseEvocation, PickPockets,StringedInstruments, Swimming,	Throwing, TigerClaw, Tracking, WindInstruments, Fishing,	MakePoison, Tinkering, Research, Alchemy, Baking,	Tailoring, SenseTraps, Blacksmithing, Fletching, Brewing,AlcoholTolerance, Begging, JewelryMaking, Pottery, PercussionInstruments,	Intimidation, Berserking, Taunt, Frenzy,RemoveTraps,TripleAttack, TwoHandPiercing,None, Count)

"skillmodvalue":

the amount of the value given from the skill mob type (number)

"skillmodtype2":

the skill the modification will affect (Crushing,OneHandBlunt, Slashing, OneHandSlashing TwoHandBlunt, TwoHandSlashing, Abjuration, Alteration, ApplyPoison, Archery, Backstab, BindWound, Bash, Block, BrassInstruments, Channeling, Conjuration, Defense,
Disarm, DisarmTraps, Divination, Dodge, DoubleAttack,DragonPunch, TailRake, DualWield, EagleStrike, Evocation, FeignDeath,FlyingKick, Forage, HandtoHand, Hide, Kick,Meditation,Mend, Offense, Parry, PickLock, OneHandPiercing, Riposte, RoundKick, SafeFall, SenseHeading,Singing, Sneak, SpecialiseAbjuration, SpecialiseAlteration, SpecialiseConjuration,SpecialiseDivination, SpecialiseEvocation, PickPockets,StringedInstruments, Swimming,	Throwing, TigerClaw, Tracking, WindInstruments, Fishing,	MakePoison, Tinkering, Research, Alchemy, Baking,	Tailoring, SenseTraps, Blacksmithing, Fletching, Brewing,AlcoholTolerance, Begging, JewelryMaking, Pottery, PercussionInstruments,	Intimidation, Berserking, Taunt, Frenzy,RemoveTraps,TripleAttack, TwoHandPiercing,None, Count)

"skillmodvalue2":

the amount of the value given from the skill mob type (number)

"skillmodtype3":

the skill the modification will affect (Crushing,OneHandBlunt, Slashing, OneHandSlashing TwoHandBlunt, TwoHandSlashing, Abjuration, Alteration, ApplyPoison, Archery, Backstab, BindWound, Bash, Block, BrassInstruments, Channeling, Conjuration, Defense,
Disarm, DisarmTraps, Divination, Dodge, DoubleAttack,DragonPunch, TailRake, DualWield, EagleStrike, Evocation, FeignDeath,FlyingKick, Forage, HandtoHand, Hide, Kick,Meditation,Mend, Offense, Parry, PickLock, OneHandPiercing, Riposte, RoundKick, SafeFall, SenseHeading,Singing, Sneak, SpecialiseAbjuration, SpecialiseAlteration, SpecialiseConjuration,SpecialiseDivination, SpecialiseEvocation, PickPockets,StringedInstruments, Swimming,	Throwing, TigerClaw, Tracking, WindInstruments, Fishing,	MakePoison, Tinkering, Research, Alchemy, Baking,	Tailoring, SenseTraps, Blacksmithing, Fletching, Brewing,AlcoholTolerance, Begging, JewelryMaking, Pottery, PercussionInstruments,	Intimidation, Berserking, Taunt, Frenzy,RemoveTraps,TripleAttack, TwoHandPiercing,None, Count)

"skillmodvalue3":

the amount of the value given from the skill mob type (number)

"skillmodtype4":

the skill the modification will affect (Crushing,OneHandBlunt, Slashing, OneHandSlashing TwoHandBlunt, TwoHandSlashing, Abjuration, Alteration, ApplyPoison, Archery, Backstab, BindWound, Bash, Block, BrassInstruments, Channeling, Conjuration, Defense,
Disarm, DisarmTraps, Divination, Dodge, DoubleAttack,DragonPunch, TailRake, DualWield, EagleStrike, Evocation, FeignDeath,FlyingKick, Forage, HandtoHand, Hide, Kick,Meditation,Mend, Offense, Parry, PickLock, OneHandPiercing, Riposte, RoundKick, SafeFall, SenseHeading,Singing, Sneak, SpecialiseAbjuration, SpecialiseAlteration, SpecialiseConjuration,SpecialiseDivination, SpecialiseEvocation, PickPockets,StringedInstruments, Swimming,	Throwing, TigerClaw, Tracking, WindInstruments, Fishing,	MakePoison, Tinkering, Research, Alchemy, Baking,	Tailoring, SenseTraps, Blacksmithing, Fletching, Brewing,AlcoholTolerance, Begging, JewelryMaking, Pottery, PercussionInstruments,	Intimidation, Berserking, Taunt, Frenzy,RemoveTraps,TripleAttack, TwoHandPiercing,None, Count)

"skillmodvalue4":

the amount of the value given from the skill mob type (number)
