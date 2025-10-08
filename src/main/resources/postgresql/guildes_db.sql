CREATE TABLE IF NOT EXISTS "config"
(
    var_name  varchar(255) PRIMARY KEY,
    var_value json
);

CREATE TABLE IF NOT EXISTS "skill"
(
    code  varchar(255) PRIMARY KEY,
    label varchar(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "occupation"
(
    code  varchar(255) PRIMARY KEY,
    label varchar(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "environment"
(
    code  varchar(255) PRIMARY KEY,
    label varchar(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "season"
(
    code  varchar(255) PRIMARY KEY,
    label varchar(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "biome"
(
    id       uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name     varchar(255) NOT NULL UNIQUE,
    code_env varchar(255) REFERENCES "environment" (code)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS "map_tile"
(
    id       uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    x        bigint NOT NULL  DEFAULT 0,
    y        bigint NOT NULL  DEFAULT 0,
    id_biome uuid REFERENCES "biome" (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS "host"
(
    id         uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name       varchar(255) NOT NULL UNIQUE,
    ip_address inet         NOT NULL,
    port       int CHECK (port >= 49152 AND port <= 65535),
    UNIQUE (ip_address, port)
);

CREATE TABLE IF NOT EXISTS "trading_post"
(
    id         uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    name       varchar(255) NOT NULL UNIQUE,
    population int          NOT NULL DEFAULT 0 CHECK ( population >= 0 ),
    id_host    uuid         REFERENCES "host" (id)
                                ON UPDATE CASCADE
                                ON DELETE SET NULL,
    location   uuid         REFERENCES "map_tile" (id)
                                ON UPDATE CASCADE
                                ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS "resource_category"
(
    id   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name varchar(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "resource"
(
    id                   uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    name                 varchar(255) NOT NULL UNIQUE,
    mass                 int          NOT NULL DEFAULT 0 CHECK (mass >= 0),
    volume               int          NOT NULL DEFAULT 0 CHECK (volume >= 0),
    id_resource_category uuid REFERENCES "resource_category" (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS "trades"
(
    id_trading_post uuid REFERENCES "trading_post" (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    id_resource     uuid REFERENCES "resource" (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    quantity        smallint NOT NULL DEFAULT 0 CHECK ( quantity >= 0 ),
    price           smallint NOT NULL DEFAULT 0 CHECK ( price >= 0 ),
    date_expiry     date,
    PRIMARY KEY (id_trading_post, id_resource)
);

CREATE TABLE IF NOT EXISTS "stores"
(
    id_trading_post uuid REFERENCES "trading_post" (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    id_resource     uuid REFERENCES "resource" (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    quantity        smallint NOT NULL DEFAULT 0 CHECK ( quantity >= 0 ),
    PRIMARY KEY (id_trading_post, id_resource)
);

CREATE TABLE IF NOT EXISTS "produces"
(
    id_resource uuid REFERENCES "resource" (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    id_biome    uuid REFERENCES "biome" (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    code_season varchar(255) REFERENCES "season" (code)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    quantity    int NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    workers     int NOT NULL DEFAULT 0 CHECK (workers >= 0),

    PRIMARY KEY (id_resource, id_biome, code_season)
);

CREATE TABLE IF NOT EXISTS "exploits"
(
    id_trading_post uuid REFERENCES "trading_post" (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    id_biome        uuid REFERENCES "biome" (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    quantity        smallint CHECK (quantity > 0)
);

CREATE TABLE IF NOT EXISTS "transportation_type"
(
    code            varchar(255) PRIMARY KEY,
    label           varchar(255) NOT NULL UNIQUE,
    mass_capacity   int          NOT NULL DEFAULT 0 CHECK (mass_capacity >= 0),
    volume_capacity int          NOT NULL DEFAULT 0 CHECK (volume_capacity >= 0),
    life_points     smallint     NOT NULL DEFAULT 0 CHECK (life_points >= 0),
    max_speed       smallint     NOT NULL DEFAULT 0 CHECK ((max_speed > 0) AND (max_speed <= 1000)),
    code_env        varchar(255) REFERENCES "environment" (code)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS "caravan"
(
    id             uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name           varchar(255) NOT NULL UNIQUE,
    location       uuid         REFERENCES "map_tile" (id)
                                    ON UPDATE CASCADE
                                    ON DELETE SET NULL,
    id_destination uuid REFERENCES "trading_post" (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    id_host        uuid         REFERENCES "host" (id)
                                    ON UPDATE CASCADE
                                    ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS "transportation"
(
    id                  uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    life_points         smallint     NOT NULL DEFAULT 0 CHECK ( life_points >= 0),

    id_caravan          uuid         REFERENCES "caravan" (id)
                                         ON UPDATE CASCADE
                                         ON DELETE SET NULL,
    code_transport_type varchar(255) REFERENCES "transportation_type" (code)
                                         ON UPDATE CASCADE
                                         ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS "consumes"
(
    id_resource         uuid REFERENCES "resource" (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    code_transport_type varchar(255) REFERENCES "transportation_type" (code)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    PRIMARY KEY (id_resource, code_transport_type),

    quantity            smallint CHECK (quantity >= 0)
);

CREATE TABLE IF NOT EXISTS "transports"
(
    id_resource       uuid REFERENCES "resource" (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    id_transportation uuid REFERENCES "transportation" (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    PRIMARY KEY (id_resource, id_transportation),

    quantity          smallint CHECK (quantity >= 0)
);

CREATE TABLE IF NOT EXISTS "person"
(
    id              uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    name            varchar(255) NOT NULL UNIQUE,
    life_points     smallint     NOT NULL DEFAULT 0 CHECK (life_points >= 0),
    damage_min      smallint     NOT NULL DEFAULT 0 CHECK (damage_min >= 0),
    damage_max      smallint     NOT NULL DEFAULT 0 CHECK (damage_max >= damage_min),
    mood            smallint     NOT NULL DEFAULT 0 CHECK (mood >= 0 AND mood <= 100),
    hiring_bonus    smallint     NOT NULL DEFAULT 0 CHECK (hiring_bonus >= 0),
    salary          smallint     NOT NULL DEFAULT 0 CHECK (salary >= 0),
    participation   smallint     NOT NULL DEFAULT 0 CHECK (participation >= 0 AND participation <= 100),

    code_occupation varchar(255) REFERENCES "occupation" (code)
                                     ON UPDATE CASCADE
                                     ON DELETE SET NULL,
    id_caravan      uuid         REFERENCES "caravan" (id)
                                     ON UPDATE CASCADE
                                     ON DELETE SET NULL,
    id_trading_post uuid         REFERENCES "trading_post" (id)
                                     ON UPDATE CASCADE
                                     ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS "masters"
(
    id_person     uuid REFERENCES "person" (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    code_skill    varchar(255) REFERENCES "skill" (code)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    PRIMARY KEY (id_person, code_skill),

    mastery_level smallint NOT NULL DEFAULT 0 CHECK (mastery_level > 0 AND mastery_level <= 100)
);
