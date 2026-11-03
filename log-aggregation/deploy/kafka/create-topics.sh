kafka-topics --bootstrap-server kafka-kraft:29092 --create --if-not-exists \
  --topic api-log --partitions 10 --replication-factor 1 \
  --config cleanup.policy=delete \
  --config retention.ms=172800000 \
  --config min.cleanable.dirty.ratio=0.1 \
  --config segment.ms=600000 \
  --config segment.bytes=67108864 \
  --config delete.retention.ms=86400000