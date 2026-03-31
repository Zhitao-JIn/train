package com.zhitao.train.business.domain.repository;

import com.zhitao.train.business.domain.entity.DailyTrainSeat;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;


public interface DailyTrainSeatRepository extends JpaRepository<DailyTrainSeat,Long>, JpaSpecificationExecutor<DailyTrainSeat> {
    void deleteByDateAndTrainCode(LocalDate date, String trainCode);

    long countByDateAndTrainCodeAndSeatType(LocalDate date, String trainCode, String seatType);

    List<DailyTrainSeat> findAllByDateAndTrainCodeAndCarriageIndexOrderByCarriageIndexAsc(LocalDate date, String trainCode, Integer carriageIndex);

}
