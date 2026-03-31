package com.zhitao.train.business.domain.repository;

import com.zhitao.train.business.domain.entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    List<Train> findByCode(String code);

    List<Train> findAllByOrderByIdAsc();
}
