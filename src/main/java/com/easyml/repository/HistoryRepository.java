package com.easyml.repository;

import com.easyml.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
public interface HistoryRepository extends JpaRepository<History, Long> {

    Optional<History> findByProjectId(Long projectId);
    Optional<History> findAllByProjectId(Long projectId);

}
