#!/bin/bash
mongoimport --db paccounts --collection currencies --type json --file /initdb/data/010_currencies.json --jsonArray
mongoimport --db paccounts --collection accounts --type json --file /initdb/data/020_accounts.json --jsonArray
mongoimport --db paccounts --collection households --type json --file /initdb/data/030_households.json --jsonArray
mongoimport --db paccounts --collection operations --type json --file /initdb/data/040_operations.json --jsonArray
