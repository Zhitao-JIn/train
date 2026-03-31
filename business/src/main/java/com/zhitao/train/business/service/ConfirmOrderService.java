package com.zhitao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import com.zhitao.train.business.domain.entity.DailyTrainCarriage;
import com.zhitao.train.business.domain.entity.DailyTrainSeat;
import com.zhitao.train.business.domain.entity.DailyTrainTicket;
import com.zhitao.train.business.domain.repository.ConfirmOrderRepository;
import com.zhitao.train.business.enums.ConfirmOrderStatusEnum;
import com.zhitao.train.business.enums.SeatColEnum;
import com.zhitao.train.business.enums.SeatTypeEnum;
import com.zhitao.train.business.req.ConfirmOrderTicketReq;
import com.zhitao.train.common.context.LoginMemberContext;
import com.zhitao.train.common.exception.BusinessException;
import com.zhitao.train.common.exception.BusinessExceptionEnum;
import com.zhitao.train.common.resp.PageResp;
import com.zhitao.train.common.util.SnowUtil;
import com.zhitao.train.business.domain.entity.ConfirmOrder;
import com.zhitao.train.business.req.ConfirmOrderQueryReq;
import com.zhitao.train.business.req.ConfirmOrderDoReq;
import com.zhitao.train.business.resp.ConfirmOrderQueryResp;
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
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    private ConfirmOrderRepository  confirmOrderRepository;

    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Resource
    private DailyTrainSeatService dailyTrainSeatService;
    @Autowired
    private AfterConfirmOrderService afterConfirmOrderService;

    public void save(ConfirmOrderDoReq req) {
        Instant now = Instant.now();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderRepository.save(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderRepository.save(confirmOrder);
        }
    }

    public PageResp<ConfirmOrderQueryResp> queryList(
            ConfirmOrderQueryReq req) {

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());

        // JPA分页对象（注意 page 从 0 开始）
        Pageable pageable = PageRequest.of(
                req.getPage() - 1,
                req.getSize(),
                Sort.by(Sort.Direction.DESC, "id")
        );

        // 查询
        Page<ConfirmOrder> page =
                confirmOrderRepository.findAll(pageable);

        // 转DTO
        List<ConfirmOrderQueryResp> list =
                BeanUtil.copyToList(
                        page.getContent(),
                        ConfirmOrderQueryResp.class
                );

        // 组装返回
        PageResp<ConfirmOrderQueryResp> pageResp =
                new PageResp<>();

        pageResp.setTotal(page.getTotalElements());
        pageResp.setList(list);

        return pageResp;
    }

    public void delete(Long id) {
        confirmOrderRepository.deleteById(id);
    }

    @Transactional
    public void doConfirm(ConfirmOrderDoReq req) {
        // 省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过

        // 保存确认订单表，状态初始
        ConfirmOrder confirmOrder = new ConfirmOrder();
        Instant now = Instant.now();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setMemberId(LoginMemberContext.getId());
        confirmOrder.setDate(req.getDate());
        confirmOrder.setTrainCode(req.getTrainCode());
        confirmOrder.setStart(req.getStart());
        confirmOrder.setEnd(req.getEnd());
        confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        Map<String, Object> tickets = new HashMap<>();
        tickets.put("tickets",req.getTickets());
        confirmOrder.setTickets(tickets);
        confirmOrderRepository.save(confirmOrder);

        // 查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(req.getDate(), req.getTrainCode(), req.getStart(), req.getEnd());
        LOG.info("查出余票记录:{}",dailyTrainTicket);
        // 扣减余票数量，并判断余票是否足够
        for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum= EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                }
            }
        }
        ConfirmOrderTicketReq confirmOrderTicketReq = req.getTickets().get(0);
        List<DailyTrainSeat> finalSeatList = new ArrayList<>();
        // 选座
        if(StrUtil.isNotBlank(confirmOrderTicketReq.getSeat())){
            LOG.info("本次购票有选座");
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(confirmOrderTicketReq.getSeatTypeCode());
            LOG.info("本次包含的列有:{}",colEnumList);
            List<String> referSeatList=new ArrayList<>();
            for (int i=1;i<=2;i++){
                for(SeatColEnum seatColEnum : colEnumList){
                    referSeatList.add(seatColEnum.getCode() + i);
                }
            }
            LOG.info("参照座位为:{}",referSeatList);
            List<Integer> absoluteOffsetSeatList=new ArrayList<>();
            for (ConfirmOrderTicketReq ticketReq : req.getTickets()) {
                absoluteOffsetSeatList.add(referSeatList.indexOf(ticketReq.getSeat()));
            }
            List<Integer> offsetSeatList=new ArrayList<>();
            for (Integer index : absoluteOffsetSeatList) {
                offsetSeatList.add(index-absoluteOffsetSeatList.get(0));
            }
            LOG.info("座位偏移值为:{}",absoluteOffsetSeatList);
            getSeat(finalSeatList,
                    req.getDate(),
                    req.getTrainCode(),
                    confirmOrderTicketReq.getSeatTypeCode(),
                    confirmOrderTicketReq.getSeat().split("")[0],
                    offsetSeatList,
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex());
        }else{
            LOG.info("本次购票没有选座");
            for (ConfirmOrderTicketReq confirmOrderTicketReq1 : req.getTickets()) {
                getSeat(finalSeatList,
                        req.getDate(),
                        req.getTrainCode(),
                        confirmOrderTicketReq1.getSeatTypeCode(),
                        null,
                        null,
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex());
            }
        }
        LOG.info("最终选座:{}",finalSeatList);
        // 选中座位后事务处理：

            // 座位表修改售卖情况sell；
            // 余票详情表修改余票；
            // 为会员增加购票记录
            // 更新确认订单为成功
        afterConfirmOrderService.afterDoConfirm(dailyTrainTicket,finalSeatList,req.getTickets());
    }
    private void getSeat(List<DailyTrainSeat>finalSeatList,LocalDate date,String trainCode,String seatType,String column,List<Integer> offsetList,Integer startIndex,Integer endIndex){
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectByTrainType(date,trainCode,seatType);
        List<DailyTrainSeat> getSeatList = new ArrayList<>();
        LOG.info("共查出{}个符合要求的车厢",carriageList.size());
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            getSeatList.clear();
            LOG.info("开始从车厢选座{}",dailyTrainCarriage.getIndex());
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date,trainCode,dailyTrainCarriage.getIndex());
            LOG.info("车厢{}的座位数:{}",dailyTrainCarriage.getIndex(),seatList.size());
            for(int i=0;i<seatList.size();i++){
                DailyTrainSeat seat = seatList.get(i);
                Integer seatIndex = seat.getCarriageSeatIndex();
                String col = seat.getCol();
                boolean alreadyChoosed=false;
                for (DailyTrainSeat finalSeat : finalSeatList){
                    if(finalSeat.getId().equals(seat.getId())){
                        alreadyChoosed=true;
                        break;
                    }
                }
                if(alreadyChoosed){
                    LOG.info("不能重复选座{}",seatIndex);
                    continue;
                }
                if(StrUtil.isBlank(column)){
                    LOG.info("无选座");
                }else{
                    if(!col.equals(column)){
                        LOG.info("座位{}列不对，当前列{}，目标列{}",seatIndex,col,column);
                        continue;
                    }
                }


                if (calSell(seat,startIndex,endIndex)){
                    LOG.info("已选中");
                    getSeatList.add(seat);
                }else {
                    continue;
                }

                boolean isGetAllOffsetSeat = true;
                if (CollUtil.isNotEmpty(offsetList)) {
                    LOG.info("有偏移值：{}，校验偏移的座位是否可选", offsetList);
                    // 从索引1开始，索引0就是当前已选中的票
                    for (int j = 1; j < offsetList.size(); j++) {
                        Integer offset = offsetList.get(j);
                        // 座位在库的索引是从1开始
                        // int nextIndex = seatIndex + offset - 1;
                        int nextIndex = i + offset;

                        // 有选座时，一定是在同一个车箱
                        if (nextIndex >= seatList.size()) {
                            LOG.info("座位{}不可选，偏移后的索引超出了这个车箱的座位数", nextIndex);
                            isGetAllOffsetSeat = false;
                            break;
                        }

                        DailyTrainSeat nextDailyTrainSeat = seatList.get(nextIndex);
                        boolean isChooseNext = calSell(nextDailyTrainSeat,
                                startIndex, endIndex);
                        if (isChooseNext) {
                            LOG.info("座位{}被选中", nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        } else {
                            LOG.info("座位{}不可选", nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }
                if (!isGetAllOffsetSeat) {
                    getSeatList.clear();
                    continue;
                }
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }
    private boolean calSell(DailyTrainSeat dailyTrainSeat,Integer startIndex,Integer endIndex){
        String sell = dailyTrainSeat.getSell();
        String sellPart = sell.substring(startIndex,endIndex);
        if (Integer.parseInt(sellPart) > 0){
            LOG.info("座位{}在本次车站区间{}-{}已售，不可重复出售",dailyTrainSeat.getCarriageSeatIndex(),startIndex,endIndex);
            return false;
        }else{
            LOG.info("座位{}在本次车站区间{}-{}未售，可出售",dailyTrainSeat.getCarriageSeatIndex(),startIndex,endIndex);
            // 1. 把选中的区间位置从0改成1（表示已售）
            String curSell = sellPart.replace('0', '1');

// 2. 右侧补0 → 扩展到和 sell 一样长（核心修复）
            curSell = StrUtil.padAfter(curSell, sell.length(), '0');

// 3. 转成二进制 | 运算（锁定座位）
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            String newSell = NumberUtil.getBinaryStr(newSellInt);

// 4. 左侧补0 → 保证长度和原sell一致
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());
            LOG.info("座位{}被选中，原售票信息{}，车站区间{}-{}，最终售票信息{}",
                    dailyTrainSeat.getCarriageSeatIndex(),sell,startIndex,endIndex,curSell,newSell);
            dailyTrainSeat.setSell(newSell);
            return true;
        }

    }
}
