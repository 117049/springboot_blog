package com.xxx.blog.common.cache;


import com.alibaba.fastjson.JSON;
import com.xxx.blog.vo.params.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;


@Aspect
@Component
@Slf4j
public class CacheAllAspect {

    private SingleInstanceAtomic singleNum = SingleInstanceAtomic.getInstance();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Pointcut("@annotation(com.xxx.blog.common.cache.CacheAll)")
    public void pt(){}

    @Around("pt()")
    public Object around(ProceedingJoinPoint pjp){
        try {
            Signature signature = pjp.getSignature();
            //类名
            String className = pjp.getTarget().getClass().getSimpleName();
            //调用的方法名
            String methodName = signature.getName();


            Class[] parameterTypes = new Class[pjp.getArgs().length];
            Object[] args = pjp.getArgs();
            //参数
            String params = "";
            for(int i=0; i<args.length; i++) {
                if(args[i] != null) {
                    params += JSON.toJSONString(args[i]);
                    parameterTypes[i] = args[i].getClass();
                }else {
                    parameterTypes[i] = null;
                }
            }
            if (StringUtils.isNotEmpty(params)) {
                //加密 以防出现key过长以及字符转义获取不到的情况
                params = DigestUtils.md5Hex(params);
            }
            Method method = pjp.getSignature().getDeclaringType().getMethod(methodName, parameterTypes);
            //获取Cache注解
            Cache annotation = method.getAnnotation(Cache.class);
            //缓存过期时间

            //先从redis获取

            Long id = (Long) args[0];
            if(args.length >= 1){
                if(methodName.equals("findArticleById") && redisTemplate.hasKey("Article_" + String.valueOf(args[0]))){

                    if(redisTemplate.hasKey("Article_" + String.valueOf(id))){
                        log.info("进入了方法。。。。。。。。。。。。。。。。。阅读全文");
                        String redisAllArticle = redisTemplate.opsForValue().get("Article_" + String.valueOf(id));
                        //更新zset表
                        redisTemplate.opsForZSet().incrementScore("sort_set", String.valueOf(id),Double.valueOf(-1000));
                        return JSON.parseObject(redisAllArticle, Result.class);
                    }
                }
            }

            Object proceed = pjp.proceed();
            //此时没有查询到缓存
            //循环遍历数据库查询到的数据，将数据添加到缓存，如果已存在则忽略

            //当查看文章详情时添加文章所有信息到redis
            String name = "Article_" + String.valueOf(id);

            //如果为空，则不进行长度判断删除
            Long sort_set = redisTemplate.opsForZSet().zCard("sort_set");
            if(sort_set > 2){
                //此时zset长度超出限制，最高点
                ZSetOperations.TypedTuple<String> sort_set1 = redisTemplate.opsForZSet().popMax("sort_set");
                String str = String.valueOf(sort_set1.getValue());
                redisTemplate.delete("Article_" + str);
            }

            //添加到redis
            redisTemplate.opsForValue().set(name, JSON.toJSONString(proceed));
            int num = singleNum.getNum();
            redisTemplate.opsForZSet().add("sort_set", String.valueOf(id),Double.valueOf(num));
            return proceed;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return Result.fail(-999,"系统错误");
    }

}
