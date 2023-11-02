#!/bin/bash
docker image rm olezhuravlev/paccounts-backend:0.0.1
docker build -t olezhuravlev/paccounts-backend:0.0.1 .
#docker login
#docker push olezhuravlev/paccounts-backend:0.0.1
