package com.sqlens.dto;

public class SampleQueryDto {
    private String id;
    private String title;
    private String category;
    private String description;
    private String expectedPlanType;
    private String query;

    public SampleQueryDto() {}

    public SampleQueryDto(String id, String title, String category, String description, String expectedPlanType, String query) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.expectedPlanType = expectedPlanType;
        this.query = query;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String title;
        private String category;
        private String description;
        private String expectedPlanType;
        private String query;

        public Builder id(String val) { this.id = val; return this; }
        public Builder title(String val) { this.title = val; return this; }
        public Builder category(String val) { this.category = val; return this; }
        public Builder description(String val) { this.description = val; return this; }
        public Builder expectedPlanType(String val) { this.expectedPlanType = val; return this; }
        public Builder query(String val) { this.query = val; return this; }

        public SampleQueryDto build() {
            return new SampleQueryDto(id, title, category, description, expectedPlanType, query);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getExpectedPlanType() { return expectedPlanType; }
    public void setExpectedPlanType(String expectedPlanType) { this.expectedPlanType = expectedPlanType; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
}
