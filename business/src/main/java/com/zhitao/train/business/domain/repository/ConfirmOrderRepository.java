package com.zhitao.train.business.domain.repository;

import com.zhitao.train.business.domain.entity.ConfirmOrder;
import com.zhitao.train.business.req.ConfirmOrderDoReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConfirmOrderRepository extends JpaRepository<ConfirmOrder,Long>, JpaSpecificationExecutor<ConfirmOrder>
{
}
