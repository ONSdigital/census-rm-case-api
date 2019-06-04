# census-rm-case-api
This service provides a read-only API for case details.

# How to run
The Case Api service requires a Postgres instance to be running which contains the new casev2 schema.
Postgres can be started using either census-rm-docker-dev or "docker-compose up -d postgres-database".

If you want to run on your local machine in a different timezone from UTC, you can force UTC by specifying `-Duser.timezone=UTC` as a JVM option.

# How to test
make test
