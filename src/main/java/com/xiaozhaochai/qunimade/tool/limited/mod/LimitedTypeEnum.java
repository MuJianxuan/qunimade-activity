package com.xiaozhaochai.qunimade.tool.limited.mod;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 如果你想的是滚动限量？
 *   那么我告诉你总体思想是 滑动时间窗口设计
 * @author Rao
 * @Date 2021/12/03
 **/
@Getter
public enum LimitedTypeEnum implements LimitedType {

    /**
     * 天限量
     */
    day(0) {
        @Override
        public String limitKey(String markId) {
            return "day-" + LocalDate.now().toString()+"_" + markId;
        }
    },
    /**
     * 总限量
     */
    all(1) {
        @Override
        public String limitKey(String markId) {
            return "all_"+ markId;
        }
    }

    ;

    /**
     * 兼容数字
     */
    private final int type;

    LimitedTypeEnum(int type) {
        this.type = type;
    }

    public final static Map<Integer,LimitedTypeEnum> LIMIT_TYPE_MAP = Stream.of( values() ).collect(Collectors.toMap(LimitedTypeEnum::getType, Function.identity() ) );


}
