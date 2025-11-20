# WIP

## RolflectionLib - A java library for modding with reflection in Starsector

### Features
- [Dynamic runtime subclass generator](./src/rolflectionlib/inheritor/Inherit.java)
  - For subclassing obfuscated classes and interfaces and hooking their methods at runtime. Works with illegal class and method names to help maintain platform and version agnosticism.
  - [Examples](./src/rolflectionlib/inheritor/examples/)

- [General utility class](./src/rolflectionlib//util/RolfLectionUtil.java) With many static helper functions and wrappers to get fields, methods, constructors etc so you don't have to clutter your code with endless try catch blocks.

- [ClassRefs](./src/rolflectionlib/util/ClassRefs.java) utility dataclass containing various references to cached classes and their methods not exposed by the regular API

- [TreeTraverser](./src/rolflectionlib/ui/TreeTraverser.java) UI Component tree utility class. Includes multiple constructors with varying degrees of overhead depending on what is needed.

- [Option Panel Listener](./src/rolflectionlib/plugins/RolfLectionLibListener.java)
  - Hooks `InteractionDialogAPI` option buttons and provides an `afterOptionSelected` method that gets called with selected `optionData` as second parameter when options are selected. Includes getters on the `OptionPanelListener` class that is the first parameter to get the current options, the current option, and currently interacted comm directory entry data if a person was interacted with.

- [EngineTex](./src/rolflectionlib/inheritor/enginetex/EngineTex.java) provides granular control over ship engine glow textures.

- [Cloner](./src/rolflectionlib/util/Cloner.java) Clones various objects that implement the `Cloneable` interface such as the obfuscated ship hull spec object.

- [UI Button hooks, custom dynamic campaign entity descriptions](./src/rolflectionlib/util/Misc.java), and more.