alter table rental_spec modify property_number varchar (20) null;
ALTER TABLE rental_spec
    ADD COLUMN dtype VARCHAR(20) NOT NULL DEFAULT 'equipment';
ALTER TABLE rental_spec
    ALTER COLUMN dtype DROP DEFAULT;
