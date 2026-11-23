ALTER TABLE person
ADD CONSTRAINT uq_person_display_name UNIQUE (display_name);