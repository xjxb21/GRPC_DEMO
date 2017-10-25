package com.xiao.grpc;

import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:
 * User: xiaojixiang
 * Date: 2017/10/25
 * Version: 1.0
 */

public class HelloGRPC_Client {

    public static void main(String[] args) throws InterruptedException {

        ManagedChannel channel = null;

        ExecutorService pool = Executors.newFixedThreadPool(3);

        try {
            //1.创建channel
            channel = ManagedChannelBuilder.forAddress("localhost", 12022).usePlaintext(true).build();    //明文发送

            //2.调用 block stub方法,生成stub
//            GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);
//            HelloReply helloReply = blockingStub.sayHello(HelloRequest.newBuilder().setName("hello").build());
//            System.out.println("receive from grpc server:" + helloReply.getMessage());

            //2.调用 noBlock stub方法
            GreeterGrpc.GreeterFutureStub futureStub = GreeterGrpc.newFutureStub(channel);
            final ListenableFuture<HelloReply> replyListenableFuture = futureStub.sayHello(HelloRequest.newBuilder().setName("hello-2").build());
            System.out.println("trace here!");

            replyListenableFuture.addListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        HelloReply helloReply = replyListenableFuture.get();
                        System.out.println("receive from grpc server:" + helloReply.getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }, pool);

            Thread.sleep(5000);
        } finally {
            if (pool != null) {
                pool.shutdownNow();
            }
            if (channel != null) {
                channel.shutdown();
            }
        }
    }
}
