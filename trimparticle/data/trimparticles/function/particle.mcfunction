# ==========================================
# VERY RARE TRIMS
# ==========================================

# Silence
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:silence"}]
execute if score @s trim_count matches 3.. run particle sculk_soul ~ ~ ~ 0.4 0.3 0.4 0.01 3 force
execute if score @s trim_count matches 3.. run return 1

# Tide
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:tide"}]
execute if score @s trim_count matches 3.. run particle trial_spawner_detection_ominous ~ ~-0.1 ~ 0.4 0.2 0.4 0.01 5 force
execute if score @s trim_count matches 3.. run return 1

# ==========================================
# RARE TRIMS
# ==========================================

# Flow
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:flow"}]
execute if score @s trim_count matches 3.. run particle small_gust ~ ~ ~ 0.4 0.5 0.4 0.01 3 force
execute if score @s trim_count matches 3.. run return 1

# Bolt
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:bolt"}]
execute if score @s trim_count matches 3.. run particle glow ~ ~ ~ 0.4 0.5 0.4 0.01 3 force
execute if score @s trim_count matches 3.. run return 1

# Rib
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:rib"}]
execute if score @s trim_count matches 3.. run particle soul_fire_flame ~ ~ ~ 0.4 0.5 0.4 0.01 3 force
execute if score @s trim_count matches 3.. run return 1

# Vex
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:vex"}]
execute if score @s trim_count matches 3.. run particle enchant ~ ~ ~ 0.4 0.5 0.4 0.01 5 force
execute if score @s trim_count matches 3.. run return 1

# Spire
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:spire"}]
execute if score @s trim_count matches 3.. run particle end_rod ~ ~ ~ 0.4 0.5 0.4 0.01 3 force
execute if score @s trim_count matches 3.. run return 1

# ==========================================
# COMMON TRIMS
# ==========================================

# Wild
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:wild"}]
execute if score @s trim_count matches 3.. run particle pale_oak_leaves ~ ~ ~ 0.4 0.5 0.4 0.01 3 force
execute if score @s trim_count matches 3.. run return 1

# Dune
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:dune"}]
execute if score @s trim_count matches 3.. run particle vault_connection ~ ~ ~ 0.4 0.5 0.4 0.01 3 force
execute if score @s trim_count matches 3.. run return 1

# Coast
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:coast"}]
execute if score @s trim_count matches 3.. run particle bubble_pop ~ ~ ~ 0.4 0.5 0.4 0.01 3 force
execute if score @s trim_count matches 3.. run return 1

# Sentry
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:sentry"}]
execute if score @s trim_count matches 3.. run particle infested ~ ~ ~ 0.4 0.5 0.4 0.01 3 force
execute if score @s trim_count matches 3.. run return 1

# Ward
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:ward"}]
execute if score @s trim_count matches 3.. run particle sculk_charge_pop ~ ~ ~ 0.4 0.5 0.4 0.01 2 force
execute if score @s trim_count matches 3.. run return 1

# Snout (Uses wax_on to replace missing vanilla FIREFLY particle)
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:snout"}]
execute if score @s trim_count matches 3.. run particle firefly ~ ~ ~ 0.4 0.5 0.4 0.01 3 force
execute if score @s trim_count matches 3.. run return 1

# ==========================================
# VERY COMMON TRIMS
# ==========================================

# Host (Uses item egg to replace missing vanilla EGG_CRACK particle)
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:host"}]
execute if score @s trim_count matches 3.. run particle egg_crack ~ ~ ~ 0.4 0.5 0.4 0.01 3 normal
execute if score @s trim_count matches 3.. run return 1

# Raiser
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:raiser"}]
execute if score @s trim_count matches 3.. run particle egg_crack ~ ~ ~ 0.4 0.5 0.4 0.01 3 normal
execute if score @s trim_count matches 3.. run return 1

# Shaper
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:shaper"}]
execute if score @s trim_count matches 3.. run particle egg_crack ~ ~ ~ 0.4 0.5 0.4 0.01 3 normal
execute if score @s trim_count matches 3.. run return 1

# Wayfinder
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:wayfinder"}]
execute if score @s trim_count matches 3.. run particle egg_crack ~ ~ ~ 0.4 0.5 0.4 0.01 3 normal
execute if score @s trim_count matches 3.. run return 1

# Eye
execute store result score @s trim_count if items entity @s armor.* *[trim~{pattern:"minecraft:eye"}]
execute if score @s trim_count matches 3.. run particle egg_crack ~ ~ ~ 0.4 0.5 0.4 0.01 3 normal
execute if score @s trim_count matches 3.. run return 1