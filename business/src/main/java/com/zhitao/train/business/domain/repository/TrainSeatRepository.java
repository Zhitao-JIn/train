package com.zhitao.train.business.domain.repository;

import com.zhitao.train.business.domain.entity.TrainSeat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainSeatRepository extends JpaRepository<TrainSeat,Long> {
    Page<TrainSeat> findByTrainCode(String trainCode, Pageable pageable);

    void deleteByTrainCode(String trainCode);

    List<TrainSeat> findByTrainCodeOrderByIdAsc(String trainCode);
}
