package com.zhitao.train.business.domain.repository;

import com.zhitao.train.business.domain.entity.DailyTrain;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;


public interface DailyTrainRepository extends JpaRepository<DailyTrain,Long>, JpaSpecificationExecutor<DailyTrain> {
    Page<DailyTrain> findAll(Specification<DailyTrain> specification, Pageable pageable);

    void deleteByDateAndCode(LocalDate date, @Size(max = 20) @NotNull String code);

    List<DailyTrain> findByDateAndCode(LocalDate date, @Size(max = 20) @NotNull String code);
}
