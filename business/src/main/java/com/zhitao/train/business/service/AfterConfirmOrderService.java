package com.zhitao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zhitao.train.business.domain.entity.ConfirmOrder;
import com.zhitao.train.business.domain.entity.DailyTrainCarriage;
import com.zhitao.train.business.domain.entity.DailyTrainSeat;
import com.zhitao.train.business.domain.entity.DailyTrainTicket;
import com.zhitao.train.business.domain.repository.ConfirmOrderRepository;
import com.zhitao.train.business.domain.repository.DailyTrainSeatRepository;
import com.zhitao.train.business.domain.repository.DailyTrainTicketRepository;
import com.zhitao.train.business.enums.ConfirmOrderStatusEnum;
import com.zhitao.train.business.enums.SeatColEnum;
import com.zhitao.train.business.enums.SeatTypeEnum;
import com.zhitao.train.business.feign.MemberFeign;
import com.zhitao.train.business.req.ConfirmOrderDoReq;
import com.zhitao.train.business.req.ConfirmOrderQueryReq;
import com.zhitao.train.business.req.ConfirmOrderTicketReq;
import com.zhitao.train.business.req.MemberTicketReq;
import com.zhitao.train.business.resp.ConfirmOrderQueryResp;
import com.zhitao.train.common.context.LoginMemberContext;
import com.zhitao.train.common.exception.BusinessException;
import com.zhitao.train.common.exception.BusinessExceptionEnum;
import com.zhitao.train.common.resp.CommonResp;
import com.zhitao.train.common.resp.PageResp;
import com.zhitao.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AfterConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);

    @Resource
    private DailyTrainSeatRepository dailyTrainSeatRepository;
    @Resource
    private DailyTrainTicketRepository dailyTrainTicketRepository;
    @Resource
    private MemberFeign memberFeign;

    @Transactional
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket,List<DailyTrainSeat> dailyTrainSeatList,List<ConfirmOrderTicketReq> confirmOrderTicketReqList){
        for (int j =0 ;j<dailyTrainSeatList.size();j++) {
            DailyTrainSeat dailyTrainSeat = dailyTrainSeatList.get(j);
            dailyTrainSeat.setUpdateTime(Instant.now());
            dailyTrainSeatRepository.save(dailyTrainSeat);


            char[] chars = dailyTrainSeat.getSell().toCharArray();

            Integer startIndex = dailyTrainTicket.getStartIndex();
            Integer endIndex = dailyTrainTicket.getEndIndex();

            /* ===== 特殊情况：只有一段 ===== */

            if (chars.length == 1) {

                LOG.info("单区间车次，影响区间 {}-{} , {}-{}",
                        startIndex, startIndex,
                        endIndex, endIndex);

                dailyTrainTicketRepository.updateCountBySell(
                        dailyTrainSeat.getTrainCode(),
                        dailyTrainSeat.getDate(),
                        dailyTrainSeat.getSeatType(),
                        startIndex,
                        startIndex,
                        endIndex,
                        endIndex
                );
            }else{
                /* ===== 正常多区间逻辑 ===== */

                Integer minStartIndex = 0;
                Integer maxStartIndex = endIndex - 1;

                for (int i = startIndex - 1; i >= 0; i--) {
                    if (chars[i] == '1') {
                        minStartIndex = i + 1;
                        break;
                    }
                }

                Integer minEndIndex = startIndex + 1;
                Integer maxEndIndex = chars.length - 1;

                for (int i = endIndex + 1; i < chars.length; i++) {
                    if (chars[i] == '1') {
                        maxEndIndex = i - 1;
                        break;
                    }
                }

                /* 防御 */

                if (minStartIndex > maxStartIndex ||
                        minEndIndex > maxEndIndex) {

                    LOG.error("非法区间: start {}-{}, end {}-{}",
                            minStartIndex, maxStartIndex,
                            minEndIndex, maxEndIndex);

                    return;
                }

                LOG.info("影响出发站区间:{}-{}",minStartIndex,maxStartIndex);
                LOG.info("影响到达站区间:{}-{}",minEndIndex,maxEndIndex);

                dailyTrainTicketRepository.updateCountBySell(
                        dailyTrainSeat.getTrainCode(),
                        dailyTrainSeat.getDate(),
                        dailyTrainSeat.getSeatType(),
                        minStartIndex,
                        maxStartIndex,
                        minEndIndex,
                        maxEndIndex
                );
            }

            // 调用会员服务接口，为会员增加一张车票
            MemberTicketReq memberTicketReq = new MemberTicketReq();
            memberTicketReq.setMemberId(LoginMemberContext.getId());
            memberTicketReq.setPassengerId(confirmOrderTicketReqList.get(j).getPassengerId());
            memberTicketReq.setPassengerName(confirmOrderTicketReqList.get(j).getPassengerName());
            memberTicketReq.setDate(dailyTrainTicket.getDate());
            memberTicketReq.setTrainCode(dailyTrainTicket.getTrainCode());
            memberTicketReq.setCarriageIndex(dailyTrainSeat.getCarriageIndex());
            memberTicketReq.setRow(dailyTrainSeat.getRow());
            memberTicketReq.setCol(dailyTrainSeat.getCol());
            memberTicketReq.setSeatType(dailyTrainSeat.getSeatType());
            memberTicketReq.setStart(dailyTrainTicket.getStart());
            memberTicketReq.setStartTime(dailyTrainTicket.getStartTime());
            memberTicketReq.setEnd(dailyTrainTicket.getEnd());
            memberTicketReq.setEndTime(dailyTrainTicket.getEndTime());


            CommonResp<Object> commonResp = memberFeign.save(memberTicketReq);
            LOG.info("调用member接口，返回：{}", commonResp);
            if (!commonResp.getSuccess()) throw new BusinessException(BusinessExceptionEnum.BUSiNESS_MEMBER_FEIGN_TICKET_SAVE_ERROR);
            memberTicketReq.setSeatType(dailyTrainSeat.getSeatType());
        }

    }
}
