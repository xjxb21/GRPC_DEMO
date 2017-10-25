package com.xiao.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloGRPC_Server {

    private final Server server;

    public HelloGRPC_Server(int port) {
        //this.server = ServerBuilder.forPort(port).directExecutor().addService(new GreeterService()).build();

        ExecutorService pool = Executors.newFixedThreadPool(3);
        this.server = ServerBuilder.forPort(port).executor(pool).addService(new GreeterService()).build();
    }

    private void start() throws IOException {
        this.server.start();
    }

    private void stop() throws InterruptedException {
        this.server.shutdownNow();
    }

    private void awaitTermination() throws InterruptedException {
        this.server.awaitTermination();
    }

    public static void main(String[] args) {

        final HelloGRPC_Server grpc_server = new HelloGRPC_Server(12022);

        //1.定义Server,需要实现Service的 XXXImplBase类
        //server = ServerBuilder.forPort(12022).addService(new GreeterService()).build();

        try {
            grpc_server.start();
            System.out.println("grpc server start on port " + grpc_server.server.getPort());
            //hook
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    // Use stderr here since the logger may has been reset by its JVM shutdown hook.
                    System.err.println("*** shutting down gRPC server since JVM is shutting down");
                    try {
                        grpc_server.stop();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.err.println("*** server shut down");
                }
            });

            grpc_server.awaitTermination();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //接口逻辑实现部分
    private static class GreeterService extends GreeterGrpc.GreeterImplBase {
        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {

            //super.sayHello(request, responseObserver);
            //1.接收到的请求信息
            String requestName = request.getName();
            System.out.println(Thread.currentThread().getName() + " >>> receive：" + requestName);

            //2
            responseObserver.onNext(HelloReply.newBuilder().setMessage("I'm server,hello client.").build());
            //responseObserver.onNext(HelloReply.parseFrom());
            responseObserver.onCompleted();
        }

        @Override
        public void sayHelloAgain(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            super.sayHelloAgain(request, responseObserver);
        }
    }
}
