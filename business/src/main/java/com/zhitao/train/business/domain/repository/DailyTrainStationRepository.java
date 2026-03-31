package com.zhitao.train.business.domain.repository;

import com.zhitao.train.business.domain.entity.DailyTrainStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;


public interface DailyTrainStationRepository extends JpaRepository<DailyTrainStation, Long>,JpaSpecificationExecutor<DailyTrainStation> {
    void deleteByDateAndTrainCode(LocalDate date, String trainCode);
}
