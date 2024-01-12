package io.github.giulong.spectrum.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.PriorityQueue;

@Slf4j
public class FixedSizeQueue<E> extends PriorityQueue<E> {

    public FixedSizeQueue() {
        super(1);
    }

    @Override
    public boolean add(E e) {
        for (E element : this) {
            if (element.equals(e)) {
                log.debug("Queue already containing {}", e);
                return false;
            }
        }

        return super.add(e);
    }

    public FixedSizeQueue<E> shrinkTo(final int maxSize) {
        final int currentSize = size();
        log.debug("Shrinking queue. Current size: {}, max size: {}", currentSize, maxSize);

        for (int i = 0; i < currentSize - maxSize; i++) {  // shrinking the queue to the proper size
            poll();
        }

        return this;
    }
}
