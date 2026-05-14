# Reschedule this function to maintain the 10-tick loop
schedule function trimparticles:loop 10t

# Only check players currently wearing at least one trimmed armor piece
execute as @a at @s if items entity @s armor.* *[minecraft:trim] run function trimparticles:particle