# ja2tools - assetManager
Classes doing some more high-level manipulation of game data, making it easier to consume.

- *ItemManager* - loads the item list from xml, translates the xml data model to a more convenient class and builds the item category tree.
- *MapManager* - provides a MapLoader hooked up to the XmlLoader for easier loading of maps. Keeps a list of maps in the current VFS tree.
- *TilesetManager* - constructs tilesets from xml or .dat definitions.