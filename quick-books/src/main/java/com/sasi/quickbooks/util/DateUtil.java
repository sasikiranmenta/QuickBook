package com.sasi.quickbooks.util;

import com.sasi.quickbooks.QuickBookConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private DateUtil(){}

    public static String convertFormatLetterFormat(String dateStr, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        try {
            Date formattedDate = df.parse(dateStr);
            df = new SimpleDateFormat(QuickBookConstants.FORMAT_DD_MMM_yyyy);
            return df.format(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
