CREATE TABLE IF NOT EXISTS "${tablePrefix}some_list" (
    "uuid" ${uuidType} NOT NULL,
    "name" tinytext NOT NULL,
    PRIMARY KEY ("uuid")
)${tableDefaults};


CREATE TABLE IF NOT EXISTS "${tablePrefix}test" (
    "name" tinytext NOT NULL
)${tableDefaults};
