package com.sasi.quickbooks.util;

import java.util.Calendar;
import java.util.Date;

public class InvoiceUtil {

    public static Integer getFinancialYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int financialYear = calendar.get(Calendar.YEAR);
        if (calendar.get(Calendar.MONTH) < Calendar.APRIL) {
            return financialYear - 1;
        }
        return financialYear;
    }
}
