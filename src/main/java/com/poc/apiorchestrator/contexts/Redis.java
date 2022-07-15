package com.poc.apiorchestrator.contexts;

import com.lambdaworks.redis.*;


public class Redis {
    public RedisConnection connect(){
        RedisClient redisClient = new RedisClient(
                RedisURI.create(getHostRedis()));
        RedisConnection<String, String> connection = redisClient.connect();
        System.out.println("Connected to Redis");
        return connection;
    }

    private String getHostRedis(){
        if (System.getenv("REDIS_HOST") != null
                && !System.getenv("REDIS_HOST").isEmpty()){
            return System.getenv("REDIS_HOST");
        }else{
            return "redis://localhost:6379";
        }
    }

}
