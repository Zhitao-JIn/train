package com.zhitao.train.business.domain.repository;

import com.zhitao.train.business.domain.entity.TrainStation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainStationRepository extends JpaRepository<TrainStation, Long> {
    List<TrainStation> findByTrainCodeAndIndex(String trainCode, Integer index);

    List<TrainStation> findByTrainCodeAndName(String trainCode, String name);

    Page<TrainStation> findByTrainCode(String trainCode, Pageable pageable);

    List<TrainStation> findByTrainCode(String trainCode, Sort sort);
}
