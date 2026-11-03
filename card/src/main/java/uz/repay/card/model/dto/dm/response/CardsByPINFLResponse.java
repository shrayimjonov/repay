package uz.repay.card.model.dto.dm.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardsByPINFLResponse {
    private ArrayList<String> tokens;
}
