Currently, damaging entities using Minecraft commands is an imprecise science.
Our best option is usually potion effects like `minecraft:instant_damage` and `minecraft:wither`, which are
imprecise at best. To aggro mobs we have to summon a projectile with the aggro target's UUID as its owner name
and wait, and if there's another entity near the entity we want to aggro there's no guarantee which will get hit.
Setting players on fire (or extinguishing them) is difficult outside of tightly controlled environments.

Therefore, I propose the addition of a `/damage` command.

# The Short Version

    /damage <entity> <amount> [<damageType> <params...>]

# The Long Version

- *`<entity>`*: The target. Can be any number of entities.
- *`<amount>`*: The amount of damage to do. Can be any non-negative float, including zero.
- *`[<damageType> <params...>]`* (optional): Here it gets interesting. As of 1.12.1 Minecraft contains about 29 different
  internal damage source types which I have compressed into 27 IDs, some of which support (or require) special params. These are:
  - `minecraft:generic` (default)
  - `minecraft:void`
  - `minecraft:in_fire`
  - `minecraft:on_fire [<fireSeconds>]`
    - *fireSeconds* (optional): A non-negative integer. If `fireSeconds` is zero, the entity is extinguished.
      Otherwise, if the entity's fire timer is less than `fireSeconds`, the entity's fire timer is set to `fireSeconds`.
  - `minecraft:lightning_bolt`
  - `minecraft:lava`
  - `minecraft:hot_floor`
  - `minecraft:in_wall`
  - `minecraft:cramming`
  - `minecraft:drown`
  - `minecraft:starve`
  - `minecraft:cactus`
  - `minecraft:fall`
  - `minecraft:fly_into_wall`
  - `minecraft:wither`
  - `minecraft:anvil`
  - `minecraft:falling_block`
  - `minecraft:dragon_breath`
  - `minecraft:fireworks`
  - `minecraft:player <attacker>`
    - *attacker*: The player that "did" the damage. Must be exactly one player.
  - `minecraft:thorns <enchanted>`
    - *enchanted*: The entity that was wearing the enchantment. Must be exactly one entity.
  - `minecraft:mob <attacker> [<weapon>]`
    - *attacker*: The mob that "did" the damage. Must be exactly one living entity.
    - *weapon* (optional): **NOT** an item. The entity that the damage was done with. Llama spit, for example.
      Must be exactly one entity.
  - `minecraft:arrow <arrow> [<shooter>]`
    - *arrow*: The arrow in question. Must be exactly one arrow.
    - *shooter* (optional): The entity that fired the arrow. Must be exactly one entity.
  - `minecraft:fireball <fireball> [<shooter>]`
    - *fireball*: The fireball in question. Must be exactly one fireball.
    - *shooter* (optional): The entity that fired the fireball. Must be exactly one entity.
  - `minecraft:thrown <thrownEntity> [<thrower>]`
    - *thrownEntity*: The entity that was thrown. Must be exactly one entity.
    - *thrower* (optional): The entity that threw it. Must be exactly one entity.
  - `minecraft:magic [<spell>] [<caster>]`
    - *spell* (optional): The magical entity that did the damage. Must be exactly one entity.
    - *caster* (optional): The entity that cast the spell or threw the potion. Must be exactly one entity.
  - `minecraft:explosion [<bomber>]`
    - *bomber* (optional): The mob that caused the explosion. Must be exactly one living entity.
  
  All damage types that specify the source entity set aggro on the target, even if zero damage was done.
