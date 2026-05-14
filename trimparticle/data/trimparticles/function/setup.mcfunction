# Setup the scoreboard dummy to track matched trim counts
scoreboard objectives add trim_count dummy

# Start the loop timer (matches Java's 10 tick interval)
schedule function trimparticles:loop 10t

# Confirmation
tellraw @a {"text":"TrimParticles v1.0.3 DataPack Loaded","color":"green"}