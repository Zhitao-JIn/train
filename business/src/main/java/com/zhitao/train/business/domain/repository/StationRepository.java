package com.zhitao.train.business.domain.repository;

import com.zhitao.train.business.domain.entity.Station;
import com.zhitao.train.business.resp.StationQueryResp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends JpaRepository<Station,Long> {

    List<Station> findByName(String name);

    List<Station> findAllByOrderByNamePinyinAsc();
}
