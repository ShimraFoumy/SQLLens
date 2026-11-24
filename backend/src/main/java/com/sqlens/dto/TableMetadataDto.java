package com.sqlens.dto;

import java.util.ArrayList;
import java.util.List;

public class TableMetadataDto {
    private String tableName;
    private long estimatedRowCount;
    private List<ColumnInfoDto> columns = new ArrayList<>();
    private List<String> indexes = new ArrayList<>();

    public TableMetadataDto() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final TableMetadataDto target = new TableMetadataDto();

        public Builder tableName(String val) { target.tableName = val; return this; }
        public Builder estimatedRowCount(long val) { target.estimatedRowCount = val; return this; }
        public Builder columns(List<ColumnInfoDto> val) { target.columns = val; return this; }
        public Builder indexes(List<String> val) { target.indexes = val; return this; }

        public TableMetadataDto build() { return target; }
    }

    public static class ColumnInfoDto {
        private String name;
        private String dataType;
        private boolean isPrimaryKey;
        private boolean isForeignKey;
        private boolean isNullable;

        public ColumnInfoDto() {}

        public ColumnInfoDto(String name, String dataType, boolean isPrimaryKey, boolean isForeignKey, boolean isNullable) {
            this.name = name;
            this.dataType = dataType;
            this.isPrimaryKey = isPrimaryKey;
            this.isForeignKey = isForeignKey;
            this.isNullable = isNullable;
        }

        public static ColumnInfoBuilder builder() {
            return new ColumnInfoBuilder();
        }

        public static class ColumnInfoBuilder {
            private String name;
            private String dataType;
            private boolean isPrimaryKey;
            private boolean isForeignKey;
            private boolean isNullable;

            public ColumnInfoBuilder name(String val) { this.name = val; return this; }
            public ColumnInfoBuilder dataType(String val) { this.dataType = val; return this; }
            public ColumnInfoBuilder isPrimaryKey(boolean val) { this.isPrimaryKey = val; return this; }
            public ColumnInfoBuilder isForeignKey(boolean val) { this.isForeignKey = val; return this; }
            public ColumnInfoBuilder isNullable(boolean val) { this.isNullable = val; return this; }

            public ColumnInfoDto build() {
                return new ColumnInfoDto(name, dataType, isPrimaryKey, isForeignKey, isNullable);
            }
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }
        public boolean isPrimaryKey() { return isPrimaryKey; }
        public void setPrimaryKey(boolean primaryKey) { isPrimaryKey = primaryKey; }
        public boolean isForeignKey() { return isForeignKey; }
        public void setForeignKey(boolean foreignKey) { isForeignKey = foreignKey; }
        public boolean isNullable() { return isNullable; }
        public void setNullable(boolean nullable) { isNullable = nullable; }
    }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public long getEstimatedRowCount() { return estimatedRowCount; }
    public void setEstimatedRowCount(long estimatedRowCount) { this.estimatedRowCount = estimatedRowCount; }
    public List<ColumnInfoDto> getColumns() { return columns; }
    public void setColumns(List<ColumnInfoDto> columns) { this.columns = columns; }
    public List<String> getIndexes() { return indexes; }
    public void setIndexes(List<String> indexes) { this.indexes = indexes; }
}
