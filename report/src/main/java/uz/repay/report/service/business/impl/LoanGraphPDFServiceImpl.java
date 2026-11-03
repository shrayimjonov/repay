package uz.repay.report.service.business.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.repay.common.exception.BadRequestException;
import uz.repay.common.exception.InvalidDataException;
import uz.repay.common.model.dto.response.core.ResultData;
import uz.repay.common.model.dto.response.loan.LoanGraphResponse;
import uz.repay.common.model.enums.ResultState;
import uz.repay.common.utils.CommonUtils;
import uz.repay.report.model.dto.report.request.LoanGraphRequestDTO;
import uz.repay.report.model.dto.report.response.LoanGraphResponseDTO;
import uz.repay.report.service.business.LoanGraphPDFService;
import uz.repay.report.service.web.NCIGatewayClientService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanGraphPDFServiceImpl implements LoanGraphPDFService {
    private final NCIGatewayClientService nciGatewayClientService;
    private static final Font COURIER = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);
    private static final Font ITALIC_SMALL = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC);
    private static final Font COURIER_SMALL_FOOTER = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);
    private static final Font PHRASE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.BOLD, BaseColor.BLUE);


    @Override
    public ResultData<LoanGraphResponseDTO> generateLoanGraphPDF(@NotNull LoanGraphRequestDTO requestDTO) {
        log.info("Received command to generate PDF for loan graph with request dto: {}", requestDTO);
        var loanExtId = requestDTO.getLoanExtId();

        var loanResult = nciGatewayClientService.getLoanGraph(loanExtId);

        if (!loanResult.getStatus()) {
            log.error("Finished method with internal error: {}", loanResult.getError().getMessage());
            return new ResultData<>(ResultState.InternalServerError, loanResult.getError().getMessage());
        }

        if (loanResult.getData().getError() != null) {
            log.error("Finished method with third party error: {}", loanResult.getData().getError().getMessage());
            return new ResultData<>(ResultState.ThirdPartyError, loanResult.getData().getError().getMessage());
        }

        var loanGraph = loanResult.getData().getResult();

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {

            PdfWriter.getInstance(document, out);
            document.open();
            addLogo(document);
            addDocTitle(document, requestDTO);

            /*
             * take columns number from response class fields count
             */

            int noOfColumns = 4;
            createTable(document, noOfColumns, loanGraph);
            addFooter(document);
            document.close();


            return ResultData.ok(
                    new LoanGraphResponseDTO(
                            Base64.getEncoder().encodeToString(out.toByteArray()),
                            getPdfNameWithDate(loanExtId)
                    )
            );
        } catch (Exception e) {
            log.error("Finished command to generate PDF for loan graph with error: {}", e.getMessage());
            return new ResultData<>(ResultState.NotProcessed, e.getMessage());
        }

    }

    private void addLogo(Document document) {
        try {
            String imagePath = "/static/davr.jpg";
            URL resource = getClass().getResource(imagePath);

            if (resource == null)
                throw new InvalidDataException("Can't find image from path: %s".formatted(imagePath));

            Image img = Image.getInstance(resource);

            img.scalePercent(50, 50);
            img.setAlignment(Element.ALIGN_RIGHT);
            document.add(img);
        } catch (DocumentException | IOException | InvalidDataException e) {
            log.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    private void addDocTitle(Document document, @NotNull LoanGraphRequestDTO requestDTO) throws DocumentException {
        Paragraph p1 = new Paragraph();
        leaveEmptyLine(p1, 1);
        p1.add(new Paragraph("Kredit anketa raqami: %s".formatted(requestDTO.getLoanExtId()), COURIER));
        p1.add(new Paragraph("FISH: %s".formatted(requestDTO.getClientName()), COURIER));
        p1.add(new Paragraph("Transit hisob raqami: %s".formatted(requestDTO.getTransitAccount()), COURIER));
        p1.add(new Paragraph("Olingan umumiy miqdori: %s".formatted(CommonUtils.handleAmount(requestDTO.getLoanAmount())), COURIER));
        p1.add(new Paragraph("Tavsiya etilgan to'lov miqdori: %s".formatted(CommonUtils.handleAmount(requestDTO.getRecommendedAmount())), COURIER));
        p1.add(new Paragraph("Umumiy to'lov miqdori: %s".formatted(CommonUtils.handleAmount(requestDTO.getMaxAmount())), COURIER));
        p1.add(new Paragraph("Kredit olingan sanasi: %s".formatted(requestDTO.getLoanOpenDate()), COURIER));

        p1.setAlignment(Element.ALIGN_CENTER);


        document.add(p1);

    }

    private void createTable(Document document, int noOfColumns, List<LoanGraphResponse> loanGraph) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        leaveEmptyLine(paragraph, 3);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(noOfColumns);

        var columnNames = List.of(
                "SANA", "OLINGAN MIQDOR", "ASOSIY QARZ TO'LOVI", "FOIZ QARZDORLIK TO'LOVI"
        );

        for (int i = 0; i < noOfColumns; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(columnNames.get(i)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.ORANGE);
            table.addCell(cell);
        }

        table.setHeaderRows(1);
        addDataToTable(table, loanGraph);
        document.add(table);
    }

    private void addDataToTable(PdfPTable table, List<LoanGraphResponse> loanGraph) {
        var principalDebt = 0L;
        var interestOnTermDebt = 0L;
        var masteredAmount = 0L;

        for (var graph : loanGraph) {
            table.setWidthPercentage(100);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);


            //    table.addCell("677");
            table.addCell(graph.getVDate().toString());
            var amount = CommonUtils.handleAmount(graph.getAmount());

            Phrase phrase;
            if (graph.getStatus().equals("0")) {
                phrase = new Phrase(amount, PHRASE_FONT);
            } else
                phrase = new Phrase(amount);

            switch (graph.getOperationId()) {
                case "1" -> {
                    masteredAmount = masteredAmount + graph.getAmount();
                    table.addCell(phrase);
                    table.addCell("");
                    table.addCell("");
                }
                case "2" -> {
                    principalDebt = principalDebt + graph.getAmount();
                    table.addCell("");
                    table.addCell(phrase);
                    table.addCell("");
                }
                default -> {
                    interestOnTermDebt = interestOnTermDebt + graph.getAmount();
                    table.addCell("");
                    table.addCell("");
                    table.addCell(phrase);
                }
            }

        }

        table.addCell("Umumiy summa:");
        table.addCell(CommonUtils.handleAmount(masteredAmount));
        table.addCell(CommonUtils.handleAmount(principalDebt));
        table.addCell(CommonUtils.handleAmount(interestOnTermDebt));

    }

    private void addFooter(Document document) throws DocumentException {
        Paragraph p2 = new Paragraph();
        leaveEmptyLine(p2, 3);
        p2.setAlignment(Element.ALIGN_MIDDLE);
        p2.add(new Paragraph(
                "------------------------ HISOBOT YAKUNLANDI ------------------------",
                COURIER_SMALL_FOOTER));

        leaveEmptyLine(p2, 1);
        p2.add(new Phrase("Tayyorlangan vaqt: " + LocalDateTime.now(), ITALIC_SMALL));

        document.add(p2);
    }

    private static void leaveEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private String getPdfNameWithDate(String loanExtId) {
        return loanExtId + "_" + UUID.randomUUID() + ".pdf";
    }

}
