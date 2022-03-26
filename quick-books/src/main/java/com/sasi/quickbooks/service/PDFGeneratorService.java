package com.sasi.quickbooks.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPRow;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sasi.quickbooks.PDFUtil;
import com.sasi.quickbooks.model.InvoiceItem;
import com.sasi.quickbooks.model.QuickBookInvoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@Service
@Slf4j
public class PDFGeneratorService {

    public static PdfPTable getDetailsTable(QuickBookInvoice quickBookInvoice) throws DocumentException {
        PdfPTable detailsTable = new PdfPTable(1);
        detailsTable.setWidthPercentage(80);

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
        customerNameTable.setWidthPercentage(80);
        customerNameTable.addCell(PDFUtil.getCellLeftAlignNoBorder("Customer's Name"));
        customerNameTable.addCell(PDFUtil.getCellInputCellLeftAlignBottomBorderColored(customerName));
        return customerNameTable;
    }

    public static PdfPTable getCustomerAddressTable(String address) throws DocumentException {
        PdfPTable customerAddressTable = new PdfPTable(2);
        float[] columnWidths = {0.25f, 2f};
        customerAddressTable.setWidths(columnWidths);
        customerAddressTable.setWidthPercentage(80);

        customerAddressTable.addCell(PDFUtil.getCellLeftAlignNoBorder("Address"));
        customerAddressTable.addCell(PDFUtil.getCellInputCellLeftAlignBottomBorderColored(address));
        return customerAddressTable;
    }

    public static PdfPTable getStateTable(String state, String stateCode) throws DocumentException {
        PdfPTable stateTable = new PdfPTable(4);
        float[] columnWidths = {0.42f, 2f, 0.75f, 2f};
        stateTable.setWidths(columnWidths);
        stateTable.setWidthPercentage(80);

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
        GstinTable.setWidthPercentage(80);

        GstinTable.addCell(PDFUtil.getCellLeftAlignNoBorder("GSTIN / PAN"));
        GstinTable.addCell(PDFUtil.getCellInputCellLeftAlignBottomBorderColored(gstin));
        return GstinTable;
    }

    public static PdfPTable getItemDetails(QuickBookInvoice quickBookInvoice) throws DocumentException {
        PdfPTable itemDetailsTable = new PdfPTable(5);
        itemDetailsTable.setWidthPercentage(80);
        itemDetailsTable.setSpacingBefore(10f);
        float[] columnWidths = {4f, 1f, 2f, 1.2f, 1.5f};
        itemDetailsTable.setWidths(columnWidths);

        itemDetailsTable.addCell(PDFUtil.getLabelCellCenterAlignColored("DESCRIPTION OF GOODS"));
        itemDetailsTable.addCell(PDFUtil.getLabelCellCenterAlignColored("HSN CODE"));
        itemDetailsTable.addCell(PDFUtil.getLabelCellCenterAlignColored("GROSS WEIGHT"));
        itemDetailsTable.addCell(PDFUtil.getLabelCellCenterAlignColored("RATE PER GRAM"));
        itemDetailsTable.addCell(PDFUtil.getLabelCellCenterAlignColored("AMOUNT Rs."));

        for (InvoiceItem item : quickBookInvoice.getInvoiceItems()) {
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(item.getDescriptionOfItem()));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(Integer.toString(quickBookInvoice.getInvoiceType().getValue())));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(Float.toString(item.getGrossWeight())));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(Float.toString(item.getRatePerGram())));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(Float.toString(item.getAmount())));
        }

        for (int i = 0; i < 5; i++) {
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
            itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignLeftRightBorderColored(" "));
        }

        itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignTopNoBorder(" "));
        itemDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignColored(" "));
        PdfPCell cell = new PdfPCell(getAmountDetailsTable(quickBookInvoice));
        cell.setColspan(3);
        cell.setRowspan(3);
        itemDetailsTable.addCell(PDFUtil.setCellBorderColor(cell));
        PdfPCell inWordsCell = PDFUtil.getLabelCellCenterAlignColored("Rupees in words");
//        PdfPCell inWordsCell = new PdfPCell(getInWordsTable(quickBookInvoice));
//        inWordsCell.setFixedHeight(100f);
        inWordsCell.setColspan(2);
        itemDetailsTable.addCell(inWordsCell);

        inWordsCell = PDFUtil.getCellInputCellCenterAlignColored(PDFUtil.NumberToWord(quickBookInvoice.getTotalAmountAfterTax().intValue()));
        inWordsCell.setColspan(2);
        itemDetailsTable.addCell(inWordsCell);
        return itemDetailsTable;
    }

    public static PdfPTable getAmountDetailsTable(QuickBookInvoice quickBookInvoice) throws DocumentException {
        PdfPTable amountDetailsTable = new PdfPTable(2);
        amountDetailsTable.setWidthPercentage(80);
        float[] columnWidths = {2f, 1f};
        amountDetailsTable.setWidths(columnWidths);

        amountDetailsTable.addCell(PDFUtil.getLabelCellLeftAlignColored("TOTAL AMOUNT BEFORE TAX"));
        amountDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignColored(Float.toString(quickBookInvoice.getAmountBeforeTax())));
        amountDetailsTable.addCell(PDFUtil.getLabelCellLeftAlignColored("CGST 1.5 %"));
        amountDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignColored(Float.toString(quickBookInvoice.getCgstAmount())));

        amountDetailsTable.addCell(PDFUtil.getLabelCellLeftAlignColored("SGST 1.5 %"));
        amountDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignColored(Float.toString(quickBookInvoice.getSgstAmount())));

        amountDetailsTable.addCell(PDFUtil.getLabelCellLeftAlignColored("IGST 3.0 %"));
        amountDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignColored(Float.toString(quickBookInvoice.getIgstAmount())));

        amountDetailsTable.addCell(PDFUtil.getLabelCellLeftAlignColored("TOTAL AMOUNT AFTER TAX"));
        amountDetailsTable.addCell(PDFUtil.getCellInputCellCenterAlignColored(Integer.toString(quickBookInvoice.getTotalAmountAfterTax().intValue())));
        return amountDetailsTable;
    }


    public static PdfPTable getInvoiceAndDateTable(QuickBookInvoice quickBookInvoice) throws DocumentException {
        PdfPTable invoiceDateTable = new PdfPTable(4);
        invoiceDateTable.setWidthPercentage(80);
        float[] columnWidths = {1.2f, 2f, 2f, 1.5f};
        invoiceDateTable.setWidths(columnWidths);
        invoiceDateTable.addCell(PDFUtil.getCellLeftAlignNoBorder("INVOICE No."));
        invoiceDateTable.addCell(PDFUtil.getInputCellLeftAlignNoBorderRedColor(Long.toString(quickBookInvoice.getInvoiceId())));
        invoiceDateTable.addCell(PDFUtil.getCellRightAlignNoBorder("DATE"));
        invoiceDateTable.addCell(PDFUtil.getCellInputCellLeftAlignBottomBorderColored(PDFUtil.getDisplayFormatDate(quickBookInvoice.getBillDate())));
        invoiceDateTable.setSpacingAfter(5f);
        return invoiceDateTable;
    }

    public void generateInvoiceFile(QuickBookInvoice quickBookInvoice, HttpServletResponse response) throws IOException, DocumentException {
        Document document = new Document(PageSize.A5, 10f, 10f, 10f, 0f);

        String headerKey = "file_name";
        String headerValue = "invoice_" + quickBookInvoice.getInvoiceId() + ".pdf";
        response.setHeader(headerKey, headerValue);
        response.setHeader("Access-Control-Expose-Headers","file_name");
        PdfWriter.getInstance(document,
                response.getOutputStream());
        document.open();
        document.add(getInvoiceAndDateTable(quickBookInvoice));
        document.add(getDetailsTable(quickBookInvoice));
        document.add(getItemDetails(quickBookInvoice));
        document.close();
    }
}
