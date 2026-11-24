package com.sqlens.dto;

public class ColumnMetadata {
    private String name;
    private String typeName;
    private int typeCode;

    public ColumnMetadata() {}

    public ColumnMetadata(String name, String typeName, int typeCode) {
        this.name = name;
        this.typeName = typeName;
        this.typeCode = typeCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String typeName;
        private int typeCode;

        public Builder name(String name) { this.name = name; return this; }
        public Builder typeName(String typeName) { this.typeName = typeName; return this; }
        public Builder typeCode(int typeCode) { this.typeCode = typeCode; return this; }
        public ColumnMetadata build() { return new ColumnMetadata(name, typeName, typeCode); }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public int getTypeCode() { return typeCode; }
    public void setTypeCode(int typeCode) { this.typeCode = typeCode; }
}
