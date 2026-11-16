# Electro-Magic Tools Reborn 1.0.0

A port of **Electro-Magic Tools** (Thaumcraft 4 + IndustrialCraft 2 addon) to **Minecraft 1.20.1 / Forge 47**.
The original EMT is by **Tombenpotter, TehNut and fewizz**. It is licensed under GPLv3 (see LICENSE.txt), and so is this port.

## Requirements
- Forge 1.20.1 (47.x)
- Modern Forge Connect Loader (MFCL) 1.0.9+ with **Thaumcraft 4.2.3.5** (and Baubles) in `mods/1.7.10`
- IndustrialCraft 2 port for 1.20.1 (Sviluppatore)
- tc-modern-lib / tc4-modern. Optional, but needed for Thaumcraft aspects on IC2 items.
- JEI. Optional.

## Install
- `EMT-Reborn-bridge-1.20.1-1.0.0.jar` → `mods/`
- `EMT-Reborn-legacy-1.7.10-1.0.0.jar` → `mods/1.7.10/`

Both halves must have the same version.

## How it works
- **Legacy half:** EMT 1.2.6.2 running inside MFCL next to Thaumcraft 4. It provides the items, blocks, research and Thaumcraft recipes.
- **1.20.1 half (emtbridge):** connects them to the modern game:
  - Forge Energy (1 EU = 1 FE) for EMT items, so the IC2 port can charge them;
  - EMT recipes inside IC2 machines;
  - Thaumcraft aspects for 421 IC2 items;
  - jetpack, wing and boots movement;
  - armour powers;
  - wing rendering;
  - lightning and archers;
  - dungeon loot.

## Diagnostics
`/emtbridge selftest` (op): checks energy on every EMT item. Lines with `FAIL` are errors.

## Known limitations
- Machines that change their face when working (ethereal processor) show no on/off animation (MFCL draws legacy blocks statically).
- Thaumcraft's Portable Hole renders as dark blocks under MFCL (you can still walk through it).
- If an EMT armour piece stops working, restart the game: MFCL switches off a legacy item that failed until the next launch.
- The ethereal processor only knows vanilla furnace recipes. It refuses food, dust, cobblestone, sand, armour and tools (as in the original).

## Changes compared to EMT 1.2.6.2
- Electric cloud is visible (drawn as a purple spark) instead of invisible.
- Ethereal processor bonus nugget no longer turns into a different item.
- Essentia generators send at most LV by default (`Max voltage tier` in EMT.cfg), so they do not blow up a BatBox.
- Russian translation fixes.
