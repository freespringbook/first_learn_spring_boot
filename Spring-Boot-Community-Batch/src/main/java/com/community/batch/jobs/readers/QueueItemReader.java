package com.community.batch.jobs.readers;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by freejava1191@gmail.com on 2019-09-13
 * Blog : https://freedeveloper.tistory.com/
 * GitHub : https://github.com/freelife1191
 */
public class QueueItemReader<T> implements ItemReader<T> {

    private Queue<T> queue;

    /**
     * QueueItemReader를 사용해 휴면회원으로 지정될 타깃 데이터를 한번에 불러와 큐에 담아놓음
     * @param data
     */
    public QueueItemReader(List<T> data) {
        this.queue = new LinkedList<>(data);
    }

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        // read() 메서드를 사용할 때 큐의 poll() 메서드를 사용하여 큐에서 데이터를 하나씩 반환함
        return this.queue.poll();
    }
}
