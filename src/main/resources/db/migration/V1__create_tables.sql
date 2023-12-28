CREATE TABLE IF NOT EXISTS ${tablePrefix}some_list (
    uuid ${uuidType} NOT NULL,
    "name" TINYTEXT NOT NULL,
    PRIMARY KEY (uuid)
)${tableDefaults};


CREATE TABLE IF NOT EXISTS ${tablePrefix}test (
    "name" TINYTEXT NOT NULL
)${tableDefaults};
