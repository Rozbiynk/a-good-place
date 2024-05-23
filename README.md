# A Good Place

A client-side mod that adds block placement animations.

The mod contains a default animation that will be applied to all suitable blocks (excluding ones like double flowers and
bets).
It also works on tile entities!

The mod is fully customizable via Resource Packs.

## Customization

To start just go in your resource packs folder, you'll find a pre made sample pack there.

You'll find a `placement_animations` folder. Here you'll be able to place one or more json files, each of which will
target a set of blocks and give them a specific animation. You can name them however you please.

Here below is the syntax for the json file that defines the animations for the placement of the blocks.
All fields are optional.

In short animations are composed of 4 separate animations: scale, translation, rotation and height scale.

| name                | default value           | explanation                                                                                                                                                                                      |
|---------------------|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `predicates`        | `[]`                    | A list of Rule Tests (predicates) used to filter the affected blocks.`                                                                                                                           |
| `priority`          | `0`                     | Priority of this file. Higher ones will be applied over ones with lower priority.                                                                                                                |
| `duration`          | `4`                     | Duration of the animation in ticks.                                                                                                                                                              |
| `scale`             | `1`                     | The initial scale of the block when it is placed.                                                                                                                                                |
| `scale_curve`       | `0.5`                   | Controls the animation of the scale parameter.                                                                                                                                                   |
| `translation`       | `0`                     | A vector of 3 components `x`, `y` and `z` representing the intensity of the translation animation relative to the player direction. If you just set z to 1 it will be exactly toward the player. |
| `translation_curve` | `0.5`                   | Controls the animation of the translation parameter.                                                                                                                                             |
| `rotation`          | `{"x":0, "y":0, "z":0}` | A vector of 3 components `x`, `y` and `z`, each representing the angle in DEGREES relative to the player look direction at which the animation will start.                                       |
| `rotation_curve`    | `0.5`                   | Controls the animation of the rotation parameter.                                                                                                                                                |
| `rotation_pivot`    | `{"x":0, "y":0, "z":0}` | A vector of 3 components `x`, `y` and `z`, each representing the pivot point of the rotation operation, relative to the block center.                                                            |
| `height`            | `1`                     | The Y scale of the block when it is placed.                                                                                                                                                      |
| `height_curve`      | `0.5`                   | Controls the animation of the height parameter.                                                                                                                                                  |
| `sound`             | `<empty>`               | Additional sound to play when the animation starts (ID of sound event).                                                                                                                          |

The animation always ends up at the block normal dimension.
For example `scale` is the initial scale of the block while its final scale will obviously be 1.

Regarding `scale`. Internally it is a vector of 3 components.
it will be calculated as follows:
Vec3 rot = player_direction.rotateY(`rotation_angle`) * `rotation_amount`;
rot.y = `rotation_y`;

This rotation will then be either applied with pivot the center of the block or its upper left corner (relative to move
direction), depending on config

All angles are in degrees.

Below follows an example of a pop in animation

```json
{
  "predicates": [
    {
      "predicate_type": "tag_match",
      "tag": "minecraft:stone"
    }
  ],
  "scale": 0.2,
  "scale_curve": 0.92
}
```

### About "curve"

All parameter that use the keyword `_curve` control the base of an exponential function used to calculate the curve othe
animation.

- A value of 0 will result in a linear "curve", smoothly interpolating from start to finish.
- A value approaching 1 will make the exponent function have upward concavity, making it steeper and steeper toward the
  end. This results in a slow change at the beginning and a fast one as time goes on. 1 is NOT a valid value.
- A value approaching -1 will make the exponent function have downward concavity, making it steeper and steeper toward
  the beginning. This results in a fast change at the beginning and a slow one as time goes on. -1 is NOT a valid value.

### About "predicates"

As mentioned above predicate field is a list of rule test objects used to filter the blocks to which you want to apply
your animation.

Rule tests are a vanilla concept, usually used for worldgen stuff.
If you are not familiar check that section on the official Minecraft wiki.

In addition to the vanilla rule test types, A Good Place adds 2 new test types:

- `a_good_place:not_in_tag`: This test checks if the block is NOT in a certain tag. The tag is specified in the `tag`
  field.
- `a_good_place:solid`: This test checks if the block is solid or not. The `solid` field determines the polarity of the
  test.
- `a_good_place:no_double_blocks`: This test checks if the block is a double block or not (hardcoded list of stuff like double flowers, beds and double chests)