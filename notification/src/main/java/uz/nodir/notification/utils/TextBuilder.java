package uz.nodir.notification.utils;

import lombok.extern.slf4j.Slf4j;
import uz.nodir.notification.model.entity.Template;
import uz.nodir.notification.model.enums.ValueType;
import uz.repay.common.model.dto.response.core.SimpleResponse;
import uz.repay.common.model.enums.Language;
import uz.repay.common.utils.CommonUtils;

@Slf4j
public class TextBuilder {

    public static SimpleResponse<String> buildTextFromTemplate(Template template, Language language, String... params) {
        var textValues = template.getTextValues()
                .get(language);

        if (textValues == null) {
            var message = "Text not found for language: %s".formatted(language);
            log.error(message);
            return new SimpleResponse<>(1052, message);
        }

        log.info("Selected textValues: {}", textValues);

        var placeNum = 0;

        var text = textValues.getText();

        for (String value : params) {

            var fieldType = textValues.getValueType().get(placeNum + 1);

            if (fieldType == null) {
                var message = "Field type not found for order: %s".formatted(placeNum + 1);
                log.error(message);
                return new SimpleResponse<>(1053, message);
            }


            if (fieldType.equals(ValueType.AMOUNT))
                value = CommonUtils.handleAmount(Long.valueOf(value));

            text = text.replaceFirst("#", value);
            placeNum = placeNum + 1;
        }

        return new SimpleResponse<>(text);
    }


    public static String hideDigits(String text) {
        return text.replaceAll("\\d", "*");
    }
}
