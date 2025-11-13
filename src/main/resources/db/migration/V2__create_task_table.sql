CREATE TABLE task (
                      id UUID PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      description VARCHAR(4000),
                      status VARCHAR(50),
                      priority VARCHAR(50),
                      due_date DATE,
                      project VARCHAR(255),
                      created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                      updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                      assignee_id UUID,
                      created_by_id UUID,
                      CONSTRAINT fk_task_assignee FOREIGN KEY (assignee_id) REFERENCES member (id),
                      CONSTRAINT fk_task_created_by FOREIGN KEY (created_by_id) REFERENCES member (id)
);
