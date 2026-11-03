package nodir.paymo_add.service;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nodir.paymo_add.entity.HumoErrorCodes;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SvgateServiceImpl implements SvgateService {


    @Override
    @SneakyThrows
    public List<HumoErrorCodes> findHumoErrors() {
        return new CsvToBeanBuilder(new FileReader("src/main/resources/cardCodes/humoerrors.csv"))
                .withType(HumoErrorCodes.class)
                .build()
                .parse();
    }

    @Override
    @SneakyThrows
    public List<HumoErrorCodes> findUzcardErrors() {
        return new CsvToBeanBuilder(new FileReader("src/main/resources/cardCodes/svgateerrorcodes.csv"))
                .withType(HumoErrorCodes.class)
                .build()
                .parse();
    }


}

