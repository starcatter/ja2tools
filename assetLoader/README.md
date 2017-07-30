# ja2tools - assetLoader
A bunch of classes for reading JA2 data. Uses [javolution](https://github.com/javolution/javolution) for reading binary data.

- *MapLoader* - reads and writes JA2 map files (.dat), supports a bunch of older versions. Currently a crazy mix of memory mapped structures and proper variables and classes, this component is in desperate need of some cleanup. Loosely dependent on the XmlLoader, it needs item data in order to properly upgrade items found on older maps.
- *StiLoader* - reads the STI data format, keeps the data mapped and provides palettes and image bytes for requested objects.
- *SlfLoader* - reads SLF files, provides other classes with ByteBuffers to access assets inside the library
- *VirtualFileSystem* - reads VFS config ini files and creates VFSConfig classes that provide access to the file tree. Default access method returns the last overriding file in the hierarchy, there's also a method to get file variants.
- *XmlLoader* - handles loading the models defined in the *xmlModels* module. Currently just a list of xml assets to be loaded and a macro-generated list of access methods.
- *TilesetLoader* - used by the TilesetManager to create the tileset data structures.

Also of note is the *ImageAdapter* which builds JavaFX Image objects out of StiLoader's output.

