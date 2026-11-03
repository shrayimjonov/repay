ALTER TABLE epos DROP CONSTRAINT onlyoneepostoonebranch;

ALTER TABLE epos ADD CONSTRAINT onlyoneepostoonebranchandtype
    UNIQUE (branch, processing, service, exceptional);