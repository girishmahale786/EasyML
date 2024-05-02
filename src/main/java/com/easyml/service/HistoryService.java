package com.easyml.service;

import com.easyml.model.History;
import com.easyml.repository.HistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void save(History history) {
        historyRepository.save(history);
//        System.out.println(historyRepository.findByProjectId(history.getProjectId()).isPresent());
//        if (historyRepository.findByProjectId(history.getProjectId()).isPresent()) {
//            Optional<History> optional = historyRepository.findByProjectId(history.getProjectId());
//            String preprocess = optional.get().getPreprocessing();
//            String update_preprocess = preprocess + "," + history.getPreprocessing();
//            history.setPreprocessing(update_preprocess);
//            historyRepository.deleteByProjectId(history.getProjectId());
//            historyRepository.save(history);
//        }
    }
}
