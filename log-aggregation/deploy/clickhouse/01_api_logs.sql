CREATE TABLE IF NOT EXISTS api_logs_queue (
    event_time      DateTime64(3, 'Asia/Tashkent'),
    trace_id        String,
    span_id         String,
    parent_span_id  String,
    direction       Enum8('IN' = 1, 'OUT' = 2),
    service         LowCardinality(String),
    username        String,
    client_ip       String,
    mac_address     Nullable(String),
    endpoint        LowCardinality(String),
    params          Map(String, String),
    http_method     LowCardinality(String),
    status_code     UInt16,
    success         Bool,
    duration_ms     UInt32,
    attempt         UInt8,
    error_message   Nullable(String),
    request_body    Nullable(String),
    response_body   Nullable(String)
) ENGINE = Kafka
SETTINGS kafka_broker_list          = '127.0.0.1:29092',
         kafka_topic_list           = 'api-log',
         kafka_group_name           = 'clickhouse-api-log',
         kafka_format               = 'JSONEachRow',
         kafka_num_consumers        = 4,
         kafka_max_block_size       = 1048576,
         kafka_skip_broken_messages = 100,
         date_time_input_format     = 'best_effort',
         input_format_null_as_default        = 1,
         input_format_skip_unknown_fields    = 1;

CREATE TABLE IF NOT EXISTS api_logs (
    event_time      DateTime64(3, 'Asia/Tashkent'),
    trace_id        String,
    span_id         String,
    parent_span_id  String DEFAULT '',
    direction       Enum8('IN' = 1, 'OUT' = 2),
    service         LowCardinality(String),
    username        String,
    client_ip       String,
    mac_address     Nullable(String),
    endpoint        LowCardinality(String),
    params          Map(String, String) CODEC(ZSTD(3)),
    http_method     LowCardinality(String),
    status_code     UInt16,
    success         Bool,
    duration_ms     UInt32,
    attempt         UInt8,
    error_message   Nullable(String) CODEC(ZSTD(3)),
    request_body    Nullable(String) CODEC(ZSTD(3)),
    response_body   Nullable(String) CODEC(ZSTD(3)),

    INDEX idx_trace  trace_id       TYPE bloom_filter(0.01) GRANULARITY 4,
    INDEX idx_parent parent_span_id TYPE bloom_filter(0.01) GRANULARITY 4,
    INDEX idx_ip     client_ip      TYPE bloom_filter(0.01) GRANULARITY 4,
    INDEX idx_user   username       TYPE bloom_filter(0.01) GRANULARITY 4
) ENGINE = MergeTree
PARTITION BY toYYYYMMDD(event_time)
ORDER BY (service, endpoint, success, event_time)
TTL toDateTime(event_time) + INTERVAL 90 DAY
SETTINGS index_granularity = 8192;


CREATE MATERIALIZED VIEW IF NOT EXISTS api_logs_mv TO api_logs AS
SELECT
    event_time, trace_id, span_id, parent_span_id, direction, service,
    username, client_ip, mac_address, endpoint, params, http_method, status_code,
    success, duration_ms, attempt, error_message, request_body, response_body
FROM api_logs_queue;
