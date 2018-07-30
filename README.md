# ja2tools
Java tools for manipulating Jagged Alliance 2 1.13 data.

![snippet example](https://i.imgur.com/shOoqo2.png)


Releases are posted in [this thread](http://thepit.ja-galaxy-forum.com/index.php?t=msg&th=23453&start=0&) on The Bear's Pit forum. Ask there if you need help, or send [me](http://thepit.ja-galaxy-forum.com/index.php?t=usrinfo&id=6812) a PM. Github issues are fine too.

Using versions prior to *Alpha 13* (27 July 2018) is not recommended due to a bug in map output code that was only fixed at that point.

The release package accepts command line parameters for converting maps:
```
Source vfs config file
Target vfs config file
Tile mapping file ("none" to skip)
Item mapping file ("none" to skip)
Map file name, or comma separated list of map names, without spaces between maps, names should include the .dat extension if present
```

All file paths can be given as full paths or relative to current, paths should be enclosed in parentheses if they contain spaces.

Usage example:
`java -jar ja2MapTool-alpha13.jar "C:\JA2\JA2.113\vfs_config.JA2113Wildfire607.ini" "C:\JA2\JA2.113\vfs_config.JA2113AIMNAS.ini" none C:\JA2\mapping\AIMNAS.itemmap b13.dat,c13.dat,d13.dat`
