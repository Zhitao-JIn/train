package com.zhitao.train.business.domain.repository;

import com.zhitao.train.business.domain.entity.DailyTrainCarriage;
import com.zhitao.train.business.domain.entity.TrainCarriage;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;


public interface DailyTrainCarriageRepository extends JpaRepository<DailyTrainCarriage, Long>, JpaSpecificationExecutor<DailyTrainCarriage> {
    void deleteByDateAndTrainCode(LocalDate date, String trainCode);

    List<DailyTrainCarriage> findAllByDateAndTrainCodeAndSeatType(@NotNull LocalDate date, @Size(max = 20) @NotNull String trainCode, @NotNull String seatType);
}
