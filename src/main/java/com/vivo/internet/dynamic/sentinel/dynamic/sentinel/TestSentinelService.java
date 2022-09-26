package com.vivo.internet.dynamic.sentinel.dynamic.sentinel;


import com.vivo.internet.content.recommend.facade.sentinel.test.SentinelParamPriorityTestFacade;
import com.vivo.internet.recommender.common.model.request.RecommendRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @className:
 * @description:
 * @author: gaomeng(11103466)
 * @create: 2022-09-24 16:10
 * @version:
 **/
@Service
public class TestSentinelService {

    //private static volatile AtomicInteger methodCount = new AtomicInteger();
    //定义方法里同一个入参的三种不同的具体参数值，其可以对应线上三种不同的业务场景，
    //一个dubbo接口里的方法在给控制业务场景的参数(sence值)传入不同的值时就对应不同的业务场景，传入gamecenter时该方法就是给游戏中心提供数据，传入shop时该方法就是给商店提供数据。
    public static final String PARAM_A = "paramA";
    public static final String PARAM_B = "paramB";
    public static final String PARAM_C = "paramC";

    //定义统计流量通过、被拦截、及总流量(通过+拦截）
    private static AtomicInteger[] pass = new AtomicInteger[3]; //AtomicInteger是专门在多线程中定义int型参数时使用的，等同于Integer
    private static AtomicInteger[] block = new AtomicInteger[3];
    private static AtomicInteger total = new AtomicInteger();

    private static Map<String, AtomicInteger> passMap = new HashMap<>();
    private static Map<String, AtomicInteger> blockMap = new HashMap<>();
    private static Map<String, AtomicInteger> totalMap = new HashMap<>();

    static {
        //定义同一个参数对应的三种参数值的流量通过、被拦截时的记录参数；
        pass[0] = new AtomicInteger();
        pass[1] = new AtomicInteger();
        pass[2] = new AtomicInteger();
        block[0] = new AtomicInteger();
        block[1] = new AtomicInteger();
        block[2] = new AtomicInteger();

        passMap.put(PARAM_A, new AtomicInteger());
        passMap.put(PARAM_B, new AtomicInteger());
        passMap.put(PARAM_C, new AtomicInteger());
        blockMap.put(PARAM_A, new AtomicInteger());
        blockMap.put(PARAM_B, new AtomicInteger());
        blockMap.put(PARAM_C, new AtomicInteger());
        totalMap.put(PARAM_A, new AtomicInteger());
        totalMap.put(PARAM_B, new AtomicInteger());
        totalMap.put(PARAM_C, new AtomicInteger());
    }

    private static volatile boolean stop = false;

    private static final int threadCount = 32;

    private static int seconds = 100;

    //*****调用dubbo接口的方式：在pom文件中配置dubbo的jar包，使用@DubboReference 标签引入对应的dubbo接口******
    @DubboReference(version = "1.0.0")
    private SentinelParamPriorityTestFacade sentinelParamPriorityTestFacade;


    public String testMehod(){
        //每秒数据统计,统计每秒内接口总流量，各个参数场景的通过的流量及被拦截的流量
        tick();

        //执行。为每个参数启动对应的线程，生成对应的接口流量
        simulateTraffic();
        return "success";
    }



    private static void tick() {
        Thread timer = new Thread(new TimerTask());
        timer.setName("sentinel-timer-task");
        timer.start();
    }


    public void simulateTraffic() {
        /*
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(new QpsFlowTest.RunTask());
            t.setName("simulate-traffic-Task");
            t.start();
        }
        */
        RecommendRequest request1 = new RecommendRequest();
        request1.setScene(PARAM_A);
        //给参数的每一个入参值定义三个线程，这样就实现了三个线程可同时对同一个入参值提供流量，达到提升qps的目的
        Thread t1 = new Thread(new RunTask1(request1));
        t1.setName("simulate-traffic-Task");
        t1.start();
        Thread t11 = new Thread(new RunTask1(request1));
        t11.setName("simulate-traffic-Task");
        t11.start();
        Thread t111 = new Thread(new RunTask1(request1));
        t111.setName("simulate-traffic-Task");
        t111.start();


        RecommendRequest request2 = new RecommendRequest();
        request2.setScene(PARAM_B);
        Thread t2 = new Thread(new RunTask1(request2));
        t2.setName("simulate-traffic-Task");
        t2.start();
        Thread t22 = new Thread(new RunTask1(request2));
        t22.setName("simulate-traffic-Task");
        t22.start();
        Thread t222 = new Thread(new RunTask1(request2));
        t222.setName("simulate-traffic-Task");
        t222.start();

        RecommendRequest request3 = new RecommendRequest();
        request3.setScene(PARAM_C);
        Thread t3 = new Thread(new RunTask1(request3));
        t3.setName("simulate-traffic-Task");
        t3.start();
        Thread t33 = new Thread(new RunTask1(request3));
        t33.setName("simulate-traffic-Task");
        t33.start();
        Thread t333 = new Thread(new RunTask1(request3));
        t333.setName("simulate-traffic-Task");
        t333.start();
    }

    static class TimerTask implements Runnable {
        //这里面的run()是给simulateTraffic()方法里启动的所有线程调用的
        @Override
        public void run() {
            long start = System.currentTimeMillis();
            System.out.println("begin to statistic!!!");

            //定义初始总流量、初始通过流量及初始被拦截的流量
            long oldTotal = 0;
            long oldPass[] = new long[3];
            long oldBlock[] = new long[3];
            while (!stop) {  //共统计100秒
                //每秒统计一次
                try {
                    TimeUnit.SECONDS.sleep(1); //定义统计间隔是1s，即每秒统计一次
                } catch (InterruptedException e) {
                }

                long globalTotal = total.get(); //获取截止当前的总流量
                long oneSecondTotal = globalTotal - oldTotal; //当前1秒内的总流量=截止当前的总流量-上一秒总流量；
                oldTotal = globalTotal; //将上1秒的总流量更新为当前的总流量，用于下一秒使用

                long aPass = passMap.get(PARAM_A).get();
                long aOneSecondPass = aPass - oldPass[0];
                oldPass[0] = aPass;

                long bPass = passMap.get(PARAM_B).get();
                long bOneSecondPass = bPass - oldPass[1];
                oldPass[1] = bPass;

                long cPass = passMap.get(PARAM_C).get();
                long cOneSecondPass = cPass - oldPass[2];
                oldPass[2] = cPass;



                long aBlock = blockMap.get(PARAM_A).get(); //获取场景paramA截止当前被拦截的总流量
                long aOneSecondBlock = aBlock - oldBlock[0]; //场景paramA当前1秒内被拦截的流量= 场景paramA被拦截的总流量-场景paramA上1秒被拦截的总流量
                oldBlock[0] = aBlock; //将场景paramA上1秒被拦截的总流量更新为场景paramA当前被拦截的总流量，用于下一秒使用

                long bBlock = blockMap.get(PARAM_B).get();
                long bOneSecondBlock = bBlock - oldBlock[1];
                oldBlock[1] = bBlock;

                long cBlock =blockMap.get(PARAM_C).get();
                long cOneSecondBlock = cBlock - oldBlock[2];
                oldBlock[2] = cBlock;

                System.out.println(seconds + " send qps is: " + oneSecondTotal);
                System.out.println(System.currentTimeMillis() + ", total:" + oneSecondTotal
                        + ", passA:" + aOneSecondPass + ", blockA:" + aOneSecondBlock
                        + ", passB:" + bOneSecondPass + ", blockB:" + bOneSecondBlock
                        + ", passC:" + cOneSecondPass + ", blockC:" + cOneSecondBlock

                );
                if (seconds-- <= 0) {
                    stop = true;
                }
            }
            long cost = System.currentTimeMillis() - start;
            System.out.println("time cost: " + cost + " ms");
            System.out.println("total:" + total.get()
                    + ", passA:" + pass[0].get() + ", blockA:" + block[0].get()
                    + ", passB:" + pass[1].get() + ", blockB:" + block[1].get()
                    + ", passC:" + pass[2].get() + ", blockC:" + block[2].get()
            );
            System.exit(0);
        }
    }


    class RunTask1 implements Runnable {

        private RecommendRequest request;

        public RunTask1(RecommendRequest request){
            this.request = request;
        }
        @Override
        public void run() {
            while (!stop) {
                try {
                    String result = sentinelParamPriorityTestFacade.sentinelParamPriorityTest("111",request);
                    //System.out.println("time:" + (System.currentTimeMillis() - timeStart));
                    //调用的dubbo接口里的方法返回了success，则表示该条流量通过了
                    if(StringUtils.equals(result, "success")){
                        AtomicInteger passCount = passMap.get(request.getScene()) != null ? passMap.get(request.getScene()) : new AtomicInteger(); //取出之前scene已经通过的流量数
                        //统计当前scene的场景的通过的流量数加1
                        passCount.addAndGet(1);  //在多线程中使用addAndGet()方法进行+1操作，保证多线程安全。等同于totle = totle+1
                        passMap.put(request.getScene(), passCount);
                    }
                    //调用的dubbo接口里的方法返回了failed，则表示该条流量被拦截了
                    else if(StringUtils.equals(result, "failed")){
                        AtomicInteger blockCount = blockMap.get(request.getScene()) != null ? blockMap.get(request.getScene()) : new AtomicInteger();
                        blockCount.addAndGet(1);
                        blockMap.put(request.getScene(), blockCount);
                    }
                    else {
                        System.out.println("------------------------未知参数值----------------------------------");
                    }
                } catch (Exception e2) {
                    System.out.println("------------------------异常----------------------------------");
                    // biz exception
                } finally {
                    //统计总的流量=通过的流量+被拦截的流量
                    AtomicInteger totalCount = totalMap.get(request.getScene()) != null ? totalMap.get(request.getScene()) : new AtomicInteger(); //取出之前的总流量数
                    totalCount.addAndGet(1);//该场景下的总流量数加1
                    totalMap.put(request.getScene(), totalCount);//将场景及对应该场景下的总流量数放入map中
                    total.addAndGet(1); //调用接口的总流量加1(不区分场景也不关注是否通过)
                }

                if (seconds > 50) {  //当前50s让线程调用先睡眠100ms，降低qps的值
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                        //TimeUnit.MILLISECONDS.sleep(random2.nextInt(50));
                    } catch (InterruptedException e) {
                        // ignore
                    }
                } else { //当最后50s让线程调用不睡眠，提升qps的值，达到qps流量突增的目的。
                    continue;
                }
            }
        }
    }

}



/*
* sentinelParamPriorityTestFacade.sentinelParamPriorityTest()方法实现逻辑：
@Slf4j
@DubboService(version = "1.0.0")
public class SentinelParamPriorityTestServiceImpl implements SentinelParamPriorityTestFacade {

    @Override
    @SentinelResource(value = "sentinelParamPriorityTest", fallback = "sentinelParamPriorityFallback")
    public String sentinelParamPriorityTest(String param1, RecommendRequest request) {
        if (request == null) {
            return "param null";
        }
        log.info("sentinelParamPriorityTest-success,scene:{}", request.getScene());
        return "success";
    }

    public String sentinelParamPriorityFallback(RecommendRequest request) {
        log.info("sentinelParamPriorityTest-failed");
        return "failed";
    }
}
*/

/*
*  运行结果：
    begin to statistic!!!
        100 send qps is: 63
        1664191855838, total:63, passA:21, blockA:0, passB:17, blockB:4, passC:17, blockC:4
        99 send qps is: 63
        1664191856839, total:63, passA:21, blockA:0, passB:0, blockB:21, passC:1, blockC:20
        98 send qps is: 72
        1664191857839, total:72, passA:20, blockA:4, passB:0, blockB:24, passC:0, blockC:24
        97 send qps is: 63
        1664191858839, total:63, passA:19, blockA:2, passB:0, blockB:21, passC:0, blockC:21
        96 send qps is: 33
*/

