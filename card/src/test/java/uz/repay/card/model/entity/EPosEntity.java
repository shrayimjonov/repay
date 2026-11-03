package uz.repay.card.model.entity;

import lombok.extern.slf4j.Slf4j;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ServiceType;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class EPosEntity {
    public final static EPos AUTOPAY_EPOS;
    public final static EPos HUMO_EPOS;
    public final static EPos UZCARD_EPOS;


    static {
        HUMO_EPOS = new EPos(1L, Branch._01121, Processing.UZCARD, "9290511", "9051058887", ServiceType.REPAY, "17409000100400981001",false);
        UZCARD_EPOS = new EPos(2L, Branch._00981, Processing.HUMO, "2563DF21", "00981J325D100", ServiceType.REPAY, "17409000100400981002",false);
        AUTOPAY_EPOS = new EPos(3L, Branch._01121, Processing.SVGATE_AUTOPAY, "9290512", "9051058888", ServiceType.REPAY, "17409000100400981003",false);
    }

    public static List<EPos> findAll() {
        var list = new ArrayList<EPos>();

        list.add(HUMO_EPOS);
        list.add(UZCARD_EPOS);
        list.add(AUTOPAY_EPOS);
        log.info("Build ePos fake list: {}", list);
        return list;
    }
}
