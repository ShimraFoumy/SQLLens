package com.sqlens.service;

import com.sqlens.dto.QueryHistoryItemDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class QueryHistoryService {

    private static final int MAX_HISTORY_SIZE = 50;
    private final ConcurrentLinkedDeque<QueryHistoryItemDto> historyDeque = new ConcurrentLinkedDeque<>();

    public QueryHistoryItemDto addHistory(QueryHistoryItemDto item) {
        if (item.getId() == null) {
            item.setId(UUID.randomUUID().toString());
        }
        historyDeque.addFirst(item);

        while (historyDeque.size() > MAX_HISTORY_SIZE) {
            historyDeque.pollLast();
        }

        return item;
    }

    public List<QueryHistoryItemDto> getHistory() {
        return new ArrayList<>(historyDeque);
    }

    public void clearHistory() {
        historyDeque.clear();
    }
}
