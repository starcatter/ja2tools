package thebob.ja2maptool.util.mapping.item;

import thebob.assetmanager.managers.items.Item;

public interface Mapping {
    Integer getSourceId();
    Integer getTargetId();

    String getSourceName();
    String getTargetName();

    void setSourceId(Integer sourceId);
    void setTargetId(Integer sourceId);

    void setSourceName(String sourceName);
    void setTargetName(String targetName);

    // --

    void setSource(Item source);
    void setTarget(Item target);
}
