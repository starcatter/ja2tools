package thebob.ja2maptool.util.mapping.item;

import thebob.assetmanager.managers.items.Item;

public class IdMapping implements Mapping {
    String sourceName;
    String targetName;

    Integer sourceId;
    Integer targetId;

    public IdMapping(Integer sourceId, String sourceName, Integer targetId, String targerName) {
        this.sourceName = sourceName;
        this.targetName = targerName;
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    @Override
    public Integer getSourceId() {
        return sourceId;
    }

    @Override
    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public Integer getTargetId() {
        return targetId;
    }

    @Override
    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    // ---

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @Override
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public String getTargetName() {
        return targetName;
    }

    // ---

    @Override
    public void setSource(Item source) {
        sourceId = source.getId();
        sourceName = source.getName();
    }

    @Override
    public void setTarget(Item target) {
        targetId = target.getId();
        targetName = target.getName();
    }

}
