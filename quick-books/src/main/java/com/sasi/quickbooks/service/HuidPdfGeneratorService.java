package com.sasi.quickbooks.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sasi.quickbooks.QuickBookConstants;
import com.sasi.quickbooks.model.ItemTypeEnum;
import com.sasi.quickbooks.model.huid.Huid;
import com.sasi.quickbooks.model.huid.HuidResponse;
import com.sasi.quickbooks.model.huid.HuidSummary;
import com.sasi.quickbooks.model.requestbody.HuidRequestBody;
import com.sasi.quickbooks.util.DateUtil;
import com.sasi.quickbooks.util.PDFUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.sasi.quickbooks.util.PDFUtil.getCell;

@Service
public class HuidPdfGeneratorService {

    public static PdfPTable generateData(List<Huid> data) {

        DecimalFormat df = new DecimalFormat("#.##");

        PdfPTable detailsTable = new PdfPTable(6);
        detailsTable.setWidthPercentage(95);
        AtomicInteger start = new AtomicInteger(1);
        detailsTable.setHeaderRows(1);
        detailsTable.addCell("S No.");
        detailsTable.addCell(getCell("HUID Created Date"));
        detailsTable.addCell(getCell("HUID NO."));
        detailsTable.addCell(getCell("Item Name"));
        detailsTable.addCell(getCell("Item Weight"));
        detailsTable.addCell(getCell("Item Saled Date"));

        if (data.isEmpty()) {
            detailsTable.addCell(getCell(" "));
            detailsTable.addCell(getCell(" "));
            detailsTable.addCell(getCell(" "));
            detailsTable.addCell(getCell(" "));
            detailsTable.addCell(getCell(" "));
            detailsTable.addCell(getCell(" "));
        }

        data.forEach(huid -> {
            detailsTable.addCell(getCell(String.valueOf(start.getAndIncrement())));
            detailsTable.addCell(getCell(PDFUtil.getDisplayFormatDate(huid.getCreatedOn())));
            detailsTable.addCell(getCell(huid.getHuidNumber()));
            detailsTable.addCell(getCell(huid.getItemName()));
            detailsTable.addCell(getCell(df.format(huid.getGrossWeight())));
            if (huid.isSaled()) {
                detailsTable.addCell(getCell(PDFUtil.getDisplayFormatDate(huid.getSaledOn())));
            } else {
                detailsTable.addCell(getCell(""));
            }
        });
        return detailsTable;
    }

    public static PdfPTable getSummaryTable(HuidSummary summary) {
        PdfPTable summaryTable = new PdfPTable(5);
        summaryTable.setWidthPercentage(95);

        DecimalFormat df = new DecimalFormat("#.##");
        summaryTable.addCell(getCell("Type"));
        summaryTable.addCell(getCell("Total In-Stock Weight"));
        summaryTable.addCell(getCell("Total Saled Weight"));
        summaryTable.addCell(getCell("Number of Items Instock"));
        summaryTable.addCell(getCell("Number of Items Saled"));

        summaryTable.addCell(getCell("Gold"));
        summaryTable.addCell(getCell(df.format(summary.getStockDetails().getTotalGoldWeight())));
        summaryTable.addCell(getCell(df.format(summary.getSaleDetails().getTotalGoldWeight())));
        summaryTable.addCell(getCell(df.format(summary.getStockDetails().getTotalGoldItems())));
        summaryTable.addCell(getCell(df.format(summary.getSaleDetails().getTotalGoldItems())));


        summaryTable.addCell(getCell("Silver"));
        summaryTable.addCell(getCell(df.format(summary.getStockDetails().getTotalSilverWeight())));
        summaryTable.addCell(getCell(df.format(summary.getSaleDetails().getTotalSilverWeight())));
        summaryTable.addCell(getCell(df.format(summary.getStockDetails().getTotalSilverItems())));
        summaryTable.addCell(getCell(df.format(summary.getSaleDetails().getTotalSilverItems())));
        return summaryTable;
    }

    public static PdfPTable addTableHeader(String headerString) {
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setSpacingBefore(25f);
        headerTable.setWidthPercentage(95);
        headerTable.addCell(getCell( headerString));
        return headerTable;
    }

    public File generateHuidFileForDownload(HuidResponse response, HuidRequestBody requestBody) {
        Document document = new Document(PageSize.A4, 1f, 1f, 1f, 0f);
        File file = new File("D:\\" + "huid.pdf");
        try {
            OutputStream outputStream = new FileOutputStream(file);
            PdfWriter.getInstance(document, outputStream);
            generateDocument(response, document, requestBody);
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void generateHuidFile(HuidResponse huidResponse, HttpServletResponse response, HuidRequestBody requestBody) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4, 1f, 1f, 1f, 0f);
        String headerKey = "file_name";
        String headerValue = "huid.pdf";
        response.setHeader(headerKey, headerValue);
        response.setHeader("Access-Control-Expose-Headers", "file_name");
        PdfWriter.getInstance(document, response.getOutputStream());
        generateDocument(huidResponse, document, requestBody);
    }

    public void generateDocument(HuidResponse huidResponse, Document document, HuidRequestBody requestBody) throws DocumentException {
        document.open();

        String summaryHeader = "HUID Summary details from "+ DateUtil.convertFormatLetterFormat(requestBody.getFrom(), QuickBookConstants.FORMAT_MM_dd_yyyy) +
                " to " + DateUtil.convertFormatLetterFormat(requestBody.getTo(), QuickBookConstants.FORMAT_MM_dd_yyyy);
        document.add(addTableHeader(summaryHeader));
        document.add(getSummaryTable(huidResponse.getSummary()));

        String goldSaledHeader = "Gold HUID saled from "+ DateUtil.convertFormatLetterFormat(requestBody.getFrom(), QuickBookConstants.FORMAT_MM_dd_yyyy) +
                " to " + DateUtil.convertFormatLetterFormat(requestBody.getTo(), QuickBookConstants.FORMAT_MM_dd_yyyy);
        document.add(addTableHeader(goldSaledHeader));
        document.add(generateData(filterData(huidResponse.getData(), ItemTypeEnum.GOLD, true)));

        String silverSaledHeader = "Silver HUID saled from "+ DateUtil.convertFormatLetterFormat(requestBody.getFrom(), QuickBookConstants.FORMAT_MM_dd_yyyy) +
                " to " + DateUtil.convertFormatLetterFormat(requestBody.getTo(), QuickBookConstants.FORMAT_MM_dd_yyyy);
        document.add(addTableHeader(silverSaledHeader));
        document.add(generateData(filterData(huidResponse.getData(), ItemTypeEnum.SILVER, true)));

        String goldInstockHeader = "Gold In-stock from --- to " + DateUtil.convertFormatLetterFormat(requestBody.getTo(), QuickBookConstants.FORMAT_MM_dd_yyyy);
        document.add(addTableHeader(goldInstockHeader));
        document.add(generateData(filterData(huidResponse.getData(), ItemTypeEnum.GOLD, false)));

        String silverInstockHeader = "Silver In-stock from --- to " + DateUtil.convertFormatLetterFormat(requestBody.getTo(), QuickBookConstants.FORMAT_MM_dd_yyyy);
        document.add(addTableHeader(silverInstockHeader));
        document.add(generateData(filterData(huidResponse.getData(), ItemTypeEnum.SILVER, false)));

        document.close();
    }

    private List<Huid> filterData(List<Huid> data, ItemTypeEnum type, boolean isSaled) {

        return data.stream().filter(huid -> {
            if(huid.isSaled() == isSaled && huid.getItemType() == type) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

    }

}

