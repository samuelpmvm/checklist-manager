CREATE TABLE checklist (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE checklist_item (
    id UUID PRIMARY KEY,
    template_id UUID REFERENCES checklist(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    required BOOLEAN DEFAULT false,
    type VARCHAR(50),
    order_index INT
);