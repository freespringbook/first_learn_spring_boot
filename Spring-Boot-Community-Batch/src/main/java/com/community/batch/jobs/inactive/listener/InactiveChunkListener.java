package com.community.batch.jobs.inactive.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.batch.api.chunk.listener.ChunkListener;

/**
 * Created by freejava1191@gmail.com on 2019-09-16
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
@Slf4j
@Component
public class InactiveChunkListener implements ChunkListener {
    @Override
    public void beforeChunk() throws Exception {
        log.info("Before Chunk");
    }

    @Override
    public void onError(Exception ex) throws Exception {
        log.info("After Chunk");
    }

    @Override
    public void afterChunk() throws Exception {
        log.info("After Chunk Error");
    }
}
