-- Create item_category_available_types table for ElementCollection mapping
CREATE TABLE item_category_available_types (
    category_id BIGINT NOT NULL,
    type VARCHAR(60) NOT NULL,
    PRIMARY KEY (category_id, type),
    FOREIGN KEY (category_id) REFERENCES item_category(id) ON DELETE CASCADE
);

-- Create index for query performance
CREATE INDEX idx_item_category_available_types_category_id ON item_category_available_types(category_id);
