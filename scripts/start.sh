#!/bin/bash
nohup java -classpath ../king/target/king-1.0-jar-with-dependencies.jar top.shauna.dfs.KingStarter -Dproperties=../config/king1.properties >../log/king1.log 2>&1 &

nohup java -classpath ../king/target/king-1.0-jar-with-dependencies.jar top.shauna.dfs.KingStarter -Dproperties=../config/king2.properties >../log/king2.log 2>&1 &

sleep 60

nohup java -classpath ../queen/target/queen-1.0-jar-with-dependencies.jar top.shauna.dfs.QueenStarter -Dproperties=../config/queen.properties >../log/queen.log 2>&1 &

nohup java -classpath ../soldier/target/soldier-1.0-jar-with-dependencies.jar top.shauna.dfs.SoldierStarter -Dproperties=../config/soldier1.properties >../log/soldier1.log 2>&1 &

nohup java -classpath ../soldier/target/soldier-1.0-jar-with-dependencies.jar top.shauna.dfs.SoldierStarter -Dproperties=../config/soldier2.properties >../log/soldier2.log 2>&1 &

nohup java -classpath ../soldier/target/soldier-1.0-jar-with-dependencies.jar top.shauna.dfs.SoldierStarter -Dproperties=../config/soldier3.properties >../log/soldier3.log 2>&1 &