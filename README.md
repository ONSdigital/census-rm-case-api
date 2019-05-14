# census-rm-case-api
This service is for use by the Contact Centre and provides a read-only API into the new (v2) case service.

# How to run
The Case Api service requires a Postgres instance to be running which contains the new casev2 schema.
Postgres can be started using either census-rm-docker-dev or "docker-compose up -d postgres-database"

# How to test
make test
