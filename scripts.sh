## 개발용 DB
docker run -p 5432:5432 --name study-db -e POSTGRES_USER=study -e POSTGRES_PASSWORD=study -e POSTGRES_DB=study -d postgres

## 테스트용 DB
docker run -p 5432:5432 --name study-testdb -e POSTGRES_USER=studytest -e POSTGRES_PASSWORD=studytest -e POSTGRES_DB=studytest -d postgres

##
docker run -p 5432:5432 --name postgres -e POSTGRES_PASSWORD=postgres -d postgres
docker exec -it postgres /bin/bash
psql -U postgres
create user study password 'study' superuser;
create user studytest password 'studytest' superuser;
create database study owner study;
create database studytest owner studytest;
