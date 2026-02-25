CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL,
  full_name VARCHAR(255) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE homeworks (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  teacher_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT NULL,
  due_at DATETIME NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_homeworks_teacher FOREIGN KEY (teacher_id) REFERENCES users(id)
);

CREATE TABLE exercises (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  homework_id BIGINT NOT NULL,
  order_index INT NOT NULL,
  instruction_text TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_exercises_homework FOREIGN KEY (homework_id) REFERENCES homeworks(id),
  CONSTRAINT uq_exercise_order UNIQUE (homework_id, order_index)
);

CREATE TABLE student_assignments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  homework_id BIGINT NOT NULL,
  student_id BIGINT NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ASSIGNED',
  assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_assignments_homework FOREIGN KEY (homework_id) REFERENCES homeworks(id),
  CONSTRAINT fk_assignments_student FOREIGN KEY (student_id) REFERENCES users(id),
  CONSTRAINT uq_assignment UNIQUE (homework_id, student_id)
);

CREATE TABLE files (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  storage_key VARCHAR(1024) NOT NULL,
  original_filename VARCHAR(512) NOT NULL,
  content_type VARCHAR(255) NOT NULL,
  size_bytes BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE exercise_submissions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_assignment_id BIGINT NOT NULL,
  exercise_id BIGINT NOT NULL,
  attempt_no INT NOT NULL,
  text_result TEXT NULL,
  submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_submissions_assignment FOREIGN KEY (student_assignment_id) REFERENCES student_assignments(id),
  CONSTRAINT fk_submissions_exercise FOREIGN KEY (exercise_id) REFERENCES exercises(id),
  CONSTRAINT uq_attempt UNIQUE (student_assignment_id, exercise_id, attempt_no)
);

CREATE TABLE submission_files (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  submission_id BIGINT NOT NULL,
  file_id BIGINT NOT NULL,
  order_index INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_submission_files_submission FOREIGN KEY (submission_id) REFERENCES exercise_submissions(id),
  CONSTRAINT fk_submission_files_file FOREIGN KEY (file_id) REFERENCES files(id),
  CONSTRAINT uq_submission_file UNIQUE (submission_id, file_id),
  CONSTRAINT uq_submission_order UNIQUE (submission_id, order_index)
);

CREATE INDEX idx_exercises_homework ON exercises(homework_id);
CREATE INDEX idx_assignments_student ON student_assignments(student_id);
CREATE INDEX idx_submissions_assignment_exercise_attempt ON exercise_submissions(student_assignment_id, exercise_id, attempt_no);
CREATE INDEX idx_submission_files_submission ON submission_files(submission_id);