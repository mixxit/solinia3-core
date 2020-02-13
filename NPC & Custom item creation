Other needed NPC Commands To do FIRST
/createfaction (Name of Faction) (0 neutral, -1500 to attack)
 (*do this if there is not already a faction being used in the dungeon being worked on. If you are creating something entirely new, you will need to make a new faction.*)

/listraces 
(*This will list the races*)

/listclasses 
(*This will list the classes*)

To check NPC creation do /spawnnpc (NPC ID)  … this is a temporary NPC and will vanish once you leave the area
-------------------------------------------------------------------------------------------------------------------------------

Creating NPC’s
/createnpc (Faction ID) (level) (Name of NPC EX: Djin_Matar)
(*This will give an NPC ID KEEP TRACK OF THIS NUMBER!! Doing so will make this much easier*)
/editnpc (NPC ID)
/editnpc (NPC ID) raceid (race ID number)
/editnpc (NPC ID) classid (Class ID number)
/editnpc (NPC ID) 
(At this point, go through the different commands listed, and see if there is anything that needs to be added. Randomchattext, boss, raid boss, deathgrantstittle, killtriggertext ECT. The commands will go such as EX: /editnpc (npc ID) randomtextchat It is pretty hot out today.)

NOW GO TO PLANET MINECRAFT, OR WHEREVER, TO FIND A SKIN. YOU ONLY NEED TO WORRY ABOUT THE HEAD. SET THAT SKIN TO A CHARACTER.
/editnpc (ID) customhead true
/editnpc (ID) customheaddata (account name skin is saved to ex: Astrid_Manumit)

*Remember at any time you can do /editnpc (NPC ID) see what has not been done <3*
CONGRATULATIONS! YOU HAVE CREATED A MOB! NOW LETS GET HIS ITEMS!!
((YOU DO NOT HAVE TO DO LOOT TABLES ANYMORE! SKIP THIS STEP AND GO TO ARMOR SETS))
LOOT TABLES

/createloottable (name of creature it is dropping off of EXACTLY how you previously named it)
*This will give loot table ID*
/createlootdrop (name of creature it is dropping off of EXACTLY how you previously named it)
*This will give loot drop ID*

/addloottablelootdrop (LOOT TABLE ID) (LOOT DROP ID)

Keep track of your loot table and loot drop ID numbers. DO NOT GET THEM MIXED UP!
-------------------------------------------------------------------------------------------------------------------------------
Armor Sets/DROPS

/createallarmorsets 
/createallarmorsets (Loot drop ID) (Tier) (Drop Chance) (Suffix)

(((   /createallarmorsets (Loot drop ID) (Tier *take the lvl, divide by ten, add one* bosses get an extra+1 ) (chance *Boss 100%, regulars 20% - 30%) (Suffix aka name *ex: Of The Wilds*  )))
-tier 1( lvl 1-9)
Tier 2 (10 -19_  
--------------------------------------------------------------------------------------------------------------------------------------------------------
Adding spells to drop from mobs

/listlootdrops lvl
*This will give you the spell drop ID’s for each group and lvl, for spells. You will have to do the next command for each individual spell class you want to drop from the mob*
/addloottablelootdrop (LOOT TABLE ID) (SPELL DROP ID)
*Again, this command must be done for each class of spells to drop from the mob you have created*
-------------------------------------------------------------------------------------------------------------------------------


Adding Individual Items

/addlootdropitem (lootdrop ID) (Item ID) (Amount) (True/False) (chance)
(((  /addlootdropitem  (LootdropID) (Item ID) (Amount that will drop *1*) (false *don’t drop every time* true *will drop every time*) (chance to drop 20%-30%) )))

*To check that the item is in your lootdrop do /editlootdrop (LootdropID)
-------------------------------------------------------------------------------------------------------------------------------

Create Spawn Groups

/createspawngroup (NPC ID) (Spawn group name)

/createspawngroup (NPCID) (Spawn group name ex: Eartly_Summoner001) *You will change the 001 to 002, 003, 004 ect. These differentiate between each mob spawned.*

CONGRATULATIONS! YOU HAVE CREATED ITEMS AND LEARNED TO SPAWN IN YOUR MOBS! NOW TO GO MAKE A BOSS >😊
-------------------------------------------------------------------------------------------------------------
BOSS CREATION
Making a boss is basically the same as making an NPC. Please see the NPC creation section and return here for further instruction <3

Now you have your mob created. You are going to want to do /editnpc (NPC ID)

This will bring up all those lovely options that you are able to play around with. Some of the things you are going to want to do for your boss is:
/editnpc (NPC ID) heroic true
/editnpc (NPC ID) boss true

/editnpc (NPC ID) respawntime (time in seconds)
(( Respawn time is also something you will want to set on your boss. This will set how long it takes for it to spawn in (1800 is 30 minutes for Bosses) to do this you will want to do  ))

If you need to put a boss to the side, for example if it is an event type NPC like Sunta, then you want to do /editnpc (NPC ID) disabled true.
((False means it is here, True means it is technically still in the world, but is not active or visible ex: Sunta is set to True unless we are using him.))

These commands are a few that you will want to do for your boss. However, there are a great many more that you will want to do as well or perhaps not do. Such as Summon ect.

Please keep in mind when making your bosses that our players are stronger than you make think <3

NOW LETS GIVE THAT BOSS A CUSTOM ITEM! REMEMBER THIS CAN BE A WEAPON, ARMOR, OR AN ITEM. WE WILL USE A SWORD AS AN EXAMPLE FOR THE STEPS TO TAKE 😊

Creating Custom Items
Spawn in an item such as an IRON sword and hold it in your hand
*You can do this by being in creative mode to make it simple. /gmc*

/createitem 
This will give you an Item ID. KEEP TRACK OF YOUR ID!

/edititem (ID)
This will bring up your list of things you are able to do to your item. Remember always do /edititem (Item ID) (What you wish to edit) (ect….. ) when doing a command. And always be careful as to what ITEM ID or LOOT DROP ID you are putting in <3

Now you want to set the level to your item. So look in your list after doing /edititem (ITEM ID) to find where it is listed and how it is spelled <3

Next is the fun part! Follow the link below and pick out ‘skin’ for your weapon if that is what you are making. Keep in mind if you are making a sword, or bow, the skin must state below it that it is for a sword, bow, axe, ect. 
https://ravand.org//refsheet_tools.html 
Once you know the skin you wish to use, do the following :

/edititem (ITEM ID) display (Name of the skin on the site, and then what you wish to name it.)
((  EX: /edititem 1254 display Bronze Longsword of the Huntress ))

Now lets set a price to it:
/edititem (ItemID) worth (numerical value)

Something else you will want to do, especially if the item is able to be placed is to set the placeable to false. By doing this, the item wont mistakenly be placed and in turn destroyed:
/edititem (ITEM ID) placeable false

Now you want to do the stats. This is a guide for giving the appropriate bonuses, listed below the coordinating command:
+1 damage/4 lvl 
+1 stat/2 lvl
+20hp/Tier
+20mana/Tier

Be sure to pay attention to if the bonus is being based off of the TIER or the LEVEL. <3

Pick two- three resists (+2 bonus per tier) EX (FR + Fireresist, use the whole word)

ALMOST DONE! JUST HANG IN THERE A LITTLE LONGER, YOU’VE GOT THIS!

FOCUS EFFECTS FOR CUSTOM WEAPONS:
http://everquest.allakhazam.com/forum.html?forum=1&mid=1458079337208885879
Go through list and find the one you want. Click on it.
Under the description of the focus click on the focus Link
*ex. Focus:Extended Enhancement lll*
This will bring you to a new list under that link. IGNORE THEM. Go to your web browser and copy the last four numbers in the web link
/edititem (ItemID) focuseffectid (Web site last four numbers)

SKILLMODS:
(only put on 50 lvl items and above)

/edititem (itemID) skillmodtype (Type ex:Abjuration) 
/edititem (ItemID) skillmodvalue (Numerical value generally +1/3 lvl)


identify message
 (If someone identifies the item. You do not have to do this, but it is a fun little thing to add for the benefit of players)
/edititem (itemID) identifymessege (messege)

Allowed Class names
 (This will set it so that only certain classes may use the weapon. Doing multiple classes be sure it is all in Caps with Comma’s and NO SPACES)
/edititem (ItemID) allowedclassnames (DRUID,CLERIC,SHAMAN)
ONCE you have your item all created, even the things we did not go over here, you can now add the item to your Boss to be dropped.

Add item to drop list
/addlootdropitem (LOOT DROP ID*from mob*) (ITEM ID) (count 1) (False) (chance 100%)

Then do /editlootdrop (lootdropid) To make sure that the new item has been added to the loot drop


If you are making Jewelry items be sure to put in where it belongs to be equipped

If you have discovered the item, go ahead and clear your name out so a player may discover it by doing the follow command:

/edititem (item id ) cleardiscoverer true

When making your custom items, keep in mind that Artifacts only ever drop once.





YOU SURVIVED!! CONGRATULATIONS!! ONLY A MILLION MORE THINGS TO LEARN! ^_^



-------------------------------------------------------------------------------------------------------------------------------

