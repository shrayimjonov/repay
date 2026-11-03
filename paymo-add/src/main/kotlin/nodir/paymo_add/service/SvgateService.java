package nodir.paymo_add.service;


import nodir.paymo_add.entity.HumoErrorCodes;
import nodir.paymo_add.entity.SvgateErrorCodes;

import java.util.List;

public interface SvgateService {
    List<HumoErrorCodes> findHumoErrors();

    List<HumoErrorCodes> findUzcardErrors();
}
