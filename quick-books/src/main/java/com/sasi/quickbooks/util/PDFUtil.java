package com.sasi.quickbooks.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFUtil {

    public static String NumberToWord(Integer number) {
        if(number == 0) {
            return "Zero";
        }
        return NumberToWord(Integer.toString(number));
    }

    public static String getDisplayFormatDate(Date invoiceDate) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(invoiceDate);
    }


    private static String NumberToWord(String number) {
        String twodigitword = "";
        String word = "";
        String[] HTLC = {"", "Hundred", "Thousand", "Lakh", "Crore"}; //H-hundread , T-Thousand, ..
        int split[] = {0, 2, 3, 5, 7, 9};
        String[] temp = new String[split.length];
        boolean addzero = true;
        int len1 = number.length();
        if (len1 > split[split.length - 1]) {
            System.out.println("Error. Maximum Allowed digits " + split[split.length - 1]);
            System.exit(0);
        }
        for (int l = 1; l < split.length; l++)
            if (number.length() == split[l]) addzero = false;
        if (addzero == true) number = "0" + number;
        int len = number.length();
        int j = 0;

        while (split[j] < len) {
            int beg = len - split[j + 1];
            int end = beg + split[j + 1] - split[j];
            temp[j] = number.substring(beg, end);
            j = j + 1;
        }

        for (int k = 0; k < j; k++) {
            twodigitword = ConvertOnesTwos(temp[k]);
            if (k >= 1) {
                if (twodigitword.trim().length() != 0) word = twodigitword + " " + HTLC[k] + " " + word;
            } else word = twodigitword;
        }
        return (word+" Only.");
    }

    private static String ConvertOnesTwos(String t) {
        final String[] ones = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        final String[] tens = {"", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        String word = "";
        int num = Integer.parseInt(t);
        if (num % 10 == 0) word = tens[num / 10] + " " + word;
        else if (num < 20) word = ones[num] + " " + word;
        else {
            word = tens[(num - (num % 10)) / 10] + word;
            word = word + " " + ones[num % 10];
        }
        return word;
    }

    public static BaseColor getGreenColor() {
        return new BaseColor(63,125,129);
    }

    public static PdfPCell setCellBorderColor(PdfPCell cell) {
        cell.setBorderColor(getGreenColor());
        return cell;
    }

    public static PdfPCell setCellNoBorder(PdfPCell cell) {
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    public static PdfPCell setOnlyBottomBorder(PdfPCell cell) {
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    public static PdfPCell setBottomAndRightBorder(PdfPCell cell) {
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    public static PdfPCell setOnlyLeftBorder(PdfPCell cell) {
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    public static PdfPCell setOnlyRightBorder(PdfPCell cell) {
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    public static PdfPCell setNoTopBorder(PdfPCell cell) {
        cell.setBorderWidthTop(0);
        return cell;
    }

    public static PdfPCell setNoRightBorder(PdfPCell cell) {
        cell.setBorderWidthRight(0);
        return cell;
    }
    public static PdfPCell setNoLeftBorder(PdfPCell cell) {
        cell.setBorderWidthLeft(0);
        return cell;
    }

    public static PdfPCell setLeftRightBorder(PdfPCell cell) {
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthTop(0);
        return cell;
    }


    public static PdfPCell setLeftAlign(PdfPCell cell) {
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

    public static PdfPCell setRightAlign(PdfPCell cell) {
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }

    public static PdfPCell setCenterAlign(PdfPCell cell) {
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    public static DottedLineSeparator setDottedLineColor(DottedLineSeparator dottedLine) {
        dottedLine.setLineColor(getGreenColor());
        return dottedLine;
    }

    public static Font getLabelFontColorSize() {
        Font font = new Font();
        font.setColor(getGreenColor());
        font.setSize(8);
        return font;
    }

    public static Font getInputFontColorSize() {
        Font font = new Font(Font.FontFamily.UNDEFINED, 10, Font.BOLD);
        font.setColor(BaseColor.DARK_GRAY);
        return font;
    }


    public static Font getSmallInputFontColorSize(int size) {
        Font font = new Font(Font.FontFamily.UNDEFINED, size);
        font.setColor(BaseColor.DARK_GRAY);
        return font;
    }

    public static Font getInputRedFontColorSize() {
        Font font = new Font(Font.FontFamily.UNDEFINED, 10, Font.BOLD);
        font.setColor(BaseColor.RED);
        return font;
    }

    public static Font getFooterFontColorSize() {
        Font font = new Font();
        font.setColor(getGreenColor());
        font.setSize(7);
        return font;
    }

    public static PdfPCell getCellLeftAlignNoBorder(String content) {
        PdfPCell cell = getCell(content, getLabelFontColorSize());
        setCellNoBorder(cell);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        setLeftAlign(cell);
        return cell;
    }

    public static PdfPCell getCellRightAlignNoBorder(String content) {
        PdfPCell cell = getCell(content, getLabelFontColorSize());
        setCellNoBorder(cell);
        setRightAlign(cell);
        return cell;
    }

    public static DottedLineSeparator getDottedUnderLine() {
        DottedLineSeparator dottedLine = new DottedLineSeparator();
//        dottedLine.setOffset(-2);
        dottedLine.setGap(2f);
        setDottedLineColor(dottedLine);
        return dottedLine;
    }

    public static PdfPCell getCellDottedUnderlineLeftAlignColored(String content) {
        Phrase p = new Phrase(content, getInputFontColorSize());
        p.add(getDottedUnderLine());
        PdfPCell cell = new PdfPCell(p);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        setLeftAlign(cell);
        setCellBorderColor(cell);
        setCellNoBorder(cell);
        return cell;
    }

    public static PdfPCell getCellInputCellLeftAlignBottomBorderColored(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        setCellBorderColor(setOnlyBottomBorder(setLeftAlign(cell)));
        return cell;
    }

    public static PdfPCell getCellInputCellCenterAlignBottomAndRightBorderColored(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        return setCellBorderColor(setBottomAndRightBorder(setCenterAlign(cell)));
    }

    public static PdfPCell getCellInputCellCenterAlignTopNoBorder(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        setCellBorderColor(setNoTopBorder(cell));
        return cell;
    }

    public static PdfPCell getCellInputCellCenterAlignTopAndLeftNoBorder(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        setCellBorderColor(setNoLeftBorder(setNoTopBorder(cell)));
        return cell;
    }

    public static PdfPCell getCellInputCellCenterAlignTopAndRightNoBorder(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        setCellBorderColor(setNoRightBorder(setNoTopBorder(cell)));
        return cell;
    }


    public static PdfPCell getCellInputCellCenterAlignLeftAndRightBorderColored(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        return setCellBorderColor(setCenterAlign(setLeftRightBorder(cell)));
    }

    public static PdfPCell getCellInputCellCenterAlignBorderColored(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        return setCellBorderColor(setCenterAlign(cell));
    }

    public static PdfPCell getCellInputCellCenterAlignLeftRightBorderColored(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        return setCellBorderColor(setCenterAlign(setLeftRightBorder(cell)));
    }

    public static PdfPCell getCellInputCellCenterAlignOnlyRightBorderColored(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        return setCellBorderColor(setOnlyRightBorder(setCenterAlign(cell)));
    }

    public static PdfPCell getCellInputCellCenterAlignOnlyRightBorderColoredSmall(String content) {
        PdfPCell cell = getCell(content, getSmallInputFontColorSize(6));
        return setCellBorderColor(setOnlyRightBorder(setCenterAlign(cell)));
    }

    public static PdfPCell getCellInputCellCenterAlignOnlyLeftBorderColored(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        return setCellBorderColor(setCenterAlign(setOnlyLeftBorder(cell)));
    }

    public static PdfPCell getCellInputCellCenterAlignNoRightBorderColored(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        return setCellBorderColor(setNoRightBorder(setCenterAlign(cell)));
    }

    public static PdfPCell getCellInputCellCenterAlignNoLeftBorderColored(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        return setCellBorderColor(setCenterAlign(setNoLeftBorder(cell)));
    }

    public static PdfPCell getCellInputCellCenterAlignColored(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        return setCellBorderColor(setCenterAlign(cell));
    }

    public static PdfPCell getCellInputCellLeftAlignOnlyRightBorderColored(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        return setCellBorderColor(setOnlyRightBorder(setLeftAlign(cell)));
    }

    public static PdfPCell getCellSmallInputCellLeftAlignOnlyRightBorderColored(String content) {
        PdfPCell cell = getCell(content, getSmallInputFontColorSize(8));
        return setCellBorderColor(setOnlyRightBorder(setLeftAlign(cell)));
    }

    public static PdfPCell getCellSmallInputCellRightAlignOnlyRightBorderColored(String content) {
        PdfPCell cell = getCell(content, getSmallInputFontColorSize(8));
        return setCellBorderColor(setOnlyRightBorder(setRightAlign(cell)));
    }

    public static PdfPCell getCellInputCellLeftAlignNoBorderRedColor(String content) {
        PdfPCell cell = getCell(content, getInputRedFontColorSize());
        return setCellNoBorder(setLeftAlign(cell));
    }

    public static PdfPCell getCellInputCellCenterAlignNoBorderRedColor(String content) {
        PdfPCell cell = getCell(content, getInputRedFontColorSize());
        return setCellNoBorder(setCenterAlign(cell));
    }

    public static PdfPCell getCellInputCellRightAlignNoRightBorderColored(String content) {
        PdfPCell cell = getCell(content, getInputFontColorSize());
        return setCellBorderColor(setNoRightBorder(setRightAlign(cell)));
    }

    public static PdfPCell getLabelCellCenterAlignColored(String content) {
        PdfPCell cell = getCell(content, getLabelFontColorSize());
        return setCellBorderColor(setCenterAlign(cell));
    }

    public static PdfPCell getLabelCellLeftAlignColored(String content) {
        PdfPCell cell = getCell(content, getLabelFontColorSize());
        return setCellBorderColor(setLeftAlign(cell));
    }

    public static PdfPCell getLabelCellRightAlignColored(String content) {
        PdfPCell cell = getCell(content, getLabelFontColorSize());
        return setCellBorderColor(setRightAlign(cell));
    }

    public static PdfPCell getLabelCellCenterAlignNoRightBorderColored(String content) {
        PdfPCell cell = getCell(content, getLabelFontColorSize());
        return setCellBorderColor(setCenterAlign(setNoRightBorder(cell)));
    }

    public static PdfPCell getLabelCellCenterAlignNoLeftBorderColored(String content) {
        PdfPCell cell = getCell(content, getLabelFontColorSize());
        return setCellBorderColor(setCenterAlign(setNoLeftBorder(cell)));
    }

    public static PdfPCell getFooterCellSmallFontLeftAlignNoBorderColored(String content){
        PdfPCell cell = getCell(content, getFooterFontColorSize());
        return setLeftAlign(setCellNoBorder(cell));
    }

    public static PdfPCell getFooterCellSmallFontRightAlignNoBorderColored(String content){
        PdfPCell cell = getCell(content, getFooterFontColorSize());
        return setRightAlign(setCellNoBorder(cell));
    }

    public static PdfPCell getFooterCellBigFontRightAlignNoBorderColored(String content){
        PdfPCell cell = getCell(content, getLabelFontColorSize());
        return setRightAlign(setCellNoBorder(cell));
    }

    public static PdfPCell getCell(String content, Font font) {
       PdfPCell cell = new PdfPCell(new Phrase(content, font));
       cell.setPaddingTop(3f);
       cell.setPaddingBottom(3f);
       return cell;
    }

    public static PdfPCell getCell(String format) {
        return new PdfPCell(new Phrase(format));
    }
}
