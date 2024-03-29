package com.sasi.quickbooks.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.sasi.quickbooks.model.SummaryReport;
import com.sasi.quickbooks.util.PDFUtil;
import com.sasi.quickbooks.model.InvoiceItem;
import com.sasi.quickbooks.model.QuickBookInvoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class PDFGeneratorService {

    public File generateSummaryReport(SummaryReport gold, SummaryReport silver, String type, String from, String to) {
        Document document = new Document(PageSize.A5, 1f, 1f, 1f, 0f);
        DecimalFormat df = new DecimalFormat("#.##");
        File file = new File("D:\\"+type+".pdf");
        try {
            OutputStream outputStream = new FileOutputStream(file);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            PdfPTable detailsTable = new PdfPTable(4);
            float[] columnWidths1 = {2, 2f, 2f, 2f};
            detailsTable.setWidths(columnWidths1);
            detailsTable.setWidthPercentage(95);

            detailsTable.addCell("From");
            detailsTable.addCell(from);
            detailsTable.addCell("To");
            detailsTable.addCell(to);
            document.add(detailsTable);
            PdfPTable summaryTable = new PdfPTable(3);
            float[] columnWidths = {2, 1.5f, 1.5f};
            summaryTable.setWidths(columnWidths);
            summaryTable.setWidthPercentage(95);

            summaryTable.setPaddingTop(10f);

            summaryTable.addCell("#");
            summaryTable.addCell("GOLD");
            summaryTable.addCell("SILVER");

            summaryTable.addCell("Total Weight");
            summaryTable.addCell(PDFUtil.setRightAlign(PDFUtil.getCell(df.format(gold.getTotalWeight()))));
            summaryTable.addCell(PDFUtil.setRightAlign(PDFUtil.getCell(df.format(silver.getTotalWeight()))));

            summaryTable.addCell("Total amount before tax");
            summaryTable.addCell(PDFUtil.setRightAlign(PDFUtil.getCell(df.format(gold.getTotalBeforeTax()))));
            summaryTable.addCell(PDFUtil.setRightAlign(PDFUtil.getCell(df.format(silver.getTotalBeforeTax()))));

            summaryTable.addCell("Total CGST");
            summaryTable.addCell(PDFUtil.setRightAlign(PDFUtil.getCell(df.format(gold.getTotalCGST()))));
            summaryTable.addCell(PDFUtil.setRightAlign(PDFUtil.getCell(df.format(silver.getTotalCGST()))));

            summaryTable.addCell("Total SGST");
            summaryTable.addCell(PDFUtil.setRightAlign(PDFUtil.getCell(df.format(gold.getTotalSGST()))));
            summaryTable.addCell(PDFUtil.setRightAlign(PDFUtil.getCell(df.format(silver.getTotalSGST()))));

            summaryTable.addCell("Total amount After tax");
            summaryTable.addCell(PDFUtil.setRightAlign(PDFUtil.getCell(df.format(gold.getTotalAfterTax()))));
            summaryTable.addCell(PDFUtil.setRightAlign(PDFUtil.getCell(df.format(silver.getTotalAfterTax()))));
            document.add(summaryTable);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static PdfPTable getDetailsTable(QuickBookInvoice quickBookInvoice) throws DocumentException {
        PdfPTable detailsTable = new PdfPTable(1);
        detailsTable.setWidthPercentage(95);
        detailsTable.getDefaultCell().setBorderWidthRight(0);
        detailsTable.getDefaultCell().setBorderWidthLeft(0);
        detailsTable.addCell(getCustomerNameTable(quickBookInvoice.getCustomerName()));
        detailsTable.addCell(getCustomerAddressTable(quickBookInvoice.getAddress()));
        detailsTable.addCell(getStateTable(quickBookInvoice.getState(), quickBookInvoice.getStateCode()));
        detailsTable.addCell(getGstinTable(quickBookInvoice.getGstin()));

        ArrayList<PdfPRow> rows = detailsTable.getRows();
        for (int j = 0; j < rows.size(); j++) {
            PdfPCell[] cells = rows.get(j).getCells();
            for (int i = 0; i < cells.length; i++) {
                PDFUtil.setCellBorderColor(cells[i]);
                if (j != 0) {
                    cells[i].setBorderWidthTop(0);
                }

                if (j == rows.size() - 1) {
                    cells[i].setPaddingBottom(7);
                } else {
                    cells[i].setBorderWidthBottom(0);
                }
            }
        }
        return detailsTable;
    }

    public static PdfPTable getCustomerNameTable(String customerName) throws DocumentException {
        PdfPTable customerNameTable = new PdfPTable(2);
        float[] columnWidths = {0.55f, 2f};
        customerNameTable.setWidths(columnWidths);
        customerNameTable.setWidthPercentage(95);
        PdfPCell cell = PDFUtil.getCellLeftAlignNoBorder("Customer's Name");
        cell.setPaddingTop(10f);
        customerNameTable.addCell(cell);
        cell = PDFUtil.getCellInputCellLeftAlignBottomBorderColored(customerName);
        cell.setPaddingTop(10f);
        customerNameTable.addCell(cell);

        return customerNameTable;
    }

    public static PdfPTable getCustomerAddressTable(String address) throws DocumentException {
        PdfPTable customerAddressTable = new PdfPTable(2);
        float[] columnWidths = {0.25f, 2f};
        customerAddressTable.setWidths(columnWidths);
        customerAddressTable.setWidthPercentage(95);

        customerAddressTable.addCell(PDFUtil.getCellLeftAlignNoBorder("Address"));
        customerAddressTable.addCell(PDFUtil.getCellInputCellLeftAlignBottomBorderColored(address));
        return customerAddressTable;
    }

    public static PdfPTable getStateTable(String state, String stateCode) throws DocumentException {
        PdfPTable stateTable = new PdfPTable(4);
        float[] columnWidths = {0.42f, 2f, 0.75f, 2f};
        stateTable.setWidths(columnWidths);
        stateTable.setWidthPercentage(95);

        stateTable.addCell(PDFUtil.getCellLeftAlignNoBorder("State"));
        stateTable.addCell(PDFUtil.getCellInputCellLeftAlignBottomBorderColored(state));
        stateTable.addCell(PDFUtil.getCellLeftAlignNoBorder("State Code"));
        stateTable.addCell(PDFUtil.getCellInputCellLeftAlignBottomBorderColored(stateCode));
        return stateTable;
    }

    public static PdfPTable getGstinTable(String gstin) throws DocumentException {
        PdfPTable GstinTable = new PdfPTable(2);
        float[] columnWidths = {0.4f, 2f};
        GstinTable.setWidths(columnWidths);
        GstinTable.setWidthPercentage(95);

        GstinTable.addCell(PDFUtil.getCellLeftAlignNoBorder("GSTIN / PAN"));
        GstinTable.addCell(PDFUtil.getCellInputCellLeftAlignBottomBorderColored(gstin));
        return GstinTable;
    }

    public static PdfPTable getItemDetails(QuickBookInvoice quickBookInvoice) throws DocumentException {
        PdfPTable itemDetailsTable = new PdfPTable(5);
        itemDetailsTable.setWidthPercentage(95);
        itemDetailsTable.setSpacingBefore(10f);
        int defaultNumItems = 11 - quickBookInvoice.getPaymentMode().size();
        float[] columnWidths = {4f, 1f, 2f, 1.2f, 1.5f};
        itemDetailsTable.setWidths(columnWidths);

        itemDetailsTable.addCell(PDFUtil.getLabelCellCenterAlignNoLeftBorderColored("DESCRIPTION OF GOODS"));
        itemDetailsTable.addCell(PDFUtil.getLabelCellCenterAlignColored("HSN CODE"));
        itemDetailsTable.addCell(PDFUtil.getLabelCellCenterAlignColored("GROSS WEIGHT"));
        itemDetailsTable.addCell(PDFUtil.getLabelCellCenterAlignColored("RATE PER GRAM"));
        itemDetailsTable.addCell(PDFUtil.getLabelCellCenterAlignNoRightBorderColored("AMOUNT Rs."));

        for (InvoiceItem item : quickBookInvoice.getInvoiceItems()) {
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignOnlyRightBorderColored(item.getDescriptionOfItem()));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(Integer.toString(quickBookInvoice.getInvoiceType().getValue())));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(Float.toString(item.getGrossWeight())));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(Float.toString(item.getRatePerGram())));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignOnlyLeftBorderColored(Float.toString(item.getAmount())));
            defaultNumItems--;
        }

        for (int i = 0; i < defaultNumItems; i++) {
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignOnlyRightBorderColored(" "));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignOnlyLeftBorderColored(" "));
        }

//        quickBookInvoice.getPaymentMode().forEach((paymentMode) -> {
//            String payment = paymentMode.getPaymentMode() + ": " + paymentMode.getAmount();
//            itemDetailsTable.addCell(PDFUtil.getCellInputCellLeftAlignOnlyRightBorderColored(payment));
//            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
//            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
//            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
//            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignOnlyLeftBorderColored(" "));
//        });
        PdfPCell paymentCell = new PdfPCell(getPaymentModeDetailsTable(quickBookInvoice));
        itemDetailsTable.addCell(PDFUtil.setCellBorderColor(paymentCell));
        itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
        itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
        itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
        itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignOnlyLeftBorderColored(" "));

        itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignTopAndLeftNoBorder(" "));
        itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignColored(" "));
        PdfPCell cell = new PdfPCell(getAmountDetailsTable(quickBookInvoice));
        cell = PDFUtil.setNoRightBorder(cell);
        cell.setColspan(3);
        cell.setRowspan(3);
        itemDetailsTable.addCell(PDFUtil.setCellBorderColor(cell));
        PdfPCell inWordsCell = PDFUtil.getLabelCellCenterAlignNoLeftBorderColored("Rupees in words");
//        PdfPCell inWordsCell = new PdfPCell(getInWordsTable(quickBookInvoice));
//        inWordsCell.setFixedHeight(100f);
        inWordsCell.setColspan(2);
        itemDetailsTable.addCell(inWordsCell);

        inWordsCell = PDFUtil.getCellInputCellCenterAlignNoLeftBorderColored(PDFUtil.NumberToWord(quickBookInvoice.getTotalAmountAfterTax().intValue()));
        inWordsCell.setColspan(2);
        itemDetailsTable.addCell(inWordsCell);
        return itemDetailsTable;
    }

    public static PdfPTable getAmountDetailsTable(QuickBookInvoice quickBookInvoice) throws DocumentException {
        PdfPTable amountDetailsTable = new PdfPTable(2);
        amountDetailsTable.setWidthPercentage(95);
        float[] columnWidths = {2f, 1f};
        amountDetailsTable.setWidths(columnWidths);

        amountDetailsTable.addCell(PDFUtil.getLabelCellRightAlignColored("TOTAL AMOUNT BEFORE TAX"));
        amountDetailsTable.addCell(PDFUtil.getCellInputCellRightAlignNoRightBorderColored(Float.toString(quickBookInvoice.getAmountBeforeTax())));
        amountDetailsTable.addCell(PDFUtil.getLabelCellRightAlignColored("CGST 1.5 %"));
        amountDetailsTable.addCell(PDFUtil.getCellInputCellRightAlignNoRightBorderColored(Float.toString(quickBookInvoice.getCgstAmount())));

        amountDetailsTable.addCell(PDFUtil.getLabelCellRightAlignColored("SGST 1.5 %"));
        amountDetailsTable.addCell(PDFUtil.getCellInputCellRightAlignNoRightBorderColored(Float.toString(quickBookInvoice.getSgstAmount())));

        amountDetailsTable.addCell(PDFUtil.getLabelCellRightAlignColored("IGST 3.0 %"));
        amountDetailsTable.addCell(PDFUtil.getCellInputCellRightAlignNoRightBorderColored(Float.toString(quickBookInvoice.getIgstAmount())));

        amountDetailsTable.addCell(PDFUtil.getLabelCellRightAlignColored("TOTAL AMOUNT AFTER TAX"));
        amountDetailsTable.addCell(PDFUtil.getCellInputCellRightAlignNoRightBorderColored(Float.toString(quickBookInvoice.getTotalAmountAfterTax())));
        return amountDetailsTable;
    }


    public static PdfPTable getInvoiceAndDateTable(QuickBookInvoice quickBookInvoice) throws DocumentException {
        PdfPTable invoiceDateTable = new PdfPTable(5);
        invoiceDateTable.setWidthPercentage(95);
        float[] columnWidths = {1.2f, 2f, 2f, 2f, 1.5f};
        invoiceDateTable.setWidths(columnWidths);
        invoiceDateTable.addCell(PDFUtil.getCellLeftAlignNoBorder("INVOICE No."));
        invoiceDateTable.addCell(PDFUtil.getCellInputCellLeftAlignNoBorderRedColor(Long.toString(quickBookInvoice.getInvoiceId())));
        invoiceDateTable.addCell(PDFUtil.getCellInputCellCenterAlignNoBorderRedColor(quickBookInvoice.getPaymentType().name()));
        invoiceDateTable.addCell(PDFUtil.getCellRightAlignNoBorder("DATE"));
        invoiceDateTable.addCell(PDFUtil.getCellInputCellLeftAlignBottomBorderColored(PDFUtil.getDisplayFormatDate(quickBookInvoice.getBillDate())));
        invoiceDateTable.setSpacingAfter(5f);
        return invoiceDateTable;
    }

    public void generateInvoiceFile(QuickBookInvoice quickBookInvoice, HttpServletResponse response) throws IOException, DocumentException {
        Document document = new Document(PageSize.A5, 1f, 1f, 1f, 0f);

        String headerKey = "file_name";
        String headerValue = "invoice_" + quickBookInvoice.getInvoiceId() + ".pdf";
        response.setHeader(headerKey, headerValue);
        response.setHeader("Access-Control-Expose-Headers","file_name");
        PdfWriter writer = PdfWriter.getInstance(document,
                response.getOutputStream());
        Rotate event = new Rotate();
        writer.setPageEvent(event);
        document.open();
        document.add(getHeaderTable());
        document.add(getInvoiceAndDateTable(quickBookInvoice));
        document.add(getDetailsTable(quickBookInvoice));
        document.add(getItemDetails(quickBookInvoice));
        document.add(getFooterDetailsTable());
        document.close();
    }

    public void generateInvoices(List<QuickBookInvoice> quickBookInvoices, HttpServletResponse response) throws IOException, DocumentException {
        Document document = new Document(PageSize.A5, 1f, 1f, 1f, 0f);

        String headerKey = "file_name";
        String headerValue = "invoice_" + new Date().getTime() + ".pdf";
        response.setHeader(headerKey, headerValue);
        response.setHeader("Access-Control-Expose-Headers","file_name");
        PdfWriter writer = PdfWriter.getInstance(document,
                response.getOutputStream());
        Rotate event = new Rotate();
        writer.setPageEvent(event);
        document.open();
        quickBookInvoices.forEach((quickBookInvoice) -> {
            try {
                document.add(getHeaderTable());
                document.add(getInvoiceAndDateTable(quickBookInvoice));
                document.add(getDetailsTable(quickBookInvoice));
                document.add(getItemDetails(quickBookInvoice));
                document.add(getFooterDetailsTable());
                document.newPage();
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        document.close();
    }

    private PdfPTable getHeaderTable() throws BadElementException, IOException {
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(95);
        headerTable.getDefaultCell().setBorder(0);

        Image image = Image.getInstance("quick-books/src/main/resources/header.PNG");
        PdfPCell imageCell = new PdfPCell(image, true);
        imageCell.setPaddingTop(5f);
        headerTable.addCell(PDFUtil.setCellNoBorder(imageCell));
        return headerTable;
    }

    private PdfPTable getFooterDetailsTable() throws DocumentException {
        PdfPTable footerTable = new PdfPTable(2);
        footerTable.getDefaultCell().setBorder(0);
        footerTable.setWidthPercentage(95);
        float[] columnWidths = {1f, 1f};
        footerTable.setWidths(columnWidths);

        footerTable.addCell(PDFUtil.getFooterCellSmallFontLeftAlignNoBorderColored("All disputes subject to Nellore jurisdiction"));
        footerTable.addCell(PDFUtil.getFooterCellSmallFontRightAlignNoBorderColored("Certified that the particulars given above are true and correct"));
        footerTable.addCell(PDFUtil.getFooterCellSmallFontLeftAlignNoBorderColored(""));
        footerTable.addCell(PDFUtil.getFooterCellBigFontRightAlignNoBorderColored("For DEEPTHI JEWELLERS"));
        footerTable.addCell(PDFUtil.getFooterCellBigFontRightAlignNoBorderColored(" "));
        footerTable.addCell(PDFUtil.getFooterCellBigFontRightAlignNoBorderColored(" "));

        footerTable.addCell(PDFUtil.getFooterCellBigFontRightAlignNoBorderColored("Receiver's Signature"));
        footerTable.addCell(PDFUtil.getFooterCellBigFontRightAlignNoBorderColored("Authorised Signatory"));

        return footerTable;
    }

    private static PdfPTable getPaymentModeDetailsTable(QuickBookInvoice quickBookInvoice) throws DocumentException {
        PdfPTable paymentModeTable = new PdfPTable(2);
        paymentModeTable.setWidthPercentage(95);
        float[] columnWidths = {6f, 4f};
        paymentModeTable.setWidths(columnWidths);
        paymentModeTable.setSpacingAfter(5f);
        quickBookInvoice.getPaymentMode().forEach((paymentMode) -> {
            paymentModeTable.addCell(PDFUtil.getCellSmallInputCellLeftAlignOnlyRightBorderColored(paymentMode.getPaymentMode().name()));
            paymentModeTable.addCell(PDFUtil.getCellSmallInputCellRightAlignOnlyRightBorderColored(paymentMode.getAmount().toString()));
        });
        return paymentModeTable;
    }

}

    class Rotate extends PdfPageEventHelper {

    protected PdfNumber orientation = PdfPage.LANDSCAPE;

    public void setOrientation(PdfNumber orientation) {
        this.orientation = orientation;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        writer.addPageDictEntry(PdfName.ROTATE, orientation);
    }
}
