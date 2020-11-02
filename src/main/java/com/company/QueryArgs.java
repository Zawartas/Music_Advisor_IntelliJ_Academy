package com.company;

public class QueryArgs {

    public static final int ZERO = 0;
    private int offset;
    private int size;
    private String category;

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    public void increaseOffset() {
        offset += size;
    }

    public void decreaseOffset() {
        offset -= size;
    }

    public String getCategory() {
        return category;
    }

    public static class Builder {
        private int offset;
        private int size;
        private String category;

        public Builder(int size) {
            this.size = size;
            offset = ZERO;
        }

        public Builder setOffset(int offset) {
            this.offset = offset;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public QueryArgs build() {
            QueryArgs queryArgs = new QueryArgs();
            queryArgs.offset = this.offset;
            queryArgs.size = this.size;
            queryArgs.category = this.category;
            return queryArgs;
        }
    }

    private QueryArgs() {
        // possible only with Builder
    }

}
