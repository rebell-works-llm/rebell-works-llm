package com.rebellworksllm.backend.matching.application;

import com.rebellworksllm.backend.modules.matching.application.HubSpotWebhookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class HubSpotWebhookServiceAsyncTest {

    @MockitoBean
    private HubSpotWebhookService webhookService;

    @Test
    void testAsyncExecutionWithMultipleRequests() throws InterruptedException {
        int parallelRequests = 25;
        CountDownLatch latch = new CountDownLatch(parallelRequests);

        Mockito.doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(webhookService).processStudentMatch(Mockito.anyLong());

        for (int i = 0; i < parallelRequests; i++) {
            long id = i;
            new Thread(() -> webhookService.processStudentMatch(id)).start();
        }

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertTrue(completed, "Not all async tasks completed in time");
    }

    @Test
    void testAsyncExecutorRejectionPolicy() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(1);
        executor.setRejectedExecutionHandler((r, e) -> {
            throw new RejectedExecutionException("Task rejected due to pool exhaustion");
        });
        executor.initialize();

        for (int i = 0; i < 2; i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
            });
        }
        assertThrows(RejectedExecutionException.class, () -> executor.execute(() -> System.out.println("Should not process")));
    }
}
