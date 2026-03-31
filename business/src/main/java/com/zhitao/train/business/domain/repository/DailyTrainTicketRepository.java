package com.zhitao.train.business.domain.repository;

import com.zhitao.train.business.domain.entity.DailyTrainTicket;
import com.zhitao.train.business.domain.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface DailyTrainTicketRepository extends JpaRepository<DailyTrainTicket,Long> , JpaSpecificationExecutor<DailyTrainTicket> {
    void deleteByDateAndTrainCode(LocalDate date, String trainCode);

    List<DailyTrainTicket> findByDateAndTrainCodeAndStartAndEnd(LocalDate date, String trainCode, String start, String end);

    @Modifying
    @Query(value = """
UPDATE daily_train_ticket 
SET
    ydz = CASE WHEN ?3 = '1' THEN ydz - 1 ELSE ydz END,
    edz = CASE WHEN ?3 = '2' THEN edz - 1 ELSE edz END,
    rw  = CASE WHEN ?3 = '3' THEN rw  - 1 ELSE rw  END,
    yw  = CASE WHEN ?3 = '4' THEN yw  - 1 ELSE yw  END
WHERE
    `date` = ?2
    AND train_code = ?1
    AND start_index BETWEEN ?4 AND ?5
    AND end_index BETWEEN ?6 AND ?7
""", nativeQuery = true)
    int updateCountBySell(
            String trainCode,
            LocalDate date,
            String seatTypeCode,
            Integer minStartIndex,
            Integer maxStartIndex,
            Integer minEndIndex,
            Integer maxEndIndex
    );
}
