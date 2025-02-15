CREATE TABLE refresh_token
(
	token_value             varchar(63) NOT NULL,
	member_certification_id binary(16)  NOT NULL,
	used                    boolean     NOT NULL DEFAULT false,
	expired_at              datetime    NOT NULL,
	created_at              datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (token_value),
	CONSTRAINT refresh_token_member_certification_id_fk FOREIGN KEY (member_certification_id) REFERENCES member (certification_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
