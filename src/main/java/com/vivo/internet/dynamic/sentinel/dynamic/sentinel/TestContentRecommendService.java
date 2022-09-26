package com.vivo.internet.dynamic.sentinel.dynamic.sentinel;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.vivo.internet.content.recommend.facade.content.ContentRecommendFacade;
import com.vivo.internet.content.recommend.facade.content.model.ContentRecommendRespItem;
import com.vivo.internet.content.recommend.facade.content.param.ContentRecommendRequestParam;
import com.vivo.internet.content.recommend.facade.model.Result;
import com.vivo.internet.content.recommend.facade.sentinel.test.SentinelParamPriorityTestFacade;
import com.vivo.internet.recommender.common.model.request.RecommendRequest;
import com.vivo.internet.recommender.common.model.request.User;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

/**
 * @Author 11123357
 * @Date 2022/9/26 11:21
 * @Version 1.0
 */
@Service
public class TestContentRecommendService {


    private static volatile AtomicInteger methodCount = new AtomicInteger();

    public static final String PARAM_A = "test.yq";
    public static final String PARAM_B = "cms.test";
    public static final String PARAM_C = "gamecenter.station.video";

    private static AtomicInteger[] pass = new AtomicInteger[3];
    private static AtomicInteger[] block = new AtomicInteger[3];
    private static AtomicInteger total = new AtomicInteger();

    private static Map<String, AtomicInteger> passMap = new HashMap<>();
    private static Map<String, AtomicInteger> blockMap = new HashMap<>();
    private static Map<String, AtomicInteger> totalMap = new HashMap<>();

    static {
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

    @DubboReference(version = "1.0.0")
    private ContentRecommendFacade contentRecommendFacade;


    public String testMehod(){
        //每秒数据统计
        tick();

        //执行
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

        ContentRecommendRequestParam request1 = new ContentRecommendRequestParam();
        request1.setCacheMinutes(0);
        request1.setContentPoolId((long)384);
        request1.setRecordLimit(new Integer(10));
        request1.setRecordStart(new Integer(0));

        User userInfo1 = new User();
        userInfo1.setImei("800000000001078");
        request1.setUserInfo(userInfo1);
        request1.setScene(PARAM_A);
        Thread t1 = new Thread(new TestContentRecommendService.RunTask1(request1));
        t1.setName("simulate-traffic-Task");
        t1.start();
        Thread t11 = new Thread(new TestContentRecommendService.RunTask1(request1));
        t11.setName("simulate-traffic-Task");
        t11.start();
        Thread t111 = new Thread(new TestContentRecommendService.RunTask1(request1));
        t111.setName("simulate-traffic-Task");
        t111.start();
        Thread t1111 = new Thread(new TestContentRecommendService.RunTask1(request1));
        t1111.setName("simulate-traffic-Task");
        t1111.start();
        Thread t11111 = new Thread(new TestContentRecommendService.RunTask1(request1));
        t11111.setName("simulate-traffic-Task");
        t11111.start();
        Thread t111111 = new Thread(new TestContentRecommendService.RunTask1(request1));
        t111111.setName("simulate-traffic-Task");
        t111111.start();
        Thread t1111111 = new Thread(new TestContentRecommendService.RunTask1(request1));
        t1111111.setName("simulate-traffic-Task");
        t1111111.start();


        ContentRecommendRequestParam request2 = new ContentRecommendRequestParam();
        request2.setCacheMinutes(0);
        request2.setContentPoolId((long)384);
        request2.setRecordLimit(new Integer(10));
        request2.setRecordStart(new Integer(0));

        User userInfo2 = new User();
        userInfo2.setImei("800000000001078");
        request2.setUserInfo(userInfo2);
        request2.setScene(PARAM_B);
        Thread t2 = new Thread(new TestContentRecommendService.RunTask1(request2));
        t2.setName("simulate-traffic-Task");
        t2.start();
        Thread t22 = new Thread(new TestContentRecommendService.RunTask1(request2));
        t22.setName("simulate-traffic-Task");
        t22.start();
        Thread t222 = new Thread(new TestContentRecommendService.RunTask1(request2));
        t222.setName("simulate-traffic-Task");
        t222.start();

        ContentRecommendRequestParam request3 = new ContentRecommendRequestParam();
        request3.setCacheMinutes(0);
        request3.setContentPoolId((long)384);
        request3.setRecordLimit(new Integer(10));
        request3.setRecordStart(new Integer(0));

        User userInfo3 = new User();
        userInfo3.setImei("800000000001078");
        request3.setUserInfo(userInfo3);
        request3.setScene(PARAM_C);
        Thread t3 = new Thread(new TestContentRecommendService.RunTask1(request3));
        t3.setName("simulate-traffic-Task");
        t3.start();
        Thread t33 = new Thread(new TestContentRecommendService.RunTask1(request3));
        t33.setName("simulate-traffic-Task");
        t33.start();
        Thread t333 = new Thread(new TestContentRecommendService.RunTask1(request3));
        t333.setName("simulate-traffic-Task");
        t333.start();
    }

    static class TimerTask implements Runnable {

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            System.out.println("begin to statistic!!!");

            long oldTotal = 0;
            long oldPass[] = new long[3];
            long oldBlock[] = new long[3];
            while (!stop) {
                //每秒统计一次
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }

                long globalTotal = total.get();
                long oneSecondTotal = globalTotal - oldTotal;
                oldTotal = globalTotal;

                long aPass = passMap.get(PARAM_A).get();
                long aOneSecondPass = aPass - oldPass[0];
                oldPass[0] = aPass;

                long bPass = passMap.get(PARAM_B).get();
                long bOneSecondPass = bPass - oldPass[1];
                oldPass[1] = bPass;

                long cPass = passMap.get(PARAM_C).get();
                long cOneSecondPass = cPass - oldPass[2];
                oldPass[2] = cPass;



                long aBlock = blockMap.get(PARAM_A).get();
                long aOneSecondBlock = aBlock - oldBlock[0];
                oldBlock[0] = aBlock;

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

        private ContentRecommendRequestParam contentRecommendRequestParam;

        public RunTask1(ContentRecommendRequestParam contentRecommendRequestParam){
            this.contentRecommendRequestParam = contentRecommendRequestParam;
        }
        @Override
        public void run() {
            while (!stop) {
                try {
                    //entry = SphU.entry(RESOURCE,  EntryType.IN, 1, PARAM_A);
                    long timeStart = System.currentTimeMillis();
                    Result<ContentRecommendRespItem> result = contentRecommendFacade.contentRecommend(contentRecommendRequestParam);
                    //System.out.println("_______________________result:" + result.getCode());
                    //System.out.println("time:" + (System.currentTimeMillis() - timeStart));
                    if ((result.getCode().intValue() == 10008) || (result.getCode().intValue() == 10002)){
                        AtomicInteger blockCount = blockMap.get(contentRecommendRequestParam.getScene()) != null ? blockMap.get(contentRecommendRequestParam.getScene()) : new AtomicInteger();
                        blockCount.addAndGet(1);
                        blockMap.put(contentRecommendRequestParam.getScene(), blockCount);
                    }else {
                        AtomicInteger passCount = passMap.get(contentRecommendRequestParam.getScene()) != null ? passMap.get(contentRecommendRequestParam.getScene()) : new AtomicInteger();
                        passCount.addAndGet(1);
                        passMap.put(contentRecommendRequestParam.getScene(), passCount);
                    }
                } catch (Exception e2) {
                    System.out.println("------------------------异常----------------------------------");
                    // biz exception
                } finally {
                    //total.incrementAndGet();
                    AtomicInteger totalCount = totalMap.get(contentRecommendRequestParam.getScene()) != null ? totalMap.get(contentRecommendRequestParam.getScene()) : new AtomicInteger();
                    totalCount.addAndGet(1);
                    totalMap.put(contentRecommendRequestParam.getScene(), totalCount);
                    total.addAndGet(1);
                }

                if (seconds > 50) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                        //TimeUnit.MILLISECONDS.sleep(random2.nextInt(50));
                    } catch (InterruptedException e) {
                        // ignore
                    }
                } else {
                    continue;
                }
            }
        }
    }

}
