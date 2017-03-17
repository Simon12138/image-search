package com.pictures.utils;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.joda.time.DateTime;

public class JodaDateConverter implements Converter {

    private static final long serialVersionUID = -1098462020159912097L;

    /**
     * Convert object type of <code>org.joda.time.DateTime</code> to data type
     * of <code>java.util.Date</code> or <code>java.util.Calendar</code>
     * 
     * @throws IllegalArgumentException
     *             if the objectValue is not of org.joda.time.DateTime type
     */
    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        if (objectValue == null) {
            return null;
        }
        if (objectValue instanceof DateTime) {
            DateTime dt = (DateTime) objectValue;

            Calendar calendar = Calendar.getInstance();
            calendar.set(dt.getYear(), dt.getMonthOfYear() - 1, dt.getDayOfMonth(), dt.getHourOfDay(),
                    dt.getMinuteOfHour(), dt.getSecondOfMinute());
            calendar.set(Calendar.MILLISECOND, dt.getMillisOfSecond());
            return calendar;
        }
        throw new IllegalArgumentException("Object value is not of org.joda.time.DateTime type.");
    }

    /**
     * Convert data type of <code>java.util.Date</code> or
     * <code>java.util.Calendar</code> to object type of
     * <code>org.joda.time.DateTime</code>
     * 
     * @throws IllegalArgumentException
     *             if the dataValue is not of java.util.Date or
     *             java.util.Calendar type
     */
    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        if (dataValue == null) {
            return null;
        }

        if (dataValue instanceof Date) {
            Date date = (Date) dataValue;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            return new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
        } else if (dataValue instanceof Calendar) {

            Calendar calendar = (Calendar) dataValue;

            return new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
        }

        throw new IllegalArgumentException("Data value is not of java.util.Date or java.util.Calendar type.");
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
    }

}

