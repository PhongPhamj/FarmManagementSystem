INSERT INTO users (id,password_hash,first_name,last_name,email,activated,created_by,last_modified_by,created_date) VALUES
                                                       ('1', '$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC','Administrator','Administrator','admin@localhost','true','system','system','2023-11-22 03:48:26.019');
INSERT INTO authority (name) VALUES
                                 ('ROLE_ADMIN'),
                                 ('ROLE_USER');

INSERT INTO user_authority (user_id, authority_name) VALUES
                                 ('1','ROLE_ADMIN'),
                                 ('1','ROLE_USER');
