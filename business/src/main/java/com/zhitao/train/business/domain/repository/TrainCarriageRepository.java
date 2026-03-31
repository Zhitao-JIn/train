package com.zhitao.train.business.domain.repository;

import com.zhitao.train.business.domain.entity.TrainCarriage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainCarriageRepository extends JpaRepository<TrainCarriage, Long> {
    List<TrainCarriage> findByTrainCodeAndIndex(String trainCode, Integer index);

    Page<TrainCarriage> findByTrainCode(String trainCode, Pageable pageable);

    List<TrainCarriage> findByTrainCode(String trainCode, Sort sort);
}
