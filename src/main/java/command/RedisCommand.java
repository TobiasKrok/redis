package command;

import args.Rawable;
import core.RedisContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RedisCommand {

    public abstract Rawable execute(List<String> args, RedisContext redisContext);



}
