#!/bin/bash
docker exec -it mongodb mongoexport --db=paccounts --collection=currencies --out=currencies.json
docker exec -it mongodb mongoexport --db=paccounts --collection=accounts --out=accounts.json
docker exec -it mongodb mongoexport --db=paccounts --collection=households --out=households.json
docker exec -it mongodb mongoexport --db=paccounts --collection=operations --out=operations.json

docker exec -it stupefied_northcutt mongoexport --db=paccounts_test --collection=currencies --out=currencies.json
docker exec -it stupefied_northcutt mongoexport --db=paccounts_test --collection=accounts --out=accounts.json
docker exec -it stupefied_northcutt mongoexport --db=paccounts_test --collection=households --out=households.json
docker exec -it stupefied_northcutt mongoexport --db=paccounts_test --collection=operations --out=operations.json
