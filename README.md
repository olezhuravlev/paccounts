## How to:

1. Start project:
````bash
docker-compose up -d
````

2. Apply to REST-interfact, e.g. to 'household' collection:
````bash
$ curl -X POST localhost:8080/household
$ curl -X POST "localhost:8080/household?id=64bf597c9c1b68482efd811d"
$ curl -X PUT "localhost:8080/household" -H "Content-Type:application/json" -d '{"title":"Test Household 3","description": "Household #3 for using in tests"}'
$ curl -X DELETE "localhost:8080/household?id=64bf597c9c1b68482efd8113"

etc.

Get some server params:
$ localhost:8080/sysinfo
````

> #### Also collections 'account', 'currency' and 'operation' available for [requests](./backend/src/main/java/org/paccounts/controller).
