package uz.repay.common.utils.converter;

import com.fasterxml.jackson.databind.util.StdConverter;


public class Int2BooleanConverter extends StdConverter<Integer, Boolean> {

    @Override
    public Boolean convert(Integer integer) {
        return integer == 1;
    }
}
