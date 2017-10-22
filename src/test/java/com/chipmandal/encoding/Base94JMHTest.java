package com.chipmandal.encoding;

import org.openjdk.jmh.annotations.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class Base94JMHTest {
    Base94 base94 = BaseEncoding.Base94;
    Base64.Encoder base64 = Base64.getEncoder();
    Base64.Decoder base64Decoder = Base64.getDecoder();


    @State(Scope.Thread)
    public static class ThreadState {
        byte [] input = new byte[2000];
        public static SecureRandom secureRandom ;
        static {
            try {
                secureRandom = SecureRandom.getInstanceStrong();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        }
        {
            secureRandom.nextBytes(input);
        }
    }


    @Benchmark
    @Warmup(iterations = 0, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @BenchmarkMode(Mode.SingleShotTime)
    public String measureEncode(ThreadState threadState) {
        return base94.encodeString(threadState.input);
    }

    @Benchmark
    @Warmup(iterations = 0, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @BenchmarkMode(Mode.SingleShotTime)
    public byte[] measureEncodeDecode(ThreadState threadState) {
        return base94.decodeString(base94.encodeString(threadState.input));
    }

    @Benchmark
    @Warmup(iterations = 0, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @BenchmarkMode(Mode.SingleShotTime)
    public String measureEncodeBase64(ThreadState threadState) {
        return base64.encodeToString(threadState.input);
    }

    @Benchmark
    @Warmup(iterations = 0, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @BenchmarkMode(Mode.SingleShotTime)
    public byte[] measureEncodeDecodeBase64(ThreadState threadState) {
        return base64Decoder.decode(base64.encodeToString(threadState.input));
    }
}
